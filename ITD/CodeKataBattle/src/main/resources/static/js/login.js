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



