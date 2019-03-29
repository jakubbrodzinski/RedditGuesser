FROM debian:9.8

RUN apt-get update -y && \
    apt-get install -y python3-pip python3

COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip3 install -r requirements.txt

COPY ./ /app

ENTRYPOINT [ "python3" ]

EXPOSE 8123/tcp

CMD [ "run.py" ]
