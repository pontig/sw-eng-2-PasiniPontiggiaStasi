// main.test.js
function logHelloWorld() {
    console.log('Hello, World in JS!');
}

function sum(a, b) {
    return a - b;
}

// If you have other code in main.test.js, make sure to export the necessary functions or variables.
module.exports = { logHelloWorld, sum };