const { BitwigClient } = require('..')


async function test(time, method) {
  let start, end, count = 0, run = true
  const bws = new BitwigClient('ws://localhost:8887', {
    traceLog: (objs) => console.log.apply(undefined, objs),
    chkArgs: false, // strict check tx method, params
    chkMessage: false // strict check rx message
  })
  try {
    await bws.connect()
    await bws.subscribe([
      'transport.getPosition'
    ])
    setTimeout(() => { run = false }, time)
    start = Date.now()
    while (run) {
      await method(bws)
      count++
    }
    end = Date.now()
    return {
      start: start,
      end: end,
      count: count,
      requestsPerSec: count * 1000 / (end - start)
    }
  } finally {
    await bws.close()
  }
}

function multiConnectionTest(numConn, name, time, method) {
  console.log(`## ------ testing ${numConn} connections: ${name} ......`)
  return Promise.all(Array.from({ length: numConn }, (v, k) => k).map(e =>
    test(time, method)
  ))
}

async function testSet(threads, requestFlush, time, method) {
  const name = `${threads} worker threads: ${!requestFlush ? 'do not ' : ''}use requestFlush(): ${method}`
  let result
  const bws = new BitwigClient('ws://localhost:8887', {
    // traceLog: (objs) => console.log.apply(undefined, objs),
    chkArgs: false, // strict check tx method, params
    chkMessage: false // strict check rx message
  })
  await bws.connect()
  await bws.config({
    logLevel: 'ERROR',
    logOutputSystemConsole: false,
    numWorkerThreads: threads,
    doNotUseRequestFlush: !requestFlush,
    useTransport: true
  }, false)
  await bws.close()

  result = await multiConnectionTest(1, name, time, method)
  console.log(name, sum(result))

  result = await multiConnectionTest(4, name, time, method)
  console.log(name, sum(result))

  result = await multiConnectionTest(16, name, time, method)
  console.log(name, sum(result))

  result = await multiConnectionTest(32, name, time, method)
  console.log(name, sum(result))

  result = await multiConnectionTest(64, name, time, method)
  console.log(name, sum(result))
}

async function main() {
  //           worker  requestFlush         RPC method
  await testSet(1,     true,         10000, ws => ws.call('rpc.echo', ['hello']))
  await testSet(8,     true,         10000, ws => ws.call('rpc.echo', ['hello']))
  await testSet(1,     true,         10000, ws => ws.call('transport.getPosition'))
  await testSet(8,     true,         10000, ws => ws.call('transport.getPosition'))
}

function sum(results) {
  const s = {
    count: 0
  }
  results.forEach(v => {
    s.start = s.start ? Math.min(s.start, v.start) : v.start
    s.end = s.end ? Math.max(s.end, v.end) : v.end
    s.count += v.count
    s.requestsPerSec = s.count * 1000 / (s.end - s.start)
  })
  return s
}

main()
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
