//  bitwig-websocket-rpc
//   (c) 2019 Masafumi Fujimaru
//    Released under the MIT license.
const { EventClient } = require('./server-push-model-client'),
      { ClientError } = require('./json-rpc-client'),
      md5 = require('md5'),
      nanoid = require('nanoid')

/**
 * JSON-RPC 2.0 Cleint for WebSocketRpcServer.extension
 * @class
 */
class BitwigClient extends EventClient {
  /**
   * Returns the default options for constructor.
   * @static
   * @property
   * @return {Object} The default options.
   */
  static get defaultOptions() {
    return Object.assign({}, EventClient.defaultOptions, {
      password: undefined
    })
  }

  /**
   * Returns a newly created JSON-RPC client.
   * new BitwigClient(url [,protocols[, options]])
   * @public
   * @constructor
   * @param {string} url
   * @param {Array} [protocols] an array of protocol strings
   * @param {object} options an options for this instance
   */
  constructor(url, protocols = undefined, options = {}) {
    if (arguments.length === 2 && !Array.isArray(protocols) &&
        typeof protocols === 'object') {
      options = protocols
      protocols = undefined
    }
    options = Object.assign({}, BitwigClient.defaultOptions, options)
    super(url, protocols, options)
  }

  /**
   * Connect to server.
   *  - connect(retry)
   *  - connect(password)
   *  - connect(options)
   * @public
   * @override
   * @method
   * @param {Object} [retry] - the retry count, -1 = infinite.
   * @param {Number} [password] - the retry count, -1 = infinite.
   * @param {object} [options]
   * @param {Number} [options.retry]
   * @param {Number} [options.timeout]
   * @param {string} [options.password]
   * @return {Promise} = Resolve value is undefined
   * @public
   */
  async connect(arg) {
    let opts = {
      retry: 0,
      timeout: this._options.connectTimeout,
      password: this._options.password
    }
    if (typeof arg === 'number') {
      opts.retry = arg
    } else if (typeof arg === 'string') {
      opts = {
        retry: 0,
        timeout: this._options.connectTimeout,
        password: arg
      }
    } else if (typeof arg === 'object') {
      opts = Object.assign({}, opts, arg)
    }

    const infinite = opts.retry === -1
    let connected = false
    /* eslint no-unmodified-loop-condition: 0 */
    do {
      try {
        if (!this.isClosed) {
          throw new ClientError('Could not connect to server: Illegal ready state..', 'TRANSPORT', undefined, {
            readyState: this.readyStateAsString,
            url: this.url,
            timeout: opts.timeout
          })
        }
        const events = ['_connected', 'close']
        if (opts.password) {
          this._urlQuery = '/auth'
          events.push('_authChallenge')
        }
        this._onInit()
        const e = await this._internalPromise(events, opts.timeout)
        switch (e.event) {
        case '_connected':
          connected = true
          break
        case '_authChallenge':
          this._urlQuery = this._generateChallengeResponse(JSON.parse(e.params), opts.password, 1)
          await super.connect(opts)
          return
        case 'closed':
          throw new ClientError('Connection was closed while connecting.', 'TRANSPORT', undefined, {
            readyState: this.readyStateAsString,
            url: this.url,
            params: e.params
          })
        }
      } catch (err) {
        if (this._ws) {
          try {
            await this.close()
          } catch (e) {
            // ignore
          }
        }
        if (opts.retry) {
          this._trace('connect() attempting to reconnect. remaining retry count:', opts.retry, 'error caused by:', err)
          await new Promise(resolve => setTimeout(resolve, 1000))
        } else {
          if (err instanceof ClientError) {
            throw err
          } else {
            throw new ClientError('Could not connect to server.', 'TRANSPORT', err, {
              readyState: this.readyStateAsString,
              url: this.url,
              timeout: opts.timeout
            })
          }
        }
      } finally {
        this._urlQuery = undefined
        opts.retry--
      }
    } while ((opts.retry >= 0 || infinite) && !connected)
  }

  /**
   * Configure server settings remotely.
   * Server may be restarted by this method.
   * @public
   * @async
   * @param {object} settings - if null is specified, server will restart with default configuration.
   * @param {boolean=true} merge - true if settings are diffrent from current configuration.
   * false if settings are diffrent from default configuration.
   * @return {Promise<object>} - present configuration after applying this method.
   */
  async config(settings, merge = true) {
    let config
    if (settings === null) {
      // reset
      config = [null]
    } else if (merge) {
      // merge
      config = await this.call('rpc.config')
      const fulFilled = Object.keys(settings)
        .every(key => settings[key] === config[key])
      if (fulFilled) {
        return config
      }
      config = Object.assign({ useAbbreviatedMethodNames: false }, config, settings)
    } else {
      config = Object.assign({ useAbbreviatedMethodNames: false }, settings)
    }
    // this rpc method trigger restart of extension.
    this.notify('rpc.config', config)
    await this.promise('close')
    // server port maybe changed
    if (config.webSocketPort) {
      this.url = this.url.replace(/(.*:)([0-9]+)$/, '$1' + config.webSocketPort)
    }
    // server may be not yet started, retry 5 times
    await this.connect(5)
    config = await this.call('rpc.config')
    return config
  }

  /**
   * @public
   * @async
   * @method report - report aveilable methods amd events.
   * @return {Promise<object>}
   */
  async report() {
    const report = await this.call('rpc.report')
    return report
  }

  /**
   * @public
   * @async
   * @method actions - list aveilable actions.
   * @return {Promise<object>}
   */
  async actions() {
    const actions = await this.call('application.getActions')
    return actions
  }

  /**
   * @public
   * @method actions - show popup message.
   */
  msg(msg, continuous = false, sec = 0) {
    let remain = sec
    if (this._msg_timer !== undefined) {
      clearInterval(this._msg_timer)
    }
    if (continuous) {
      const onTimeout = () => {
        if (sec === 0 || remain > 0) {
          remain--
          this.notify('host.showPopupNotification', [msg])
        } else {
          clearInterval(this._msg_timer)
          this._msg_timer = undefined
        }
      }
      remain--
      this.notify('host.showPopupNotification', [msg])
      this._msg_timer = setInterval(onTimeout, 1000)
    } else {
      this.notify('host.showPopupNotification', [msg])
    }
  }

  /**
   * @public
   * @method actions - invoke action.
   */
  action(id) {
    this.notify('application.getAction.invoke', [id])
  }

  /**
   * @public
   * @method broadcast
   * @param {strng} event
   * @param {params} params
   */
  broadcast(event, params) {
    this.notify('rpc.broadcast', [event, params])
  }

  /**
   * as-promised API
   * @api
   * @override
   * @example <caption>as-promised syntax</caption>
   *  this[.next()]
   *       .event(event[, once])
   *      [.atSlot(...indexes)]
   *       .{occur() | become(comparedValue) | match(matcher)}
   *      [.within(timeout)[.sec() | .millis()]]
   *       .asPromised()
   * @param {string} event - the event name
   * @param {boolean = false} once - true is same as next().event(event)
   * @param {indexes} indexes - the bannk slot indexes.
   * @param {array | obeject} comparedValue - for compute that event params equals compared value. support deep object comparsion.
   * @param {function} matcher -
   * @param {number} timeout - default millseconds.
   */
  event(event, once) {
    const original = super.event(event, once),
          atSlot = (...indexes) => {
            original._ctx.slotIndexes = indexes
            return original
          }
    original.atSlot = atSlot
    return {
      _ctx: original._ctx,
      occur: original.occur,
      become: original.become,
      match: original.match,
      atSlot: atSlot
    }
  }

  /**
   * @protected
   * @override
   */
  _asPromised(context) {
    return this.promise(context.event, context.once,
      context.timeout === undefined ? 0 : context.timeout,
      context.matcher, context.slotIndexes)
  }

  /**
   * Promisify handlig event.
   * @public
   * @method
   * @param {String} event - An event name
   * @param {boolean} [once = true]  - If true, call matcher only once.
   * @param {Number} [timeout = 3000] - the timeout millis. 0 = infinite wait until receiving event.
   * @param {function} [matcher = params => true] - the function to matching params.
   * @param {Array<Number>} slotIndexs - the bank slot indexes.
   * @return {Promise<array|object|undefined>} - Reesolve the event params.
   * @public
   */
  promise(event, once = true, timeout = this._options.eventTimeout, matcher = params => true, slotIndexes) {
    this._trace('promise()', {
      event: event,
      once: once,
      timeout: timeout,
      matcher: matcher,
      slotIndexes: slotIndexes
    })
    if (!slotIndexes || !slotIndexes.length) {
      return super.promise(event, once, timeout, matcher)
    }
    return super.promise(event, false, timeout, params => {
      const bankIndexes = params.slice(0, slotIndexes.length)
      const slotParams = params.slice(bankIndexes.length)
      var i = 0
      if (slotIndexes.every(index => index === bankIndexes[i++])) {
        const match = matcher(slotParams)
        if (!match && once) {
          throw new ClientError(
            'Promise is rejected by unmatching params.', 'EVENT', undefined, {
              event: event,
              once: once,
              timeout: timeout,
              slotIndexes: slotIndexes,
              params: params
            })
        }
        return match
      }
      return false
    })
  }

  _generateChallengeResponse(challenge, passwd, nc) {
    const username = 'bitwig',
          uri = `${this.url}/auth`,
          strNc = nc.toString(16).padStart(8, '0'),
          cnonce = nanoid(16),
          a1 = md5(`${username}:${challenge.realm}:${passwd}`),
          a2 = md5(`:${uri}`),
          digest = md5(`${a1}:${challenge.nonce}:${strNc}:${cnonce}:${challenge.qop}:${a2}`)
    return '/auth?' +
      `username=${username}` +
      `&realm=${challenge.realm}` +
      `&nonce=${challenge.nonce}` +
      `&uri=${uri}` +
      `&algorithm=${challenge.algorithm}` +
      `&response=${digest}` +
      `&qop=${challenge.qop}` +
      `&nc=${strNc}` +
      `&cnonce=${cnonce}`
  }

  /**
   * Handle WebnSocket.onclose()
   * @override
   * @param {object} e
   */
  _onClose(e) {
    this._trace('WebSocket.onclose() code:', e.code, ' reason:', e.reason, 'wasClean:', e.wasClean)
    if (e.code === 4401) {
      // server return authentication challenge as reason
      this.emit('_authChallenge', e.reason)
    } else {
      this.emit('close', {
        code: e.code,
        reason: e.reason,
        wasClean: e.wasClean
      })
      this._onDestroy()
    }
  }
}

module.exports = {
  BitwigClient: BitwigClient
}
