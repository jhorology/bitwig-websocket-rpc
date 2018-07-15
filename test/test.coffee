chai      = require 'chai'
WebSocket = require 'ws'
beautify  = require 'js-beautify'
_         = require 'underscore'

assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()
  
# functions
json = (data) ->
  data = JSON.stringify data if !_.isString data and _.isObject data
  ret = beautify data, indent_size: 2
  console.info ret
  ret

processRequest = (req) ->
  return new Promise (resolve, reject) ->
    ws = new WebSocket('ws://localhost:8887')
    res = err = undefined
    ws.on 'open', ->
      ws.once 'message', (message) ->
        console.info "# <-- #{message}"
        res = message
        ws.close()
      ws.send req
      console.info "# --> #{req}"
    ws.on 'error', (error) ->
      err = error
      ws.close()
    ws.on 'close', ->
      if (err)
        reject err
      else
        try
          data = JSON.parse res
          resolve data
        catch error
          reject error

processNotify = (req) ->
  return new Promise (resolve, reject) ->
    ws = new WebSocket('ws://localhost:8887')
    res = err = undefined
    echo = '{"jsonrpc":"2.0", "method":"test.echo", "params":"ok", "id": 1}'
    ws.on 'open', ->
      ws.once 'message', (message) ->
        console.info "# <-- #{message}"
        try
          ans = JSON.parse message
          assert.deepEqual ans,
            jsonrpc: "2.0"
            result: "ok"
            id: 1
          res = "ok"
        catch error
          err = error
        ws.close()
      # send notify message
      ws.send req
      console.info "# --> #{req}"
      # send echo message
      ws.send echo
      console.info "# --> #{echo}"
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
    processRequest '{"jsonrpc":"2.0", "method":"test.sum", "params":[1, 2], "id": 1}'
      .should.become
        jsonrpc: '2.0'
        result: 3
        id: 1
    
  it 'rpc call with positional parameters. id:2', ->
    processRequest '{"jsonrpc":"2.0", "method":"test.sum", "params":[1, 2, 3], "id": 2}'
      .should.become
        jsonrpc: '2.0'
        result: 6
        id: 2
        
  it 'rpc call with named parameters. id:3', ->
    processRequest '{"jsonrpc":"2.0", "method":"test.sum", "params":{"left":1, "right":2}, "id": 3}'
      .should.become
        jsonrpc: '2.0'
        result: 3
        id: 3
