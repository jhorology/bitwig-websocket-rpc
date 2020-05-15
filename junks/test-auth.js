const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main(bws) {
  // console.log('### set Authetication off.')
  // await wait(5000)
  // console.log('### connecting... with no password')
  // await bws.connect()
  // await wait(1000)
  // console.log('OK', 'status:', bws.readyStateAsString)
  // await bws.close()
  // await wait(3000)
  // console.log('### connecting... with password')
  // await bws.connect('bitwig')
  // await wait(1000)
  // console.log('OK', 'status:', bws.readyStateAsString)
  // await bws.close()
  // console.log('### set Authetication on, password=bitwig')
  // await wait(10000)
  console.log('### connecting... with password')
  await bws.connect(-1)
  console.log('OK', 'status:', bws.readyStateAsString)
  await bws.close()
  // try {
  //   console.log('### connecting... with no password')
  //   await bws.connect()
  // } catch (err) {
  //   await wait(1000)
  //   console.log('OK', 'status:', bws.readyStateAsString, 'err:', err)
  // }
}

const bws = new BitwigClient('ws://localhost:8887', {
  password: 'bitwig'
  // traceLog: datas => console.log.apply(undefined, datas)
})
main(bws)
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
  .finally(async () => {
    try {
      await bws.close()
    } catch (err) {
    }
  })
