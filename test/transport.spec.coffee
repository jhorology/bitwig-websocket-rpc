chai   = require 'chai'
utils  = require './test-utils'

assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

wsRequest = utils.wsRequest
wsNotify = utils.wsNotify
wsConnect= utils.wsConnect
wsClose= utils.wsClose

$ =
  OK: 'ok'
  expectError: on

describe 'Transport Module', ->
  ws = undefined
  before ->
    wsConnect().then (conn) ->
      ws = conn
      @
  after ->
    wsClose ws
    
  # it 'isPlaying.markInterested() id:1', ->
  #   wsRequest ws, {jsonrpc: '2.0', method: 'transport.isPlaying.markInterested'}
  #     .should.become $.OK
      
  it 'isPlaying.subscrive()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'transport.isPlaying.subscribe'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
      
  it 'stop()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'transport.stop'}
      .should.become $.OK
      
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
      
  it 'isPlaying.get id:1', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'transport.isPlaying.get', id:1}
      .should.become   {jsonrpc: '2.0', result: false, id: 1}

  it 'isPlaying() convert BooleanValue to primitive. id:2', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'transport.isPlaying', id:2}
      .should.become   {jsonrpc: '2.0', result: false, id: 2}

  it 'play()', ->
    wsNotify ws, {jsonrpc:'2.0', method: 'transport.play'}
      .should.become $.OK
      
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
  it 'nextTick()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'rpc.nextTick'}
      .should.become $.OK
      
  it 'isPlaying.get() id:2', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'transport.isPlaying.get', id: 3}
      .should.become   {jsonrpc: '2.0', result: true, id: 3}

  it 'isPlaying.unsubscrive()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'transport.isPlaying.unsubscribe'}
      .should.become $.OK
