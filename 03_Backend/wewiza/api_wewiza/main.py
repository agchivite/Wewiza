from fastapi import FastAPI
import requests

app = FastAPI()

# TODO: Especific Service and Database to Model Products with UUID and num_likes


@app.get("/saludo")
def enviar_saludo():
    return {"mensaje": "¡Hola! Bienvenido desde el Contenedor Wewiza"}


@app.get("/test")
def enviar_saludo():
    response = requests.get("http://api_market_02:8082/saludo")
    message = "Desde Wewiza: "
    print(message)
    print(response.json())

    return {message: response.json()}
