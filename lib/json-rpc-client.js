//  bitwig-websocket-rpc
//   (c) 2019 Masaafumi Fujimaru
//    Realeased under the MIT license.
//
//  Portions are using rpc-websockets as reference:
//    https://github.com/elpheria/rpc-websockets
//    Released under MIT license.
//
const WebSocket = require('isomorphic-ws'),
      { EventEmitter } = require('events')

/**
 * Error class that indicates the failure of this client library.
 * @class
 */
class ClientError extends Error {
  /**
   * Constructor.
   * @class
   */
  constructor(message, layer, causedBy, data, options) {
    super(message)
    this.layer = layer
    this.causedBy = causedBy
    this.data = data
  }
  // TODO formatting, options, etc...
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
      { prop: 'id',           required: true,  assert: (v) => v !== null && (typeof v === 'number' || typeof v === 'string') }
    ]
  }, {
    id: 'error',
    props: [
      { prop: 'jsonrpc',      required: true,  assert: (v) => v === '2.0' },
      { prop: 'error',        required: true,  assert: (v) => v && typeof v === 'object' },
      { prop: 'id',           required: true,  assert: (v) => v === null || typeof v === 'number' || typeof v === 'string' }
    ]
  }
]

/**
 * JSON-RPC 2.0 over WebSocket Client.
 * @class
 */
class Client extends EventEmitter {
  /**
   * Returns the default options for constructor.
   * @public
   * @property
   * @static
   * @property
   * @return {Object} The default options.
   */
  static get defaultOptions() {
    return {
      connectTimeout: 5000,
      responseTimeout: 5000,
      chkArgs: true,   // strict checking tx arguments
      chkMessage: true, // strict checking rx message
      errorWithNullId: 'REJECT_FIRST', // choice of 'JUST_REPORT', 'REJECT_FIRST' or 'REJECT_ALL'
      debugLog: (objs) => console.log.apply(undefined, objs),
      traceLog: (objs) => console.log.apply(undefined, objs)
    }
  }

  /**
   * Returns the all aveilable events.
   * @public
   * @property
   * @return {Array} The all aveilable events.
   */
  get events() {
    return [
      'open',
      'close',
      'error',
      '_message'
    ]
  }

  /**
   * Returns a newly created JSON-RPC client object.
   * new Client(url [,protocols[, options]])
   * @public
   * @constructor
   * @param {String} url
   * @param {Array} protocols an array of protocol strings
   * @param {Object} options an options for this instance
   * @see https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/WebSocket
   */
  constructor(url, protocols = undefined, options = {}) {
    // new Client(url [,protocols[, options]])
    if (arguments.length === 2 && !Array.isArray(protocols) &&
        typeof protocols === 'object') {
      options = protocols
      protocols = undefined
    }
    super()
    this._options = Object.assign({}, Client.defaultOptions, options)
    this._url = url
    this._protocols = protocols
    this._observers = new Map()
  }

  /**
   * The property returns the current state of the WebSocket connection.
   *   0: 'CONNECTING'
   *   1: 'OPEN'
   *   2: 'CLOSING'
   *   3: 'CLOSED'
   * @public
   * @property
   * @see https://developer.mozilla.org/ja/docs/Web/API/WebSocket/readyState
   * @return {Number} The state of the WebSocket connection
   */
  get readyState() {
    return this._ws ? this._ws.readyState : WebSocket.CLOSED
  }

  /**
   * @public
   * @property
   * @return {boolean}
   */
  get isConnecting() {
    return this.readyState === WebSocket.CONNECTING
  }

  /**
   * @public
   * @property
   * @return {boolean}
   */
  get isOpen() {
    return this.readyState === WebSocket.OPEN
  }

  /**
   * @public
   * @property
   * @return {boolean}
   */
  get isClosing() {
    return this.readyState === WebSocket.CLOSING
  }

  /**
   * @public
   * @property
   * @return {boolean}
   */
  get isClosed() {
    return this.readyState === WebSocket.CLOSED
  }

  /**
   * Connect to server.
   * @return {Promise} = Resolve value is undefined
   * @public
   */
  async connect(timeout = this._options.connectTimeout) {
    try {
      this._trace('connect() timeout:', timeout)
      this._onInit()
      await this.promise('open', true, timeout)
    } catch (err) {
      if (this._ws) {
        await this.close()
      }
      throw new ClientError('Could not connect to server.', 'TRANSPORT', err)
    }
  }

  /**
   * Send a notification to server.
   *
   * @public
   * @method
   * @param method {String}  A String containing the name of the method to be invoked.
   *
   * @param params {Array|Object} optional  A Structured value that holds the parameter
   *   values to be used during the invocation of the method.
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
   * @see https://www.jsonrpc.org/specification#notification
   */
  notify(method, params) {
    this.send(JSON.stringify(this._createRequest(method, params, true)))
  }

  /**
   * Send a request to server and return the result.
   *
   * @public
   * @method
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
   * @param timeout {Number} optional - timeout sec for waiting response
   * @return {Promise} = Resolve {Object} the result of server response.
   * @see https://www.jsonrpc.org/specification#request_object
   */
  call(method, params, timeout = this._options.responseTimeout) {
    return new Promise((resolve, reject) => {
      var request
      try {
        request = this._createRequest(method, params)
        this.send(JSON.stringify(request))
      } catch (err) {
        reject(err)
        return
      }
      this._addResponseObserver(request, resolve, reject, timeout)
    })
  }

  /**
   * Send a batch request to server and return the result.
   *
   * @public
   * @method
   * @param builder {Function} - A function to build batch request.
   * @return {Promise} Resolve {Object} result of server response.
   *   - All requests are notification
   *    const result = await ws.batchRequest((context) => {
   *       context.notify('test.nop')
   *       context.notify('test.nop')
   *    })
   *    result -> undefined
   *
   *   - Some requests are call
   *    const result = await ws.batchRequest((context) => {
   *       context.call('test.echo', ['yahoo'])
   *       context.notify('test.nop')
   *       context.call('test.echo', ['hello'])
   *    })
   *    result -> ['yahoo', 'hello]
   * @see https://www.jsonrpc.org/specification#batch
   */
  batchRequest(builder, timeout = this._options.responseTimeout) {
    return new Promise((resolve, reject) => {
      const requests = [],
            context = {
              notify: (method, params) => {
                requests.push(this._createRequest(method, params, true))
              },
              call: (method, params) => {
                requests.push(this._createRequest(method, params))
              }
            }
      try {
        builder(context)
      } catch (err) {
        reject(err)
        return
      }
      if (!requests.length) {
        reject(new ClientError(
          'Invalid request. Empty batch is not allowed.',
          'JSON-RPC', undefined, requests))
        return
      }
      try {
        this.send(JSON.stringify(requests))
      } catch (err) {
        reject(err)
        return
      }
      const promises = requests
        .filter(request => request.id !== undefined)
        .map(request => new Promise((resolve, reject) =>
          this._addResponseObserver(request, resolve, reject, timeout)))
      if (promises.length === 0) {
        // all requests are notification
        resolve()
      } else if (promises.length === 1) {
        // only 1 request is call method.
        promises[0]
          .then(result => resolve([result]))
          .catch(reject)
      } else if (promises.length > 1) {
        Promise.all(promises)
          .then(resolve)
          .catch(reject)
      }
    })
  }

  /**
   * Enqueues the specified data to be transmitted to the server over the WebSocket connection,
   * increasing the value of bufferedAmount by the number of bytes needed to contain the data.
   * If the data can't be sent (for example, because it needs to be buffered but the buffer is full),
   * the socket is closed automatically.
   * @public
   * @method
   * @param data {String} - The data to send to the server..
   * @see https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/send
   */
  send(data) {
    try {
      this._trace('send() data:', data)
      this._ws.send(data)
    } catch (err) {
      throw new ClientError('Could not send messag.', 'TRANSPORT', err, data)
    }
  }

  /**
   * Closes the WebSocket connection or connection attempt, if any.
   * If the connection is already CLOSED, this method does nothing.
   * @public
   * @method
   * @param code {Number} - a message to be sent.
   * @param reason {String} - a message to be sent.
   * @return {Promise} resolve value is undefined.
   * @see https://developer.mozilla.org/ja/docs/Web/API/WebSocket/close
   * @see https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent#Status_codes
   */
  async close(code = 1000, reason = undefined) {
    if (this._ws && this._ws.readyState < WebSocket.CLOSED) {
      if (this._ws.readyState === WebSocket.CONNECTING) {
        await this.promise('open', true, this._options.connectTimeout)
        this._ws.close(code, reason)
      } else if (this._ws.readyState < WebSocket.CLOSING) {
        this._ws.close(code, reason)
      }
      await this.promise('close', true, this._options.connectTimeout)
    }
  }

  /**
   * @method promise - Promisify waiting event.
   * @param {String} event - An event name
   * @param {boolean} [once = true]  - call matcher only once.
   * @param {Number} [timeout = 3000] - the timeout millis.
   *   0 = infinite wait until match params.
   * @param {Function} [matcher = params => true] - the function to match params.
   * @return {Promise<any>} - resolve event params.
   * @public
   */
  promise(event, once = true, timeout = 3000, matcher = params => true) {
    return new Promise((resolve, reject) => {
      var onEvent, timerId
      const clean = () => {
        if (!once) {
          this.off(event, onEvent)
        }
        if (typeof timerId !== 'undefined') {
          clearTimeout(timerId)
        }
      }
      onEvent = (event, ...params) => {
        const match = matcher(params)
        if (match) {
          clean()
          resolve(params)
        } else {
          if (once) {
            clean()
            reject(new ClientError('Unmatch event params.', 'EVENT', undefined, params))
          }
        }
      }
      if (timeout) {
        timerId = setTimeout(() => {
          clean()
          reject(new ClientError(`Timeout error while waiting event [${event}].`, 'EVENT'))
        }, timeout)
      }
      if (once) {
        this.once(event, onEvent)
      } else {
        this.on(event, onEvent)
      }
    })
  }

  // ---> life-cylce of WebSocket connection

  _onInit() {
    this._observers.clear()
    this.removeAllListeners()
    this._ws = new WebSocket(this._url, this._protocols)
    this._ws.onopen = this._onOpen.bind(this)
    this._ws.onclose = this._onClose.bind(this)
    this._ws.onmessage = this._onMessage.bind(this)
    this._ws.onerror = this._onError.bind(this)
    this._id = 0
  }

  _onDestroy() {
    this.removeAllListeners()
    this._observers.clear()
    if (this._ws) {
      this._ws.onopen = undefined
      this._ws.onclose = undefined
      this._ws.onmessage = undefined
      this._ws.onerror = undefined
      delete this._ws
    }
    this._id = 0
  }

  // ---> JSON-RPC handlers

  /**
   * @protected
   * @method _onNotification - Handles notification message.
   * @param {string} notification -
   */
  _onNotification(notification) {
    // inherited class should override this method.
    this.emit('error', new ClientError(
      'Recieved Invalid message.',
      'JSON-RPC', undefined, notification))
  }

  _onJsonRpcResult(result) {
    const observer = this._observers.get(result.id)
    if (!observer) {
      this.emit('error', new ClientError(
        'Recieved message has unknown id.',
        'JSON-RPC', undefined, result))
    }
    observer.resolve(result.result)
    this._observers.delete(result.id)
  }

  _onJsonRpcError(error) {
    const observer = error.id === null
      ? this._observers.values().next().value
      : this._observers.get(error.id)
    if (observer) {
      observer.reject(
        new ClientError(
          'Recieved error response from server.',
          'JSON-RPC', undefined, error))
      this._observers.delete(observer.id)
    }
  }

  // ---> WebSocket handlers

  _onOpen(e) {
    this._trace('WebSocket.onopen() readyState:', this.readyState)
    this.emit('open')
  }

  _onClose(e) {
    this._trace('WebSocket.onclose() code:', e.code, ' reason:', e.reason, 'wasClean:', e.wasClean)
    this.emit('close', {
      code: e.code,
      reason: e.reason,
      wasClean: e.wasClean
    })
    this._onDestroy()
  }

  _onMessage(e) {
    this._trace('WebSocket.onmessage() data:', e.data)
    // event for server test, debugging
    this.emit('_message', e.data)
    var response
    try {
      response =  JSON.parse(e.data)
    } catch (err) {
      this.emit('error', new ClientError(
        'JSON parse error.', 'JSON-RPC', err, e.data))
      return
    }
    if (Array.isArray(response)) {
      response.forEach((res) => this._processResponse(res))
    } else {
      this._processResponse(response)
    }
  }

  _onError(e) {
    this._trace('WebSocket.onerror() err:', e)
    this.emit('error', e)
  }

  // ---> routines

  /**
   * @protected
   * @method _debug - debug logging
   * @param {...any} data
   */
  _debug(...data) {
    this._options.debugLog &&
      this._options.debugLog(data)
  }

  /**
   * @protected
   * @method _trace - trace logging
   * @param {...any} data
   */
  _trace(...data) {
    this._options.traceLog &&
      this._options.traceLog(data)
  }

  /**
   * @private
   * @method _createRequest - create an JSON-RPC 2.0 request object.
   * @param {string} method - the name of rpc method.
   * @param {Array<any> | object} params
   * @param {boolean} notify - request is notification, or not.
   */
  _createRequest(method, params, notify) {
    this._options.chkArgs &&
      this._chkArgs(method, params)
    const id = notify ? undefined : (this._id++) & 0xffff,
          request = {
            jsonrpc: '2.0',
            method: method
          }
    // params MAY be omitted.
    if (typeof params !== 'undefined') {
      request.params = params
    }
    if (typeof id !== 'undefined') {
      request.id = id
    }
    return request
  }

  _processResponse(response) {
    const rxMsgDef = rxMessages.find(d => {
      return this._options.chkMessage
        ? d.props.every(p => {
          const v = response[p.prop]
          if (p.required) {
            return typeof v !== 'undefined' && p.assert(v)
          } else {
            return typeof v === 'undefined' || p.assert(v)
          }
        })
        : response[d.id]
    })
    if (!rxMsgDef) {
      this.emit(
        'error', new ClientError(
          'Recieved Invalid message.',
          'JSON-RPC', undefined, response))
      return
    }
    switch (rxMsgDef.id) {
    case 'notification':
      this._onNotification(response)
      break
    case 'result':
      this._onJsonRpcResult(response)
      break
    case 'error':
      this._onJsonRpcError(response)
      break
    }
  }

  _chkArgs(method, params) {
    if (!method || typeof method !== 'string') {
      throw new ClientError(
        'Invalid argument: method should be string.',
        'JSON-RPC', undefined, {
          method: method,
          params: params
        })
    }
    if (params === null) {
      throw new ClientError(
        'Invalid argument: null params are not allowed.',
        'JSON-RPC', undefined, {
          method: method,
          params: params
        })
    }
    if (!(typeof params === 'undefined' ||
        Array.isArray(params) ||
        typeof params === 'object')) {
      throw new ClientError(
        'Invalid argument: params should be array or object.',
        'JSON-RPC', undefined, {
          method: method,
          params: params
        })
    }
  }

  _addResponseObserver(request, resolve, reject, timeout) {
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
  }
}

module.exports = {
  ClientError: ClientError,
  Client: Client
}
