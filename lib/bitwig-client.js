const { ClientError } = require('./json-rpc-client'),
      { EventClient } = require('./server-push-model-client')

/**
 * @class
 * Cleint for Bitwig Studio extension WebSocketRpcServer.extension
 */
class BitwigClient extends EventClient {
}

module.exports = {
  BitwigClientt: BitwigClient
}
