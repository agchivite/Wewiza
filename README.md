# Wewiza

Wewiza es una aplicación diseñada para comparar precios de diversos mercados. El objetivo principal es crear una API que integre datos de tiendas importantes como Mercadona, Carrefour, Dia, Ahorramas, etc., permitiendo a los usuarios comparar precios dentro de la aplicación.

## Miembros

- JiaCheng Zhang
- Angel Maroto Chivite

### Branding

**Wewiza**: Inspirado en el meme "Somos magos", enfatizando la experiencia en encontrar los mejores precios.

### Planificación del Proyecto

- **Trello**: Para la gestión de tareas y seguimiento del progreso.
- **Github (Gitflow)**: Utilizando el modelo de ramificación Gitflow con ramas main, release, dev, feature y hotfix.
- **Gitkraken**: Representación visual mejorada de los procesos de Git.
- **Metodologías**: Implementando metodologías ágiles para una mejora continua.

### APIs

Crearemos dos API´s de dos mercadones o si nos vemos apresurados, utilizaremos APIs existentes como [Fake Store API](https://fakestoreapi.com/) y [FakeStore1](https://github.com/keikaavousi/fake-store-api).

### Base de Datos

- **Relacional**: MariaDB o MySQL para datos estructurados.
- **No Relacional**: MongoDB para manejo flexible de datos, especialmente para integración con API.
- **Esquema de Producto**: id, título, precio, descripción, imagen, categoría.
- **Esquema de Usuario**: id, correo electrónico, nombre de usuario, contraseña (considerando futuras características como ubicación/dirección).
- **Esquema de Lista de Deseos**: id, userId, fecha, lista de productos.

### Autenticación

Dos opciones:

- **Firebase con Google**: Para una autenticación sin problemas.
- **Modelo de Usuario Personalizado**: Utilizando el esquema de Usuario para la autenticación.

### GUI

Aplicación Android con diseño de UI/UX creado usando Figma para maquetas.

### Despliegue con Contenedores

Utilizando Docker y Docker-Compose para una gestión de dependencias sencilla y compatibilidad entre máquinas.

### Inspiraciones

1. PriceSpy
2. idealo Shopping
3. Chollometro

### Roles

Sistema de usuario único, no se requieren roles de usuario adicionales.

### Requisitos Funcionales, No Funcionales, Información

Utilizando un gestor de sistema para una gestión eficiente.

### Escalabilidad para Futuras Versiones

- **V1.0**: Integración con dos API de tiendas importantes para comparación de precios.
- **V2.0**: Expansión para incluir datos contribuidos por usuarios, especialmente para tiendas físicas.

### Seguridad

Consulta con experto en seguridad (JAVI) para la seguridad de las contraseñas.

### Pruebas de Código

Utilizando JUnit, Mockito (u otro equivalente) para asegurar aproximadamente un 80% de cobertura de código con pruebas.

Este README sirve como una visión general completa del proyecto, sus objetivos, tecnologías y metodologías empleadas. Siéntete libre de añadir o modificar secciones a medida que el proyecto evolucione.
