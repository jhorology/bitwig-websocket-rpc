const { Client, ClientError } = require('./json-rpc-client')
const { EventClient } = require('./server-push-model-client')
const { BitwigClient } = require('./bitwig-client')
const {
  installExtension,
  defaultExtensionsDir
} = require('./extension-installer')
module.exports = {
  Client,
  ClientError,
  EventClient,
  BitwigClient,
  installExtension,
  defaultExtensionsDir
}
