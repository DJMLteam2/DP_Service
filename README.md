# DP_Service
## System Architecture
![System Architecture](https://github.com/DJMLteam2/DP_Service/assets/135206238/cebda86c-e8df-4bee-bf3a-18be9d2b7024)
### dockerfile
https://github.com/Sam1000won/dp_docker 
## Docker Hub
### Python
python 주소 : https://hub.docker.com/r/jiugnim/dp_python
docker pull jiugnim/dp_mysql
### Mysql
mysql 주소 : https://hub.docker.com/r/jiugnim/dp_mysql
docker pull jiugnim/dp_python
## Docker Compose
### Compose.yml
git clone 사용시 
```
# 모든 컨테이너 실행
docker compose up -d
# 모든 컨테이너 다운
docker compose down
# 도커 컨테이너 정지
docker stop <컨테이너 이름>
# 도커 컨테이너 삭제
docker rm <컨테이너 이름>
# 도커 이미지 삭제
docekr irm <이미지 이름>
```
```
version: '2'
services:
  mysql:
    image: mysql
    command: --default-authentication-plugin=mysql_native_password
    restart: always
    environment:
      - MYSQL_ROOT_PASSWORD=1234
      - MYSQL_HOST=121.140.69.95
      - MYSQL_PORT=3308
    ports:
      - 3308:3306
  python-app:
    build: ./python/
    volumes:
      - $PWD/data:/home/code/python/data
    ports:
      - "5000:5000"
    command: sh -c 'sh etl.sh && python -m flask run --host=0.0.0.0'
```
## Use docker 
```
docker pull jiugnim/dp_python
docker pull jiugnim/dp_mysql

docker run <이미지>

# mysql,python 사용
docker exec -it <컨테이너 이름-mysql> bash

# Myslq Use
mysql -u root -p -h 121.140.69.95 -P 3308
# 비밀번호 문의하세요
# python은 바로 사용하시면 됩니다. 
```
mysql 포트는 3308 입니다.
flask 포트는 5000 입니다.
### Python mysql
```
#데이터 저장
user = 'root'
password = '1234'
host='121.140.69.95' # localhost
port = '3308'
database = 'coin_db' # DB 만들면 그거 이름
engine = sqlalchemy.create_engine(f"mysql://{user}:{password}@{host}:{port}/{database}")
# MYSQL오류 발생 => # sqlalchemy 의존성 패키지 설치

csv_file_path = f"tc_codea_코드A.csv" # 실제 데이터 장소 기입
df = pd.read_csv(csv_file_path)
df.to_sql(name='tc_codea_코드A', con=engine, if_exists='append', index=False)

print("CSV 파일을 데이터베이스에 저장 완료!")

---------------------------------------------------------------------
# 데이터 불러오기
from sqlalchemy import create_engine
import pandas as pd

engine = create_engine('mysql+pymysql://root:1234@121.140.69.95:3308/sam_db')
df = pd.read_sql_query("SELECT * FROM tr_df;", engine)
print(df['VISIT_AREA_NM'].value_counts())
# 혹은
connection = pymysql.connect(
    host='121.140.69.95',
    user='root',
    password='1234',
    database='<table_name>', # 테이블 이름을 넣어주시면 됩니다.
    port=3308,
    cursorclass=pymysql.cursors.DictCursor
)

try:
    with connection.cursor() as cursor:
        # sql 문으로 작성
        sql = "show databases;"
        cursor.execute(sql)
        
        # 결과 가져오기
        result = cursor.fetchall()
        for row in result:
            print(row)  # 가져온 데이터 출력 (여기서는 딕셔너리 형태)
finally:
    connection.close()

```
넣어서 사용하시면 바로 데이터 베이스 연결될겁니다. 

### .env
```
MYSQL_HOST=121.140.69.95
MYSQL_PORT=3308
MYSQL_ROOT_PASSWORD=1234

MYSQL_USER=<User Nickname>
MYSQL_PASSWORD=<User Password>
```
안되는 부분있으면 이야기 해주세요.
