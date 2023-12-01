from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from datetime import datetime, timedelta
import pandas as pd
import requests
import json
import pymysql
import numpy as np
import pickle
import os
import crawl
import python.modeling as modeling
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
from konlpy.tag import Okt



now = datetime.now()
month = str(now.month)
day = str(now.day)
date = month+'_'+day


current_dir = os.path.dirname(os.path.abspath(__file__))
info_path = os.path.join(current_dir, 'DB_INFO.pkl')
model_path = os.path.join(current_dir, f'/model/model_{date}.pkl')
data_path = os.path.join(current_dir, f'/data/data_{date}.pkl')

with open(info_path, "rb") as file:
    USER = pickle.load(file)
    PASSWD = pickle.load(file)
    HOST = pickle.load(file)
    PORT = pickle.load(file)
    NAME = pickle.load(file)


# Airflow DAG 정의
default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2023, 11, 29),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'dp_airflow',
    default_args=default_args,
    description='A simple DAG to crawl travel spots and insert into the database',
    schedule_interval='@daily',  # DAG를 실행할 주기 (매일 실행)
)

# DAG에 포함된 Task 정의
crawl_task = PythonOperator(
    task_id='crawl_and_insert_to_db',
    python_callable= crawl.crawl_and_insert_to_db,
    dag=dag,
)

# DAG에 포함된 Task 정의
modeling_task = PythonOperator(
    task_id='modeling',
    python_callable= modeling.modeling,
    dag=dag,
)
# Task 간의 의존성 설정
modeling_task >> crawl_task

if __name__ == "__main__":
    dag.cli()
