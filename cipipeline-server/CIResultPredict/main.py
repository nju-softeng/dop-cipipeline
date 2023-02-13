import datetime
import os.path
import sys
import time
import importlib

importlib.reload(sys)

import joblib
import pandas as pd
import numpy as np
import requests
import urllib3
import data
from sklearn.model_selection import TimeSeriesSplit
from sklearn import metrics
from sklearn.metrics import confusion_matrix
from sklearn.metrics import precision_recall_fscore_support
from imblearn.ensemble import RUSBoostClassifier
from imblearn.ensemble import BalancedRandomForestClassifier
from imblearn.ensemble import BalancedBaggingClassifier
from imblearn.ensemble import EasyEnsembleClassifier
from imblearn.under_sampling import (ClusterCentroids, RandomUnderSampler,
                                     NearMiss,
                                     InstanceHardnessThreshold,
                                     CondensedNearestNeighbour,
                                     EditedNearestNeighbours,
                                     RepeatedEditedNearestNeighbours,
                                     AllKNN,
                                     NeighbourhoodCleaningRule,
                                     OneSidedSelection)
from imblearn.over_sampling import ADASYN, SMOTE, BorderlineSMOTE, SVMSMOTE, SMOTENC, RandomOverSampler
from imblearn.combine import SMOTETomek, SMOTEENN
from mycostcla import (gen_cost_mat,
                       cla_BayesMinimumRiskClassifier,
                       cla_ThresholdingOptimization,
                       cla_CostSensitiveLogisticRegression,
                       cla_CostSensitiveDecisionTreeClassifier,
                       cla_CostSensitiveRandomForestClassifier,
                       cla_CostSensitiveBaggingClassifier,
                       cla_CostSensitivePastingClassifier,
                       cla_CostSensitiveRandomPatchesClassifier)
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC, OneClassSVM
from sklearn.neighbors import LocalOutlierFactor
from sklearn.ensemble import GradientBoostingClassifier


# 获取历史pr数据作为训练集
def getPRDataFromGithub(projectPath, codeBaseInfo, authorization):
    session = requests.Session()
    session.trust_env = False
    urllib3.disable_warnings()

    codeBaseApi = 'https://api.github.com/repos/' + codeBaseInfo
    pr_id = []
    build_result = []
    git_branch = []
    last_build_result_branch = []
    time_elapse_branch = []
    committer_history = []
    committer_recent = []
    committer_exp = []
    project_history = []
    project_recent = []
    days_last_failed = []

    # 获得所有的分支名并编号
    branches = {}
    branchNumber = 0
    responsePage = 1
    try:
        while True:
            response = requests.get(codeBaseApi + '/branches', params={'per_page': 100, 'page': responsePage},
                                    headers={'authorization': authorization}, verify=False).json()
            if len(response) == 0:
                break
            for r in response:
                branches[r['name']] = branchNumber
                branchNumber += 1
            responsePage += 1
        branches[None] = branchNumber
    except Exception as e:
        return 'get data error!'

    # 获得所有的pr信息并提取存入csv
    latest_pr_reslt_sameBranch = {}
    latest_pr_time_sameBranch = {}
    latest_pr_failTime_sameBranch = {}
    commiter_pr_allNumber = {}
    commiter_pr_failNumber = {}
    commiter_pr_latestFive = {}
    prNumber = 0
    failNumber = 0
    latestFive = []
    responsePage = 1
    try:
        while True:
            response = requests.get(codeBaseApi + '/pulls',
                                    params={'state': 'closed', 'direction': 'asc', 'per_page': 100,
                                            'page': responsePage},
                                    headers={'authorization': authorization}, verify=False).json()
            responsePage += 1
            if len(response) == 0:
                break
            if type(response) == type({}):
                continue
            for r in response:
                # 提取pr信息
                current_pr_id = r['number']
                current_pr_branch = r['base']['ref']
                current_pr_committer = r['user']['id']
                current_pr_createTime = datetime.datetime.strptime(r['created_at'], '%Y-%m-%dT%H:%M:%SZ')
                current_pr_result = 0
                if r.get('merged_at') is not None:
                    current_pr_result = 1

                # pr编号
                pr_id.append(current_pr_id)
                # pr执行结果
                build_result.append(current_pr_result)
                # pr执行分支
                git_branch.append(branches.get(current_pr_branch, branches.get(None)))
                # 同一分支上一次执行的结果
                last_build_result_branch.append(latest_pr_reslt_sameBranch.get(current_pr_branch, 1))
                # 同一分支距离上一次执行的时间
                time_elapse_branch.append(
                    (current_pr_createTime - latest_pr_time_sameBranch.get(current_pr_branch,
                                                                           current_pr_createTime)).days)
                # 同一提交者历史提交的失败率
                committer_history.append(
                    commiter_pr_failNumber.get(current_pr_committer, 0) / commiter_pr_allNumber.get(
                        current_pr_committer,
                        1))
                # 同一提交者近五次提交的失败率
                commiter_latestFive = commiter_pr_latestFive.get(current_pr_committer, [])
                if len(commiter_latestFive) == 0:
                    committer_recent.append(0)
                else:
                    committer_recent.append(commiter_latestFive.count(0) / len(commiter_latestFive))
                # 同一提交者在本项目上的历史提交数的对数
                committer_exp.append(np.log(commiter_pr_allNumber.get(current_pr_committer, 1)))
                # 整个项目历史提交的失败率
                if prNumber == 0:
                    project_history.append(0)
                else:
                    project_history.append(failNumber / prNumber)
                # 整个项目近五次提交的失败率
                if len(latestFive) == 0:
                    project_recent.append(0)
                else:
                    project_recent.append(latestFive.count(0) / len(latestFive))
                # 同一分支距离上一次执行失败的时间
                days_last_failed.append((current_pr_createTime - latest_pr_failTime_sameBranch.get(current_pr_branch,
                                                                                                   current_pr_createTime)).days)

                # 存储历史数据
                prNumber += 1
                latest_pr_time_sameBranch[current_pr_branch] = current_pr_createTime
                latest_pr_reslt_sameBranch[current_pr_branch] = current_pr_result
                commiter_pr_allNumber[current_pr_committer] = commiter_pr_allNumber.get(current_pr_committer, 0) + 1
                if current_pr_result == 0:
                    commiter_pr_failNumber[current_pr_committer] = commiter_pr_failNumber.get(current_pr_committer,
                                                                                              0) + 1
                    failNumber += 1
                    latest_pr_failTime_sameBranch[current_pr_branch] = current_pr_createTime
                else:
                    commiter_pr_failNumber[current_pr_committer] = commiter_pr_failNumber.get(current_pr_committer, 0)
                commiter_latestFive = commiter_pr_latestFive.get(current_pr_committer, [])
                commiter_latestFive.append(current_pr_result)
                if len(commiter_latestFive) > 5:
                    commiter_latestFive.pop()
                commiter_pr_latestFive[current_pr_committer] = commiter_latestFive
                latestFive.append(current_pr_result)
                if len(latestFive) > 5:
                    latestFive.pop()

    except Exception as e:
        return 'get data error!'

    # 保存结果
    df = pd.DataFrame(columns=[])
    df.insert(0, 'pr_id', pr_id)
    df.insert(1, 'build_result', build_result)
    df.insert(2, 'git_branch', git_branch)
    df.insert(3, 'last_build_result_branch', last_build_result_branch)
    df.insert(4, 'time_elapse_branch', time_elapse_branch)
    df.insert(5, 'committer_history', committer_history)
    df.insert(6, 'committer_recent', committer_recent)
    df.insert(7, 'committer_exp', committer_exp)
    df.insert(8, 'project_history', project_history)
    df.insert(9, 'project_recent', project_recent)
    df.insert(10, 'days_last_failed', days_last_failed)
    df.to_csv(projectPath + '/Data-Original/' + codeBaseInfo.replace('/', '_') + '.csv', index=False, header=True,
              mode='w')
    return 'get data success'


def traversal_run(projectPath, filename):
    inputfile = projectPath + "/Data-Original/" + filename + ".csv"
    init_x, init_y, names = data.dataset(inputfile)
    if not 0 in init_y:
        return '训练集中没有失败构建样本'
    outputfile = projectPath + "/Data-Results/" + filename + "_results.csv"
    result = runtimeSeriesSplit(init_x, init_y, outputfile, filename,projectPath)
    return result


class no_sampling:
    def __init__(self, random_state=None):
        self.random_state = random_state

    def fit_resample(self, x_train, y_train):
        return x_train, y_train


# 采样算法
sam_operator = {
    # Over-sampling
    "RandomOverSampler": RandomOverSampler,
    "SMOTE": SMOTE,
    "BorderlineSMOTE": BorderlineSMOTE,
    "SVMSMOTE": SVMSMOTE,
    "ADASYN": ADASYN,
    # Under-sampling
    "RandomUnderSampler": RandomUnderSampler,
    "OneSidedSelection": OneSidedSelection,
    "NeighbourhoodCleaningRule": NeighbourhoodCleaningRule,
    "NearMiss": NearMiss,
    "InstanceHardnessThreshold": InstanceHardnessThreshold,
    # Combination
    "SMOTETomek": SMOTETomek,
    "SMOTEENN": SMOTEENN,
    # no_sampling
    "no_sampling": no_sampling}


# Ensemble Classifier
def cla_BalancedRandomForestClassifier(x_train, y_train, x_test):
    cla = BalancedRandomForestClassifier(max_depth=5, random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_RUSBoostClassifier(x_train, y_train, x_test):
    cla = RUSBoostClassifier(random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_BalancedBaggingClassifier(x_train, y_train, x_test):
    cla = BalancedBaggingClassifier(random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_EasyEnsembleClassifier(x_train, y_train, x_test):
    cla = EasyEnsembleClassifier(random_state=0)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


# SVM
def cla_SVC(x_train, y_train, x_test):
    cla = SVC(gamma='scale')
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_OneClassSVM(x_train, y_train, x_test):
    x_train, y_train = x_train[y_train == 1], y_train[y_train == 1]
    cla = OneClassSVM(nu=0.1, kernel="rbf", gamma=0.1)
    cla.fit(x_train)
    y_pred = cla.predict(x_test)
    y_pred = np.where(y_pred == -1, 0, 1)
    return y_pred, cla


def cla_LocalOutlierFactor(x_train, y_train, x_test):
    cla = LocalOutlierFactor(n_neighbors=20)
    cla.fit(x_train)
    y_pred = cla.fit_predict(x_test)
    y_pred = np.where(y_pred == -1, 0, 1)
    return y_pred, cla


def cla_RandomForestClassifier(x_train, y_train, x_test):
    cla = RandomForestClassifier(max_depth=5, random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_GaussianNB(x_train, y_train, x_test):
    cla = GaussianNB()
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_DecisionTreeClassifier(x_train, y_train, x_test):
    cla = DecisionTreeClassifier(random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


def cla_GradientBoostingClassifier(x_train, y_train, x_test):
    cla = GradientBoostingClassifier(n_estimators=100, learning_rate=0.1, max_depth=5, random_state=42)
    cla.fit(x_train, y_train)
    y_pred = cla.predict(x_test)
    return y_pred, cla


# 不平衡学习分类器
cla_operator = {
    "BalancedRandomForestClassifier": cla_BalancedRandomForestClassifier,
    "EasyEnsembleClassifier": cla_EasyEnsembleClassifier,
    "RUSBoostClassifier": cla_RUSBoostClassifier,
    "BalancedBaggingClassifier": cla_BalancedBaggingClassifier,
    "SVC": cla_SVC,
    "OneClassSVM": cla_OneClassSVM,
    "LocalOutlierFactor": cla_LocalOutlierFactor,
}

# 平衡学习分类器
bal_cla_operator = {"RandomForestClassifier": cla_RandomForestClassifier,
                    "DecisionTreeClassifier": cla_DecisionTreeClassifier,
                    "GradientBoostingClassifier": cla_GradientBoostingClassifier,
                    "GaussianNB": cla_GaussianNB, }

# 代价敏感学习
cos_cla_operator = {"BayesMinimumRiskClassifier": cla_BayesMinimumRiskClassifier,
                    "ThresholdingOptimization": cla_ThresholdingOptimization,
                    "CostSensitiveLogisticRegression": cla_CostSensitiveLogisticRegression,
                    "CostSensitiveDecisionTreeClassifier": cla_CostSensitiveDecisionTreeClassifier,
                    "CostSensitiveRandomForestClassifier": cla_CostSensitiveRandomForestClassifier,
                    "CostSensitiveBaggingClassifier": cla_CostSensitiveBaggingClassifier,
                    "CostSensitivePastingClassifier": cla_CostSensitivePastingClassifier,
                    "CostSensitiveRandomPatchesClassifier": cla_CostSensitiveRandomPatchesClassifier, }


def runtimeSeriesSplit(x, y, outputfile, project,projectPath):
    NumSplit = []
    SamplerAndClassifer = []
    Precision = []
    Recall = []
    Fscore05 = []
    Fscore1 = []
    Fscore2 = []
    Precision0 = []
    Recall0 = []
    Fscore05_anti = []
    Fscore1_anti = []
    Fscore2_anti = []

    TN = []
    FP = []
    FN = []
    TP = []
    AUC = []
    AUC0 = []
    Accuracy = []

    n_splits = round(len(x) / 1000) - 1
    if n_splits < 2:
        return '训练样本太少'
    kf = TimeSeriesSplit(n_splits=n_splits)
    for train_index, test_index in kf.split(x):
        x_train, x_test = x[train_index], x[test_index]
        y_train, y_test = y[train_index], y[test_index]
        if 0 in y_train:
            ## balanced models：12种采样方法 + no_sampling  搭配 4种平衡学习算法，可组合 13*4 = 52 model
            for i in sam_operator.keys():
                if i == 'NeighbourhoodCleaningRule' or i == 'NearMiss':
                    sampler = sam_operator.get(i)()
                elif i == 'ADASYN':
                    sampler = sam_operator.get(i)(sampling_strategy='minority')
                else:
                    sampler = sam_operator.get(i)(random_state=42)
                try:
                    sam_x_train, sam_y_train = sampler.fit_resample(x_train, y_train)
                except Exception as e:
                    # import traceback
                    # traceback.print_exc()
                    sam_x_train, sam_y_train = x_train, y_train
                for j in bal_cla_operator.keys():  # 4 models: GNB、CART、GB、RF(搭配不同采用算法，组成 imbalanced model)
                    try:
                        y_pred, cla = bal_cla_operator.get(j)(sam_x_train, sam_y_train, x_test)
                        joblib.dump(cla, projectPath + '/Predict-Model/' + project + '/' + i + "_" + j + '.pkl')
                        append_results(train_index[-1], i, j, y_test, y_pred,
                                       NumSplit, SamplerAndClassifer, Precision, Recall, Fscore05, Fscore1, Fscore2,
                                       Precision0, Recall0,
                                       Fscore05_anti, Fscore1_anti, Fscore2_anti, TN, FP, FN, TP, AUC, AUC0, Accuracy)
                    except Exception as e:
                        # import traceback
                        # traceback.print_exc()
                        continue

            ## imbalanced models：无需采样
            sam_x_train, sam_y_train = no_sampling(random_state=42).fit_resample(x_train, y_train)
            cost_mat_train = gen_cost_mat(len(sam_x_train))
            cost_mat_test = gen_cost_mat(len(x_test))

            ## 7 models：4 Ensemble(BRF、EE、RUSBoost、BB)+ 1 Kernel modification(SVC) + 2 One-class learning(OCSVM+LOF)
            for j in cla_operator.keys():
                try:
                    y_pred, cla = cla_operator.get(j)(sam_x_train, sam_y_train, x_test)
                    joblib.dump(cla, projectPath + '/Predict-Model/' + project + '/' + "no_sampling_" + j + '.pkl')
                    append_results(train_index[-1], "no_Sampling", j, y_test, y_pred,
                                   NumSplit, SamplerAndClassifer, Precision, Recall, Fscore05, Fscore1, Fscore2,
                                   Precision0, Recall0,
                                   Fscore05_anti, Fscore1_anti, Fscore2_anti, TN, FP, FN, TP, AUC, AUC0, Accuracy)
                except Exception as e:
                    # import traceback
                    # traceback.print_exc()
                    continue

            # ## 8 models: cost-sensitive
            # for k in cos_cla_operator.keys():
            #     try:
            #         y_pred = cos_cla_operator.get(k)(sam_x_train, sam_y_train, x_test, y_test, cost_mat_train,
            #                                          cost_mat_test)
            #         append_results(train_index[-1], "no_sampling", k, y_test, y_pred,
            #                        NumSplit, SamName, ClaName, Precision, Recall, Fscore05, Fscore1, Fscore2,
            #                        Precision0, Recall0,
            #                        Fscore05_anti, Fscore1_anti, Fscore2_anti, TN, FP, FN, TP, AUC, AUC0, Accuracy)
            #     except Exception as e:
            #         import traceback
            #         traceback.print_exc()
            #         print('[ERROR Classifer]:%s\t[Project]:%s' % (k, project))
            #         continue
        else:
            continue

    # 保存结果
    df = pd.DataFrame(columns=[])
    df.insert(0, 'TN', TN)
    df.insert(1, 'FP', FP)
    df.insert(2, 'FN', FN)
    df.insert(3, 'TP', TP)
    df.insert(4, 'Round', NumSplit)
    df.insert(5, 'SamplerAndClassifer', SamplerAndClassifer)
    df.insert(6, 'Precision', Precision)
    df.insert(7, 'Recall', Recall)
    df.insert(8, 'F05', Fscore05)
    df.insert(9, 'F1', Fscore1)
    df.insert(10, 'F2', Fscore2)
    df.insert(11, 'Precision0', Precision0)
    df.insert(12, 'Recall0', Recall0)
    df.insert(13, 'F05_anti', Fscore05_anti)
    df.insert(14, 'F1_anti', Fscore1_anti)
    df.insert(15, 'F2_anti', Fscore2_anti)
    df.insert(16, 'AUC', AUC)
    df.insert(17, 'AUC0', AUC0)
    df.insert(18, 'Accuracy', Accuracy)
    # df.to_excel(outputfile,sheet_name=str(train_index[-1]))
    df.to_csv(outputfile, index=False, header=True, mode='w')
    return 'Success'


def append_results(numsplit, samname, claname, y_test, y_pred,
                   NumSplit, SamplerAndClassifer, Precision, Recall,
                   Fscore05, Fscore1, Fscore2, Precision0, Recall0,
                   Fscore05_anti, Fscore1_anti, Fscore2_anti, TN, FP, FN, TP, AUC, AUC0, Accuracy):
    # 根据 Precision, Recall 计算 F1、F2、F05
    def cal_Fscore(precision, recall):
        if precision == 0 or recall == 0:
            F1, F2, F05 = 0, 0, 0
        else:
            F1 = 2 * precision * recall / (precision + recall)
            F2 = 5 * precision * recall / (4 * precision + recall)
            F05 = 1.25 * precision * recall / (0.25 * precision + recall)
        return F1, F2, F05

    # classifier.fit(sam_x_train, sam_y_train,x_test)
    # y_pred = classifier.predict(x_test)
    # target_names = ['failed', 'passed']  # doctest : +NORMALIZE_WHITESPACE
    # report = classification_report_imbalanced(y_test, y_pred, target_names=target_names)
    tn, fp, fn, tp = confusion_matrix(y_test, y_pred).ravel()
    prec, recall, fscore, _ = precision_recall_fscore_support(y_test, y_pred, pos_label=1, average="binary")
    f1, f2, f05 = cal_Fscore(prec, recall)
    prec0, recall0, fscore0, _ = precision_recall_fscore_support(y_test, y_pred, pos_label=0, average="binary")
    f1_anti, f2_anti, f05_anti = cal_Fscore(prec0, recall0)
    fpr, tpr, thresholds = metrics.roc_curve(y_test, y_pred, pos_label=1)
    auc = metrics.auc(fpr, tpr)
    fpr0, tpr0, thresholds0 = metrics.roc_curve(y_test, y_pred, pos_label=0)
    auc0 = metrics.auc(fpr0, tpr0)
    accuracy = (tn + tp) / (tn + tp + fn + fp)

    TN.append(tn)
    FP.append(fp)
    FN.append(fn)
    TP.append(tp)
    Precision.append(prec)
    Recall.append(recall)
    Fscore05.append(f05)
    Fscore1.append(f1)
    Fscore2.append(f2)
    Precision0.append(prec0)
    Recall0.append(recall0)
    Fscore05_anti.append(f05_anti)
    Fscore1_anti.append(f1_anti)
    Fscore2_anti.append(f2_anti)
    AUC.append(auc)
    AUC0.append(auc0)
    Accuracy.append(accuracy)
    NumSplit.append(numsplit)
    SamplerAndClassifer.append(samname + "_" + claname)  # record sampler name and classifier name


def trainModel(projectPath, codeBaseInfo, authorization):
    # 提取历史pr信息导csv文件用于训练和验证
    getDataResult = getPRDataFromGithub(projectPath, codeBaseInfo, authorization)
    if getDataResult != 'get data success':
        return getDataResult
    # 给定项目集，每个项目67个模型的多轮验证。
    if not os.path.exists(projectPath + '/Predict-Model/' + codeBaseInfo.replace('/', '_')):
        os.mkdir(projectPath + '/Predict-Model/' + codeBaseInfo.replace('/', '_'))
    trainResult = traversal_run(projectPath, codeBaseInfo.replace('/', '_'))
    return trainResult


def trainAll():
    projectPath = '/Users/yangyuyan/Desktop/dop-cipipeline/cipipeline-server/CIResultPredict'
    codeBaseInfo = [
        'CloudifySource/cloudify', 'diaspora/diaspora', 'gradle/gradle', 'Graylog2/graylog2-server', 'jruby/jruby',
        'mitchellh/vagrant-aws', 'openSUSE/open-build-service',
        'opf/openproject', 'owncloud/android', 'rails/rails', 'rapid7/metasploit-framework',
        'rubinius/rubinius', 'ruby/ruby', 'SonarSource/sonarqube']
    authorization = 'Bearer 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'
    for info in codeBaseInfo:
        trainModel(projectPath,info, authorization)


def trainOne():
    projectPath = '/Users/yangyuyan/Desktop/dop-cipipeline/cipipeline-server/CIResultPredict'
    codeBaseInfo = 'jruby/jruby'
    authorization = 'Bearer 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'
    trainModel(projectPath,codeBaseInfo, authorization)


if __name__ == '__main__':
    # trainOne()
    # trainAll()
    projectPath = sys.argv[1]
    codeBaseInfo = sys.argv[2]
    authorization = sys.argv[3]
    res=trainModel(projectPath,codeBaseInfo, authorization)
    if res=='Success':
        print('Success')
    else:
        print("Fail "+res)
