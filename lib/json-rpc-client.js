const WebSocket = require('isomorphic-ws'),
      { EventEmitter } = require('events')

class ClientError extends Error {
  constructor(message, layer, cause, data) {
    super(message)
    this.layer = layer
    this.cause = cause
    this.data = data
  }
}

/* eslint no-multi-spaces: 0 */
const rxMessages = [
  {
    // compatible with rpc-websockets
    id: 'notification',
    props: [
      { prop: 'notification', required: true,  assert: (v) => v && typeof v === 'string' },
      { prop: 'params',       required: false, assert: (v) => v && (typeof Array.isArray(v) || typeof v === 'object') }
    ]
  }, {
    id: 'result',
    props: [
      { prop: 'jsonrpc',      required: true,  assert: (v) => v === '2.0' },
      { prop: 'result',       required: false, assert: (v) => true },
      { prop: 'id',           required: true,  assert: (v) => v && (typeof v === 'number' || typeof v === 'string') }
    ]
  }, {
    id: 'error',
    pops: [
      { prop: 'jsonrpc',      required: true,  assert: (v) => v === '2.0' },
      { prop: 'error',        required: true,  assert: (v) => v && typeof v === 'object' },
      { prop: 'id',           required: true,  assert: (v) => v === null || typeof v === 'number' || typeof v === 'string' }
    ]
  }
]

// const readyStates = {
//   0: 'CONNECTING',
//   1: 'OPEN',
//   2: 'CLOSING',
//   3: 'CLOSED'
// }

/**
 * @class
 * JSON-RPC 2.0 over WebSocket Client.
 */
class Client extends EventEmitter {
  /**
   * Returns the default options for constructor.
   * @static
   * @property
   * @return {Object} The default options.
   */
  static get defaultOpstions() {
    return {
      debug: true,
      chkArgs: true,   // strict checking tx arguments
      chkMessage: true, // strict checking rx message
      errorWithNullId: 'REJECT_FIRST' // choice of 'JUST_REPORT', 'REJECT_FIRST' or 'REJECT_ALL'
    }
  }

  /**
   * @constructor
   * @return {Number} The state of the WebSocket connection
   */
  constructor(url, protocols = undefined, options = {}) {
    super()
    this._url = url
    this._protocols = protocols
    this._options = Object.assign({}, Client.defaultOptions, options)
    this._observers = new Map()
  }

  /**
   * The property returns the current state of the WebSocket connection.
   *   0: 'CONNECTING'
   *   1: 'OPEN'
   *   2: 'CLOSING'
   *   3: 'CLOSED'
   * @see https://developer.mozilla.org/ja/docs/Web/API/WebSocket/readyState
   * @return {Number} The state of the WebSocket connection
   */
  get readyState() {
    return this._ws.readyState
  }

  async connect(timeout = 5000) {
    this._init()
    try {
      await this._waitEvent('open', timeout)
      console.info('connect()', this.readyState)
    } catch (err) {
      this._destroy()
      throw new ClientError('Timeout error for waiting connect.', 'TRANSPORT')
    }
  }

  /**
   * Send a notification to server.
   *
   * @see https://www.jsonrpc.org/specification#notification
   *
   * @param method {String} - A String containing the name of the method to be invoked.
   *
   * @param params {Array|Object} optional - A Structured value that holds the parameter values
   *               to be used during the invocation of the method.
   *
   *   - by-position: params MUST be an Array, containing the values in the Server
   *                  expected order.
   *
   *   - by-name: params MUST be an Object, with member names that match the Server
   *              expected parameter names. The absence of expected names MAY result
   *              in an error being generated. The names MUST match exactly, including
   *              case, to the method's expected parameters.
   *
   *   - no params: params MUST be 'undefined' or empty array.
   */
  notify(method, params) {
    if (this._options.chkArgs) {
      const error = this._checkTxMethodAndParams(method, params)
      if (error) {
        throw error
      }
    }
    this.send(this._createMessage(method, params))
  }

  /**
   * Send a request to server and return the result.
   *
   * @see https://www.jsonrpc.org/specification#request_object
   *
   * @param method {String} - A String containing the name of the method to be invoked.
   *
   * @param params {Array|Object} - A Structured value that holds the parameter values
   *               to be used during the invocation of the method.
   *
   *   - by-position: params MUST be an Array, containing the values in the Server
   *                  expected order.
   *
   *   - by-name: params MUST be an Object, with member names that match the Server
   *              expected parameter names. The absence of expected names MAY result
   *              in an error being generated. The names MUST match exactly, including
   *              case, to the method's expected parameters.
   *
   * @param timeout {Array|Object} - A Structured value that holds the parameter values
   *                to be used during the invocation of the method.
   * @return {Promise} = Resolve {Object} result of server response.
   */
  call(method, params, timeout = 5000) {
    return new Promise((resolve, reject) => {
      var request
      try {
        request = this._createRequest(method, params, false, reject)
      } catch (err) {
        reject(err)
        return
      }

      try {
        this.send(JSON.stringify(request))
      } catch (err) {
        reject(new ClientError(
          'Could not send messag.',
          'TRANSPORT', err, request))
        return
      }

      const timerId = setTimeout(() => {
        this._observers.delete(request.id)
        reject(new ClientError(
          'Timeout error for waiting result.',
          'JSON-RPC', undefined, request))
      }, timeout)

      this._observers.set(request.id, {
        id: request.id,
        resolve: result => {
          clearTimeout(timerId)
          resolve(result)
        },
        reject: err => {
          clearTimeout(timerId)
          reject(err)
        }
      })
    })
  }

  async batch(builder) {
    const requests = [],
          context = {
            notify: (method, params) => {
              requests.push(this._createRequest(method, params, true))
            },
            call: (method, params) => {
              requests.push(this._createRequest(method, params))
            }
          }
    builder(context)
    console.log(requests)
  }

  /**
   * Enqueues the specified data to be transmitted to the server over the WebSocket connection,
   * increasing the value of bufferedAmount by the number of bytes needed to contain the data.
   * If the data can't be sent (for example, because it needs to be buffered but the buffer is full),
   * the socket is closed automatically.
   * @see https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/send
   * @param data {String} - The data to send to the server..
   */
  send(data) {
    try {
      this._options.debug && console.log('send(msg)', data)
      this._ws.send(data)
    } catch (err) {
      throw new ClientError('Could not send messag.', 'TRANSPORT', err, data)
    }
  }

  /**
   * Closes the WebSocket connection or connection attempt, if any.
   * If the connection is already CLOSED, this method does nothing.
   * @see https://developer.mozilla.org/ja/docs/Web/API/WebSocket/close
   * @see https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent#Status_codes
   * @param code {Number} - a message to be sent.
   * @param reason {String} - a message to be sent.
   */
  close(code = 1000, reason = undefined) {
    this._ws.close(code, reason)
  }

  // ---> life-cylce of WebSocket connection

  _init() {
    this._observers.clear()
    this.removeAllListeners()
    const ws = new WebSocket(this._url, this._protocols)
    ws.onopen = this._onOpen.bind(this)
    ws.onclose = this._onClose.bind(this)
    ws.onmessage = this._onMessage.bind(this)
    ws.onerror = this._onError.bind(this)
    this._ws = ws
    this._id = 0
  }

  _destroy() {
    this.removeAllListeners()
    this._observers.clear()
    this._ws.onopen = undefined
    this._ws.onclose = undefined
    this._ws.onmessage = undefined
    this._ws.onerror = undefined
    delete this._ws
    this._id = 0
  }

  // ---> JSON-RPC handlers

  _onNotification(jsonrpc) {
    // inherited class should override this method.
    this.emit('error', new ClientError(
      'Recieved Invalid message.',
      'JSON-RPC', undefined, jsonrpc))
  }

  _onJsonRpcResult(jsonrpc) {
    const observer = this._observers.get(jsonrpc.id)
    if (!observer) {
      this.emit('error', new ClientError(
        'Recieved message has unknown id.',
        'JSON-RPC', undefined, jsonrpc))
    }
    observer.resolve(jsonrpc.result)
    this._observers.delete(jsonrpc.id)
  }

  _onJsonRpcError(jsonrpc) {
    const observer = jsonrpc.id === null
      ? this._observers.values().next().value
      : this._observers.get(jsonrpc.id)
    if (observer) {
      observer.reject(
        new ClientError(
          'Recieved error response from server.',
          'JSON-RPC', undefined, jsonrpc))
      this._observers.delete(observer.id)
    }
  }

  // ---> WebSocket handlers

  _onOpen(e) {
    console.log('_onOpen()', this.readyState)
    this.emit('open')
  }

  _onClose(e) {
    this._options.debug && console.log('_onClose()', e)
    this.emit('close', {
      code: e.code,
      reason: e.reason,
      wasClean: e.wasClean
    })
    this._destroy()
  }

  _onMessage(e) {
    this._options.debug && console.log('_onMessage()', e)
    var jsonrpc
    try {
      jsonrpc =  JSON.parse(e.data)
    } catch (err) {
      this.emit('error', new ClientError(`JSON parse error. message:[${e.data}]`, 'JSON-RPC', err))
      return
    }
    if (Array.isArray(jsonrpc)) {
      jsonrpc.forEach(this._processMessage)
    } else {
      this._processMessage(jsonrpc)
    }
  }

  _onError(e) {
    this._options.debug && console.log('_onError()', e);
    this.emit('error', new ClientError('WebSocket.onerror', 'TRANSPORT', e))
  }

  _createRequest(method, params, notify) {
    if (this._options.chkArgs) {
      const error = this._checkTxMethodAndParams(method, params)
      if (error) throw error
    }
    const id = notify ? undefined : (this._id++) & 0xffff,
          jsonrpc = {
            jsonrpc: '2.0',
            method: method
          }
    // params MAY be omitted.
    if (typeof params !== 'undefined') {
      jsonrpc.params = params
    }
    if (typeof id !== 'undefined') {
      jsonrpc.id = id
    }
    return jsonrpc
  }

  _processMessage(jsonrpc) {
    const msgId = this._validateRxMessage(jsonrpc)
    if (msgId) {
      switch (msgId) {
      case 'notification':
        this._onNotification(jsonrpc)
        break
      case 'result':
        this._onJsonRpcResult(jsonrpc)
        break
      case 'error':
        this._onJsonRpcError(jsonrpc)
        break
      }
    }
  }

  _checkTxMethodAndParams(method, params) {
    if (!method || typeof method !== 'string') {
      return new ClientError(
        'Argumet error: method should be string.',
        'JSON-RPC')
    }
    if (params === null) {
      return new ClientError(
        'Argument error: null params are not allowed.',
        'JSON-RPC')
    }
    if (!(Array.isArray(params) || typeof params !== 'object')) {
      return new ClientError(
        'Argument error: params should be array or strng.',
        'JSON-RPC')
    }
    return undefined
  }

  _validateRxMessage(jsonrpc) {
    const rxMsgDef = rxMessages.find(d => {
      return this._options.chkMessage
        ? d.props.every(p => {
          const v = jsonrpc[p.prop]
          if (p.required) {
            return typeof v !== 'undefined' && p.assert(v)
          } else {
            return typeof v === 'undefined' || p.assert(v)
          }
        })
        : jsonrpc[d.id]
    })
    if (!rxMsgDef) {
      this.emit(
        'error', new ClientError(
          'Recieved Invalid message.',
          'JSON-RPC', undefined, jsonrpc))
    }
    return rxMsgDef.id
  }

  _waitEvent(event, timeout) {
    return new Promise((resolve, reject) => {
      var onEvent, timerId
      onEvent = () => {
        clearTimeout(timerId)
        resolve()
      }
      timerId = setTimeout(() => {
        this.removeListener(event, onEvent)
        reject(new Error(`Timeout error for waiting event [${event}].`))
      }, timeout)
      this.once(event, onEvent)
      console.log('_waitEvent()', timeout)
    })
  }
}

module.exports = {
  ClientError: ClientError,
  Client: Client
}
