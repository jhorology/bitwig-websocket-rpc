const { BitwigClient } = require('..')
const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main() {
  const bws = new BitwigClient('ws://localhost:8887')
  await bws.connect()
  // await bws.config({
  //   useArranger: true,
  //   arrangerCueMarkerSize: 4
  // }, false)
  const report = await bws.report(),
        events = report.events
          .filter(e => e.event.startsWith('arranger.'))
          .map(e => e.event)

  // listen all arranger events
  events.forEach(e => bws.on(e, params => console.log('event:', e, params)))
  await bws.subscribe(events)
  await wait(2000)
  console.log('size of bank:', await bws.call('arranger.cueMarkerBank.getSizeOfBank'))
  console.log('capacity of bank:', await bws.call('arranger.cueMarkerBank.getCapacityOfBank'))
  for (let i = 0; i < 4; i++) {
    console.log(`cue maker position[${i}] is subscribed:`, await bws.call('arranger.cueMarkerBank.getItemAt.position.isSubscribed', [i]))
  }
  bws.msg('move 4th cue marker.', true)
  await bws.event('arranger.cueMarkerBank.getItemAt.position').atSlot(3).occur().asPromised()
  bws.msg('OK')
  await wait(2000)

  // set bank size 4 -> 3
  await bws.call('arranger.cueMarkerBank.setSizeOfBank', [3])
  await wait(1000)
  console.log('size of bank:', await bws.call('arranger.cueMarkerBank.getSizeOfBank'))
  console.log('capacity of bank:', await bws.call('arranger.cueMarkerBank.getCapacityOfBank'))
  for (let i = 0; i < 4; i++) {
    console.log(`cue maker position[${i}] is subscribed:`, await bws.call('arranger.cueMarkerBank.getItemAt.position.isSubscribed', [i]))
  }
  bws.msg('move 4th cue marker, again.', true)
  await bws.event('arranger.cueMarkerBank.getItemAt.position').atSlot(3).occur().asPromised()
  bws.msg('OK')
  // nothing affects observers
  await wait(2000)

  let cueMarkerNames = await bws.batch(context => {
    for (let i = 0; i < 4; i++) {
      context.call('arranger.cueMarkerBank.getItemAt.getName', [i])
    }
  })
  console.log('cue marker names 0-3', cueMarkerNames)
  bws.notify('arranger.cueMarkerBank.scrollPageForwards')
  await wait(1000)
  cueMarkerNames = await bws.batch(context => {
    for (let i = 0; i < 4; i++) {
      context.call('arranger.cueMarkerBank.getItemAt.getName', [i])
    }
  })
  console.log('cue marker names 0-3', cueMarkerNames)
  // nothing affects scrolling page
  // what?
}

main().catch(e => console.log('done with error!', e))
