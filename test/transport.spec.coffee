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
    # wait for restart extension
  after ->
    await ws.close()
    
  it 'subscribe()', ->
    ws.subscribe ['transport.getPlaying']
      .should.become
        'transport.getPlaying': 'ok'

  # it 'stop()', ->
  #   ws.promise 'transport.getPlaying', false, 1000, (params) -> !params[0]
  #     .should.become [false]
  #   ws.notify 'transporttp.stop'
      
  # it 'getPlaying.get()', ->
  #   ws.call('transport.getPlaying.get')
  #     .should.become false

  # it 'getPlaying() convert BooleanValue to primitive.', ->
  #   ws.call('transport.getPlaying')
  #     .should.become false

  # it 'play()', ->
  #   ws.promise 'transport.getPlaying', false, 1000, (params) -> params[0]
  #     .should.become [true]
  #   ws.notify 'transport.play'
      
  # it 'playing.get()', ->
  #   ws.call('transport.getPlaying.get')
  #     .should.become true

  # it 'unsubscribe()', ->
  #   ws.unsubscribe ['transport.getPlaying']
  #     .should.become
  #       'transport.getPlaying': 'ok'
