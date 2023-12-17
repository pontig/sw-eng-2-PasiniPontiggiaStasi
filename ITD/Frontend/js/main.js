// var permissions = "STU"

window.addEventListener("load", async () => {

    //getTournaments("https://pontiggiaelia.altervista.org/ckb/ownedTournaments.php?user=1")
    changePage("dashboard", 0)

    document.getElementById("importante").style.display = "none"

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


function showAllRanking(text) {
    document.querySelectorAll("#ranking ol li").forEach(li => {
        li.style.display = "list-item"
    })
    text.parentNode.removeChild(text)
}

// When searching for a STU or a EDU
async function showResult(listened, other, url) {
    resultBox = document.getElementById("queryResult")
    if (listened.value.length == 0) {
        other.disabled = false
        resultBox.innerHTML = ""
        resultBox.style.display = "none"
    } else {
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
            li.addEventListener("click", () => {
                listened.value = e.name + " " + e.surname
                // alert("clicked " + listened.value)
                if (listened.id == "findSTUSearchBox") {
                    changePage("profile", e.id)
                    listened.value = ""
                    other.disabled = false
                    resultBox.innerHTML = ""
                    resultBox.style.display = "none"
                }

            })
            resultBox.appendChild(li)
        })
    }
}

// Makes the request to the server to get the tournaments, all or owned depending on the url
// and then creates the divs to show them
async function getTournaments(url) {
    let res = await fetch(url)
    let data = await res.json()
    let container = document.querySelector("#list > div")
    container.innerHTML = ""
    data
        .sort((a, b) => b.active - a.active)
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

function requestAllTournaments() {
    document.querySelector("#list h2").innerHTML = "All tournaments"
    getTournaments("https://pontiggiaelia.altervista.org/ckb/allTournaments.php")
}

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

async function changePage(page, id) {
    console.log(page, id, permissions)
    window.scrollTo(0, 0)
    Array.from(document.querySelectorAll("#content > div, #article > button")).forEach(e => {
        e.style.display = "none"
    })

    let link = document.createElement("link")
    link.rel = "stylesheet"
    link.href = "css/profile.css"
    link.type = "text/css"

    if (document.querySelector("link[href='css/profile.css']"))
        document.head.removeChild(document.querySelector("link[href='css/profile.css']"))

    let apath = document.createElement("a")
    apath.onclick = (() => { changePage("dashboard", 0) })
    apath.innerHTML = "Dashboard"

    let apatht, apathb, c, c2

    let res, data, container
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

                    break

                case "tournament":
                    // manca la richiesta al server
                    document.getElementById("list").style.display = "block"
                    document.querySelector("#list h2").innerHTML = "Battles"
                    document.getElementById("ranking").style.display = "block"
                    document.getElementById("share").style.display = "block"

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
                            li.innerHTML = e.name + " - " + e.points + "pts" // TODO: aggiungere il link al profilo
                            if (i > 9) li.style.display = "none"

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

                            document.getElementById("importante").style.display = "block"
                            document.getElementById("importante").innerHTML = "Close tournament"
                            document.getElementById("importante").onclick = (() => { closeTournament(id) }) // TODO: implementare la chiusura del torneo
                        }
                    }

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
                        document.getElementById("importante").style.display = "block"
                        document.getElementById("importante").innerHTML = "Manual evaluation"
                        document.getElementById("importante").onclick = (() => { changePage("manualEvaluation", id) })
                    }
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
                            li.innerHTML = e.name + " - " + e.points + "pts" // TODO: aggiungere il link al profilo
                            if (i > 9) li.style.display = "none"

                            container.appendChild(li)
                            if (i == 9) container.innerHTML += '<a class="more" onclick="showAllRanking(this)">Show all...</a>'
                        })


                    if (data.active && !data.subscribed) {
                        document.getElementById("importante").style.display = "block"
                        document.getElementById("importante").innerHTML = "Subscribe"
                        document.getElementById("importante").onclick = (() => { subscribeTournament(id) })
                    }

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

function showForm(which) {
    document.getElementById("formContainer").style.display = "block"
    document.getElementById("new" + which).style.display = "flex"
    let screen = document.getElementById("screen")
    document.querySelector("#formContainer h4").innerHTML = "New " + which
    screen.style.display = "block"
    screen.onclick = (() => { hideForm() })
    setTimeout(() => { screen.style.backdropFilter = "blur(5px)" }, 100)
}

function hideForm() {
    document.querySelectorAll("#formContainer form").forEach(e => e.style.display = "none")
    document.getElementById("formContainer").style.display = "none"
    document.getElementById("screen").style.display = "none"
}