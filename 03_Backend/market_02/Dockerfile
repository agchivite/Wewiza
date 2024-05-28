FROM python:3.11-bullseye

WORKDIR /market_02

# Actualizar pip y herramientas esenciales
RUN pip install -U pip setuptools wheel

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    build-essential \
    gfortran \
    libatlas-base-dev \
    virtualenv \
    git \
    && rm -rf /var/lib/apt/lists/*

# Crear y activar el entorno virtual
RUN virtualenv /opt/spacy-build --python=python3
RUN /opt/spacy-build/bin/pip install -U pip setuptools wheel

# Configurar variables de entorno
ENV VIRTUAL_ENV /opt/spacy-build
ENV PATH $VIRTUAL_ENV/bin:$PATH

# Clonar y construir blis
RUN git clone https://github.com/explosion/cython-blis && cd cython-blis \
    && git pull && git submodule init && git submodule update && git submodule status \
    && /opt/spacy-build/bin/pip install -r requirements.txt \
    && export BLIS_ARCH="generic" \
    && ./bin/generate-make-jsonl linux $BLIS_ARCH \
    && python setup.py bdist_wheel \
    && cp dist/*.whl /tmp/

# Instalar blis desde el wheel generado
RUN /opt/spacy-build/bin/pip install /tmp/blis-*.whl

# Descargar y construir spaCy desde el código fuente
RUN /opt/spacy-build/bin/pip wheel spacy --no-binary=spacy --no-build-isolation --wheel-dir=/tmp/

# Instalar spaCy desde el wheel generado
RUN /opt/spacy-build/bin/pip install /tmp/spacy-*.whl

RUN python -m spacy download en_core_web_sm
RUN python -m spacy download es_core_news_sm
    
COPY ./api_market_02/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8082

# Ruta actual copiar a la ruta de destino
COPY . .

CMD [ "python", "./api_market_02/main.py" ]
# Esto es apra que la API se quede siempre activa, en caso de no utilizar ninguna dependencia de SERVER
#CMD [ "sh", "-c", "python ./main.py & tail -f /dev/null" ] 

