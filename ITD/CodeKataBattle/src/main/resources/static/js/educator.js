var newTourForm

window.onload = () => {
    newTourForm = document.getElementById("newTournament")

    const errorBox = document.getElementById('errorNewTournament');
    if (errorBox)
        errorBox.style.display = 'none';

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
        const url = 'http://localhost:8080/ckb_platform/tournament/create';
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
                        window.location.href = "indexEDU.html";
                        response.text().then(result => {
                            window.alert("Tournament " + tournamentName + " succesfully created with Id: " + result);
                        })
                        break;

                    case 400:
                    case 403:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorLogIn');
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
}