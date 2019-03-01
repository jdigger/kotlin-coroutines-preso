// bring in the basic HTTP support from Node.js
const http = require("http");


/**
 * Create and start an HTTP server on port 8070 using the raw Node callbacks.
 */
function startCallbackServer() {
    // create a server instance and register and HTTP request listener
    const callbackServer = http.createServer((req, res) => {
        res.statusCode = 200;
        res.setHeader('Content-Type', 'text/plain');
        res.end('Hello World\n');
    });

    const port = 8070;
    const hostname = 'localhost';

    // start the server on the given hostname and port
    callbackServer.listen(port, hostname, () => {
        console.log(`Callback server running at http://${hostname}:${port}/`);
    });
}


/**
 * Adapter function to translate the Node callback API for creating a Server into a Promise.
 *
 * @returns {Promise<Server>}
 */
function createServerPromise() {
    return new Promise((resolve, reject) => {
        const server = http.createServer((req, res) => {
            res.statusCode = 200;
            res.setHeader('Content-Type', 'text/plain');
            res.end('Hello World\n');
        });
        resolve(server);
    });
}

/**
 * Adapter function to translate the Node callback API for create a connection listener for the Server into a Promise.
 *
 * @param {Server} server - the Server instance to create the connection listener for
 * @param {number} port - the port to create the connection listener on
 * @param {string} hostname - the hostname for the connection listener
 * @returns {Promise<{server, port, hostname}>}
 */
function createServerListenerPromise(server, port, hostname) {
    return new Promise((resolve, reject) => {
        server.listen(port, hostname, () => {
            resolve({server, port, hostname})
        })
    })
}


/**
 * Create and start an HTTP server on port 8089 using Promises.
 *
 * @returns {Promise<void>}
 */
function startPromiseServer() {
    return createServerPromise()
        .then((server) => {
            return createServerListenerPromise(server, 8089, 'localhost')
        })
        .then(({port, hostname}) => {
            console.log(`PromiseServer running at http://${hostname}:${port}/`);
        });
}


/**
 * Create and start an HTTP server on port 8090 using async/await.
 *
 * @returns {Promise<void>}
 */
async function startAsyncAwaitServer() {
    const server = await createServerPromise();
    const {port, hostname} = await createServerListenerPromise(server, 8090, 'localhost');
    console.log(`AsyncAwaitServer running at http://${hostname}:${port}/`);
}


// ******************************************
// ******************************************
//
// Start three servers using the different techniques.
//

startCallbackServer();
startPromiseServer();
startAsyncAwaitServer();

console.log('Servers starting');
