var regForm, logForm

window.onload = () => {
    regForm = document.getElementById("register")
    logForm = document.getElementById("login")
    logForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        console.log('Tentativo di login');
    
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
    
        // console.log('Dati inseriti: ', {email, password});
    
        const url = '/ckb_platform/login'; 
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
                            const errorBox = document.getElementById('errorLogIn');
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



    var registerButton = document.getElementById('registerButton');

    regForm.addEventListener('submit', (e) => {
        

        // Get form value
        const name = document.getElementById('name').value;
        const surname = document.getElementById('surname').value;
        const email = document.getElementById('emailR').value;
        const uni = document.getElementById('uni').value;
        const role = document.getElementById('role').value;
        const password = document.getElementById('passwordR').value;
        const password2 = document.getElementById('password2').value;
        const terms = document.getElementById('terms').checked;

        console.log('Form data: ', {name, surname, email, uni, role, password, password2, terms});

        // Define url and data
        const url = 'http://localhost:8080/ckb_platform/register';
        const data = {name, surname, email, uni, role, password, password2, terms};

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
                        login();
                        break;

                    case 302:
                        response.text().then(result => {
                            console.log(result);
                            window.location.href = result + ".html";
                        })
                        break;

                    case 400:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorRegister');
                            if (errorBox) {
                                errorBox.textContent = result;
                            }
                        })
                        break;

                    case 409:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorRegister');
                            if (errorBox) {
                                errorBox.textContent = result;
                            }
                        })
                        break;

                    default:
                        const errorBox = document.getElementById('errorRegister');
                        if (errorBox) {
                            errorBox.textContent = "Internal error";
                        }
                        break;
                }

            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })
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



