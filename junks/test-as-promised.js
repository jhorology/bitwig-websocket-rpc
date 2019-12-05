const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main(bws) {
  await bws.connect()
  await bws.config({
    useMainTrackBank: true,
    mainTrackBankNumTracks: 8,
    mainTrackBankNumSends: 2,
    mainTrackBankNumScenes: 8
  })
  bws.on('mainTrackBank.getItemAt.solo', params => {
    console.log('solo value', params)
  })
  bws.on('mainTrackBank.getItemAt.sendBank.getItemAt.value', params => {
    console.log('send value', params)
  })

  bws.subscribe([
    'mainTrackBank.getItemAt.solo',
    'mainTrackBank.getItemAt.sendBank.getItemAt.value'
  ])
  await wait(1000)
  bws.msg('push any solo button.', true)
  await bws.next().event('mainTrackBank.getItemAt.solo')
    .occur()
    .asPromised()
  bws.msg('OK')
  await wait(2000)
  bws.msg('push Track 1 solo button.', true)
  await bws.next().event('mainTrackBank.getItemAt.solo')
    .atSlot(0)
    .occur()
    .asPromised()
  bws.msg('OK')
  await wait(2000)

  bws.msg('push Track 2 solo button within 2 seconds.', true)
  let ok = false
  while (!ok) {
    try {
      await bws.next().event('mainTrackBank.getItemAt.solo')
        .atSlot(1)
        .occur()
        .within(2).sec()
        .asPromised()
      ok = true
    } catch (e) {
      bws.msg('timeout! again, push Track 2 solo button within 2 seconds.', true)
    }
  }
  bws.msg('OK')

  await wait(500)
  await bws.call('mainTrackBank.getItemAt.solo.set', [0, false])
  await bws.call('mainTrackBank.getItemAt.solo.set', [1, false])
  await bws.call('mainTrackBank.getItemAt.sendBank.getItemAt.value.setImmediately', [0, 0, 0])
  await bws.call('mainTrackBank.getItemAt.sendBank.getItemAt.value.setImmediately', [0, 1, 0])
  await bws.call('mainTrackBank.getItemAt.sendBank.getItemAt.value.setImmediately', [1, 0, 0])
  await bws.call('mainTrackBank.getItemAt.sendBank.getItemAt.value.setImmediately', [1, 1, 0])
  await wait(2000)
  bws.msg('increase Track 2, Send 1 to 50%.', true)
  await bws.event('mainTrackBank.getItemAt.sendBank.getItemAt.value')
    .atSlot(1, 0)
    .match(params => params[0] >= 0.5)
    .asPromised()
  bws.msg('OK')
  await wait(2000)
  bws.msg('increase Track 2, Send 2 to 100%.', true)
  await bws.event('mainTrackBank.getItemAt.sendBank.getItemAt.value')
    .atSlot(1, 1)
    .become([1])
    .asPromised()
  bws.msg('OK')
}

const bws = new BitwigClient('ws://localhost:8887')
main(bws)
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
  .finally(() => bws.close())
