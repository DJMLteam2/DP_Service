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

df = pd.read_csv('./travel_spot.csv', index_col=0)
okt = Okt()
app = FastAPI()

# model load
try:
    # 미리 계산된 tfidf_matrix를 로드.
    with open('travel_model.pkl', 'rb') as file:
        loaded_data = pickle.load(file)
        
    # 불러온 tfidf_vectorizer와 tfidf_matrix를 사용
    tfidf_vectorizer = loaded_data["tfidf_vectorizer"]
    tfidf_matrix = loaded_data["tfidf_matrix"]


except FileNotFoundError:
    # 파일이 없으면 tfidf_vectorizer를 새로 피팅하고 저장
    

    tfidf_vectorizer = TfidfVectorizer()
    
    tag_list = [' '.join([j for j in okt.morphs(i) if len(j) > 1]) for i in df['tagName'].tolist()]
    tfidf_matrix = tfidf_vectorizer.fit_transform(tag_list)
    
    # 피팅된 tfidf_vectorizer를 저장
    with open('travel_model.pkl', 'wb') as file:
        pickle.dump({"tfidf_vectorizer": tfidf_vectorizer, "tfidf_matrix": tfidf_matrix}, file)


@app.post("/getAnswer")
def getAnswer(question: Question):
    
    question_tfidf = tfidf_vectorizer.transform(okt.morphs(question.question))
    cos_similarities = cosine_similarity(question_tfidf, tfidf_matrix)
    sorted_indices = np.argsort(cos_similarities[0])[::-1][:5]

    similar_tags = [{"title": str(df.loc[index, 'title']), "overView": str(df.loc[index, 'overView']), "similarity": float(cos_similarities[0][index])} for index in sorted_indices]

    return JSONResponse(content={"question": question.question, "similar_tags": similar_tags})




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
    uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)
