const fs = require('fs'),
      { Server, Client } = require('node-ssdp')

const rpcServices = {}

// this is a server-side code

/**
 * Start the SSDP listener service
 */
function start() {
  const server = new Server(),
        client = new Client({
          // TODO why need this on windows ?
          explicitSocketBind: true
        }),
        SERVICE_TYPE = /^urn:bitwig-websocket-rpc:service:json-rpc 2\.0:(.+)$/

  server.on('advertise-alive', (heads, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.NT)
    if (match) {
      _addServer(heads, match[1])
    }
  })

  server.on('advertise-bye', (heads, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.NT)
    if (match) {
      _removeServer(heads)
    }
  })

  client.on('response', (heads, statusCode, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.ST)
    if (match) {
      _addServer(heads, match[1])
    }
  })

  // start server on all interfaces
  server.start()
    .catch(e => {
      console.log('Failed to start SSDP listener:', e)
    })
    .then(() => {
      console.log('SSDP listener started.')
    })

  // search all SSDP devices
  client.search('ssdp:all')
}

/**
 * Get the liet of Bitwig Studio(s) that are currently running.
 * @return {Array}
 */
function getRpcServices() {
  const services = rpcServices
  const list = Object.keys(services).map(usn => {
    return services[usn]
  })
  return list
}

function _addServer(heads, extensionVersion) {
  rpcServices[heads.USN] = {
    extension: heads.EXTENSION,
    extensionVersion: extensionVersion,
    bitwigVersion: heads['BITWIG-VERSION'],
    apiVersion: heads['API-VERSION'],
    platform: heads.PLATFORM,
    location: heads.LOCATION
  }
}

function _removeServer(heads) {
  delete rpcServices[heads.USN]
}

module.exports = {
  start: start,
  getRpcServices: getRpcServices
}
