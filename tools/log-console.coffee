WebSocket = (require 'rpc-websockets').Client
colors = require 'colors'

$ =
  url: 'ws://localhost:8887'
  theme: colors.setTheme
    D: 'green'   # debug
    T: 'cyan'    # trace
    I: 'gray'    # info
    W: 'yellow'  # warn
    E: 'red'     # error

ws = new WebSocket($.url)
error = undefined
event = 'rpc.log'
connected = undefined

ws.on 'open', ->
  connected = true
  ws.subscribe(event)
    .then () ->
    .catch (err) ->
      error = err
      ws.close()
  ws.on event, (log) ->
    console.info logFormat log
      
  ws.on 'error', (err) ->
    error = err
    ws.close() if connected
    
  ws.on 'close', ->
    connected = false
    if (error)
      console.error error

process.on 'exit', ->
  ws.close() if connected


logFormat = (s) ->
  severity = (s.split '|')[1]
  s[severity]
