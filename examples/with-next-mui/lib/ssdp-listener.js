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
      explicitSocketBind: true
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
      _removeRpcService(heads.USN)
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
  client.search('urn:bitwig-websocket-rpc')
  console.log('[ssdp-listner] send query:', 'urn:bitwig-websocket-rpc')
  setInterval(() => {
    _removeExpiredRpcServices()
    client.search('urn:bitwig-websocket-rpc')
    console.log('[ssdp-listner] send query:', 'urn:bitwig-websocket-rpc')
  }, 120 * 1000)
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
  const now = new Date().getTime()
  let expireTime = now + 1200 * 1000
  const match = /^max-age\s*=\s*([0-9]+)/.exec(heads['CACHE-CONTROL'])
  if (match) {
    expireTime = now + parseInt(match[1]) * 1000
  }
  const rpcService = {
    expireTime: expireTime,
    extension: heads.EXTENSION,
    extensionVersion: extensionVersion,
    bitwigVersion: heads['BITWIG-VERSION'],
    apiVersion: heads['API-VERSION'],
    platform: heads.PLATFORM,
    location: heads.LOCATION
  }
  if (rpcServices[heads.USN]) {
    console.log('[ssdp-listener] update RPC service.', rpcService)
  } else {
    console.log('[ssdp-listener] newly detecct RPC service.', rpcService)
  }
  rpcServices[heads.USN] = rpcService
}

function _removeExpiredRpcServices() {
  const now = new Date().getTime()
  Object.keys(rpcServices)
    .filter(usn => rpcServices[usn].expireTime <= now)
    .forEach(usn => _removeRpcService(usn))
}

function _removeRpcService(usn) {
  const rpcService = rpcServices[usn]
  if (rpcService) {
    console.log('[ssdp-listener] remove RPC service.', rpcService)
  }
  delete rpcServices[usn]
}

module.exports = {
  start: start,
  getRpcServices: getRpcServices
}
