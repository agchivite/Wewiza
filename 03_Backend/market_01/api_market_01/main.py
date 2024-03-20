from fastapi import FastAPI
import requests

app = FastAPI()


@app.get("/saludo")
def enviar_saludo():
    return {"mensaje": "¡Hola! Bienvenido desde el Contenedor Market-01"}


@app.get("/test")
def enviar_saludo():
    response = requests.get("http://api_wewiza:8080/saludo")
    message = "Desde Market 1: "
    print(message)
    print(response.json())

    return {message: response.json()}
