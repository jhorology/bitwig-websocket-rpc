const WebSocket = require('rpc-websockets').Client,
      assert = require('assert')

/**
 * Configure accessible RPC modules of Bitwig Studio.
 * @method
 * @param {String} url - URL of WebSockets server.
 * @param {Object} config - Configuration settings for RPC modules of Bitwig Studio.
 * @return {Promise}
 */
module.exports = async (url, config) => {
  const settings = Object.assign({ useAbbreviatedMethodNames: false }, config)
  const ws = await bwsOpen(url)
  // this method trigger restart extension, so connection will be closed by server.
  await ws.notify('rpc.config', settings)
  await waitEvent(ws, 'close', 3000)
  // await ws.close()
  const rws = await bwsOpen(url)
  rws.close()
}

async function bwsOpen(url) {
  const ws = new WebSocket(url, {
    autoconnect: true,
    reconnect: true,
    max_reconnects: 4
  })
  try {
    await waitEvent(ws, 'open', 5000)
    // assert connection is established. timeout 2000msec
    assert.ok((await ws.call('rpc.echo', ['hello'], 2000)) === 'hello')
  } catch (err) {
    ws.close()
    throw err
  } finally {
    ws.reconnect = false
  }
  return ws
}

function waitEvent(ws, event, timeout) {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      ws.off(event, resolve)
      reject(new Error(`timeout waiting for event:${event}`))
    }, timeout)
    ws.on(event, () => {
      clearTimeout(timer)
      resolve()
    })
  })
}
