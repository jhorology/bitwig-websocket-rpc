// const bitwig = require('bitwig-websocket-rpc')
const { BitwigClient } = require('./lib/bitwig-client')

const wait = millis => {
  return new Promise(resolve => setTimeout(resolve, millis))
}

async function main() {
  const ws = new BitwigClient('ws://localhost:8887')
  await ws.connect()
  // configure interest modules.
  // this function may trigger restart of extension.
  // so all client connections will maybe closed by server.

  // ws.config(settings, merge, reconnect)
  const config = await ws.config({
    useTransport: true
  }, true, true)

  // host module is accessible without configuration.
  ws.notify('host.showPopupNotification', ['Hello Bitwig Studio!'])
  await wait(1000)
  ws.msg('こんにちわ Bitwig Studio!')
  await wait(1000)

  try {
    // SettableBooleanValue Transport#isPlaying()
    // this calling will causes error. this is a limitation of Bitwig API.
    const isPlaying0 = await ws.call('transport.isPlaying')
    console.log('isPlaying0:', isPlaying0)
  } catch (err) {
    // { code: -32603,
    //   message: 'Internal error',
    //   data: 'Trying to get a value while not being subscribed.' }
    console.log(err)
  }

  // subscribe interest events
  const result = await ws.subscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  console.log('subscribe result:', result)

  // now you can read Transport#isPlaying()
  // Value#subscribe() is invoked internally because of subscribing event.

  // SettableBooleanValue Transport#isPlaying()
  const isPlaying1 = await ws.call('transport.isPlaying')
  // boolean Transport#isPlaying().get()
  const isPlaying2 = await ws.call('transport.isPlaying.get')
  // Both values are same boolean value.
  // API's value objects (inherited Value class) are serialized via custom serializer.
  // see com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters
  console.log('isPlaying1:', isPlaying1, ', isPlaying2:', isPlaying2)

  // handling events
  ws.on('transport.getPosition', position => {
    console.log('position:', position)
  })
  ws.on('transport.isPlaying', playing => {
    console.log('playing:', playing ? 'start' : 'stop')
  })

  ws.notify('transport.play')
  await wait(6000)
  ws.notify('transport.stop')
  await wait(1000)

  // unsubscribe events
  ws.unsubscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  // close a connection
  ws.close()
}

main()
  .then(() => {
    console.log('done!')
  })
  .catch((err) => {
    console.log('error:', err)
  })
