const { BitwigClient } = require('..')
const EventStateManager = require('../lib/event-state-manager')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main() {
  const bws = new BitwigClient('ws://localhost:8887')
  await bws.connect()
  const events = await bws.call('rpc.reportEvents')
  console.info(events)
  const mngr = new EventStateManager(events)
  console.info(mngr.store)
  bws.close()
}
main()
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
