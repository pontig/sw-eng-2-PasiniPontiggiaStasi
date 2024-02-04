function checkInviteOthers() {
    let target = document.getElementsByName("others")[0]
    if (target.checked) {
        generateOghers(target.parentNode.parentNode, 1)
    } else {
        document.querySelectorAll("input[name^='Emailothers']").forEach(e => e.parentNode.removeChild(e))
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

window.addEventListener('load', () => {
    // checkInviteOthers()
    /* JOIN BATTLE */
    document.getElementById("newRegistration").addEventListener('submit', e => {
        e.preventDefault()
        console.log('JOIN BATTLE');

        let studentsEmail = []
        Array.from(document.querySelectorAll("input[name^='Emailothers']")).filter(e => e.value != "").forEach(e => studentsEmail.push(e.value))

        let name = document.getElementsByName("teamName")[0].value
        let battleId = sessionStorage.getItem("battle");

        console.log('Form data: ', { studentsEmail, name, battleId });

        // Define url and data
        const url = '/ckb_platform/battle/join';
        const data = { studentsEmail, name, battleId };

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
                                title: "Battle joined!",
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
                                title: "Battle not joined!",
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
                                title: "Battle not joined!",
                                text: result,
                                type: "error",
                                confirmButtonColor: '#CC208E'
                            })
                        })
                        break;
                    case 400:
                        response.text().then(result => {
                            Swal.fire({
                                title: "Battle not joined, the name is already taken!",
                                text: result,
                                type: "error",
                                confirmButtonColor: '#CC208E'
                            })
                        })
                        break;

                    default:
                        alert("Internal error")
                        break;
                }
            })
            .catch(error => {
                console.error('Error during Fetch: ', error);
            });
    })
})

document.addEventListener('DOMContentLoaded', function () {
    // Get the error parameter from the URL for joining a team
    const urlParams = new URLSearchParams(window.location.search);
    const joined = urlParams.get('joined');

    if(joined){
        Swal.fire({
            title: "Team joined",
            text: joined,
            type: "success",
            confirmButtonColor: '#CC208E'
        })
    }
});