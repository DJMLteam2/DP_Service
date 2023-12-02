# 기본 이미지 선택
FROM python:3.7-slim-buster

ARG AIRFLOW_USER_HOME=/usr/local/airflow
ENV AIRFLOW_HOME=${AIRFLOW_USER_HOME}
ENV JAVA_HOME /usr/lib/jvm/default-java
ENV PATH $PATH:$JAVA_HOME/bin


# 작업 디렉토리 설정
WORKDIR ${AIRFLOW_USER_HOME}

RUN apt-get update && apt-get install -y default-jre default-jdk
# Set the JAVA_HOME environment variable

COPY requirements.txt ${AIRFLOW_USER_HOME}/dags/requirements.txt
RUN pip install --no-cache-dir -r ${AIRFLOW_USER_HOME}/dags/requirements.txt

RUN useradd -ms /bin/bash -d ${AIRFLOW_USER_HOME} airflow
RUN chmod -R 777 ${AIRFLOW_USER_HOME}

USER airflow
# 초기화 및 사용자 생성
RUN airflow db init  && \
    airflow users create \
        --username admin \
        --firstname dp \
        --lastname airflow \
        --role Admin \
        --email admin@example.com \
        --password admin
    
RUN rm -rf ${AIRFLOW_USER_HOME}/dags/*
# 필요한 파일들을 복사
COPY . ${AIRFLOW_USER_HOME}/dags
    

# 에어플로우 설정 파일 수정: dags_folder 변경

CMD ["bash", "-c", "airflow webserver --port 5000 -D & airflow scheduler"]
