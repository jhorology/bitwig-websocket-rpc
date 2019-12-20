var Client = require('node-ssdp').Client,
    client = new Client();
 
client.on('response', (headers, statusCode, rinfo) => {
  console.log('## response '.padEnd(80, '='))
  console.log('headers:', headers, 'statusCode:', statusCode, 'rinfo:', rinfo)
})
 
client.search('ssdp:all');


setInterval(() => {
  // search for WebSocketRpcServer API10
  console.log('## searching:', 'urn:bitwig-websocket-rpc:service:JSONRPC20:0.2.0-SNAPSHOT')
  client.search('urn:bitwig-websocket-rpc:service:JSONRPC20:0.2.0-SNAPSHOT');
}, 5000)
 


