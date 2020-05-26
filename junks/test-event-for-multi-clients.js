const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main() {
  const client0 = new BitwigClient('ws://localhost:8887')
  const client1 = new BitwigClient('ws://localhost:8887')
  await client0.connect()
  await client1.connect()
  client0.on('transport.isPlaying', params => {
    console.log('client0 isPlaying:', params[0])
  })
  client1.on('transport.isPlaying', params => {
    console.log('client1 isPlaying:', params[0])
  })
  client0.subscribe(['transport.isPlaying'])
  await wait(10000)
  client1.subscribe(['transport.isPlaying'])
  await wait(3000)
  client1.close()
  client0.close()
}
main()
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
