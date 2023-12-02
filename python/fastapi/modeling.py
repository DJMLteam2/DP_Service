import os
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
import pandas as pd
import os
import pymysql
from konlpy.tag import Okt


okt = Okt()
current_dir = os.path.dirname(os.path.abspath(__file__))
code = os.path.join(current_dir,'DB_INFO.pkl')
model = os.path.join(current_dir,'model.pkl')

with open(code, "rb") as file:
    USER = pickle.load(file)
    PASSWD = pickle.load(file)
    HOST = pickle.load(file)
    PORT = pickle.load(file)
    NAME = pickle.load(file)

# pymysql로 dataFrame 호출

host = HOST
user = USER
password = PASSWD
database = NAME
charset = 'utf8mb4'

conn = pymysql.connect(host=host,user=user,password=password,database=database,charset=charset)
sql = "select * from TRAVEL_SPOT"
cursor = conn.cursor()
cursor.execute(sql)
df = pd.DataFrame(cursor.fetchall(), columns= [i[0] for i in cursor.description])
cursor.execute(sql)
df2 = pd.DataFrame(cursor.fetchall(), columns= [i[0] for i in cursor.description])
df = df.fillna('')
df2 = df2.fillna('')


df2['SPOT_CITY'] = '전체'
joined_df = pd.concat([df,df2], axis=0, ignore_index=True)
len(df),len(df2),len(joined_df)

joined_df['vec'] = joined_df['SPOT_CATCHTITLE'].apply(lambda x: x[:100]) + joined_df['SPOT_TREATMENU'].apply(lambda x: x[:200]) + joined_df['SPOT_TAGNAME'].apply(lambda x: x[:200]) + joined_df['SPOT_OVERVIEW'].apply(lambda x: x[:200])
joined_df['vec_f'] = joined_df['SPOT_CATCHTITLE'].apply(lambda x: x[:100]) + joined_df['SPOT_TREATMENU'].apply(lambda x: x[:200]) + joined_df['SPOT_TAGNAME'].apply(lambda x: x[:200])

spot_df = joined_df[joined_df['SPOT_CITY_CONTENT_TYPE'] != 39]
food_df = joined_df[joined_df['SPOT_CITY_CONTENT_TYPE'] == 39]
spot_df.__len__(), food_df.__len__()



# spot 학습

vectorizer_spot = TfidfVectorizer()
vectorizer_food = TfidfVectorizer()



# 전체 도시에 대한 TF-IDF 행렬 계산
tag_list_all = []
for i in spot_df[spot_df['SPOT_CITY'] != '전체']['vec'].tolist():
    tag_list_all.append(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
    # print(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
    # print(len(tag_list_all))

matrix_spot = vectorizer_spot.fit_transform(tag_list_all)

# 각 도시에 대한 TF-IDF 행렬 계산
city_matrices_spot = {}
for city in spot_df['SPOT_CITY'].unique():
    tag_list_city = []
    city_df = spot_df[spot_df['SPOT_CITY'] == city]
    
    for i in city_df['vec'].tolist():
        tag_list_city.append(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
        # print(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
        # print(tag_list_city[-1][-5:-1])
        

    city_matrices_spot[city] = vectorizer_spot.transform(tag_list_city)


# food 학습

# 전체 도시에 대한 TF-IDF 행렬 계산
tag_list_all_food = []
for i in food_df[food_df['SPOT_CITY'] != '전체']['vec'].tolist():
    tag_list_all_food.append(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
    # print(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
    # print(len(tag_list_all_food))


matrix_food = vectorizer_food.fit_transform(tag_list_all_food)

# 각 도시에 대한 TF-IDF 행렬 계산
city_matrices_food = {}
for city in food_df['SPOT_CITY'].unique():
    tag_list_city_food = []
    city_df = food_df[food_df['SPOT_CITY'] == city]
    
    for i in city_df['vec'].tolist():
        tag_list_city_food.append(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
        # print(' '.join(list(set([j for j in okt.nouns(i) if len(j) > 1 and any(char.isalpha() and not char.isascii() for char in j)]))))
        # print(tag_list_city_food[-1][-5:-1])
        
        
    city_matrices_food[city] = vectorizer_food.transform(tag_list_city_food)


# 저장
with open(model, 'wb') as file:
        pickle.dump({"spot":{
                "vectorizer": vectorizer_spot,
                "matrix": matrix_spot,
                "city_matrices": city_matrices_spot
        },"food":{
                "vectorizer": vectorizer_food,
                "matrix": matrix_food,
                "city_matrices": city_matrices_food
        }}, file)
