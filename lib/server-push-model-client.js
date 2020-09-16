//  bitwig-websocket-rpc
//   (c) 2020 Masafumi Fujimaru
//    Released under the MIT license.
//
//  Portions are using rpc-websockets as reference:
//    https://github.com/elpheria/rpc-websockets
//    Released under MIT license.
//
const { Client, ClientError } = require('./json-rpc-client'),
  equal = require('fast-deep-equal')

/**
 * JSON-RPC 2.0 over WebSockets Client with supporting Server-Push-Model.
 * @class
 */
class EventClient extends Client {
  /**
   * Returns the default options for constructor.
   * @static
   * @property {Object} defaultOptions default options.
   * @property {boolean = true} defaultOptions.raiseErrorOnSubscibe
   * @property {boolean = true} defaultOptions.removeraiseErrorOnSubscibe
   */
  static get defaultOptions() {
    return Object.assign({}, Client.defaultOptions, {
      raiseErrorOnSubscribe: true,
      removeListenersOnUnsubscribe: true,
      eventTimeout: 3000,
      unpackEventParams: false
    })
  }

  /**
   * Returns a newly created JSON-RPC client object.
   * new Client(url [,protocols[, options]])
   * @see https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/WebSocket
   * @public
   * @constructor
   * @param {String} url
   * @param {Array} protocols an array of protocol strings
   * @param {Object} options an options for this instance
   */
  constructor(url, protocols = undefined, options = {}) {
    if (
      arguments.length === 2 &&
      !Array.isArray(protocols) &&
      typeof protocols === 'object'
    ) {
      options = protocols
      protocols = undefined
    }
    options = Object.assign({}, EventClient.defaultOptions, options)
    super(url, protocols, options)
    this._eventSet = new Set()
  }

  /**
   * Subscribes server-push events.
   * @param {Array<String>} events - An array of event name.
   * @param {Number} timeout - response timeout millis.
   * @return {Promise} resolve server response.
   *  - const result = await ws.subscribe(['hoehoge', 'foobar'])
   *    result -> {hogehoge: 'ok', foobar: 'Event not found.'}
   */
  async subscribe(events, timeout = this._options.responseTimeout) {
    const result = await this._processEvents(events, true, timeout)
    Object.keys(result).forEach(e => this._eventSet.add(e))
    return result
  }

  /**
   * Return the wheather or not the event has been subscribed.
   * @param {String} event - event
   * @return {boolean} true if eveny has been subscribed
   */
  isSubscribed(event) {
    return this._eventSet.has(event)
  }

  /**
   * Unubscribes server-push events.
   * @param {String} events An array of event name.
   * @param {Number} timeout - response timeout millis.
   * @return {Promise} resolve server response.
   *  - const result = await ws.unsubscribe(['hoehoge', 'foobar'])
   *    result -> {hogehoge: 'ok', foobar: 'Event not found.'}
   */
  async unsubscribe(events, timeout = this._options.responseTimeout) {
    const result = await this._processEvents(events, false, timeout)
    events.forEach(e => {
      this._eventSet.delete(e)
      if (this._options.removeListenersOnUnsubscribe) {
        this.removeAllListeners(e)
      }
    })
    return result
  }

  /**
   * as promised API
   * @example <caption>as-promised syntax</caption>
   *  this[.next()]
   *     .event(event[, once])
   *     .{occur() | become(comparedValue) | match(matcher)}
   *    [.within(timeout)[.sec() | .millis()]]
   *     .asPromised()
   * @param {string} event - the event name
   * @param {boolean = false} once - true is same as next().event(event)
   * @param {array | obeject} comparedValue - for compute that event params equals compared value. support deep object comparsion.
   * @param {function} matcher -
   * @param {number} timeout - default millseconds.
   */
  next() {
    return {
      event: event => this.event(event, true)
    }
  }

  event(event, once = false) {
    const context = {
        event: event,
        once: once
      },
      asPromised = () => {
        return this._asPromised(context)
      },
      sec = () => {
        context.timeout *= 1000
        return {
          _ctx: context,
          asPromised: asPromised
        }
      },
      millis = () => {
        return {
          asPromised: asPromised
        }
      },
      within = timeout => {
        context.timeout = timeout
        return {
          _ctx: context,
          sec: sec,
          millis: millis,
          asPromised: asPromised
        }
      },
      occur = () => {
        context.matcher = () => true
        return {
          _ctx: context,
          asPromised: asPromised,
          within: within
        }
      },
      become = expectedParams => {
        context.matcher = params => equal(expectedParams, params)
        return {
          _ctx: context,
          asPromised: asPromised,
          within: within
        }
      },
      match = (matcher = params => true) => {
        context.matcher = matcher
        return {
          _ctx: context,
          asPromised: asPromised,
          within: within
        }
      }
    return {
      _ctx: context,
      occur: occur,
      become: become,
      match: match
    }
  }

  /**
   * as-promised for inherit class
   * @protected
   */
  _asPromised(context) {
    return this.promise(
      context.event,
      context.once,
      context.timeout === undefined ? 0 : context.timeout,
      context.matcher
    )
  }

  /**
   * Promisify handlig event.
   * @public
   * @method
   * @param {String} event - An event name
   * @param {boolean} [once = true]  - If true, call matcher only once.
   * @param {Number} [timeout = 3000] - the timeout millis. 0 = infinite wait until receiving event.
   * @param {function} [matcher = params => true] - the function to matching params.
   * @return {Promise<array|object|undefined>} - Reesolve the event params.
   * @public
   */
  promise(
    event,
    once = true,
    timeout = this._options.eventTimeout,
    matcher = params => true
  ) {
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
      onEvent = params => {
        let match
        try {
          match = matcher(params)
        } catch (e) {
          clean()
          if (e instanceof ClientError) {
            reject(e)
          } else {
            reject(
              new ClientError('Promise is rejected by matcher.', 'EVENT', e, {
                event: event,
                once: once,
                timeout: timeout,
                params: params
              })
            )
          }
        }
        if (match) {
          clean()
          resolve({
            event: event,
            params: params
          })
        } else {
          if (once) {
            clean()
            reject(
              new ClientError(
                'Promise is rejected by unmatching params.',
                'EVENT',
                undefined,
                {
                  event: event,
                  once: once,
                  timeout: timeout,
                  params: params
                }
              )
            )
          }
        }
      }
      if (timeout) {
        timerId = setTimeout(() => {
          timerId = undefined
          clean()
          reject(
            new ClientError(
              'Timeout error while waiting event',
              'EVENT',
              undefined,
              {
                event: event,
                once: once,
                timeout: timeout
              }
            )
          )
        }, timeout)
      }
      if (once) {
        this.once(event, onEvent)
      } else {
        this.on(event, onEvent)
      }
    })
  }

  /**
   * @override
   * @inheritdoc
   */
  _onInit() {
    super._onInit()
    this._eventSet.clear()
  }

  /**
   * @inheritdoc
   * @override
   */
  _onDestroy() {
    super._onDestroy()
    this._eventSet.clear()
  }

  /**
   * @inheritdoc
   * @override
   */
  _onNotification(message) {
    this.emit(message.notification, message.params)
  }

  // ---> routines

  /**
   * @private
   * @async
   * @param events {Array<string>} - array of event name
   * @param on {boolen} - true=subscribe, false=unsubscribe
   * @param timeout {Number} - response timeout millis.
   */
  async _processEvents(events, on, timeout) {
    const method = on ? 'rpc.on' : 'rpc.off'
    if (!Array.isArray(events) || !events.length) {
      throw new ClientError(
        'Invalid argument: Empty events is not allowed.',
        'EVENT',
        undefined,
        {
          method: method,
          params: events
        }
      )
    }
    // events maybe more than thousand.
    const result = await this.call(method, events, timeout + events.length * 5)
    if (this._options.raiseErrorOnSubscribe) {
      const errors = events
        .filter(e => result[e] !== 'ok')
        .map(e => ({ event: e, error: result[e] }))
      if (errors.length) {
        throw new ClientError(
          `Event ${on ? '' : 'un'}subscribe error.`,
          'EVENT',
          undefined,
          errors
        )
      }
    }
    return result
  }
}

module.exports = {
  EventClient: EventClient
}
