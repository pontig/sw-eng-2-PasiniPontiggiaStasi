[
    {
        "id": Number,
        "name": String,
        "admin": Boolean,
        "active": Boolean,
        "battles": [
            {
                "id": Number,
                "name": String,
                "language": String,
                "participants": Number,
                "phase": Number[1 - 4],
                "remaining": String(Date) // format: d + "d" + h + "h" 
            },
            ...
        ],
        "ranking": [
            {
                "id": Number,
                "name": String,
                "points": Number
            },
            ...
        ]
    }
]

/*
 phase:
    1 - Registration
    2 - Code Submission
    3 - Manual evaluation
    4 - Finished, closed
*/