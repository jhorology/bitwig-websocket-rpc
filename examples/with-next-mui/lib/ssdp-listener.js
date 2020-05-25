const fs = require('fs'),
  { Server, Client } = require('node-ssdp'),
  dev = process.env.NODE_ENV !== 'production'

const rpcServices = {}

// this is a server-side code

/**
 * Start the SSDP listener service
 */
function start() {
  const server = new Server(),
    client = new Client({
      // TODO why need this on windows ?
      // explicitSocketBind: true
    }),
    SERVICE_TYPE = /^urn:bitwig-websocket-rpc:service:json-rpc 2\.0:(.+)$/

  server.on('advertise-alive', (heads, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.NT)
    if (match) {
      _addRpcService(heads, match[1])
    }
  })

  server.on('advertise-bye', (heads, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.NT)
    if (match) {
      _removeRpcService(heads)
    }
  })

  client.on('response', (heads, statusCode, rinfo) => {
    const match = SERVICE_TYPE.exec(heads.ST)
    if (match) {
      _addRpcService(heads, match[1])
    }
  })

  // start server on all interfaces
  server
    .start()
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

function _addRpcService(heads, extensionVersion) {
  if (dev && !rpcServices[heads.USN]) {
    console.log('[ssdp-listener] detecct RPC service.', heads)
  }
  rpcServices[heads.USN] = {
    extension: heads.EXTENSION,
    extensionVersion: extensionVersion,
    bitwigVersion: heads['BITWIG-VERSION'],
    apiVersion: heads['API-VERSION'],
    platform: heads.PLATFORM,
    location: heads.LOCATION
  }
}

function _removeRpcService(heads) {
  if (dev && rpcServices[heads.USN]) {
    console.log('[ssdp-listener] remove RPC service.', heads)
  }
  delete rpcServices[heads.USN]
}

module.exports = {
  start: start,
  getRpcServices: getRpcServices
}
