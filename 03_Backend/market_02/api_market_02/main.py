from fastapi import FastAPI
import requests

app = FastAPI()


@app.get("/saludo")
def enviar_saludo():
    return {"mensaje": "¡Hola! Bienvenido desde el Contenedor Market-02"}


@app.get("/test")
def enviar_saludo():
    response = requests.get("http://api_market_01:8081/saludo")
    message = "Desde Market 2: "
    print(message)
    print(response.json())

    return {message: response.json()}
