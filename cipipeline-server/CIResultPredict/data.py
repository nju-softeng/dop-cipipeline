from sklearn.preprocessing import StandardScaler
from  sklearn import  preprocessing
import pandas as pd
import  numpy as np
def dataset(filepath):
    dataset = pd.read_csv(filepath)
    # print(dataset.head())
    # del dataset['build_id']
    # portion = dataset['build_result'].value_counts()
    # label = dataset['tr_status'].value_counts()
    # print('比例 =',portion)
    col = dataset.columns.values.tolist()
    # print(col)
    names = col[1:]
    def feature_normalize(dataset):
        mu = np.mean(dataset, axis=0)
        sigma = np.std(dataset, axis=0)
        return (dataset - mu) / sigma

    def feature_normalize1(data):
        ss = StandardScaler()
        ss.fit(data)
        data = ss.transform(data)
        return data
    def feature_normalize2(data): #标准化
        min_max_scaler = preprocessing.MinMaxScaler()
        data = min_max_scaler.fit_transform(data)
        return data
    y = dataset.iloc[:, 1]
    X = dataset.iloc[:, 2:]
    X = feature_normalize1(X)
    return X,y.values,names