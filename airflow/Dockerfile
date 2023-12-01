# 기본 이미지 선택
FROM apache/airflow

# 작업 디렉토리 설정
WORKDIR /opt/airflow

# 필요한 파일들을 복사
COPY . /opt/airflow/dags

RUN pip install --no-cache-dir pandas sqlalchemy pymysql requests numpy pyarrow==10.0.1


# 초기화 및 사용자 생성
RUN airflow db init ; \
    airflow users create \
        --username admin \
        --firstname John \
        --lastname Doe \
        --role Admin \
        --email admin@example.com \
        --password admin

CMD ["bash", "-c", "airflow webserver --port 5000 -D & airflow scheduler"]
