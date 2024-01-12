var regForm, logForm

window.onload = () => {
    regForm = document.getElementById("register")
    logForm = document.getElementById("login")
}

function register() {
    logForm.style.opacity = "0"
    setTimeout(() => {
        logForm.style.display = "none"
        regForm.style.display = "flex"
        setTimeout(() => {
            regForm.style.opacity = "1"
        }, 100)
    }, 400)
}

function login() {
    regForm.style.opacity = "0"
    setTimeout(() => {
        regForm.style.display = "none"
        logForm.style.display = "flex"
        setTimeout(() => {
            logForm.style.opacity = "1"
        }, 100)
    }, 400)
}

//TODO: Non mi va dal file JS ci ho perso anni e non so che ha
/*
document.addEventListener('DOMContentLoaded', function () {
    var loginButton = document.getElementById('loginButton');

    loginButton.addEventListener('click', function () {
        console.log('Tentativo di login');

        // Ottieni i valori della form
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        console.log('Dati inseriti: ', {email, password});

        // Effettua la richiesta Fetch
        const url = 'http://localhost:8080/ckb_platform/login'; // Assicurati di usare il tuo URL effettivo
        const data = {email, password};

        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        };

        fetch(url, options)
            .then(response => {
                //console.log('Dati ricevuti dal server:', data, response.body);

                switch (response.status) {
                    case 200:
                    case 302:
                        response.text().then(result => {
                            console.log(result);
                            window.location.href = result + ".html";
                        })
                        break;

                    case 400:
                    case 404:
                        response.text().then(result => {
                            console.log(result); // Questo sarà il risultato della Promise
                            const errorBox = document.getElementById('errorLogIn');
                            console.log(errorBox); // Aggiungi questo log per debug
                            if (errorBox) {
                                errorBox.textContent = result;
                            }
                        })
                        break;

                    default:
                        const errorBox = document.getElementById('errorLogIn');
                        if (errorBox) {
                            errorBox.textContent = "Internal error";
                        }
                        break;
                }

            })
            .catch(error => {
                console.error('Errore durante la richiesta Fetch:', error);
            });
    })
})*/