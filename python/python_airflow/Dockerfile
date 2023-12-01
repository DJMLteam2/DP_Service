# 기본 이미지 선택
FROM python:3.8

# 작업 디렉토리 설정
WORKDIR /opt/airflow

# 필요한 파일들을 복사
COPY . /opt/airflow

RUN apt-get update && apt-get install -y default-jre default-jdk
# Set the JAVA_HOME environment variable
ENV JAVA_HOME /usr/lib/jvm/default-java
ENV PATH $PATH:$JAVA_HOME/bin

COPY requirements.txt /opt/airflow
RUN pip install --no-cache-dir -r requirements.txt

# 초기화 및 사용자 생성
RUN airflow db init ; \
    airflow users create \
        --username admin \
        --firstname dp \
        --lastname airflow \
        --role Admin \
        --email admin@example.com \
        --password admin

# 에어플로우 설정 파일 수정: dags_folder 변경
RUN sed -i 's|dags_folder = /root/airflow/dags|dags_folder = /opt/airflow/dags|g' airflow.cfg

CMD ["bash", "-c", "airflow webserver --port 5000 -D & airflow scheduler"]
