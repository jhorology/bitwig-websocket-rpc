WebSocket = (require 'rpc-websockets').Client
fs        = require 'fs'
spec      = require '../rpc-implementation-spec.json'

$ =
  url: 'ws://localhost:8887'

events = []

events.push event.event for event in module.events for module in spec.modules when module.name isnt 'test' and module.name isnt 'rpc'
ws = new WebSocket($.url)
error = undefined
connected = undefined
maxLength = 0
maxLength = Math.max(maxLength, event.length) for event in events
ws.on 'open', ->
  connected = true
  for event in events
    new Subscriber ws, event
      .subscribe()
  ws.on 'error', (err) ->
    error = err
    ws.close() if connected
  ws.on 'close', ->
    if (error)
      console.error error

process.on 'exit', ->
  ws.close() if connected


class Subscriber
  constructor: (@ws, @event) ->
    @e = @event.padEnd maxLength
  subscribe: () ->
    @ws.on @event, @onNotify
    @ws.subscribe @event
      .then (result) => 
      .catch (err) =>
        console.info "[#{@e}] subscribe error:", err
  onNotify: () =>
    console.info "[#{@e}]", arguments
