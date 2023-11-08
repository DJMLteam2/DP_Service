FROM python:3.9

WORKDIR /home/code/python

COPY . .

# 실제 사용하실때는 풀어야 합니다.
RUN apt-get update \
    && apt-get install -y software-properties-common \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --upgrade pip
RUN python -m pip install --upgrade pip
RUN pip3 install --no-cache-dir -r requirements.txt

EXPOSE 5000

COPY etl.sh etl.sh

CMD ["sh", "etl.sh"]