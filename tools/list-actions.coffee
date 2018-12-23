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
      autoconnect: on
      reconnect: off
    ws.once 'open', ->
      ws.notify 'rpc.config', $.config
    ws.once 'error', (err) ->
      ws.close()
      error = err;
    ws.once 'close', ->
      if error
        reject error
      else
        ws = new WebSocket $.url,
          autoconnect: on
          reconnect: on
        ws.once 'open', ->
          resolve ws
        ws.once 'error', (err) ->
          error = err;
          ws.close()
        ws.once 'close', -> 
         if error
            reject error
    
conn = undefined
(configure $.config)
  .then (ws) ->
    conn = ws
    error = undefined
    ws.call 'app.actions'
  .then (result) ->
    console.info beautify (JSON.stringify result), indent_size: 2
  .catch (err) ->
    console.error err
  .finally ->
    conn.close() if conn 
