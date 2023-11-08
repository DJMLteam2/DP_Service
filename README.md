# DP_Service
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
mysql -u root -p -h 121.140.69.95 -P 3308 = mysql
# python은 바로 사용하시면 됩니다. 
```
mysql 포트는 3308 입니다.
flask 포트는 5000 입니다.

### .env
```
MYSQL_HOST=121.140.69.95
MYSQL_PORT=3308
MYSQL_ROOT_PASSWORD=1234

MYSQL_USER=<User Nickname>
MYSQL_PASSWORD=<User Password>
```
안되는 부분있으면 이야기 해주세요.
