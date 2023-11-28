import os
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import numpy as np
import pandas as pd
from konlpy.tag import Okt
from typing import List
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import os
import time
import random

# 둘중 하나만 사용


###################################################################################
# path 
current_dir = os.path.dirname(os.path.abspath(__file__))
df_path = os.path.join(current_dir, 'travel_spot_v1.csv')
model_path = os.path.join(current_dir, 'travel_model_v4.pkl')

###################################################################################



df = pd.read_csv(df_path, index_col=0)

okt = Okt()
app = FastAPI()
df = df.fillna('')

spot_df = df[df['contentType'] != 39]
food_df = df[df['contentType'] == 39]


# getAnswer Model type
class Question(BaseModel):
    question: str
    area: str

class OutputData(BaseModel):
    question: str
    recommend: List[str]

# tf-idf model load
try:
    with open(model_path, 'rb') as file:
        loaded_data = pickle.load(file)
        
        vectorizer_spot = loaded_data["spot"]["vectorizer"]
        matrix_spot = loaded_data["spot"]["matrix"]
        city_matrices_spot = loaded_data["spot"]["city_matrices"]

        vectorizer_food = loaded_data["food"]["vectorizer"]
        matrix_food = loaded_data["food"]["matrix"]
        city_matrices_food = loaded_data["food"]["city_matrices"]

except Exception as e:
    print(e)



# response data def
def add_to_similar_tags(df, sorted_indices, cos_similarities, similar_tags):
    bag = []
    for index in sorted_indices:
        bag.append({
            "id": str(df.iloc[index]['id']),
            "area": str(df.iloc[index]['city']),
            "title": str(df.iloc[index]['title']),
            "similarity": str(("{:.1f}".format(cos_similarities[0][index]*100))) + '%',
            "catchtitle": str(df.iloc[index]['catchtitle']),
            "detail": str(df.iloc[index]['detail']),
            "treatMenu": str(df.iloc[index]['treatMenu']),
            "tagName": str(df.iloc[index]['tagName']),
            "addr": str(df.iloc[index]['addr']),
            "info": str(df.iloc[index]['info']),
            # "lat": str(df.iloc[index]['parking']),
            "useTime": str(df.iloc[index]['useTime']),
            "conLike": str(df.iloc[index]['conLike']),
            "conRead": str(df.iloc[index]['conRead']),
            "conShare": str(df.iloc[index]['conShare']),
            "imgPath": str(df.iloc[index]['imgPath']),
            "overView": str(df.iloc[index]['overView'][:100]),
            "lat": str(df.iloc[index]['lat']),
            "lon": str(df.iloc[index]['lon'])
            })
    similar_tags.append(bag)



@app.post("/getAnswer", response_model=OutputData)
def getAnswer(question: Question):

    
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    city_spot = city_matrices_spot[question.area]
    city_food = city_matrices_food[question.area]

    food_city_df = food_df[food_df['city']== question.area]
    spot_city_df = spot_df[spot_df['city']== question.area]

    
    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    question_spot = vectorizer_spot.transform([' '.join(okt.nouns(f'{question.question}'))])
    question_food = vectorizer_food.transform([' '.join(okt.nouns(f'{question.question}'))])

    #spot 인덱스 추출
    cos_similarities_spot = linear_kernel(question_spot, city_spot)
    sorted_indices_spot = np.argsort(cos_similarities_spot[0])[::-1][:5]

    #food 인덱스 추출
    cos_similarities_food = linear_kernel(question_food, city_food)
    sorted_indices_food = np.argsort(cos_similarities_food[0])[::-1][:5]

     
    # 결과를 저장할 리스트 초기화
    similar_tags = []

    # 리스트로 추가
    try:
        add_to_similar_tags(spot_city_df, sorted_indices_spot, cos_similarities_spot, similar_tags)
        add_to_similar_tags(food_city_df, sorted_indices_food, cos_similarities_food, similar_tags)
        print('done')
    
    except Exception as e:
        print(e)
    
    
    return JSONResponse(content={"question": question.question, "recommend": similar_tags})



@app.post("/getAnswer_debug", response_model= OutputData)
def getAnswer(question: Question):
    # global df, spot_df, food_df
    
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    city_spot = city_matrices_spot[question.area]
    city_food = city_matrices_food[question.area]

    # df = df[df['city']== question.area]
    food_city_df = food_df[food_df['city']== question.area]
    spot_city_df = spot_df[spot_df['city']== question.area]
    print('spot_tabel\'s len =',len(food_city_df))
    print('food_tabel\'s len =',len(spot_city_df))

    question_spot = vectorizer_spot.transform([' '.join(okt.nouns(f'{question.question}'))])
    question_food = vectorizer_food.transform([' '.join(okt.nouns(f'{question.question}'))])

    #spot 인덱스 추출
    cos_similarities_spot = linear_kernel(question_spot, city_spot)
    sorted_indices_spot = np.argsort(cos_similarities_spot[0])[::-1][:5]

    #food 인덱스 추출
    cos_similarities_food = linear_kernel(question_food, city_food)
    sorted_indices_food = np.argsort(cos_similarities_food[0])[::-1][:5]
    print('spot_index =', sorted_indices_spot)
    print('food_index =', sorted_indices_food)
     
    # 결과를 저장할 리스트 초기화
    similar_tags = []
 
    # 리스트로 추가
    try:
        bag = []
        for num, index in enumerate(sorted_indices_spot):
            bag.append({
                "id": str(spot_city_df.iloc[index]['id']),
                "area": str(spot_city_df.iloc[index]['city']),
                "title": str(spot_city_df.iloc[index]['title']),
                "similarity": str(("{:.1f}".format(cos_similarities_spot[0][index]*100))) + '%',
                "catchtitle": str(spot_city_df.iloc[index]['catchtitle']),
                # "detail": str(spot_city_df.iloc[index]['detail']),
                "treatMenu": str(spot_city_df.iloc[index]['treatMenu']),
                "tagName": str(spot_city_df.iloc[index]['tagName']),
                "addr": str(spot_city_df.iloc[index]['addr']),
                "info": str(spot_city_df.iloc[index]['info']),
                # "lat": str(spot_city_df.iloc[index]['parking']),
                "useTime": str(spot_city_df.iloc[index]['useTime']),
                "conLike": str(spot_city_df.iloc[index]['conLike']),
                "conRead": str(spot_city_df.iloc[index]['conRead']),
                "conShare": str(spot_city_df.iloc[index]['conShare']),
                "imgPath": str(df.iloc[index]['imgPath']),
                # "overView": str(spot_city_df.iloc[index]['overView']),
                "lat": str(spot_city_df.iloc[index]['lat']),
                "lon": str(spot_city_df.iloc[index]['lon'])
                
                }) 
        similar_tags.append(bag)
        bag = []
        for num, index in enumerate(sorted_indices_food):
            bag.append({
                "id": str(food_city_df.iloc[index]['id']),
                "area": str(food_city_df.iloc[index]['city']),
                "title": str(food_city_df.iloc[index]['title']),
                "similarity": str(("{:.1f}".format(cos_similarities_food[0][index]*100))) + '%',
                "catchtitle": str(food_city_df.iloc[index]['catchtitle']),
                # "detail": str(food_city_df.iloc[index]['detail']),
                "treatMenu": str(food_city_df.iloc[index]['treatMenu']),
                "tagName": str(food_city_df.iloc[index]['tagName']),
                "addr": str(food_city_df.iloc[index]['addr']),
                "info": str(food_city_df.iloc[index]['info']),
                # "lat": str(food_city_df.iloc[index]['parking']),
                "useTime": str(food_city_df.iloc[index]['useTime']),
                "conLike": str(food_city_df.iloc[index]['conLike']),
                "conRead": str(food_city_df.iloc[index]['conRead']),
                "conShare": str(food_city_df.iloc[index]['conShare']),
                "imgPath": str(df.iloc[index]['imgPath']),
                # "overView": str(food_city_df.iloc[index]['overView']),
                "lat": str(food_city_df.iloc[index]['lat']),
                "lon": str(food_city_df.iloc[index]['lon'])
                })
        similar_tags.append(bag)
        
        print('done')
    
    except Exception as e:
        print(e)
        print('indexing error?')
        print(index,'and...',num)
    
    
    return JSONResponse(content={"question": [question.question, morphed_question], "recommend": similar_tags})


@app.get("/test")
def test():

    question = "bts 효도여행 가고싶어"

    question_tfidf = vectorizer_spot.transform(okt.morphs(question))
    cos_similarities = linear_kernel(question_tfidf, matrix_spot)
    sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]

    print('질문 = ', question)
    for i, index in enumerate(sorted_indices):
        similarity = cos_similarities[0][index]
        print(f"{i + 1}위, index:{index}, 유사도: {similarity}")
    
    similar_tags = [{"title": str(df.loc[index, 'title']), "overView": str(df.loc[index, 'overView']), "similarity": float(cos_similarities[0][index])} for index in sorted_indices]

    return JSONResponse(content={"question": question, "similar_tags": similar_tags})
    


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="0.0.0.0", port=4000, workers=4)
