testUtils = require './lib/test-utils'
chai      = require 'chai'
assert = chai.assert
chai.use require 'chai-as-promised'
chai.should()

processRequest = testUtils.processRequest
processNotify = testUtils.processNotify

describe 'Transport Module', ->
  it 'play() then isPlaying() id:1', ->
    processRequest [
      {jsonrpc:'2.0', method:'transport.stop'}
      {jsonrpc:'2.0', method:'transport.isPlaying', id:1}
    ]
      .should.become
        jsonrpc: '2.0'
        result: true
        id: 1
        
  it 'stop() then isPlaying() id:2', ->
    processRequest [
      {jsonrpc:'2.0', method:'transport.stop'}
      {jsonrpc:'2.0', method:'transport.isPlaying', id:2}
    ]
      .should.become [
        {jsonrpc: '2.0',  result: false , id: 2}
      ]
