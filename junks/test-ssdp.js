const {Server, Client} = require('node-ssdp'),
      server = new Server(),
      client = new Client({
        // TODO why need this on windows ?
        explicitSocketBind: true
      }),
      SERVICE_TYPE = 'urn:bitwig-websocket-rpc:service:json-rpc 2.0:0.2.0-SNAPSHOT'

server.addUSN('upnp:rootdevice')
server.addUSN('urn:schemas-upnp-org:device:MediaServer:1')
server.addUSN('urn:schemas-upnp-org:service:ContentDirectory:1')
server.addUSN('urn:schemas-upnp-org:service:ConnectionManager:1')

server.on('advertise-alive', (heads, rinfo) => {
  if (heads.NT === SERVICE_TYPE) {
    console.log('## ssdp:alive '.padEnd(80, '='))
    console.log('heads:', heads, 'rinfo:', rinfo)
  }
})

server.on('advertise-bye', (heads, rinfo) => {
  if (heads.NT === SERVICE_TYPE) {
    console.log('## ssdp:byebye '.padEnd(80, '='))
    console.log('heads:', heads, 'rinfo:', rinfo)
  }
})

client.on('response', (heads, statusCode, rinfo) => {
  if (heads.ST === SERVICE_TYPE) {
    console.log('## response '.padEnd(80, '='))
    console.log('heads:', heads, 'statusCode:', statusCode, 'rinfo:', rinfo)
  }
})
// start server on all interfaces
server.start()
  .catch(e => {
    console.log('Failed to start server:', e)
  })
  .then(() => {
    console.log('Server started.')
  })

process.on('exit', function() {
  server.stop() // advertise shutting down and stop listening
})

client.search('ssdp:all')

setInterval(() => {
  // search for WebSocketRpcServer API10
  console.log('## searching:', SERVICE_TYPE)
  client.search(SERVICE_TYPE)
}, 5000)
