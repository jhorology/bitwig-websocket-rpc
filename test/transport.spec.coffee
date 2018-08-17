chai   = require 'chai'
utils  = require './test-utils'

assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

wsRequest = utils.wsRequest
wsNotify = utils.wsNotify
wsConnect= utils.wsConnect
wsClose= utils.wsClose
wait = utils.wait

$ =
  OK: 'ok'
  expectError: on
  waitMillis: 100
describe 'Transport Module', ->
  ws = undefined
  before ->
    wsConnect().then (conn) ->
      ws = conn
  after ->
    wsClose ws
    
  # it 'playing.markInterested() id:1', ->
  #   wsRequest ws, {jsonrpc: '2.0', method: 'transport.playing.markInterested'}
  #     .should.become $.OK
      
  it 'playing.subscrive()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'tp.playing.subscribe'}
      .should.become $.OK

  it 'stop()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'tp.stop'}
      .should.become $.OK
      
  it "wait(#{$.waitMillis})", ->
    wait $.waitMillis
    
  it 'playing.get id:1', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'tp.playing.get', id:1}
      .should.become   {jsonrpc: '2.0', result: false, id: 1}

  it 'playing() convert BooleanValue to primitive. id:2', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'tp.playing', id:2}
      .should.become   {jsonrpc: '2.0', result: false, id: 2}

  it 'play()', ->
    wsNotify ws, {jsonrpc:'2.0', method: 'tp.play'}
      .should.become $.OK
      
  it "wait(#{$.waitMillis})", ->
    wait $.waitMillis
    
  it 'playing.get() id:2', ->
    wsRequest ws, {jsonrpc: '2.0', method: 'tp.playing.get', id: 3}
      .should.become   {jsonrpc: '2.0', result: true, id: 3}

  it 'playing.unsubscrive()', ->
    wsNotify ws, {jsonrpc: '2.0', method: 'tp.playing.unsubscribe'}
      .should.become $.OK
