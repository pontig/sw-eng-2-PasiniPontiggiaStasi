{
    "name": String,
    "surname": String,
    "tournaments": [
        {
            "id": Number,
            "name": String
        },
        ...
    ],
    "badges": [
        {
            "id": Number,
            "name": String,
            "rank": Number[1 - 7],
            "obtained": [
                {
                    "date": String(Date),
                    "tournament": String,
                    "tournament_id": Number,
                    "battle": String,
                    "battle_id": Number
                },
                ...
            ]
        },
        ...
    ]
}