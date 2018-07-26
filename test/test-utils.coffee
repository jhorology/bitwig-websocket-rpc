chai      = require 'chai'
WebSocket = require 'isomorphic-ws'
_         = require 'underscore'
assert = chai.assert

$ =
  url: 'ws://localhost:8887'
  timeout: 1000
  printMessage: off
  echoRequest:  {jsonrpc: '2.0', method: 'rpc.echo', params: ['ok'], id: 999}
  echoResponse: {jsonrpc: '2.0', result: 'ok', id: 999}

# process request
#    ... connect
#    --> requset
#    <-- response
#    ... close
# @req request message
# -----------------------------
wsRequest = (ws, req, assertError) ->
  # use static onnection
  return _sendAndResponse ws, req, assertError, off  if ws
  # use one-by-one connection
  ws = undefined
  return wsConnect()
    .then (conn) ->
      ws = conn
      _sendAndResponse  ws, req, assertError, off
    .finally -> wsClose ws

  # process notify
  #    ... connect
  #    --> notify
  #    --> request echo
  #    <-- response
  #    ... close
  # @req  notify message
  # -----------------------------
wsNotify = (ws, req, assertError) ->
  # use static onnection
  return _sendAndResponse ws, req, assertError, on if ws
  # use one-by-one connection
  ws = undefined
  return wsConnect()
    .then (conn) ->
      ws = conn
      _sendAndResponse ws, req, assertError, on
    .finally -> wsClose ws

# connect to BitigStudio via WebSocket
wsConnect = ->
  return new Promise (resolve, reject) ->
    timer = undefined
    ws = new WebSocket $.url
    clear = ->
      ws.onopen = undefined
      ws.onerror = undefined
      clearTimeout timer
    ws.onopen = (err) ->
      clear()
      resolve ws
    ws.onerror = () ->
      clear()
      reject err
    timer = setTimeout ->
      clear()
      reject new Error 'connection open timeout.'
    , $.timeout

# close connection
wsClose = (ws) ->
  return new Promise (resolve, reject) ->
    return resolve() unless ws
    timer = undefined
    clear = () ->
      ws.onclose = null
      ws.onerror = null
      clearTimeout timer
    ws.onclose = ->
      clear()
      resolve()
    ws.onerror = (err) ->
      clear()
      reject err
    timer = setTimeout ->
      clear()
      reject new Error 'connection close timeout.'
    , $.timeout
    ws.close()
    
_sendAndResponse = (ws, req, assertError, notify) ->
  return new Promise (resolve, reject) ->
    timer = undefined
    clear = () ->
      ws.onmessage = null
      ws.onerror = null
      clearTimeout timer
    ws.onmessage = (message) ->
      console.info "# <-- #{message}" if $.printMessage
      clear()
      try
        response = _parseResponse message.data, assertError
        if notify
          assert.deepEqual response, $.echoResponse
          response = 'ok'
        resolve response
      catch error
        reject error
    ws.onerror = (error) ->
      clear()
      reject error
    timer = setTimeout ->
      clear()
      reject new Error 'operation timeout.'
    , $.timeout
    _send ws, req
    if notify
      _send ws, JSON.stringify $.echoRequest

_send = (ws, message) ->
  message = JSON.stringify message unless _.isString message
  ws.send message
  console.info "# --> #{message}" if $.printMessage

_parseResponse = (res, assertError) ->
  data = JSON.parse res
  # remove data property from error object
  if assertError
    if _.isArray data
      for d in data
        delete d.error['data'] if d.error
    else
      delete data.error['data'] if data.error
  data


module.exports =
 wsRequest: wsRequest
 wsNotify:  wsNotify
 wsConnect: wsConnect
 wsClose:   wsClose
 
