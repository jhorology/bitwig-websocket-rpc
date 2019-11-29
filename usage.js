// const bitwig = require('bitwig-websocket-rpc')
const { BitwigClient } = require('.')

const wait = millis => {
  return new Promise(resolve => setTimeout(resolve, millis))
}

async function main() {
  const ws = new BitwigClient('ws://localhost:8887', {
    traceLog: undefined
  })

  // connect to server
  await ws.connect()

  // configure interest modules.
  // this function may trigger restart of extension.
  // so all client connections will maybe closed by server.

  // ws.config(settings, merge, reconnect)
  const config = await ws.config({
    useTransport: true
  }, true, true)
  console.log('config:', config)

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

  // handling & subscribe interest events

  // if initial value is needed, event listeners should be registered before subscribe.
  ws.on('transport.getPosition', params => {
    console.log('position:', params)
  })
  ws.on('transport.isPlaying', params => {
    console.log('playing:', params[0] ? 'play' : 'stop')
  })
  const subscribeResult = await ws.subscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  console.log('subscribe result:', subscribeResult)

  // now you can read Transport#isPlaying()
  // Value#subscribe() is invoked internally because of subscribing event.

  // batch request can reduce communication and server side thread dispatching cost.
  const batchResult = await ws.batchRequest(context => {
    // SettableBooleanValue Transport#isPlaying()
    context.call('transport.isPlaying')
    // boolean Transport#isPlaying().get()
    context.call('transport.isPlaying.get')
  })
  // Both values are same boolean value.
  // API's value objects (inherited Value class) are serialized via custom serializer.
  // see com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters
  console.log('isPlaying1:', batchResult[0], ', isPlaying2:', batchResult[1])

  ws.notify('transport.play')
  await wait(6000)
  ws.notify('transport.stop')
  await wait(1000)

  // unsubscribe events
  await ws.unsubscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  // close a connection
  await ws.close()
}

main()
  .then(() => console.log('done!'))
  .catch((err) => console.log('done with error!', err))
