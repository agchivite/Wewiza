import requests
import selenium
import os  
import pymongo

from pymongo import MongoClient
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from bs4 import BeautifulSoup
from database.DatabaseManager import DatabaseManager

from models.Product import Product
from repositories.ProductRepository import ProductRepository
from utils.SimulationMercadona import SimulationMercadona

# DataBase 
connection_mongo = "mongodb://localhost:27017"

# Servicio Chrome
service = Service()
options = webdriver.ChromeOptions()
driver_chrome = webdriver.Chrome(service=service, options=options)

simulation = SimulationMercadona(driver_chrome, connection_mongo)
simulation.run_simulation(40, 45)
