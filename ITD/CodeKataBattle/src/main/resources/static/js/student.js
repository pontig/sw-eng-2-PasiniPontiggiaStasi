function checkInviteOthers() {
    let target = document.getElementsByName("others")[0]
    if (target.checked) {
        generateOghers(target.parentNode.parentNode, 1)
    } else {
        document.querySelectorAll("input[name^='Emailothers']").forEach(e =>  e.parentNode.removeChild(e) )
    }
}

function generateOghers(parentForm, level) {
    let t = document.createElement("input")
    t.setAttribute("type", "email")
    t.setAttribute("name", "Emailothers" + level)
    t.setAttribute("placeholder", "Insert email")

    let button = document.getElementById("subscribeButtonForm")

    t.addEventListener('input', (e) => {
        if (t.nextElementSibling.type == "submit") {
            generateOghers(parentForm, level + 1)
        } else if (t.value == "") {
            parentForm.removeChild(t)
        }
    })

    parentForm.insertBefore(t, button)
}