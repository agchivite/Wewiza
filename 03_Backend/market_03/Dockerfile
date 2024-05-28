FROM python:3.11-bullseye

WORKDIR /market_03

RUN pip install -U pip setuptools wheel

RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    ninja-build

RUN pip install -U spacy --no-binary blis
RUN python -m spacy download en_core_web_sm
RUN python -m spacy download es_core_news_sm
    
COPY ./api_market_03/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8083

# Ruta actual copiar a la ruta de destino
COPY . .

CMD [ "python", "./api_market_03/main.py" ]
# Esto es apra que la API se quede siempre activa, en caso de no utilizar ninguna dependencia de SERVER
#CMD [ "sh", "-c", "python ./main.py & tail -f /dev/null" ] 

