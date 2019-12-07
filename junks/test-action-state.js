const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main(conn1, conn2) {
  const actions = [
    'bounce_in_place',
    'Undo'
  ]
  // first connection
  await conn1.connect()
  await conn1.config({
    useApplication: true
  })
  conn1.on('application.observedAction', params => {
    console.log('conn-1 listener:', params)
  })

  // second connecton
  await conn2.connect()
  conn2.on('application.observedAction', params => {
    console.log('conn-2 listener:', params)
  })

  console.log('# server doesn\'t notify actions-state on first client subscribe()')
  const subscribeResult1 = await conn1.subscribe([
    'application.observedAction'
  ])
  console.log('conn-1 subscribe:', subscribeResult1)

  await wait(3000)

  // server should notify action states on setObserverdIds.
  console.log('\n\n# server should notify actions-state on first client setObservedIds() ids:', actions)
  conn1.notify('application.observedAction.setObservedIds', actions)
  const result1 = await Promise.all([
    conn1.event('application.observedAction')
      .match(params => params.id === 'bounce_in_place')
      .within(2).sec()
      .asPromised(),
    conn1.event('application.observedAction')
      .match(params => params.id === 'Undo')
      .within(2).sec()
      .asPromised()
  ])
  console.log('# OK conn-1 result:', result1)

  await wait(3000)

  console.log('\n\n# server should notify actions-state on second client subscribe()')
  conn2.subscribe([
    'application.observedAction'
  ])
  const result2 = await Promise.all([
    conn2.event('application.observedAction')
      .match(params => params.id === 'bounce_in_place')
      .within(2).sec()
      .asPromised(),
    conn2.event('application.observedAction')
      .match(params => params.id === 'Undo')
      .within(2).sec()
      .asPromised()
  ])
  console.log('# OK conn-2 result:', result2)

  await wait(3000)

  console.log('\n\nLook at Bitwig Studio!')
  conn1.msg('Do something to enable Bounce in Place!', true)
  await Promise.all([
    conn1.event('application.observedAction')
      .become({ id: 'bounce_in_place', value: true })
      .asPromised(),
    conn2.event('application.observedAction')
      .become({ id: 'bounce_in_place', value: true })
      .asPromised()
  ])
  conn1.msg('OK!')
  await wait(3000)

  conn1.msg('Let\'s do Bounce in Place!', true)
  await Promise.all([
    conn1.event('application.observedAction')
      .become({ id: 'bounce_in_place', value: false })
      .asPromised(),
    conn2.event('application.observedAction')
      .become({ id: 'bounce_in_place', value: false })
      .asPromised()
  ])
  conn1.msg('OK!')

  // server should notify action state on subscribe
  conn1.close()
  conn2.close()
}

const conn1 = new BitwigClient('ws://localhost:8887'),
      conn2 = new BitwigClient('ws://localhost:8887', {
        // traceLog: data => console.log.apply(undefined, data)
      })
main(conn1, conn2)
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
