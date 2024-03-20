product_likes_schema = {
    "$jsonSchema": {
        "bsonType": "object",
        "title": "Product Likes Object Validation",
        "required": [
            "uuid",
            "num_likes",
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
        },
    }
}
