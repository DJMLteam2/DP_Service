import os
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import pandas as pd
from konlpy.tag import Okt
from typing import List
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel

class Question(BaseModel):
    question: str
    area: str


okt = Okt()
app = FastAPI()
df = pd.read_csv('./travel_spot_v1.csv', index_col=0)
spot_df = df[df['contentType'] != 39]
food_df = df[df['contentType'] == 39]

# model load

try:
    with open('./travel_model_v2.pkl', 'rb') as file:
        loaded_data = pickle.load(file)
        
        vectorizer_spot = loaded_data["spot"]["vectorizer"]
        matrix_spot = loaded_data["spot"]["matrix"]
        city_matrices_spot = loaded_data["spot"]["city_matrices"]

        vectorizer_food = loaded_data["food"]["vectorizer"]
        matrix_food = loaded_data["food"]["matrix"]
        city_matrices_food = loaded_data["food"]["city_matrices"]

except Exception as e:
    print(e)


def add_to_similar_tags(df, sorted_indices, cos_similarities, similar_tags):
    bag = []
    for index in sorted_indices:
        bag.append({
            "id": str(df.iloc[index]['id']),
            "area": str(df.iloc[index]['city']),
            "title": str(df.iloc[index]['title']),
            "similarity": float(cos_similarities[0][index]),
            "catchtitle": str(df.iloc[index]['catchtitle']),
            # "detail": str(df.iloc[index]['detail']),
            "treatMenu": str(df.iloc[index]['treatMenu']),
            "tagName": str(df.iloc[index]['tagName']),
            "addr": str(df.iloc[index]['addr']),
            "info": str(df.iloc[index]['info']),
            # "lat": str(df.iloc[index]['parking']),
            "useTime": str(df.iloc[index]['useTime']),
            "conLike": str(df.iloc[index]['conLike']),
            "conRead": str(df.iloc[index]['conRead']),
            "conShare": str(df.iloc[index]['conShare']),
            # "overView": str(df.iloc[index]['overView']),
            "lat": str(df.iloc[index]['lat']),
            "lon": str(df.iloc[index]['lon'])
            
            })
    similar_tags.append(bag)

@app.post("/getAnswer")
def getAnswer(question: Question):
    global df, spot_df, food_df, city_matrices_spot, city_matrices_food
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    if question.area == '전체':
        city_matrices_spot = matrix_spot
        city_matrices_food = matrix_food
    else:
        city_matrices_spot = city_matrices_spot[(question.area)]
        city_matrices_food = city_matrices_food[(question.area)]

        df = df[df['city']== f'{question.area}']
        food_df = food_df[food_df['city']== f'{question.area}']
        spot_df = spot_df[spot_df['city']== f'{question.area}']

    print('여기여기')

    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    question_spot = vectorizer_spot.transform(okt.morphs(f'{question.question}'))
    question_food = vectorizer_food.transform(okt.morphs(f'{question.question}'))

    #spot 인덱스 추출
    cos_similarities_spot = cosine_similarity(question_spot, city_matrices_spot)
    sorted_indices_spot = np.argsort(cos_similarities_spot[0])[::-1][:5]

    #food 인덱스 추출
    cos_similarities_food = cosine_similarity(question_food, city_matrices_food)
    sorted_indices_food = np.argsort(cos_similarities_food[0])[::-1][:5]
    
    # 결과를 저장할 리스트 초기화
    similar_tags = []
    # 리스트로 추가

    print('여기여기4')

    # 함수를 사용
    add_to_similar_tags(spot_df, sorted_indices_spot, cos_similarities_spot, similar_tags)
    add_to_similar_tags(food_df, sorted_indices_food, cos_similarities_food, similar_tags)
    

    return JSONResponse(content={"question": question.question, "recommend": similar_tags})





@app.get("/test")
def test():

    question = "bts 효도여행 가고싶어"

    question_tfidf = vectorizer_spot.transform(okt.morphs(question))
    cos_similarities = cosine_similarity(question_tfidf, matrix_spot)
    sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]

    print('질문 = ', question)
    for i, index in enumerate(sorted_indices):
        similarity = cos_similarities[0][index]
        print(f"{i + 1}위, index:{index}, 유사도: {similarity}")
    
    similar_tags = [{"title": str(df.loc[index, 'title']), "overView": str(df.loc[index, 'overView']), "similarity": float(cos_similarities[0][index])} for index in sorted_indices]

    return JSONResponse(content={"question": question, "similar_tags": similar_tags})
    


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="127.0.0.1", port=3000, workers=4)
