[
    {
        "id": Number,
        "title": String,
        "description": String,
        "language": String,
        "opening": String(Date),
        "registration": String(Date),
        "closing": String(Date),
        "min_group_size": Number,
        "max_group_size": Number,
        "link": String,
        "phase": Number[1 - 4],
        "tournament_name": String,
        "tournament_id": Number,
        "admin": Boolean,
        "manual": Boolean, // Could be reduntant with phase, but it's easier to check
        "ranking": [
            {
                "id": Number,
                "name": String,
                "score": Number
            },
            ...
        ]
    }
]