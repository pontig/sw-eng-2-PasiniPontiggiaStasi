var regForm, logForm

window.onload = () => {
    regForm = document.getElementById("register")
    logForm = document.getElementById("login")

    sessionStorage.clear();

    const errorBox = document.getElementById('errorLogIn');
    if (errorBox)
        errorBox.style.display = 'none';

    /* LOGIN */
    logForm.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log('LOGIN');

        // Get form value
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        console.log('Form data: ', {email, password});

        // Define url and data
        const url = '/ckb_platform/login';
        const data = {email, password};

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
                    case 302:
                        response.json().then(result => {
                            console.log(result);
                            sessionStorage.setItem('name', result.name)
                            sessionStorage.setItem('surname', result.surname)
                            sessionStorage.setItem('userId', result.id)
                            window.location.href = "index"+result.role+".html"
                        })
                        break

                    case 400:
                    case 404:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorLogIn');
                            if (errorBox)
                                errorBox.textContent = result;
                            errorBox.style.display = 'flex';
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

    /* REGISTER */
    regForm.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log('REGISTER');

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
        const url = '/ckb_platform/register';
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
                            if (errorBox)
                                errorBox.textContent = result;
                            errorBox.style.display = 'flex';
                        })
                        break;

                    case 409:
                        response.text().then(result => {
                            const errorBox = document.getElementById('errorRegister');
                            if (errorBox)
                                errorBox.textContent = result;
                            errorBox.style.display = 'flex';
                        })
                        break;

                    default:
                        const errorBox = document.getElementById('errorRegister');
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

function register() {
    const errorBox = document.getElementById('errorRegister');
    if (errorBox)
        errorBox.style.display = 'none';

    regForm.reset()

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
    const errorBox = document.getElementById('errorLogIn');
    if (errorBox)
        errorBox.style.display = 'none';

    logForm.reset()

    regForm.style.opacity = "0"
    setTimeout(() => {
        regForm.style.display = "none"
        logForm.style.display = "flex"
        setTimeout(() => {
            logForm.style.opacity = "1"
        }, 100)
    }, 400)
}