const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main() {
  const bws = new BitwigClient('ws://localhost:8887')
  await bws.connect()
  bws.msg('hello')
  await wait(5000)
  bws.msg('continuous hello', true)
  await wait(10000)
  bws.msg('3sec hello', true, 3)
  await wait(5000)
  bws.msg('continuous hello again', true)
  await wait(2000)
  bws.msg('interrupt hello')
  await wait(3000)
  await bws.close()
}
main()
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
