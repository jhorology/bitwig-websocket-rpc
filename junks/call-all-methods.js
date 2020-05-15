const { BitwigClient, ClientError } = require('..'),
      config = require('./use-all-config.json'),
      { methods, events } = require('./methods-and-events.json'),
      eventMap = events.reduce((map, e) => map.set(e.event, e), new Map()),
      blackList = [
      ],
      targetMethods = methods.filter(m =>
        m.method.startsWith('noteInput0.') &&
        !m.method.startsWith('test.') &&
        !m.method.startsWith('rpc.') &&
        !m.method.endsWith('.unsubscribe') &&
        !m.method.endsWith('.subscribe') &&
        !m.method.endsWith('.setIsSubscribed') &&
        !m.method.endsWith('.setShouldConsumeEvents') &&
        !blackList.includes(m.method))

process.on('unhandledRejection', console.log)

async function main(bws) {
  await bws.connect()
  await bws.config(config)
  await bws.subscribe([...Array.from(eventMap.keys()).filter(e => e.startsWith('noteInput0'))], 20 * 1000)

  await new Promise(resolve => setTimeout(resolve, 5000))

  for (let i = 0; i < targetMethods.length; i++) {
    const method = targetMethods[i],
          params = method.params.map(p => {
            switch (p) {
            case 'number': return 0
            case 'string': return 'hogehoge'
            case 'boolean': return false
            case 'number[]': return [0]
            case 'string[]': return ['hogehoge']
            case 'boolean[]': return [false]
            case 'object':
            case 'object[]':
            default:
              console.log('WTF!', method)
              return null
            }
          })
    let result, err
    try {
      result = await bws.call(method.method, params)
    } catch (e) {
      err = e
      if (e instanceof ClientError && e.data && e.data.error) {
        err = e.data.error
      }
    }
    const log = {
      method: method.method,
      params: params,
      resultType: method.result,
      result: result
    }
    if (err) {
      log.error = err
    }
    console.log(log)
    await new Promise(resolve => setTimeout(resolve, 100))
    if (bws.readyState > 1) {
      console.log('Bitwig Studio is dead!')
      break
    }
  }
}

const bws = new BitwigClient('ws://localhost:8887', {
  // traceLog: (objs) => console.log.apply(undefined, objs)
})

main(bws)
  .then(() => console.log('done!'))
  .catch((err) => console.log('done with error!', err))
  .finally(() => bws.close())
