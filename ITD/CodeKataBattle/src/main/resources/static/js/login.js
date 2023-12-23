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
