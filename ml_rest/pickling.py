import pickle

import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier, GradientBoostingRegressor
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.pipeline import Pipeline

from tokenizers import CustomTokenizerC, CustomTokenizerR

clf_class= Pipeline(steps=[
    ('norm',TfidfVectorizer(lowercase=True, tokenizer=CustomTokenizerC(), analyzer='word', stop_words=None, ngram_range=(1, 1),use_idf=True, smooth_idf=True, sublinear_tf=False)),
     ('clf',GradientBoostingClassifier(learning_rate=0.1,loss='deviance',max_depth=200,max_features='log2',max_leaf_nodes=300,min_samples_leaf=3,min_samples_split=2,n_estimators=350,subsample=0.7,warm_start=False,tol=1e-3))
     ])


clf_reg= Pipeline(steps=[
    ('norm',TfidfVectorizer(lowercase=True, tokenizer=CustomTokenizerR(), analyzer='word', stop_words=None, ngram_range=(1, 2),use_idf=True, smooth_idf=True, sublinear_tf=False)),
     ('clf',GradientBoostingRegressor(learning_rate=0.1,loss='lad',max_depth=200,max_features='log2',max_leaf_nodes=300,min_samples_leaf=3,min_samples_split=2,n_estimators=350,subsample=0.7,warm_start=False,tol=1e-3))
     ])

science=pd.read_csv("computed_science_data.csv", encoding='ISO-8859-1')


clf_class.fit(science.body,science.removed)

clasif_file=open('pickle_class','wb')
pickle.dump(clf_class,clasif_file)
clasif_file.close()

clf_reg.fit(science.body,science.score)

reg_file=open('pickle_reg','wb')
pickle.dump(clf_reg,reg_file)
reg_file.close()