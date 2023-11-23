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
import haversine
import dijkstra

class Question(BaseModel):
    question: str
    area: str

df = pd.read_csv('./travel_spot.csv', index_col=0)
okt = Okt()
app = FastAPI()

# model load

try:
    with open('./travel_model.pkl', 'rb') as file:
        loaded_data = pickle.load(file)
        
    tfidf_vectorizer = loaded_data["tfidf_vectorizer"]
    tfidf_matrix = loaded_data["tfidf_matrix"]

    # 미리 계산된 모든 지역의 TF-IDF 행렬
    city_tfidf_matrices = loaded_data["city_tfidf_matrices"]

except Exception as e:
    print(e)
    tfidf_vectorizer = TfidfVectorizer()

    tag_list = [' '.join([j for j in okt.morphs(i) if len(j) > 1]) for i in df['tagName'].tolist()]
    tfidf_matrix = tfidf_vectorizer.fit_transform(tag_list)

    # 각 지역에 대한 TF-IDF 행렬 계산
    city_tfidf_matrices = {}    
    for city in df['city'].unique():
        city_df = df[df['city'] == city]
        city_tag_list = [' '.join([j for j in okt.morphs(i) if len(j) > 1]) for i in city_df['tagName'].tolist()]
        city_tfidf_matrices[city] = tfidf_vectorizer.transform(city_tag_list)
        
    print(city_tfidf_matrices)

    # 저장
    with open('travel_model.pkl', 'wb') as file:
        pickle.dump({
            "tfidf_vectorizer": tfidf_vectorizer,
            "tfidf_matrix": tfidf_matrix,
            "city_tfidf_matrices": city_tfidf_matrices
        }, file)

@app.post("/getAnswer")
def getAnswer(question: Question):
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    city_tfidf_matrix = city_tfidf_matrices.get(question.area)

    if city_tfidf_matrix is None:
        return JSONResponse(content={"error": f"No data found for city: {question.area}"}, status_code=404)

    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    question_tfidf = tfidf_vectorizer.transform(okt.morphs(question.question))
    cos_similarities = cosine_similarity(question_tfidf, city_tfidf_matrix)
    sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]
    
    # 선택된 지역의 상위 5개 여행 스팟 반환
    similar_tags = [
        {
            "title": str(df[df['city']== f'{question.area}'].iloc[index, ]['title']),

            "area" : str(df[df['city']== f'{question.area}'].iloc[index, ]['city']),

            "overView": str(df[df['city']== f'{question.area}'].iloc[index, ]['overView']),

            "detail": str(df[df['city']== f'{question.area}'].iloc[index, ]['detail']),

            "tagName": str(df[df['city']== f'{question.area}'].iloc[index, ]['tagName']),

            "lat": str(df[df['city']== f'{question.area}'].iloc[index, ]['lat']),

            "lon": str(df[df['city']== f'{question.area}'].iloc[index, ]['lon']),

            "similarity": float(cos_similarities[0][index])
        } for index in sorted_indices
    ]

    travel_order, travel_distances = dijkstra.dijkstra(similar_tags, start=0)
    
    travel_spots_in_order = [similar_tags[order]['title'] for order in travel_order]
    travel_distance_in_order = [travel_distances[travel_order[i:i+2][0]][travel_order[i:i+2][1]]
                                for i in range(len(travel_order) - 1)]
    lats = [similar_tags[order]['lat'] for order in travel_order]
    lons = [similar_tags[order]['lon'] for order in travel_order]
    return JSONResponse(content={"question": question.question, "similar_tags": similar_tags,
                                 "travel_spots": travel_spots_in_order,
                                 "travel_distance": travel_distance_in_order,
                                 "spot_latitudes": lats,
                                 "spot_longitudes": lons})


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
    
    similar_tags = [{"title": str(df.loc[index, 'title']), "overView": str(df.loc[index, 'overView']), "similarity": float(cos_similarities[0][index]),
                     "lat": float(df.loc[index, 'lat']), "lon": float(df.loc[index, 'lon'])} for index in sorted_indices]
    
    # 여행지 방문 순서, 각 여행지로부터 다른 여행지까지의 거리
    travel_order, travel_distances = dijkstra.dijkstra(similar_tags, start=0)
    travel_spots_in_order = [similar_tags[order]['title'] for order in travel_order]
    travel_distance_in_order = [travel_distances[travel_order[i:i+2][0]][travel_order[i:i+2][1]]
                                for i in range(len(travel_order) - 1)]
    lats = [similar_tags[order]['lat'] for order in travel_order]
    lons = [similar_tags[order]['lon'] for order in travel_order]

    return JSONResponse(content={"question": question, "similar_tags": similar_tags,
                                 "travel_spots": travel_spots_in_order,
                                 "travel_distance": travel_distance_in_order,
                                 "spot_latitudes": lats,
                                 "spot_longitudes": lons})

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="127.0.0.1", port=3000, reload=True)
