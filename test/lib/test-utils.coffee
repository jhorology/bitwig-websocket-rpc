WebSocket = require 'ws'
_         = require 'underscore'
chai      = require 'chai'
assert = chai.assert

$ =
  printMessage: off

module.exports =
# process promised request
# @req request message
# -----------------------------
  processRequest: (req) ->
    req = JSON.stringify req unless _.isString req
    return new Promise (resolve, reject) ->
      ws = new WebSocket('ws://localhost:8887')
      res = err = undefined
      ws.on 'open', ->
        ws.once 'message', (message) ->
          console.info "# <-- #{message}" if $.printMessage
          res = message
          ws.close()
        ws.send req
        console.info "# --> #{req}" if $.printMessage
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
  processNotify: (req, id) ->
    req = JSON.stringify req unless _.isString req
    return new Promise (resolve, reject) ->
      ws = new WebSocket('ws://localhost:8887')
      res = err = undefined
      echo = '{"jsonrpc":"2.0","method":"test.echo","params":["ok"],"id":999}'
      ws.on 'open', ->
        ws.once 'message', (message) ->
          console.info "# <-- #{message}" if $.printMessage
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
        console.info "# --> #{req}"  if $.printMessage
        # send echo message
        ws.send echo
        console.info "# --> #{echo}"  if $.printMessage
      ws.on 'error', (error) ->
        err = error
        ws.close()
      ws.on 'close', ->
        if (err)
          reject err
        else
          resolve res
