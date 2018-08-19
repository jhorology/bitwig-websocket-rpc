spec      = require '../rpc-implementation-spec.json'

methods = []

methods.push m for m in spec.methods when not ((m.method.startsWith 'test') or (m.method.startsWith 'rpc'))

maxLength = 0
maxLength = Math.max maxLength, m.method.length for m in methods;

console.info "[#{m.method.padEnd maxLength}]", m.expression for m in methods

console.info ""
console.info "total:", methods.length, "methods and", spec.events.length, "events.", "max length of method name:", maxLength
console.info ""
console.info "method names longer than (maxlength - 5):"
console.info ""
console.info "[#{m.method.padEnd maxLength}]", m.expression for m in methods when m.method.length > (maxLength - 5)
