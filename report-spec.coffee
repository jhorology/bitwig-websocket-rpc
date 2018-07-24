testUtils = require './test/lib/test-utils'
beautify  = require 'js-beautify'
fs        = require 'fs'
processRequest = testUtils.processRequest

$ =
  reportFile: 'rpc-implementation-spec.json'
  
processRequest {jsonrpc: '2.0', method:'rpc.report', id:1}
 .then (response) ->
   if response.error
     console.error response.error
   else
     report = beautify (JSON.stringify response.result), indent_size: 2
     console.info report
     fs.writeFileSync $.reportFile, report
 , (err) ->
   console.error err 
