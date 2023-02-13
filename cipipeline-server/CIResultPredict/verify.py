import datetime
import os.path
import sys

import joblib
import numpy as np
import pandas as pd
import requests
import urllib3
from sklearn.preprocessing import StandardScaler


# 获取当前验证pr的数据
def getCurrentPRData(projectPath,codeBaseInfo, authorization, prId):
    session = requests.Session()
    session.trust_env = False
    urllib3.disable_warnings()

    codeBaseApi = 'https://api.github.com/repos/' + codeBaseInfo

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

        currentPrData = requests.get(codeBaseApi + '/pulls/' + str(prId), headers={'authorization': authorization},
                                     verify=False).json()
        current_pr_id = prId
        current_pr_branch = currentPrData['base']['ref']
        current_pr_committer = currentPrData['user']['id']
        current_pr_createTime = datetime.datetime.strptime(currentPrData['created_at'], '%Y-%m-%dT%H:%M:%SZ')

        # 获得历史pr信息用于生成当前pr验证参数
        latest_pr_reslt_sameBranch = 1
        latest_pr_time_sameBranch = current_pr_createTime
        latest_pr_failTime_sameBranch = current_pr_createTime
        commiter_pr_allNumber = 0
        commiter_pr_failNumber = 0
        commiter_pr_latestFive = []
        prNumber = 0
        failNumber = 0
        latestFive = []
        responsePage = 1
        while True:
            response = requests.get(codeBaseApi + '/pulls',
                                    params={'state': 'closed', 'direction': 'asc', 'per_page': 100, 'page': responsePage},
                                    headers={'authorization': authorization}, verify=False).json()
            if len(response) == 0:
                break
            for r in response:
                if r['number'] >= current_pr_id:
                    break
                history_pr_branch = r['base']['ref']
                history_pr_committer = r['user']['id']
                history_pr_createTime = datetime.datetime.strptime(r['created_at'], '%Y-%m-%dT%H:%M:%SZ')
                history_pr_result = 0
                if r.get('merged_at') is not None:
                    history_pr_result = 1
                if current_pr_branch == history_pr_branch:
                    latest_pr_reslt_sameBranch = history_pr_result
                    latest_pr_time_sameBranch = history_pr_createTime
                    if history_pr_result == 0:
                        latest_pr_failTime_sameBranch = history_pr_createTime
                if current_pr_committer == history_pr_committer:
                    commiter_pr_allNumber += 1
                    if history_pr_result == 0:
                        commiter_pr_failNumber += 1
                    commiter_pr_latestFive.append(history_pr_result)
                    if len(commiter_pr_latestFive)>5:
                        commiter_pr_latestFive.pop()
                prNumber += 1
                latestFive.append(history_pr_result)
                if len(latestFive)>5:
                    latestFive.pop()
                if history_pr_result == 0:
                    failNumber += 1
            else:
                responsePage += 1
                continue
            break
    except Exception as e:
        print(e)
        return 'get test data error!',-1

    # 保存结果
    if os.path.isfile(projectPath+'/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv'):
        df = pd.read_csv(projectPath+'/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv')
    else:
        df = pd.DataFrame(columns=['pr_id', 'build_result', 'git_branch', 'last_build_result_branch', 'time_elapse_branch',
                               'committer_history', 'committer_recent', 'committer_exp', 'project_history',
                               'project_recent', 'days_last_failed', ])
    lineNumber=len(df)
    df.loc[lineNumber,'pr_id']=current_pr_id
    df.loc[lineNumber, 'build_result']= -1
    df.loc[lineNumber, 'git_branch']= branches.get(current_pr_branch, branches.get(None))
    df.loc[lineNumber, 'last_build_result_branch']= latest_pr_reslt_sameBranch
    df.loc[lineNumber, 'time_elapse_branch']= (current_pr_createTime - latest_pr_time_sameBranch).days
    if commiter_pr_allNumber == 0:
        df.loc[lineNumber, 'committer_history']= 0
    else:
        df.loc[lineNumber, 'committer_history']= commiter_pr_failNumber / commiter_pr_allNumber
    if len(commiter_pr_latestFive) == 0:
        df.loc[lineNumber, 'committer_recent']= 0
    else:
        df.loc[lineNumber, 'committer_recent']= commiter_pr_latestFive.count(0) / len(commiter_pr_latestFive)
    if commiter_pr_allNumber==0:
        df.loc[lineNumber, 'committer_exp'] =0
    else:
        df.loc[lineNumber, 'committer_exp']= np.log(commiter_pr_allNumber)
    if prNumber == 0:
        df.loc[lineNumber, 'project_history']= 0
    else:
        df.loc[lineNumber, 'project_history']= failNumber / prNumber
    if len(latestFive) == 0:
        df.loc[lineNumber, 'project_recent']= 0
    else:
        df.loc[lineNumber, 'project_recent']= latestFive.count(0) / len(latestFive)
    df.loc[lineNumber, 'days_last_failed']= (current_pr_createTime - latest_pr_failTime_sameBranch).days
    df.to_csv(projectPath+'/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv', index=False, header=True, mode='w')
    return "Success",lineNumber

# 获取已保存的验证所需数据
def getTestData(projectPath,codeBaseInfo,lineNumber):
    dataset = pd.read_csv(projectPath+'/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv')
    Y=dataset.iloc[lineNumber,1]
    X = dataset.iloc[lineNumber, 2:]
    def feature_normalize1(data):
        ss = StandardScaler()
        ss.fit(data)
        data = ss.transform(data)
        return data

    # X = feature_normalize1(np.array(X).reshape(1, -1))
    return Y,np.array(X).reshape(1, -1)

# 选取训练中精确度最高的模型
def getModel(projectPath,codeBaseInfo,standard):
    model_result_file=projectPath+"/Data-Results/" + codeBaseInfo.replace('/', '_') + "_results.csv"
    result = pd.read_csv(model_result_file)
    index=result[standard].argmax()
    return result.iloc[index,5]

def verify(projectPath,codeBaseInfo, authorization, prId,standard):
    if not os.path.exists(projectPath+"/Data-Results/" + codeBaseInfo.replace('/', '_') + "_results.csv"):
        return '训练模型不存在'
    getDataResult,lineNumber=getCurrentPRData(projectPath,codeBaseInfo, authorization, prId)
    if getDataResult!='Success':
        return getDataResult
    Y, X = getTestData(projectPath,codeBaseInfo, lineNumber)
    modelName = getModel(projectPath,codeBaseInfo, standard)
    model = joblib.load(projectPath+'/Predict-Model/' + codeBaseInfo.replace('/', '_') + '/' + modelName + '.pkl')
    if modelName == 'no_sampling_OneClassSVM' or modelName == 'no_sampling_LocalOutlierFactor':
        predict_result = model.predict(X)
        predict_result = np.where(predict_result == -1, 0, 1)
    else:
        predict_result = model.predict(X)
    dataset = pd.read_csv(projectPath + '/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv')
    dataset.loc[lineNumber,'build_result']=predict_result[0]
    dataset.to_csv(projectPath+'/Data-Verify-Result/' + codeBaseInfo.replace('/', '_') + '.csv', index=False, header=True, mode='w')
    return predict_result[0]

def trainTest(projectPath,codeBaseInfo,standard):
    if not os.path.exists(projectPath+"/Data-Results/" + codeBaseInfo.replace('/', '_') + "_results.csv"):
        return '训练模型不存在'
    total = 0
    TP = 0
    FP = 0
    TN = 0
    FN = 0
    dataset = pd.read_csv(projectPath+'/Data-Original/' + codeBaseInfo.replace('/', '_') + '.csv')
    list = dataset['pr_id'].tolist()
    for id in list:
        Y, X = getTestData(projectPath,codeBaseInfo, id)
        modelName = getModel(projectPath,codeBaseInfo, standard)
        model = joblib.load(projectPath+'/Predict-Model/' + codeBaseInfo.replace('/', '_') + '/' + modelName + '.pkl')
        if modelName == 'no_sampling_OneClassSVM' or modelName == 'no_sampling_LocalOutlierFactor':
            predict_result = model.predict(X)
            predict_result = np.where(predict_result == -1, 0, 1)
        else:
            predict_result = model.predict(X)
        total += 1
        if predict_result[0] == 0 and Y == 0:
            TP += 1
        if predict_result[0] == 1 and Y == 0:
            FP += 1
        if predict_result[0] == 1 and Y == 1:
            TN += 1
        if predict_result[0] == 0 and Y == 1:
            FN += 1
    print("project="+codeBaseInfo)
    print("standard=" + standard)
    print("total=" + str(total))
    print("TP=" + str(TP))
    print("FP=" + str(FP))
    print("TN=" + str(TN))
    print("FN=" + str(FN))

def testAll():
    # , 'rails/rails'
    projectPath = '/Users/yangyuyan/Desktop/dop-cipipeline/cipipeline-server/CIResultPredict'
    codeBaseInfo = ['CloudifySource/cloudify', 'diaspora/diaspora', 'gradle/gradle',
                    'Graylog2/graylog2-server', 'jruby/jruby', 'mitchellh/vagrant-aws', 'openSUSE/open-build-service',
                    'opf/openproject', 'owncloud/android', 'rapid7/metasploit-framework',
                    'rubinius/rubinius', 'ruby/ruby', 'SonarSource/sonarqube']
    standard = 'Precision'
    for info in codeBaseInfo:
        trainTest(projectPath,info, standard)

def testOne():
    projectPath= '/Users/yangyuyan/Desktop/dop-cipipeline/cipipeline-server/CIResultPredict'
    codeBaseInfo = 'jruby/jruby'
    authorization = 'Bearer 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'
    prId = 7541
    standard = 'Precision'
    verify(projectPath,codeBaseInfo, authorization, prId,standard)

if __name__ == '__main__':
    # testOne()
    # testAll()
    projectPath = sys.argv[1]
    codeBaseInfo = sys.argv[2]
    prId = int(sys.argv[3])
    authorization = sys.argv[4]
    standard = 'Precision'
    predictResult=verify(projectPath,codeBaseInfo, authorization, prId, standard)
    print(predictResult)
