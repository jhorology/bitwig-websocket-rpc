const WebSocket = require('rpc-websockets').Client,
      assert = require('assert')

/**
 * Configure accessible RPC modules of Bitwig Studio.
 * @method
 * @param {String} url - URL of WebSockets server.
 * @param {Object} config - Configuration settings for RPC modules of Bitwig Studio.
 * @param {boolean} merge - merge config object into current configuration, or not.
 * @return {Promise} resolve present configuration after applying this method.
 */
module.exports = async (url, config, merge) => {
  const ws = await bwsOpen(url),
        currentConfig = await ws.call('rpc.config', [], 5000),
        fullFilled = Object.keys(config).every(key => {
          return config[key] === currentConfig[key]
        })
  if (merge && fullFilled) {
    return currentConfig
  }

  const settings = merge
    ? Object.assign(currentConfig, config)
    : Object.assign({ useAbbreviatedMethodNames: false }, config)
  // this method trigger restart extension, so connection will be closed by server.
  await ws.notify('rpc.config', settings)
  await waitEvent(ws, 'close', 3000)
  // await ws.close()

  const newUrl = settings.webSocketPort
    ? url.replqce(/(.*:)([0-9]+)$/, '$1' + settings.webSocketPort)
    : url,
        newWs = await bwsOpen(newUrl),
        newConfig = await ws.call('rpc.config', [], 5000)
  newWs.close()
  return newConfig
}

async function bwsOpen(url) {
  const ws = new WebSocket(url, {
    autoconnect: true,
    reconnect: true,
    max_reconnects: 4
  })
  try {
    await waitEvent(ws, 'open', 5000)
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
