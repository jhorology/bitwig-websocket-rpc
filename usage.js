// const bitwig = require('bitwig-websocket-rpc')
const bitwig = require('./'),
      WebSocket = require('rpc-websockets').Client

const wait = millis => {
  return new Promise(resolve => setTimeout(resolve, millis))
}

(async () => {
  // configure interest modules.
  // this function trigger restart of extension.
  // so all client connections will be closed by server.
  await bitwig('ws://localhost:8887', {
    useTransport: true,
    useMainTrackBank: true
  })

  // recommended client library.
  // https://github.com/elpheria/rpc-websockets
  const ws = new WebSocket('ws://localhost:8887', {
    autoconnect: true,
    reconnect: true
  })

  ws.on('open', async () => {
    // host module is accessible without configuration.
    ws.notify('host.showPopupNotification', ['Hello Bitwig Studio!'])

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
    ws.subscribe([
      'transport.getPosition',
      'transport.isPlaying'
    ])

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
  })
})()
