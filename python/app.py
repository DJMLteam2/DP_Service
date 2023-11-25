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
import time
import haversine
import dijkstra

current_dir = os.path.dirname(os.path.abspath(__file__))
df_path = os.path.join(current_dir, 'travel_spot_v1.csv')
model_path = os.path.join(current_dir, 'travel_model_v2.pkl')

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
# try:
#     with open('./travel_model.pkl', 'rb') as file:
#         loaded_data = pickle.load(file)
        
#     tfidf_vectorizer = loaded_data["tfidf_vectorizer"]
#     tfidf_matrix = loaded_data["tfidf_matrix"]

#     # 미리 계산된 모든 지역의 TF-IDF 행렬
#     city_tfidf_matrices = loaded_data["city_tfidf_matrices"]

# except Exception as e:
#     print(e)
#     tfidf_vectorizer = TfidfVectorizer()

#     tag_list = [' '.join([j for j in okt.morphs(i) if len(j) > 1]) for i in df['tagName'].tolist()]
#     tfidf_matrix = tfidf_vectorizer.fit_transform(tag_list)

#     # 각 지역에 대한 TF-IDF 행렬 계산
#     city_tfidf_matrices = {}    
#     for city in df['city'].unique():
#         city_df = df[df['city'] == city]
#         city_tag_list = [' '.join([j for j in okt.morphs(i) if len(j) > 1]) for i in city_df['tagName'].tolist()]
#         city_tfidf_matrices[city] = tfidf_vectorizer.transform(city_tag_list)
        
#     print(city_tfidf_matrices)

#     # 저장
#     with open('travel_model.pkl', 'wb') as file:
#         pickle.dump({
#             "tfidf_vectorizer": tfidf_vectorizer,
#             "tfidf_matrix": tfidf_matrix,
#             "city_tfidf_matrices": city_tfidf_matrices
#         }, file)
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

@app.post("/getAnswer", response_model=OutputData)
def getAnswer(question: Question):
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    # city_tfidf_matrix = city_tfidf_matrices.get(question.area)

    # if city_tfidf_matrix is None:
    #     return JSONResponse(content={"error": f"No data found for city: {question.area}"}, status_code=404)
    city_spot = city_matrices_spot[question.area]
    city_food = city_matrices_food[question.area]

    food_city_df = food_df[food_df['city'] == question.area]
    spot_city_df = spot_df[spot_df['city'] == question.area]

    morphed_question = okt.morphs(question.question)

    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    # question_tfidf = tfidf_vectorizer.transform(okt.morphs(question.question))
    # cos_similarities = cosine_similarity(question_tfidf, city_tfidf_matrix)
    # sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]
    question_spot = vectorizer_spot.transform(morphed_question)
    question_food = vectorizer_food.transform(morphed_question)
    
    # spot 인덱스 추출
    cos_similarities_spot = cosine_similarity(question_spot, city_spot)
    sorted_indices_spot = np.argsort(cos_similarities_spot[0])[::-1][:5]

    # food 인덱스 추출
    cos_similarities_food = cosine_similarity(question_food, city_food)
    sorted_indices_food = np.argsort(cos_similarities_food[0])[::-1][:5]
    
    # 결과를 저장할 리스트 초기화
    similar_tags = []
    
    # # 선택된 지역의 상위 5개 여행 스팟 반환
    # similar_tags = [
    #     {
    #         "title": str(df[df['city']== f'{question.area}'].iloc[index, ]['title']),
    #         "area" : str(df[df['city']== f'{question.area}'].iloc[index, ]['city']),
    #         "overView": str(df[df['city']== f'{question.area}'].iloc[index, ]['overView']),
    #         "detail": str(df[df['city']== f'{question.area}'].iloc[index, ]['detail']),
    #         "tagName": str(df[df['city']== f'{question.area}'].iloc[index, ]['tagName']),
    #         "lat": float(df[df['city']== f'{question.area}'].iloc[index, ]['lat']),
    #         "lon": float(df[df['city']== f'{question.area}'].iloc[index, ]['lon']),
    #         "similarity": float(cos_similarities[0][index])
    #     } for index in sorted_indices
    # ]
    # 리스트로 추가
    try:
        add_to_similar_tags(spot_city_df, sorted_indices_spot, cos_similarities_spot, similar_tags)
        add_to_similar_tags(food_city_df, sorted_indices_food, cos_similarities_food, similar_tags)
        print('done')
    
    except Exception as e:
        print(e)

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

@app.post("/getAnswer_debug", response_model= OutputData)
def getAnswer(question: Question):
    # global df, spot_df, food_df
    
    # 미리 계산된 해당 지역의 TF-IDF 행렬 사용
    city_spot = city_matrices_spot[question.area]
    city_food = city_matrices_food[question.area]

    # df = df[df['city']== question.area]
    food_city_df = food_df[food_df['city'] == question.area]
    spot_city_df = spot_df[spot_df['city'] == question.area]
    print('spot_table\'s len =',len(food_city_df))
    print('food_table\'s len =',len(spot_city_df))

    morphed_question = okt.morphs(question.question)
    # 질문과 선택된 지역의 TF-IDF로 유사도 계산
    question_spot = vectorizer_spot.transform(morphed_question)
    question_food = vectorizer_food.transform(morphed_question)

    #spot 인덱스 추출
    cos_similarities_spot = cosine_similarity(question_spot, city_spot)
    sorted_indices_spot = np.argsort(cos_similarities_spot[0])[::-1][:5]

    #food 인덱스 추출
    cos_similarities_food = cosine_similarity(question_food, city_food)
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
    cos_similarities = cosine_similarity(question_tfidf, matrix_spot)
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
