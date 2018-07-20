chai      = require 'chai'
WebSocket = require 'ws'
beautify  = require 'js-beautify'
_         = require 'underscore'

assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

printMessage = off  

# functions
json = (data) ->
  data = JSON.stringify data if !_.isString data and _.isObject data
  ret = beautify data, indent_size: 2
  console.info ret
  ret

# process promised request
# @req request message
# -----------------------------
processRequest = (req) ->
  return new Promise (resolve, reject) ->
    ws = new WebSocket('ws://localhost:8887')
    res = err = undefined
    ws.on 'open', ->
      ws.once 'message', (message) ->
        console.info "# <-- #{message}" if printMessage
        res = message
        ws.close()
      ws.send req
      console.info "# --> #{req}" if printMessage
    ws.on 'error', (error) ->
      err = error
      ws.close()
    ws.on 'close', ->
      if (err)
        reject err
      else
        try
          data = JSON.parse res
          # remove data property from error object
          if _.isArray data
            for d in data
              delete d.error['data'] if d.error
          else
            delete data.error['data'] if data.error
          resolve data
        catch error
          reject error

# process promised notufy
# @req  notify message
# @id   follow on echo message id
# -----------------------------
processNotify = (req, id) ->
  return new Promise (resolve, reject) ->
    ws = new WebSocket('ws://localhost:8887')
    res = err = undefined
    echo = '{"jsonrpc":"2.0","method":"test.echo","params":["ok"],"id":999}'
    ws.on 'open', ->
      ws.once 'message', (message) ->
        console.info "# <-- #{message}" if printMessage
        try
          ans = JSON.parse message
          assert.deepEqual ans,
            jsonrpc: "2.0"
            result: "ok"
            id: 999
          res = "ok"
        catch error
          err = error
        ws.close()
      # send notify message
      ws.send req
      console.info "# --> #{req}"  if printMessage
      # send echo message
      ws.send echo
      console.info "# --> #{echo}"  if printMessage
    ws.on 'error', (error) ->
      err = error
      ws.close()
    ws.on 'close', ->
      if (err)
        reject err
      else
        resolve res
      
describe 'JSON-RPC 2.0 Specification, see https://www.jsonrpc.org/specification', ->
  it 'rpc call with positional parameters. id:1', ->
    processRequest '{"jsonrpc":"2.0","method":"test.sum","params":[1,2],"id":1}'
      .should.become
        jsonrpc: '2.0'
        result: 3
        id: 1
    
  it 'rpc call with positional parameters. id:2', ->
    processRequest '{"jsonrpc":"2.0","method":"test.sum","params":[1,2,3],"id":2}'
      .should.become
        jsonrpc: '2.0'
        result: 6
        id: 2
        
  it 'rpc call with named parameters. mapping params to POJO class. id:3', ->
    processRequest '{"jsonrpc":"2.0","method":"test.sum","params":{"left":1,"right":2},"id":3}'
      .should.become
        jsonrpc: '2.0'
        result: 3
        id: 3
        
  it 'rpc call with named parameters. mapping params to generic class. id:4', ->
    processRequest '{"jsonrpc":"2.0","method":"test.repeat","params":{"left":"abc","right":3},"id":4}'
      .should.become
        jsonrpc: '2.0'
        result: 'abcabcabc'
        id: 4

  it 'a Notification.', ->
    processNotify '{"jsonrpc":"2.0","method":"test.notifies","params":[999]}'
      .should.become 'ok'

  it 'rpc call of non-existent method. id:6', ->
    processRequest '{"jsonrpc":"2.0","method":"foobar","id":6}'
      .should.become
        jsonrpc: '2.0'
        error:
          code: -32601
          message: 'Method not found'
        id: 6

  it 'rpc call with invalid JSON.', ->
    processRequest '{"jsonrpc": "2.0", "method": "foobar, "params": "bar", "baz]'
      .should.become
        jsonrpc: '2.0'
        error:
          code: -32700
          message: 'Parse error'
        id: null

  it 'rpc call with invalid Request object.', ->
    processRequest '{"jsonrpc":"2.0","method":1,"params":"bar"}'
      .should.become
        jsonrpc: '2.0'
        error:
          code: -32600
          message: 'Invalid Request'
        id: null

  it 'rpc call Batch, invalid JSON.', ->
    processRequest '''[
      {"jsonrpc": "2.0", "method": "sum", "params": [1,2,4], "id": "1"},
      {"jsonrpc": "2.0", "method"
]'''
      .should.become
        jsonrpc: '2.0'
        error:
          code: -32700
          message: 'Parse error'
        id: null

  it 'rpc call with an empty Array.', ->
    processRequest '[]'
      .should.become
        jsonrpc: '2.0'
        error:
          code: -32600
          message: 'Invalid Request'
        id: null
        
  it 'rpc call with an invalid Batch (but not empty).', ->
    processRequest '[1]'
      .should.become [{
        jsonrpc: '2.0'
        error:
          code: -32600
          message: 'Invalid Request'
        id: null
      }]
      
  it 'rpc call with invalid Batch.', ->
    processRequest '[1,2,3]'
      .should.become [
        {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
        {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
        {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
      ]

  it 'rpc call Batch.', ->
    processRequest '''[
      {"jsonrpc":"2.0","method":"test.sum","params":[1,2,4],"id":"1"},
      {"jsonrpc":"2.0","method":"test.notifies","params":[7]},
      {"jsonrpc":"2.0","method":"test.staticRepeat","params":["abc",2], "id": "2"},
      {"foo": "boo"},
      {"jsonrpc": "2.0","method": "test.foobar", "params": {"name": "myself"}, "id": "5"},
      {"jsonrpc":"2.0","method":"test.hello","id":"9"}
]'''
      .should.become [
        {jsonrpc: '2.0', result: 7, id: "1"}
        {jsonrpc: '2.0', result: "abcabc", id: "2"}
        {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
        {jsonrpc: '2.0', error: {code: -32601, message: 'Method not found'}, id: "5"}
        {jsonrpc: '2.0', result: "hello", id: "9"}
      ]

  it 'rpc call Batch (all notifications).', ->
    processNotify '''[
      {"jsonrpc":"2.0","method":"test.notifiesWithIntPair","params":{"left":111,"right":333}},
      {"jsonrpc":"2.0","method":"test.notifiesWithGenericPair","params":{"left":222,"right":444}}
]'''
      .should.become "ok"
