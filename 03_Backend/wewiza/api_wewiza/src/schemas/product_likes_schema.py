product_likes_schema = {
    "$jsonSchema": {
        "bsonType": "object",
        "title": "Product Likes Object Validation",
        "required": [
            "uuid",
            "num_likes",
            "likes_email",
            "unlikes_email",
            "date_created",
        ],
        "properties": {
            "uuid": {
                "bsonType": "string",
                "description": "must be a string and is required",
            },
            "num_likes": {
                "bsonType": "int",
                "description": "must be a int and is required",
            },
            "likes_email": {
                "bsonType": "array",
                "description": "must be a array",
            },
            "unlikes_email": {
                "bsonType": "array",
                "description": "must be a array",
            },
            "date_created": {
                "bsonType": "string",
                "description": "must be a string",
            },
        },
    }
}
