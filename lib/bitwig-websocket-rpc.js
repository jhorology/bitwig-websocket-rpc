const { Client, ClientError } = require('./json-rpc-client')
const { EventClient } = require('./server-push-model-client')
const { BitwigClient } = require('./bitwig-client')
module.exports = {
  Client: Client,
  ClientError: ClientError,
  EventClient: EventClient,
  BitwigClient: BitwigClient
}
