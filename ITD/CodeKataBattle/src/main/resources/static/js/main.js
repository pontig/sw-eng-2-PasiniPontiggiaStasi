// var permissions = "STU"

// Loading the page
window.addEventListener("load", async () => {

    changePage("dashboard", 0)
    document.getElementById("importantButton").style.display = "none"

    document.getElementById("findSTUSearchBox").addEventListener("input", async () => {
        showResult(document.getElementById("findSTUSearchBox"), document.getElementById("findEDUSearchBox"), "https://pontiggiaelia.altervista.org/ckb/users.php?query=")
        document.getElementById("queryResult").style.right = "15vw"
        document.getElementById("queryResult").style.left = "unset"
    })
    document.getElementById("findEDUSearchBox").addEventListener("input", async () => {
        showResult(document.getElementById("findEDUSearchBox"), document.getElementById("findSTUSearchBox"), "https://pontiggiaelia.altervista.org/ckb/users.php?query=")
        document.getElementById("queryResult").style.left = "10vw"
        document.getElementById("queryResult").style.right = "unset"
    })
})

// Hide the fotms in a more user friendly way: pushing the esc key
window.addEventListener("keydown", (e) => {
    if (e.key == "Escape") {
        document.getElementById("findSTUSearchBox").value = ""
        document.getElementById("findSTUSearchBox").disabled = false
        document.getElementById("findEDUSearchBox").value = ""
        document.getElementById("findEDUSearchBox").disabled = false
        document.getElementById("queryResult").style.display = "none"
        hideForm()
    }
})

// When clicking on the "Show all..." button in the ranking, it shows all the users
// @param text: the button itself, to remove it after showing all the users
function showAllRanking(text) {
    document.querySelectorAll("#ranking ol li").forEach(li => {
        li.style.display = "list-item"
    })
    text.parentNode.removeChild(text)
}

// When searching for a STU or a EDU, it shows the result box and makes the request to the server keeping the result updated
// @param listened: the input box that is being written in
// @param other: the other input box, to disable it
// @param url: the url to make the request to (STU or EDU)
async function showResult(listened, other, url) {
    resultBox = document.getElementById("queryResult")
    if (listened.value.length == 0) {
        // Empty box, hide the result box
        other.disabled = false
        resultBox.innerHTML = ""
        resultBox.style.display = "none"
    } else {
        // Not empty box, show the result box and update it
        other.disabled = true
        resultBox.style.display = "flex"
        let res = await fetch(url + listened.value)
        let data = await res.json()
        resultBox.innerHTML = ""
        console.log(data)
        data.forEach(e => {
            let li = document.createElement("span")
            li.classList.add("liRes")
            li.innerHTML = e.name + " " + e.surname
            li.addEventListener("click", async () => {
                listened.value = e.name + " " + e.surname
                // alert("clicked " + listened.value)
                if (listened.id == "findSTUSearchBox") {
                    changePage("profile", e.id)
                    listened.value = ""
                    other.disabled = false
                    resultBox.innerHTML = ""
                    resultBox.style.display = "none"
                } else if (listened.id == "findEDUSearchBox") {
                    if (confirm("Do you want to invite " + listened.value + " to your tournament?")) {
                        let res2 = await fetch("https://pontiggiaelia.altervista.org/ckb/invite.php?user=" + e.id + "&tournament=" + sessionStorage.getItem("tournament"))
                        let status = await res2.status
                        switch (status) {
                            case 200:
                                alert("Invitation sent!")
                                break
                            case 400:
                                alert("Invitation already sent!")
                                break
                            // case 403:
                            //     alert("You are not the admin of this tournament!")
                            //     break
                            // case 404:
                            //     alert("User not found!")
                            //     break
                            default:
                                alert("Something went wrong!")
                                break

                        }
                    }
                }

            })
            resultBox.appendChild(li)
        })
    }
    // showScreen() // Non si può fare, perché se no non si vede la search box
}

// Makes the request to the server to get the tournaments, all or owned depending on the url and then creates the divs to show them
// @param url: the url to make the request to (all or owned or subscribed or unsubscribed)
async function getTournaments(url) {
    let res = await fetch(url)
    let data = await res.json()
    let container = document.querySelector("#list > div")
    container.innerHTML = ""
    data
        .sort((a, b) => b.active - a.active) // penso che si possa togliere, che le ordini il db
        .forEach(e => {
            let div = document.createElement("div")
            div.classList.add("selectable")
            div.classList.add("tournament")
            if (e.active == 0) div.classList.add("inactive")
            div.onclick = (() => { changePage("tournament", e.id) })
            let h3 = document.createElement("h3")
            h3.innerHTML = e.name
            let p = document.createElement("p")
            p.innerHTML = "created by " + e.first_name + " " + e.last_name

            div.appendChild(h3)
            div.appendChild(p)
            container.appendChild(div)

        })
}

// Gets the list of all the tournaments and shows them through the getTournaments function
function requestAllTournaments() {
    document.querySelector("#list h2").innerHTML = "All tournaments"
    getTournaments("https://pontiggiaelia.altervista.org/ckb/allTournaments.php")
}

// Sends the score of a manual evaluation to the server, it automatically retrieves the inofs from the html
function sendScore() {
    let res = document.getElementsByName("core")[0]
    let battle_id = document.getElementsByName("battle_id")[0].value
    let group = document.getElementsByName("group_id")[0].value
    if (res.checkValidity()) {
        // TODO: fare la richiesta al server
        changePage("manualEvaluation", battle_id)
        res.value = ""
    }
}

// Page orchestrator, it changes the page and shows the right elements
// @param page: the page to show
// @param id: the id of the infos to ask to the server
async function changePage(page, id) {
    // console.log(page, id, permissions)
    window.scrollTo(0, 0)

    // Hide all the elements
    Array.from(document.querySelectorAll("#content > div, #article > button")).forEach(e => { e.style.display = "none" })

    // Eventually, remove the css refernce for the profile
    let link = document.createElement("link")
    link.rel = "stylesheet"
    link.href = "css/profile.css"
    link.type = "text/css"
    if (document.querySelector("link[href='css/profile.css']"))
        document.head.removeChild(document.querySelector("link[href='css/profile.css']"))

    // Initialization of the path
    let apath = document.createElement("a")
    apath.onclick = (() => { changePage("dashboard", 0) })
    apath.innerHTML = "Dashboard"
    let apatht, apathb, c, c2 // Other variables for the path
    let res, data, container // Other variables for the page


    switch (permissions) {
        case "EDU":
            switch (page) {
                case "dashboard":
                    document.getElementById("list").style.display = "block"
                    document.getElementById("pushbutton").style.display = "block"
                    document.querySelector("#list h2").innerHTML = "Your tournaments"
                    document.getElementById("ranking").style.display = "none"
                    document.getElementById("share").style.display = "none"

                    document.getElementById("title").innerHTML = "Dashboard"

                    getTournaments("https://pontiggiaelia.altervista.org/ckb/ownedTournaments.php?user=1")
                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)

                    sessionStorage.setItem("tournament", null)
                    sessionStorage.setItem("battle", null)

                    break

                case "tournament":
                    // manca la richiesta al server
                    document.getElementById("list").style.display = "block"
                    document.querySelector("#list h2").innerHTML = "Battles"
                    document.getElementById("ranking").style.display = "block"
                    document.getElementById("share").style.display = "flex"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/tournamentInfo.php?id=" + id)
                    data = await res.json()
                    data = data[0]
                    console.log(data)

                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)
                    apatht = document.createElement("a")
                    apatht.onclick = (() => { changePage("tournament", data.id) })
                    apatht.innerHTML = data.name
                    c = document.createElement("span")
                    c.innerHTML = " > "
                    document.getElementById("path").appendChild(c)
                    document.getElementById("path").appendChild(apatht)

                    document.getElementById("title").innerHTML = data.name
                    container = document.querySelector("#list > div")
                    container.innerHTML = ""

                    data.battles
                        .sort((a, b) => a.phase - b.phase) // penso che si possa togliere, che le ordini il db
                        .forEach(e => {
                            let div = document.createElement("div")
                            div.classList.add("selectable")
                            div.classList.add("battle")
                            if (e.phase == 4) div.classList.add("inactive")
                            div.onclick = (() => { changePage("battle", e.id) })
                            let h3 = document.createElement("h3")
                            h3.innerHTML = e.name + " - " + e.language
                            let p1 = document.createElement("p")
                            p1.innerHTML = e.participants + " participants"
                            let p2 = document.createElement("p")
                            p2.innerHTML = "time left: " + e.remaining

                            div.appendChild(h3)
                            div.appendChild(p1)
                            div.appendChild(p2)
                            if (e.phase != 4) div.appendChild(p2)
                            container.appendChild(div)
                        })

                    container = document.querySelector("#ranking > ol")
                    container.innerHTML = ""

                    data.ranking
                        .sort((a, b) => b.points - a.points)
                        .forEach((e, i) => {
                            let li = document.createElement("li")
                            let aaa = document.createElement("a")
                            aaa.onclick = function () { changePage("profile", e.id) }
                            aaa.innerHTML = e.name + " - " + e.points + "pts"

                            if (i > 9) li.style.display = "none"

                            li.appendChild(aaa)
                            container.appendChild(li)
                            if (i == 9) container.innerHTML += '<a class="more" onclick="showAllRanking(this)">Show all...</a>'
                        })

                    if (data.active && data.admin) {
                        let btn = document.createElement("button")
                        btn.innerHTML = "New..."
                        btn.onclick = (() => { showForm("Battle") })
                        let list = document.querySelector("#list > div")
                        list.insertBefore(btn, list.firstChild)

                        let numberActive = data.battles.filter(e => e.phase != 4).length
                        if (numberActive == 0) {
                            // Close tournament
                            document.getElementById("importantButton").style.display = "block"
                            document.getElementById("importantButton").innerHTML = "Close tournament"
                            document.getElementById("importantButton").onclick = (() => { closeTournament(id) }) // TODO: implementare la chiusura del torneo
                        }
                    }

                    sessionStorage.setItem("tournament", id)
                    sessionStorage.setItem("battle", null)

                    break

                case "battle":
                    document.getElementById("description").style.display = "block"
                    document.getElementById("ranking").style.display = "block"
                    document.getElementById("share").style.display = "none"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/battleInfo.php?id=" + id)
                    data = await res.json()
                    data = data[0]
                    console.log(data)

                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)
                    apatht = document.createElement("a")
                    apatht.onclick = (() => { changePage("tournament", data.tournament_id) })
                    apatht.innerHTML = data.tournament_name
                    c = document.createElement("span")
                    c.innerHTML = " > "
                    document.getElementById("path").appendChild(c)
                    document.getElementById("path").appendChild(apatht)
                    apathb = document.createElement("a")
                    apathb.onclick = (() => { changePage("battle", data.id) })
                    apathb.innerHTML = data.title
                    c2 = document.createElement("span")
                    c2.innerHTML = " > "
                    document.getElementById("path").appendChild(c2)
                    document.getElementById("path").appendChild(apathb)


                    document.getElementById("title").innerHTML = data.title
                    document.getElementById("description").innerHTML = ""

                    let p1 = document.createElement("p")
                    p1.innerHTML = data.description
                    let p2 = document.createElement("p")
                    p2.innerHTML = "<b>Language: </b>" + data.language + "<br>"
                    p2.innerHTML += "<b>Opening date: </b>" + data.opening + "<br>"
                    p2.innerHTML += "<b>Registration deadline: </b>" + data.registration + "<br>"
                    p2.innerHTML += "<b>Submission deadline: </b>" + data.closing + "<br>"
                    p2.innerHTML += "<b>Group size: </b> from " + data.min_group_size + " to " + data.max_group_size + " students <br>"
                    let a = document.createElement("a")
                    a.href = data.link
                    a.innerHTML = "<u>Click here to access the GitHub battle repository</u>"
                    a.target = "_blank"

                    document.getElementById("description").appendChild(p1)
                    document.getElementById("description").appendChild(p2)
                    document.getElementById("description").appendChild(a)

                    container = document.querySelector("#ranking > ol")
                    container.innerHTML = ""

                    data.ranking
                        .sort((a, b) => b.points - a.points)
                        .forEach((e, i) => {
                            let li = document.createElement("li")
                            li.innerHTML = e.name + " - " + e.score + "pts" // TODO: aggiungere il link al profilo
                            if (i > 9) li.style.display = "none"

                            container.appendChild(li)
                            if (i == 9) container.innerHTML += '<a class="more" onclick="showAllRanking(this)">Show all...</a>'
                        })

                    // SOLO SE VA FATTA MANUAL EVALUATION
                    if (data.phase == 3 && data.admin) {
                        document.getElementById("importantButton").style.display = "block"
                        document.getElementById("importantButton").innerHTML = "Manual evaluation"
                        document.getElementById("importantButton").onclick = (() => { changePage("manualEvaluation", id) })
                    }

                    sessionStorage.setItem("battle", id)
                    sessionStorage.setItem("tournament", data.tournament_id)
                    break

                case "manualEvaluation":
                    document.getElementById("toBeReviewed").style.display = "block"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/manualEval.php?id=" + id)
                    data = await res.json()
                    console.log(data)

                    container = document.querySelector("#toBeReviewed > table")
                    container.innerHTML = "<tr><th>#</th><th>Team name</th><th>Score</th><th>Action</th></tr>"
                    let CanConfirm = true

                    data.forEach((e, i) => {
                        let tr = document.createElement("tr")
                        let td1 = document.createElement("td")
                        td1.innerHTML = i + 1
                        let td2 = document.createElement("td")
                        td2.innerHTML = e.team
                        let td3 = document.createElement("td")
                        td3.innerHTML = e.score == null ? "N/D" : e.score
                        let td4 = document.createElement("td")
                        let a = document.createElement("a")
                        a.innerHTML = e.score == null ? "Evaluate" : "Review"
                        a.onclick = (() => { changePage("codeToReview", e.id) })
                        if (e.score == null) CanConfirm = false

                        td4.appendChild(a)
                        tr.appendChild(td1)
                        tr.appendChild(td2)
                        tr.appendChild(td3)
                        tr.appendChild(td4)
                        document.querySelector("#toBeReviewed > table").appendChild(tr)

                    })

                    let finalRow = document.createElement("tr")
                    finalRow.innerHTML = '<td colspan="3"></td><td><b><a>&gt&gtConfirm</a></b></td>' // TODO: implementare la conferma

                    if (CanConfirm) container.appendChild(finalRow)
                    break

                case "codeToReview":
                    document.getElementById("codeToReview").style.display = "block"
                    container = document.querySelector("#codeToReview > pre")
                    container.innerHTML = ""

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/code.php?id=" + id)
                    data = await res.json()
                    console.log(data)
                    data = data

                    document.getElementById("title").innerHTML = data.name + "'s code"

                    data.code.replace(/</g, "&lt").replace(/>/g, "&gt")
                    console.log(data.code)
                    container.innerHTML = data.code
                    document.getElementById("scoreEvaluated").value = data.score == null ? null : data.score
                    break

                case "profile":
                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)
                    apathb = document.createElement("a")
                    apathb.innerHTML = "Profile inspection"
                    c2 = document.createElement("span")
                    c2.innerHTML = " > "
                    document.getElementById("path").appendChild(c2)
                    document.getElementById("path").appendChild(apathb)

                    document.head.appendChild(link)
                    document.getElementById("prof1").style.display = "flex"
                    document.getElementById("prof2").style.display = "flex"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/profile.php?id=" + id)
                    data = await res.json()

                    document.getElementById("title").innerHTML = data.name + " " + data.surname + "'s profile"

                    container = document.querySelector("#prof1 > ul")
                    data.tournaments.forEach(e => {
                        let li = document.createElement("li")
                        let a = document.createElement("a")
                        a.innerHTML = e.name
                        a.onclick = (() => { changePage("tournament", e.id) })
                        li.appendChild(a)
                        container.appendChild(li)
                    })

                    container = document.querySelector("#prof2")
                    container.innerHTML = "<h2>Collected badges</h2>"

                    data.badges.forEach(e => {
                        let div = document.createElement("div")
                        div.classList.add("expandable")
                        let span1, span2, span3
                        span1 = document.createElement("span")
                        span1.innerHTML = "&#9679;"
                        span1.classList.add("badge")
                        span1.classList.add("grade" + e.rank)
                        span2 = document.createElement("span")
                        span2.innerHTML = e.name + " | tot earned: " + e.obtained.length
                        span3 = document.createElement("span")
                        span3.innerHTML = "►"
                        span3.classList.add("togglable")

                        let innercontainer = document.createElement("ul")
                        innercontainer.classList.add("surprise")
                        innercontainer.classList.add("hidden")

                        e.obtained.forEach(f => {
                            let li = document.createElement("li")
                            let aa = document.createElement("a")
                            aa.innerHTML = f.battle + ", " + f.date
                            aa.onclick = (() => { changePage("battle", f.id) })
                            li.appendChild(aa)
                            innercontainer.appendChild(li)

                        })

                        div.appendChild(span1)
                        div.appendChild(span2)
                        div.appendChild(span3)
                        div.appendChild(innercontainer)
                        container.appendChild(div)

                    })

                    document.querySelectorAll(".togglable").forEach(e => {
                        e.parentNode.addEventListener("click", () => {
                            e.nextElementSibling.classList.toggle("hidden")
                            e.innerHTML = e.innerHTML == "►" ? "▼" : "►"
                        })
                    })

                    sessionStorage.setItem("battle", null)
                    sessionStorage.setItem("tournament", null)

                    break


                default:
                    break
            }

            break

        case "STU":
            switch (page) {
                case "dashboard":
                    document.getElementById("list").style.display = "block"
                    document.getElementById("xplore").style.display = "block"
                    document.querySelector("#list h2").innerHTML = "Your tournaments"
                    document.getElementById("ranking").style.display = "none"
                    document.getElementById("share").style.display = "none"

                    document.getElementById("title").innerHTML = "Dashboard"

                    getTournaments("https://pontiggiaelia.altervista.org/ckb/subscribedTournaments.php?user=1")
                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/unsubscribedTournaments.php?user=1")
                    data = await res.json()
                    console.log(res.status)

                    container = document.querySelector("#xplore > table")
                    container.innerHTML = "<tr><th><b>Name</b></th><th>&#8987</th></tr>"

                    data.forEach(e => {
                        let tr = document.createElement("tr")
                        let td1 = document.createElement("td")
                        let a = document.createElement("a")
                        a.innerHTML = e.name
                        a.onclick = (() => { changePage("tournament", e.id) })
                        td1.appendChild(a)
                        let td2 = document.createElement("td")
                        td2.innerHTML = e.daysLeft + "d"
                        tr.appendChild(td1)
                        tr.appendChild(td2)
                        container.appendChild(tr)
                    })

                    sessionStorage.setItem("tournament", null)
                    sessionStorage.setItem("battle", null)

                    break

                case "tournament":
                    // manca la richiesta al server
                    document.getElementById("list").style.display = "block"
                    document.querySelector("#list h2").innerHTML = "Battles"
                    document.getElementById("ranking").style.display = "block"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/STUTournamentInfo.php?id=" + id)
                    data = await res.json()
                    data = data[0]
                    console.log(data)

                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)
                    apatht = document.createElement("a")
                    apatht.onclick = (() => { changePage("tournament", data.id) })
                    apatht.innerHTML = data.name
                    c = document.createElement("span")
                    c.innerHTML = " > "
                    document.getElementById("path").appendChild(c)
                    document.getElementById("path").appendChild(apatht)

                    document.getElementById("title").innerHTML = data.name
                    container = document.querySelector("#list > div")
                    container.innerHTML = ""

                    data.battles
                        .sort((a, b) => a.phase - b.phase) // penso che si possa togliere, che le ordini il db
                        .forEach(e => {
                            let div = document.createElement("div")
                            div.classList.add("selectable")
                            div.classList.add("battle")
                            if (e.phase == 4) div.classList.add("inactive")
                            div.onclick = (() => { changePage("battle", e.id) })
                            let h3 = document.createElement("h3")
                            h3.innerHTML = e.name + " • " + e.language
                            let p2 = document.createElement("p")
                            p2.innerHTML = "time left: " + e.remaining

                            div.appendChild(h3)
                            if (e.subscribed) {
                                let p1 = document.createElement("p")
                                p1.innerHTML = "Score: " + e.score + "pts"
                                div.appendChild(p1)
                            }
                            if (e.phase != 4) div.appendChild(p2)
                            container.appendChild(div)
                        })

                    container = document.querySelector("#ranking > ol")
                    container.innerHTML = ""

                    data.ranking
                        .sort((a, b) => b.points - a.points)
                        .forEach((e, i) => {
                            let li = document.createElement("li")
                            let aaa = document.createElement("a")
                            aaa.onclick = function () { changePage("profile", e.id) }
                            aaa.innerHTML = e.name + " - " + e.points + "pts"

                            if (i > 9) li.style.display = "none"

                            li.appendChild(aaa)
                            container.appendChild(li)
                            if (i == 9) container.innerHTML += '<a class="more" onclick="showAllRanking(this)">Show all...</a>'
                        })


                    if (data.active && !data.subscribed && data.canSubscribe) {
                        document.getElementById("importantButton").style.display = "block"
                        document.getElementById("importantButton").innerHTML = "Subscribe"
                        document.getElementById("importantButton").onclick = (() => { subscribeTournament(id) })
                    }

                    sessionStorage.setItem("tournament", id)
                    sessionStorage.setItem("battle", null)

                    break

                case "battle":
                    document.getElementById("description").style.display = "block"
                    document.getElementById("ranking").style.display = "block"
                    document.getElementById("share").style.display = "none"

                    res = await fetch("https://pontiggiaelia.altervista.org/ckb/STUBattleInfo.php?id=" + id)
                    data = await res.json()
                    data = data[0]
                    console.log(data)

                    document.getElementById("path").innerHTML = ""
                    document.getElementById("path").appendChild(apath)
                    apatht = document.createElement("a")
                    apatht.onclick = (() => { changePage("tournament", data.tournament_id) })
                    apatht.innerHTML = data.tournament_name
                    c = document.createElement("span")
                    c.innerHTML = " > "
                    document.getElementById("path").appendChild(c)
                    document.getElementById("path").appendChild(apatht)
                    apathb = document.createElement("a")
                    apathb.onclick = (() => { changePage("battle", data.id) })
                    apathb.innerHTML = data.title
                    c2 = document.createElement("span")
                    c2.innerHTML = " > "
                    document.getElementById("path").appendChild(c2)
                    document.getElementById("path").appendChild(apathb)


                    document.getElementById("title").innerHTML = data.title
                    document.getElementById("description").innerHTML = ""

                    let p1 = document.createElement("p")
                    p1.innerHTML = data.description
                    let p2 = document.createElement("p")
                    p2.innerHTML = "<b>Language: </b>" + data.language + "<br>"
                    p2.innerHTML += "<b>Opening date: </b>" + data.opening + "<br>"
                    p2.innerHTML += "<b>Registration deadline: </b>" + data.registration + "<br>"
                    p2.innerHTML += "<b>Submission deadline: </b>" + data.closing + "<br>"
                    p2.innerHTML += "<b>Group size: </b> from " + data.min_group_size + " to " + data.max_group_size + " students <br>"
                    let a = document.createElement("a")
                    a.href = data.link
                    a.innerHTML = "<u>Click here to access the GitHub battle repository</u>"
                    a.target = "_blank"

                    document.getElementById("description").appendChild(p1)
                    document.getElementById("description").appendChild(p2)
                    if (data.subscribed && data.phase > 1) document.getElementById("description").appendChild(a)

                    container = document.querySelector("#ranking > ol")
                    container.innerHTML = ""

                    data.ranking
                        .sort((a, b) => b.points - a.points)
                        .forEach((e, i) => {
                            let li = document.createElement("li")
                            li.innerHTML = e.name + " - " + e.score + "pts" // TODO: aggiungere il link al profilo
                            if (i > 9) li.style.display = "none"

                            container.appendChild(li)
                            if (i == 9) container.innerHTML += '<a class="more" onclick="showAllRanking(this)">Show all...</a>'
                        })

                    if (/*!data.subscribed && data.phase == 1 &&*/ data.canSubscribe) {
                        document.getElementById("importantButton").style.display = "block"
                        document.getElementById("importantButton").innerHTML = "Subscribe"
                        document.getElementById("importantButton").onclick = (() => {
                            showForm("Registration")
                            document.getElementById("newRegistration").onsubmit = ((event) => {
                                event.preventDefault()
                                let res = document.getElementById("newRegistration").checkValidity()
                                if (res) {
                                    // TODO: fare la richiesta al server
                                    document.getElementById("newRegistration").reset()
                                }
                            })
                        })
                    }

                    if (data.canInviteOthers) {
                        document.getElementById("importantButton").style.display = "block"
                        document.getElementById("importantButton").innerHTML = "Invite others"
                        document.getElementById("importantButton").onclick = (() => {
                            showForm("Invitation")
                            document.getElementById("newInvitation").onsubmit = ((event) => {
                                event.preventDefault()
                                let res = document.getElementById("newInvitation").checkValidity()
                                if (res) {
                                    // TODO: fare la richiesta al server
                                    document.getElementById("newInvitation").reset()
                                    alert("Invitation sent!")
                                }
                            })
                        })
                    }

                    if (!data.minConstraintSatisfied) {
                        let spsp = document.createElement("span")
                        spsp.innerHTML = "⚠️"
                        spsp.id = "warningHover"
                        let dividiv = document.createElement("span")
                        dividiv.id = "warning"
                        dividiv.innerHTML = "Your team does not have enough participants, invite others, or your registration will be cancelled"
                        spsp.appendChild(document.createElement("br"))
                        spsp.appendChild(dividiv)
                        document.getElementById("title").appendChild(spsp)
                    }

                    sessionStorage.setItem("battle", id)
                    sessionStorage.setItem("tournament", data.tournament_id)

                    break

                case "profile":
                    // TODO: è la stessa cosa di EDU
                    break

                default:
                    break
            }
            break

        default:
            break
    }
}

// Shows a particular form in the page
// @param which: the form to show
function showForm(which) {
    document.getElementById("formContainer").style.display = "block"
    document.getElementById("new" + which).style.display = "flex"
    document.querySelector("#formContainer h4").innerHTML = "New " + which
    showScreen()
}

// Shows the div that blurs the background, in order to make the form more visible
function showScreen() {
    let screen = document.getElementById("screen")
    screen.style.display = "block"
    screen.onclick = (() => { hideForm() })
    setTimeout(() => { screen.style.backdropFilter = "blur(5px)" }, 100)
}

// Hides the form and the div that blurs the background
function hideForm() {
    document.querySelectorAll("#formContainer form").forEach(e => e.style.display = "none")
    document.getElementById("formContainer").style.display = "none"
    document.getElementById("screen").style.display = "none"
}