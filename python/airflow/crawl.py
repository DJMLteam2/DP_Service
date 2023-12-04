import os
import pickle
import numpy as np
import pandas as pd
import pymysql
from datetime import datetime
import requests
import json
import pytz

# 현재 시간
now_utc = datetime.now(pytz.utc)

# 서울 시간대로 변환
seoul_timezone = pytz.timezone('Asia/Seoul')
now = now_utc.astimezone(seoul_timezone)

print('crawling.py--------')

month = str(now.month)
day = str(now.day)
date = month+'_'+day


# test
try:
    airflow_home = os.environ.get('AIRFLOW_HOME')
    print('airflow_home = ',airflow_home)
except Exception as e:
    print(e)

# current_dir = '/usr/local/airflow/'


current_dir = os.path.dirname(os.path.abspath(__file__))
info_path = os.path.join(current_dir, 'DB_INFO.pkl')
model_path = os.path.join(current_dir, f'data/model_{date}.pkl')
data_path = os.path.join(current_dir, f'data/data_{date}.csv')



print('info_path = ', info_path)
print('model_path = ', model_path)
print('data_path = ', data_path)

if os.path.exists(os.path.dirname(data_path)):
    print('path exists')
else:
    print('path non exists')
    





with open(info_path, "rb") as file:
    USER = pickle.load(file)
    PASSWD = pickle.load(file)
    HOST = pickle.load(file)
    PORT = pickle.load(file)
    NAME = pickle.load(file)

print('------------------')


def crawl_and_insert_to_db():
    print('crawling.py--------')
    if os.path.exists(os.path.dirname(data_path)):
        print('path exists')
    else:
        print('path non exists')
    sido_code ={
        "서울":1,
        "인천":2,
        "대전":3,
        "대구":4,
        "광주":5,
        "부산":6,
        "울산":7,
        "세종시":8,
        "경기":31,
        "강원":32,
        "충북":33,
        "충남":34,
        "경북":35,
        "경남":36,
        "전북":37,
        "전남":38,
        "제주":39
    }
    sido_code = {v:k for k,v in sido_code.items()}

    sido = list(range(1,9))+ list(range(31,40))
    sort = ['MAIN_B','MAIN_A']
    result = pd.DataFrame()

    for idx, code in enumerate(sido):
        print('crawlling...(', idx,'/', len(sido), ')')
        for main in sort:
            offset = 1
            url = f'https://korean.visitkorea.or.kr/api/v1/curation/list?offset={offset}&page=1&device=PC&type={main}&regionCode={code}&sequence=0'
            res = requests.get(url)
            group = json.loads(res.text)

            offset = group['data']['total']

            print('now_working :', sido_code[code], offset)
            url = f'https://korean.visitkorea.or.kr/api/v1/curation/list?offset={offset}&page=1&device=PC&type={main}&regionCode={code}&sequence=0'
                    
            res = requests.get(url)
            group = json.loads(res.text)

            
            print('total_rows_number :', len(result))
            bag = []
            for data in group['data']['items']:
                
                try:
                    city = sido_code[code]
                    title = data['content']['title']
                    overView = data['content']['databaseMaster']['overView']
                    cotId = data['content']['cotId']
                    addr1 = data['content']['databaseMaster']['addr1']
                    mapx = data['content']['databaseMaster']['mapx']
                    mapy = data['content']['databaseMaster']['mapy']

                    url2 = 'https://korean.visitkorea.or.kr/call'
                    data = {
                            'cmd': 'TOUR_CONTENT_BODY_DETAIL',
                            'cotId': cotId,
                            'locationx': 0.0,
                            'locationy': 0.0,
                            'stampId': '1589345b-b030-11ea-b8bd-020027310001'
                    }
                    res = requests.post(url2, data=data)
                    group = json.loads(res.text)

                    infoCenter = group['body']['detail']['infoCenter']
                    parking = group['body']['detail']['parking']
                    catchtitle = group['body']['detail'].get('catchtitle', np.nan)

                    if main == 'MAIN_B':
                        useTime = group['body']['detail'].get('openTime', np.nan)
                    else:
                        useTime = group['body']['detail'].get('useTime', np.nan)

                    treatMenu = group['body']['detail'].get('treatMenu', np.nan)
                    homepage = group['body']['detail']['homepage']
                    contentType = group['body']['detail']['contentType']
                    conLike = group['body']['detail']['conLike']
                    conRead = group['body']['detail']['conRead']
                    conShare = group['body']['detail']['conShare']
                    cid = group['body']['detail']['cid']
                    imgPath = 'https://cdn.visitkorea.or.kr/img/call?cmd=VIEW&id=' + group['body']['detail']['imgPath']

                    tagName = group['body']['detail']['tagName']
                    tagName = ','.join([i for i in tagName.split('|') if not str.isdigit(i[0])])

                    append_dict ={
                        'id' : cid,
                        'city' : city,
                        'cityCode': code,
                        'contentType':contentType,
                        'title': title,
                        'catchtitle': catchtitle,
                        'overView': overView,
                        'treatMenu': treatMenu,
                        'conLike': conLike,
                        'conRead': conRead,
                        'conShare': conShare,
                        'imgPath': imgPath,
                        'addr': addr1,
                        'info': infoCenter,
                        'parking': parking,
                        'useTime': useTime,
                        'tagName': tagName
                        }

                    if (group['body']['subArticle']):
                        for i in group['body']['subArticle']:
                            append_list = []
                            content = i['contentBody']
                            text = i['displayTitle'] + ':' + content
                            append_list.append(text)
                        append_dict['detail'] = ','.join(append_list)
                    append_dict['lat'] = mapy
                    append_dict['lon'] = mapx

                    bag.append(append_dict)
                except Exception as e:
                    print('this is error--------\n', e)
                    continue

            result = pd.concat([result, pd.DataFrame(bag)], ignore_index=True)
            result2 = pd.concat([result, pd.DataFrame(bag)], ignore_index=True)
    print('crawling done!')


    df = result
    df2 = result2
    df2['city'] = '전체'
    df2['id'] = df2['id'].apply(lambda x: int(x)*10) # 전체의 id는 기본값 *10으로 구분

    joined_df = pd.concat([df,df2], axis=0, ignore_index=True)

    joined_df.to_csv(data_path)
    print('saving crawling file')

    port = PORT
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

    print('sending to mysql')
    joined_df = joined_df.fillna('')
    try:
        
        for inx, row in joined_df.iterrows():
            # 모든 레코드를 업데이트하고, 없는 경우에는 새로 삽입
            query = f"""
                INSERT INTO TRAVEL_SPOT (
                    SPOT_ID, SPOT_CITY, SPOT_CITY_CODE, SPOT_CITY_CONTENT_TYPE,
                    SPOT_TITLE, SPOT_CATCHTITLE, SPOT_OVERVIEW, SPOT_TREATMENU,
                    SPOT_CONLIKE, SPOT_CONREAD, SPOT_CONSHARE, SPOT_IMGPATH,
                    SPOT_ADDR, SPOT_INFOCENTER, SPOT_PARKING, SPOT_USETIME,
                    SPOT_TAGNAME, SPOT_DETAIL, SPOT_LON, SPOT_LAT
                )
                VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
                )
                ON DUPLICATE KEY UPDATE
                    SPOT_CITY = VALUES(SPOT_CITY),
                    SPOT_CITY_CODE = VALUES(SPOT_CITY_CODE),
                    SPOT_CITY_CONTENT_TYPE = VALUES(SPOT_CITY_CONTENT_TYPE),
                    SPOT_TITLE = VALUES(SPOT_TITLE),
                    SPOT_CATCHTITLE = VALUES(SPOT_CATCHTITLE),
                    SPOT_OVERVIEW = VALUES(SPOT_OVERVIEW),
                    SPOT_TREATMENU = VALUES(SPOT_TREATMENU),
                    SPOT_CONLIKE = VALUES(SPOT_CONLIKE),
                    SPOT_CONREAD = VALUES(SPOT_CONREAD),
                    SPOT_CONSHARE = VALUES(SPOT_CONSHARE),
                    SPOT_IMGPATH = VALUES(SPOT_IMGPATH),
                    SPOT_ADDR = VALUES(SPOT_ADDR),
                    SPOT_INFOCENTER = VALUES(SPOT_INFOCENTER),
                    SPOT_PARKING = VALUES(SPOT_PARKING),
                    SPOT_USETIME = VALUES(SPOT_USETIME),
                    SPOT_TAGNAME = VALUES(SPOT_TAGNAME),
                    SPOT_DETAIL = VALUES(SPOT_DETAIL),
                    SPOT_LON = VALUES(SPOT_LON),
                    SPOT_LAT = VALUES(SPOT_LAT)
            """
            cursor.execute(query, (
                row['id'],
                row['city'],
                row['cityCode'],
                row['contentType'],
                row['title'],
                row['catchtitle'],
                row['overView'],
                row['treatMenu'],
                row['conLike'],
                row['conRead'],
                row['conShare'],
                row['imgPath'],
                row['addr'],
                row['info'],
                row['parking'],
                row['useTime'],
                row['tagName'],
                row['detail'],
                row['lon'],
                row['lat']
            ))
            conn.commit()

    except Exception as e:
        print(f"Error: {e}")
    finally:
        conn.commit()
        conn.close()
        print('crawling done')
        
        conn = pymysql.connect(host=host,user=user,password=password,database=database,charset=charset)
        cursor = conn.cursor()
        sql = "select * from TRAVEL_SPOT"
        cursor.execute(sql)
        new_df = pd.DataFrame(cursor.fetchall(), columns= [i[0] for i in cursor.description])
        print('length of,, old df = ', len(df), 'new df = ', len(new_df))
        # 업데이트 여부 확인
        updated_rows = new_df[~new_df.isin(df)].dropna()
        if not updated_rows.empty:
            print('Updated Rows:')
            print(updated_rows)
            print('------------------')
        else:
            print('No Updates Detected')
            print('------------------')