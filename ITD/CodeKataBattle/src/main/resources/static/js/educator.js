var newTourForm, logoutForm, newBattleForm
var closeTour, shareTour

window.onload = () => {
    logoutForm = document.getElementById("logoutEDU")
    newTourForm = document.getElementById("newTournament")
    closeTour = document.getElementById("importantButton")
    shareTour = document.getElementById("queryResult")
    newBattleForm = document.getElementById("newBattle")

    const errorBox = document.getElementById('errorNewTournament');
    if (errorBox)
        errorBox.style.display = 'none';

    /* LOGOUT */
    logoutForm.addEventListener('click', (e) => {
        e.preventDefault();
        console.log('LOGOUT');

        // Define url and data
        const url = '/ckb_platform/logout';

        // Prepare data to send to the Server
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        };

        // Fetch data to url
        fetch(url, options)
            .then(response => {
                // Analise server response code
                switch (response.status) {
                    case 200:
                    case 401:
                    case 404:
                        response.text().then(result => {
                            Swal.fire({
                                title: "Good bye!",
                                text: result,
                                type: "success",
                                confirmButtonColor: '#CC208E'
                            }).then(() => {
                                setTimeout(() => {
                                    window.location.href = "index.html";
                                }, 500);
                            });
                        })
                        break;

                    default:
                        // TODO: cambiare errorLogIn
                        const errorBox = document.getElementById('errorLogIn');
                        if (errorBox)
                            errorBox.textContent = "Internal error";
                        errorBox.style.display = 'flex';
                        break;
                }
            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })

    /* NEW TOURNAMENT */
    newTourForm.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log('NEW TOURNAMENT');

        // Get form value
        const tournamentName = document.getElementById('tournamentName').value;
        const registerDeadline = document.getElementById('tournamentRegistrationDeadline').value;
        const badges = null;

        console.log('Form data: ', {tournamentName, registerDeadline, badges});

        // Define url and data
        const url = '/ckb_platform/tournament/create';
        const data = {tournamentName, registerDeadline, badges};

        // Prepare data to send to the Server
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        };

        // Fetch data to url
        fetch(url, options)
            .then(response => {
                // Analise server response code
                switch (response.status) {
                    case 200:
                        response.text().then(result => {
                            Swal.fire({
                                type: "success",
                                title: "Tournament created!",
                                text: tournamentName + " with Id: " + result,
                                confirmButtonColor: '#CC208E'
                            }).then(() => {
                                setTimeout(() => {
                                    location.reload();
                                }, 500);
                            });
                        })
                        break;

                    case 400:
                    case 403:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorNewTournament');
                            if (errorBox)
                                errorBox.textContent = result;
                            errorBox.style.display = 'flex';
                        })
                        break;

                    case 401:
                        response.text().then(result => {
                            window.location.href = result + ".html";
                        })
                        break;

                    default:
                        const errorBox = document.getElementById('errorNewTournament');
                        if (errorBox)
                            errorBox.textContent = "Internal error";
                        errorBox.style.display = 'flex';
                        break;
                }
            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })

    /* CLOSE TOURNAMENT */
    closeTour.addEventListener('click', (e) => {
        e.preventDefault();
        console.log('CLOSE TOURNAMENT');

        // Get form value
        const id = sessionStorage.getItem("tournament")

        console.log('Form data: ', {id});

        // Define url and data
        const url = '/ckb_platform/tournament/close';
        const data = {id};

        // Prepare data to send to the Server
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        };

        // Fetch data to url
        fetch(url, options)
            .then(response => {
                // Analise server response code
                switch (response.status) {
                    case 200:
                        response.text().then(result => {
                            Swal.fire({
                                title: "Tournament closed!",
                                text: result,
                                type: "success",
                                confirmButtonColor: '#CC208E'
                            }).then(() => {
                                setTimeout(() => {
                                    location.reload();
                                }, 500);
                            });
                        })
                        break;

                    case 401:
                        response.text().then(result => {
                            Swal.fire({
                                title: "Tournament not closed!",
                                text: result,
                                type: "error",
                                confirmButtonColor: '#CC208E'
                            }).then(() => {
                                setTimeout(() => {
                                    window.location.href = "index.html";
                                }, 500);
                            });
                        })

                    case 403:
                    case 404:
                        response.text().then(result => {
                            Swal.fire({
                                title: "Tournament not closed!",
                                text: result,
                                type: "error",
                                confirmButtonColor: '#CC208E'
                            })
                        })
                        break;

                    default:
                        // TODO: Cambiare errorNewTorunement
                        const errorBox = document.getElementById('errorNewTournament');
                        if (errorBox)
                            errorBox.textContent = "Internal error";
                        errorBox.style.display = 'flex';
                        break;
                }
            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })

    /* NEW BATTLE */
    newBattleForm.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log('NEW TOURNAMENT');

        // Get form value
        const tournamentId = sessionStorage.getItem("tournament");
        const battleName = document.getElementById('battleName').value;
        const registerDeadline = document.getElementById('battleRegistrationDeadline').value;
        const submissionDeadline = document.getElementById('battleSubmissionDeadline').value;
        const language = document.getElementById('language').value;
        const minSize = document.getElementById('minSize').value;
        const maxSize = document.getElementById('maxSize').value;
        const fileCKBProblem = document.getElementById('ckbProblem');
        const manualEvaluation = document.getElementById('manualEvaluation').checked;

        console.log('Form data: ', {tournamentId, battleName, registerDeadline, submissionDeadline, language, minSize, maxSize, manualEvaluation});

        // Define url and data
        const url = '/ckb_platform/battle/create';

        // Create FormData object
        const formData = new FormData();
        // Add form values to FormData
        formData.append('tournamentId', tournamentId);
        formData.append('battleName', battleName);
        formData.append('registerDeadline', registerDeadline);
        formData.append('submissionDeadline', submissionDeadline);
        formData.append('language', language);
        formData.append('minSize', minSize);
        formData.append('maxSize', maxSize);
        formData.append('manualEvaluation', manualEvaluation);
        formData.append('ckbProblem', fileCKBProblem.files[0]);

        // Prepare data to send to the Server
        // Prepare data to send to the Server
        const options = {
            method: 'POST',
            body: formData
        };

        // Fetch data to url
        fetch(url, options)
            .then(response => {
                // Analise server response code
                switch (response.status) {
                    case 200:
                        response.text().then(result => {
                            Swal.fire({
                                type: "success",
                                title: "Battle created!",
                                text: battleName + " created with Id: " + result,
                                confirmButtonColor: '#CC208E'
                            }).then(() => {
                                setTimeout(() => {
                                    location.reload(); // TODO: reload ma nella schemata delle battle
                                }, 500);
                            });
                        })
                        break;

                    case 400:
                    case 403:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorNewBattle');
                            if (errorBox)
                                errorBox.textContent = result;
                            errorBox.style.display = 'flex';
                        })
                        break;

                    case 401:
                        response.text().then(result => {
                            window.location.href = result + ".html";
                        })
                        break;

                    default:
                        const errorBox = document.getElementById('errorNewBattle');
                        if (errorBox)
                            errorBox.textContent = "Internal error";
                        errorBox.style.display = 'flex';
                        break;
                }
            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })
}