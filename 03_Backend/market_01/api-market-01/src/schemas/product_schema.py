product_schema = {
    "$jsonSchema": {
        "bsonType": "object",
        "title": "Product Object Validation",
        "required": [
            "id",
            "category_id",
            "name",
            "currency",
            "price",
            "measure",
            "price_by_measure",
            "image_url",
            "store_name",
            "store_image_url",
        ],
        "properties": {
            "id": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "category_id": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "name": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "currency": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "price": {
                "bsonType": "double",
                "description": "must be a double and is required",
            },
            "measure": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "price_by_measure": {
                "bsonType": "double",
                "description": "must be a double and is required",
            },
            "image_url": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "store_name": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "store_image_url": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
        },
    }
}
