WebSocket = (require 'rpc-websockets').Client
beautify  = require 'js-beautify'
fs        = require 'fs'

$ =
  url: 'ws://localhost:8887'
  config:
    useApplication: on

configure = (config) ->
  return new Promise (resolve, reject) ->
    error = undefined
    ws = new WebSocket $.url,
      autoconnect: off
      reconnect: off
    ws.once 'open', ->
      ws.notify 'rpc.config', $.config
    ws.once 'error', (err) ->
      ws.close()
      error = err;
    ws.once 'close', ->
      if error
        reject err
      else
        resolve()
    ws.connect()
    
(configure $.config).then ->
  error = undefined
  ws = new WebSocket $.url,
    autoconnect: on
    reconnect: on
  ws.once 'open', ->
    ws.call 'app.actions'
      .then (result) ->
        console.info beautify (JSON.stringify result), indent_size: 2
        error = undefined
      .catch (err) ->
        error = err
      .finally ->
        ws.close()
  ws.once 'error', (err) ->
    ws.close()
    error = err;
  ws.once 'close', ->
    if error
      console.error error
  ws.connect()
