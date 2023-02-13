import numpy as np
from costcla.models import BayesMinimumRiskClassifier
from costcla.models import CostSensitiveLogisticRegression
from costcla.models import CostSensitiveDecisionTreeClassifier
from costcla.models import CostSensitiveRandomForestClassifier
from costcla.models import CostSensitiveBaggingClassifier
from costcla.models import CostSensitivePastingClassifier
from costcla.models import CostSensitiveRandomPatchesClassifier
from sklearn.ensemble import RandomForestClassifier
from costcla.models import ThresholdingOptimization

def gen_cost_mat(len_set):
    cost_mat = np.zeros((len_set,4))
    cost_mat[:,0] = 3
    cost_mat[:,1] = 1
    cost_mat[:,2] = 0.0
    cost_mat[:,3] = 0.0
    return cost_mat

# cost-sensitive classifiers

def cla_BayesMinimumRiskClassifier(x_train, y_train, x_test, y_test, cost_mat_train, cost_mat_test):
    print("BayesMinimumRiskClassifier")
    cla = BayesMinimumRiskClassifier()
    rf = RandomForestClassifier(random_state=0).fit(x_train, y_train)
    y_prob_test = rf.predict_proba(x_test)
    # 必须将 y_test(Series)转为 ndarray(Series.values)，否则会因为Series的indexs与y_prob_test的indexs不一致出错
    cla.fit(y_test, y_prob_test)
    y_pred = cla.predict(y_prob_test, cost_mat_test)
    return y_pred
#
def cla_ThresholdingOptimization(x_train, y_train, x_test,y_test,cost_mat_train, cost_mat_test):
    print("ThresholdingOptimization")
    rf = RandomForestClassifier(random_state=0).fit(x_train, y_train)
    y_prob_train = rf.predict_proba(x_train)
    y_prob_test = rf.predict_proba(x_test)
    cla = ThresholdingOptimization().fit(y_prob_train, cost_mat_train, y_train)
    y_pred = cla.predict(y_prob_test)
    return y_pred

def cla_CostSensitiveLogisticRegression(x_train, y_train, x_test,y_test,cost_mat_train, cost_mat_test):
    print("CostSensitiveLogisticRegression")
    cla = CostSensitiveLogisticRegression()
    cla.fit(x_train, y_train, cost_mat_train)
    y_pred = cla.predict(x_test)
    return y_pred

def cla_CostSensitiveDecisionTreeClassifier(x_train, y_train, x_test, y_test,cost_mat_train, cost_mat_test):
    print("CostSensitiveDecisionTreeClassifier")
    cla= CostSensitiveDecisionTreeClassifier()
    y_pred = cla.fit(x_train, y_train, cost_mat_train).predict(x_test)
    return y_pred

def cla_CostSensitiveRandomForestClassifier(x_train, y_train, x_test, y_test,cost_mat_train, cost_mat_test):
    print("CostSensitiveRandomForestClassifier")
    cla= CostSensitiveRandomForestClassifier()
    y_pred = cla.fit(x_train, y_train, cost_mat_train).predict(x_test)
    return y_pred

def cla_CostSensitiveBaggingClassifier(x_train, y_train, x_test,y_test,cost_mat_train, cost_mat_test):
    print("CostSensitiveBaggingClassifier")
    cla = CostSensitiveBaggingClassifier()
    y_pred = cla.fit(x_train, y_train, cost_mat_train).predict(x_test)
    return y_pred

def cla_CostSensitivePastingClassifier(x_train, y_train, x_test,y_test,cost_mat_train, cost_mat_test):
    print("CostSensitivePastingClassifier")
    cla = CostSensitivePastingClassifier()
    y_pred = cla.fit(x_train, y_train, cost_mat_train).predict(x_test)
    return y_pred

def cla_CostSensitiveRandomPatchesClassifier(x_train, y_train, x_test,y_test,cost_mat_train, cost_mat_test):
    print("CostSensitiveRandomPatchesClassifier")
    cla = CostSensitiveRandomPatchesClassifier(combination='weighted_voting')
    y_pred = cla.fit(x_train, y_train, cost_mat_train).predict(x_test)
    return y_pred