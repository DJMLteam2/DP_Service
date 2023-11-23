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

df = pd.read_csv('./travel_spot_v1.csv', index_col=0)
okt = Okt()
app = FastAPI()

# model load

try:
    with open('./travel_model_v1.pkl', 'rb') as file:
        loaded_data = pickle.load(file)
        
    tfidf_vectorizer = loaded_data["tfidf_vectorizer"]
    tfidf_matrix = loaded_data["tfidf_matrix"]

    # 미리 계산된 모든 지역의 TF-IDF 행렬
    city_tfidf_matrices = loaded_data["city_tfidf_matrices"]

except Exception as e:
    print(e)
    tfidf_vectorizer = TfidfVectorizer()
    df['tf'] = df['tagName'] + df['treatMenu']

    tag_list = [' '.join([j for j in okt.morphs(str(i)) if len(j) > 1]) for i in df['tf'].tolist()]
    tfidf_matrix = tfidf_vectorizer.fit_transform(tag_list)

    # 각 지역에 대한 TF-IDF 행렬 계산
    city_tfidf_matrices = {}
    for city in df['city'].unique():
        city_df = df[df['city'] == city]
        city_tag_list = [' '.join([j for j in okt.morphs(str(i)) if len(j) > 1]) for i in city_df['tf'].tolist()]
        city_tfidf_matrices[city] = tfidf_vectorizer.transform(city_tag_list)
        

    # 저장
    with open('travel_model_v1.pkl', 'wb') as file:
        pickle.dump({
            "tfidf_vectorizer": tfidf_vectorizer,
            "tfidf_matrix": tfidf_matrix,
            "city_tfidf_matrices": city_tfidf_matrices
        }, file)

@app.post("/getAnswer")
def getAnswer(question: Question):
    global df

    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    if question.area == '전체':
        city_tfidf_matrix = tfidf_matrix
    else:
        city_tfidf_matrix = city_tfidf_matrices.get(question.area)
        df = df[df['city']== f'{question.area}']

    print('여기여기')

    if city_tfidf_matrix is None:
        return JSONResponse(content={"error": f"No data found for city: {question.area}"}, status_code=404)

    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    question_tfidf = tfidf_vectorizer.transform(okt.morphs(question.question))
    cos_similarities = cosine_similarity(question_tfidf, city_tfidf_matrix)
    sorted_indices = np.argsort(cos_similarities[0])[::-1]
    print('여기여기2')
    from itertools import islice
    # 식당인 것, 아닌 것
    restaurant = islice((index for index in sorted_indices if df.iloc[index]['contentType'] == 39 ), 5)
    non_restaurant = islice((index for index in sorted_indices if df.iloc[index]['contentType'] != 39 ), 5)

    # 결과를 저장할 리스트 초기화
    similar_tags = []
    print('여기여기 3')
    # 리스트로 추가
    def add_to_similar_tags(sorted_indices, similar_tags):
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
    print('여기여기4')

    # 함수를 사용
    add_to_similar_tags(non_restaurant, similar_tags)
    add_to_similar_tags(restaurant, similar_tags)
    

    return JSONResponse(content={"question": question.question, "recommend": similar_tags})





@app.get("/test")
def test():

    question = "bts 효도여행 가고싶어"

    question_tfidf = tfidf_vectorizer.transform(okt.morphs(question))
    cos_similarities = cosine_similarity(question_tfidf, tfidf_matrix)
    sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]

    print('질문 = ', question)
    for i, index in enumerate(sorted_indices):
        similarity = cos_similarities[0][index]
        print(f"{i + 1}위, index:{index}, 유사도: {similarity}")
    
    similar_tags = [{"title": str(df.loc[index, 'title']), "overView": str(df.loc[index, 'overView']), "similarity": float(cos_similarities[0][index])} for index in sorted_indices]

    return JSONResponse(content={"question": question, "similar_tags": similar_tags})
    


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="127.0.0.1", port=3000, reload=True)
