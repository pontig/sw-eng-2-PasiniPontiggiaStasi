// src/test/javascript/main.test.js
const { logHelloWorld } = require('../../main/js/main');
test('should log "Hello, World in JS!"', () => {
    const consoleLogSpy = jest.spyOn(console, 'log');
    logHelloWorld();
    expect(consoleLogSpy).toHaveBeenCalledWith('Hello, World in JS!');
    consoleLogSpy.mockRestore();
});
