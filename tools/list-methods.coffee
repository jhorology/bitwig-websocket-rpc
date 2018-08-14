spec      = require '../rpc-implementation-spec.json'

methods = []

methods.push m for m in spec.methods when not ((m.method.startsWith 'test') or (m.method.startsWith 'rpc'))

maxLength = 0
maxLength = Math.max maxLength, m.method.length for m in methods;

console.info "[#{m.method.padEnd maxLength}]", m.expression for m in methods
