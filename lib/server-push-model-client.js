const {Client, ClientError} = require('./json-rpc-client')

/**
 * @class
 * JSON-RPC 2.0 over WebSocket Client with supporting Server-Push-Model.
 */
class EventClient extends Client {
}

module.exports = {
  EventClient: EventClient
}


