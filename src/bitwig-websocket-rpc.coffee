import {Client} from 'rpc-websockets'

module.exports = (url, config) ->
  new Promise (resolve, reject) ->
    error = undefined
    ws = new Client $.url,
      autoconnect: on
      reconnect: off
    ws.once 'open', ->
      # this method trigger restart extension, and connection will close.
      ws.notify 'rpc.config', config
    ws.once 'error', (err) ->
      ws.close()
      error = err;
    ws.once 'close', ->
      if error
        reject error
      else
        resolve()
