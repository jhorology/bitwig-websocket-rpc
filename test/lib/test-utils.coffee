WebSocket = require 'ws'
_         = require 'underscore'
chai      = require 'chai'
assert = chai.assert

$ =
  url: 'ws://localhost:8887'
  timeout: 700
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
processRequest = (ws, req, assertError) ->
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
processNotify = (ws, req, assertError) ->
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
    timer = onOpen = onError = undefined
    ws = new WebSocket $.url
    clear = ->
      ws.off 'open', onOpen
      ws.off 'error', onError
      clearTimeout timer
    onOpen = (err) ->
      clear()
      if err
        reject err
      resolve ws
    onError = (err) ->
      clear()
      reject err
    timer = setTimeout ->
      clear()
      reject new Error 'connection open timeout.'
    , $.timeout
    ws.once 'open', onOpen
    ws.once 'error',onError

# close connection
wsClose = (ws) ->
  return new Promise (resolve, reject) ->
    return resolve() unless ws
    timer = onClose = onError = undefined
    clear = () ->
      ws.off 'close', onClose
      ws.off 'error', onError
      clearTimeout timer
    onClose = ->
      clear()
      resolve()
    onError = (err) ->
      clear()
      reject err
    timer = setTimeout ->
      clear()
      reject new Error 'connection close timeout.'
    , $.timeout
    ws.once 'close', onClose
    ws.once 'error', onError
    ws.close()
    
_sendAndResponse = (ws, req, assertError, notify) ->
  return new Promise (resolve, reject) ->
    timer = onMessage = onError = undefined
    clear = () ->
      ws.off 'message', onMessage
      ws.off 'error', onError
      clearTimeout timer
    onMessage = (message) ->
      console.info "# <-- #{message}" if $.printMessage
      clear()
      try
        response = _parseResponse message, assertError
        if notify
          assert.deepEqual response, $.echoResponse
          response = 'ok'
        resolve response
      catch error
        reject error
    onError = (error) ->
      clear()
      reject error
    timer = setTimeout ->
      clear()
      reject new Error 'operation timeout.'
    , $.timeout
    ws.once 'message', onMessage
    ws.once 'error', onError
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
 processRequest: processRequest
 processNotify: processNotify
 wsConnect: wsConnect
 wsClose: wsClose
 
