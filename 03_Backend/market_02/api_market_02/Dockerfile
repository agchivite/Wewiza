FROM python:3.11-bullseye

WORKDIR /api-market-02

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8082

# Ruta actual copiar a la ruta de destino
COPY . .

CMD [ "python", "./main.py" ]
# Esto es apra que la API se quede siempre activa, en caso de no utilizar ninguna dependencia de SERVER
#CMD [ "sh", "-c", "python ./main.py & tail -f /dev/null" ] 

