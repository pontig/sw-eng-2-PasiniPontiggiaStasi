[
    {
        "id": Number,
        "name": String,
        "active": Boolean,
        "subscribed": Boolean,
        "battles": [
            {
                "id": Number,
                "name": String,
                "language": String,
                "participants": Number,
                "subscribed": Boolean,
                "score"?: Number,
                "phase": Number[1 - 4],
                "remaining": String(Date) // Time remaining in the current phase
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