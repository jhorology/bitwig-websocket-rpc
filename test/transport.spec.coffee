chai   = require 'chai'
{BitwigClient}  = require '../lib/bitwig-websocket-rpc'


assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

describe 'Transport Module', ->
  ws = undefined
  before ->
    ws = new BitwigClient('ws://localhost:8887')
    await ws.connect()
    await ws.config
      useTransport: on
    , false, true
    await ws.call 'transport.stop'
    await ws.call 'transport.getPosition.set', [0]
    await ws.subscribe ['transport.isPlaying']
    
  after ->
    await ws.unsubscribe ['transport.isPlaying']
    await ws.close()
    
  it 'isPlaying.get() stop', ->
    ws.call('transport.isPlaying.get')
      .should.become false

  it 'isPlaying() convert BooleanValue to primitive.', ->
    ws.call('transport.isPlaying')
      .should.become false

  # it 'play()', ->
  #   ws.promise 'transport.isPlaying', false, 1000, (params) -> params[0]
  #     .should.become [true]
  #   ws.notify 'transport.play'
      
  # it 'isPlaying.get() start', ->
  #   ws.call('transport.isPlaying.get')
  #     .should.become true
