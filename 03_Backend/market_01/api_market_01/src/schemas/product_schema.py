product_schema = {
    "$jsonSchema": {
        "bsonType": "object",
        "title": "Product Object Validation",
        "required": [
            "uuid",
            "category_id",
            "name",
            "price",
            "quantity_measure",
            "measure",
            "price_by_standard_measure",
            "image_url",
            "url",
            "store_name",
            "store_image_url",
        ],
        "properties": {
            "uuid": {
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
            "price": {
                "bsonType": "double",
                "description": "must be a double and is required",
            },
            "quantity_measure": {
                "bsonType": "double",
                "description": "must be a double and is required",
            },
            "measure": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "price_by_standard_measure": {
                "bsonType": "double",
                "description": "must be a double and is required",
            },
            "image_url": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "url": {
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
