//  bitwig-websocket-rpc
//   (c) 2019 Masaafumi Fujimaru
//    Released under the MIT license.
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
      { prop: 'result',       required: true,  assert: (v) => v !== undefined },
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
      chkArgs: true,   // strict checking tx method, params
      chkMessage: true, // strict checking rx message
      errorWithNullId: 'DONT_CARE', // choice of 'DONT_CARE', 'REJECT_FIRST' or 'REJECT_ALL'
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
      '_error',
      '_message',
      '_WebSocket.error'
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

  get readyStateAsString() {
    const state = this.readyState
    return state >= 0 && state < 4
      ? [
        '0: CONNECTING',
        '1: OPEN',
        '2: CLOSING',
        '3: CLOSED'
      ][state]
      : '' + state + ': unknwon state'
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
   * @public
   * @property
   * @return {string}
   */
  get url() {
    return this._url
  }

  /**
   * @public
   * @property
   * @return {string}
   */
  set url(url) {
    this._url = url
  }

  /**
   * Connect to server.
   * @public
   * @method
   * @param {string} [url]
   * @param {Number} [retry]
   * @param {Number} [timeout]
   * @return {Promise} = Resolve value is undefined
   * @public
   */
  async connect(retry = 0, timeout = this._options.connectTimeout) {
    this._trace('connect() timeoutMillis:', timeout, 'retry count:', retry)
    let connected = false
    do {
      try {
        if (!this.isClosed) {
          throw new ClientError('Could not connect to server: Illegal ready state..', 'TRANSPORT', undefined, {
            readyState: this.readyStateAsString,
            url: this.url,
            timeout: timeout
          })
        }
        this._onInit()
        await this._internalPromise('open', timeout)
        connected = true
      } catch (err) {
        if (this._ws) {
          try {
            await this.close()
          } catch (e) {
            // ignore
          }
        }
        if (retry) {
          this._trace('connect() attempting to reconnect. remaining retry count:', retry, 'error caused by:', err)
          await new Promise(resolve => setTimeout(resolve, 1000))
        } else {
          if (err instanceof ClientError) {
            throw err
          } else {
            throw new ClientError('Could not connect to server.', 'TRANSPORT', err, {
              readyState: this.readyStateAsString,
              url: this.url,
              timeout: timeout
            })
          }
        }
      } finally {
        retry--
      }
    } while (retry >= 0 && !connected)
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
    this._trace('send() data:', data)
    if (!this.isOpen) {
      throw new ClientError('Could not connect to server: Illegal ready state.', 'TRANSPORT', undefined, {
        readyState: this.readyStateAsString,
        message: data
      })
    }
    try {
      this._ws.send(data)
    } catch (err) {
      throw new ClientError('Could not send message.', 'TRANSPORT', err, data)
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
        await this._internalPromise('open')
        this._ws.close(code, reason)
      } else if (this._ws.readyState < WebSocket.CLOSING) {
        this._ws.close(code, reason)
      }
      await this._internalPromise('close')
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
  promise(event, once = true, timeout = this._options.responseTimeout, matcher = params => true) {
    return new Promise((resolve, reject) => {
      var onEvent, timerId
      const clean = () => {
        if (onEvent) {
          this.off(event, onEvent)
        }
        if (timerId !== undefined) {
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
          timerId = undefined
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
    this._trace('_onNotification() unhandled notification.', notification)
    this.emit('_error', new ClientError(
      'Recieved Invalid message.',
      'JSON-RPC', undefined, notification))
  }

  _onJsonRpcResult(result) {
    const observer = this._observers.get(result.id)
    if (observer) {
      observer.resolve(result.result)
      this._observers.delete(result.id)
    } else {
      this._trace('_onJsonRpcResult() recieved unknown id.', result)
      this.emit('_error', new ClientError(
        'Recieved message has unknown id.',
        'JSON-RPC', undefined, result))
    }
  }

  _onJsonRpcError(error) {
    const reject = observer => observer.reject(
      new ClientError(
        'Recieved error response from server.',
        'JSON-RPC', undefined, error))
    if (error.id !== null) {
      const observer = this._observers.get(error.id)
      if (observer) {
        reject(observer)
        this._observers.delete(error.id)
      } else {
        this._trace('_onJsonRpcError() recieved unknown id.', error)
        this.emit('_error', new ClientError(
          'Recieved message has unknown id.',
          'JSON-RPC', undefined, error))
      }
    } else  {
      switch (this._options.errorWithNullId) {
      case 'REJECT_FIRST':
        var e = this._observers.values().next().value
        reject(e)
        this._obserbers.delete(e.id)
        break
      case 'REJECT_ALL':
        this._observers.forEatch(e => reject(e))
        this._observers.clear()
        break
      case 'DONT_CARE':
      default:
      }
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
      this._trace('_onMessage() JSON parse error.', e.data)
      this.emit('_error', new ClientError(
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
    this.emit('_WebSocket.error', e)
  }

  // ---> routines

  /**
   * @private
   * @method promise - Promisify waiting event.
   * @param {String} event - An event name
   * @param {Number} [timeout = 3000] - the timeout millis.
   *   0 = infinite wait until match params.
   * @return {Promise<undefined>}
   */
  _internalPromise(event, timeout = this._options.connectTimeout) {
    this._trace('_internalPromise() waiting for event:', event, 'timeout:', timeout)
    return new Promise((resolve, reject) => {
      var onEvent, onError, timerId
      const clean = () => {
        if (onEvent) {
          this.off(event, onEvent)
        }
        if (timerId !== undefined) {
          clearTimeout(timerId)
        }
      }
      onEvent = () => {
        clean()
        resolve(resolve)
      }
      onError = (e) => {
        clean()
        reject(new ClientError(`WebSocket Error: while waiting event [${event}].`, 'EVENT', e))
      }
      if (timeout) {
        timerId = setTimeout(() => {
          timerId = undefined
          clean()
          reject(new ClientError(`WebSocket error: while waiting event [${event}].`, 'EVENT'))
        }, timeout)
      }
      this.once(event, onEvent)
      this.once('_WebSocket.error', onError)
    })
  }

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
    // has 'result', 'error' or 'notification' property ?
    let msgDef = rxMessages.find(d => response[d.id] !== undefined), msg
    if (msgDef && this._options.chkMessage) {
      msgDef = msgDef.props.every(p => {
        const v = response[p.prop]
        const ok = p.required
          ? v !== undefined && p.assert(v)
          : v === undefined || p.assert(v)
        if (!ok) {
          msg = `Property [${p.prop}] is missing or incorrect`
        }
        return ok
      })
        ? msgDef
        : undefined
    }
    if (!msgDef) {
      this.emit(
        'error', new ClientError(
          `Recieved Invalid message${msg ? (': ' + msg) : '.'}`,
          'JSON-RPC', undefined, response))
      return
    }
    switch (msgDef.id) {
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
