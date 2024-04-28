FROM python:latest

COPY app.py /app.py

COPY times.ttf /times.ttf

RUN apt-get update
RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN pip3 install psycopg2-binary 
RUN pip3 install Pillow 
RUN pip3 install captcha

ENTRYPOINT ["python3", "/app.py"]