// const bitwig = require('bitwig-websocket-rpc')
const { BitwigClient } = require('.')

async function main(bws) {
  // connect to server
  await bws.connect()

  // configure interest modules.
  // this function may trigger restart of extension.
  // so all client connections will maybe closed by server.

  const config = await bws.config({
    useApplication: true,
    useTransport: true
  })
  console.log('config:', config)

  // host module is accessible without configuration.
  bws.notify('host.showPopupNotification', ['Hello Bitwig Studio!'])

  try {
    // SettableBooleanValue Transport#isPlaying()
    // this calling will causes error. this is a limitation of Bitwig API.
    const isPlaying0 = await bws.call('transport.isPlaying')
    console.log('isPlaying0:', isPlaying0)
  } catch (err) {
    // { code: -32603,
    //   message: 'Internal error',
    //   data: 'Trying to get a value while not being subscribed.' }
    console.log(err)
  }

  // subscribe interest events

  // if initial value is needed, event listeners should be registered before subscribe.
  const subscribeResult = await bws.subscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  console.log('subscribe result:', subscribeResult)

  // now you can read Transport#isPlaying()
  // Value#subscribe() is invoked internally because of subscribing event.

  // batch request can reduce communication costs and thread dispatching costs of server-side.
  const batchResult = await bws.batch(context => {
    // SettableBooleanValue Transport#isPlaying()
    context.call('transport.isPlaying')
    // boolean Transport#isPlaying().get()
    context.call('transport.isPlaying.get')
  })
  // Both values are same boolean value.
  // API's value objects (inherited Value class) are serialized via custom serializer.
  // see com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters
  console.log('isPlaying1:', batchResult[0], ', isPlaying2:', batchResult[1])

  // play 4 bars from 1.1

  // rewind if play-hedad is not at 1.1
  const position = await bws.call('transport.getPosition.get')
  if (position.raw !== 0) {
    bws.notify('transport.rewind')
    await bws.next().event('transport.getPosition')
      .match(params => params.raw === 0)
      .within(1).sec() // timeout
      .asPromised()
  }
  bws.notify('transport.play')

  await bws.event('transport.getPosition')
    .match(params => params.bars > 4)
    .asPromised()

  bws.notify('transport.stop')

  // unsubscribe events
  await bws.unsubscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
}

const bws = new BitwigClient('ws://localhost:8887', {
  traceLog: undefined
})

main(bws)
  .then(() => console.log('done!'))
  .catch((err) => console.log('done with error!', err))
  .finally(() => bws.close())
