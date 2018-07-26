testUtils = require './test/test-utils'
beautify  = require 'js-beautify'
fs        = require 'fs'
wsRequest = testUtils.wsRequest

$ =
  reportFile: 'rpc-implementation-spec.json'
  
wsRequest undefined, {jsonrpc: '2.0', method:'rpc.report', id:1}
 .then (res) ->
   if res.error
     console.error res.error
   else
     report = beautify (JSON.stringify res.result), indent_size: 2
     console.info report
     fs.writeFileSync $.reportFile, report
 , (err) ->
   console.error err 
