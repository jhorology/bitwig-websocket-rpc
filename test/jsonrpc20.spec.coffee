testUtils = require './lib/test-utils'
chai      = require 'chai'
assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

processRequest = testUtils.processRequest
processNotify = testUtils.processNotify
wsConnect= testUtils.wsConnect
wsClose= testUtils.wsClose

describe 'JSON-RPC 2.0 Specification, see https://www.jsonrpc.org/specification', ->
  for test in [{name: "One bye one connection", connect: off}, {name: "Static connection", connect: on}]
    describe test.name, ->
      ws = undefined
      if test.connect
        before ->
          wsConnect().then (conn) ->
            ws = conn
            @

        after ->
          wsClose ws
        
      it 'rpc call with positional parameters. id:1', ->
        processRequest ws, {jsonrpc: '2.0', method: 'test.sum', params: [1,2],id: 1}
          .should.become {jsonrpc: '2.0', result: 3, id: 1}

      it 'rpc call with positional parameters. id:2', ->
        processRequest ws, {jsonrpc: '2.0', method: 'test.sum', params: [1,2,3], id: 2}
          .should.become {jsonrpc: '2.0', result: 6, id: 2}

      it 'rpc call with named parameters. mapping params to POJO class. id:3', ->
        processRequest ws, {jsonrpc: '2.0', method: 'test.sum', params: {left: 1, right: 2}, id:3}
          .should.become {jsonrpc: '2.0', result: 3, id: 3}

      it 'rpc call with named parameters. mapping params to generic class. id:4', ->
        processRequest ws, {jsonrpc: '2.0', method: 'test.repeat', params: {left: 'abc', right: 3}, id: 4}
          .should.become {jsonrpc: '2.0', result: 'abcabcabc', id: 4}

      it 'a Notification.', ->
        processNotify ws, {jsonrpc: '2.0', method: 'test.consume', params: [999]}
          .should.become 'ok'

      it 'rpc call of non-existent method. id:6', ->
        processRequest ws, {jsonrpc: '2.0', method: 'foobar', id: 6}, on
          .should.become {jsonrpc: '2.0', error: {code: -32601, message: 'Method not found'}, id: 6}

      it 'rpc call with invalid JSON.', ->
        processRequest ws, '{"jsonrpc": "2.0", "method": "fooba", "params": "bar", "baz]', on
          .should.become {jsonrpc: '2.0', error: {code: -32700, message: 'Parse error'}, id: null}

      it 'rpc call with invalid Request object.', ->
        processRequest ws, {jsonrpc: '2.0', method: 1, params: 'bar'}, on
          .should.become {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}

      it 'rpc call Batch, invalid JSON.', ->
        processRequest ws, '''[
          {jsonrpc: '2.0', method: 'sum', params: [1,2,4], id: '1'},
          {jsonrpc: '2.0', method
    ]''', on
          .should.become {jsonrpc: '2.0', error: {code: -32700, message: 'Parse error'}, id: null}

      it 'rpc call with an empty Array.', ->
        processRequest ws, [], on
          .should.become {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}

      it 'rpc call with an invalid Batch (but not empty).', ->
        processRequest ws, [1], on
          .should.become [
            {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
          ]

      it 'rpc call with invalid Batch.', ->
        processRequest ws, [1,2,3], on
          .should.become [
            {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
            {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
            {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
          ]

      it 'rpc call Batch.', ->
        processRequest ws, [
          {jsonrpc: '2.0', method: 'test.sum',     params: [1,2,4],   id: '1'},
          {jsonrpc: '2.0', method: 'test.consume', params: [7]               },
          {jsonrpc: '2.0', method: 'test.repeat',  params: ['abc',2], id: '2'},
          {foo: 'boo'},
          {jsonrpc: '2.0', method: 'test.foobar',  params: {name: 'myself'}, id: '5'},
          {jsonrpc: '2.0', method: 'test.hello',                             id: '9'}
        ], on
          .should.become [
            {jsonrpc: '2.0', result: 7, id: '1'}
            {jsonrpc: '2.0', result: 'abcabc', id: '2'}
            {jsonrpc: '2.0', error: {code: -32600, message: 'Invalid Request'}, id: null}
            {jsonrpc: '2.0', error: {code: -32601, message: 'Method not found'}, id: '5'}
            {jsonrpc: '2.0', result: 'hello', id: '9'}
          ]

      it 'rpc call Batch (all notifications).', ->
        processNotify ws, [
          {jsonrpc:'2.0', method:'test.nop'},
          {jsonrpc:'2.0', method:'test.consume', params: [1.1, 2.2]},
          {jsonrpc:'2.0', method:'test.consume', params: [1.1, 2.2, 3.3]},
          {jsonrpc:'2.0', method:'test.consume', params: [1.1, 2.2, 3.3, 4.4]}
        ]
          .should.become 'ok'
