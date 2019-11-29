//  bitwig-websocket-rpc
//   (c) 2019 Masafumi Fujimaru
//    Released under the MIT license.
//
//  Portions are using rpc-websockets as reference:
//    https://github.com/elpheria/rpc-websockets
//    Released under MIT license.
//
const { Client, ClientError } = require('./json-rpc-client')

/**
 * @class
 * JSON-RPC 2.0 over WebSocket Client with supporting Server-Push-Model.
 */
class EventClient extends Client {
  /**
   * Returns the default options for constructor.
   * @static
   * @property
   * @return {Object} The default options.
   */
  static get defaultOptions() {
    return Object.assign({}, Client.defaultOptions, {
      raiseErrorOnSubscribe: true,
      removeListenersOnUnsubscribe: true
    })
  }

  /**
   * Returns the all aveilable events.
   * @property
   * @return {Array} The all aveilable events.
   */
  get events() {
    const events = Array.from(this._eventSet)
    return events.concat(super.events)
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
    if (arguments.length === 2 && !Array.isArray(protocols) &&
        typeof protocols === 'object') {
      options = protocols
      protocols = undefined
    }
    options = Object.assign({}, EventClient.defaultOptions, options)
    super(url, protocols, options)
    this._eventSet = new Set()
  }

  /**
   * Subscribes server-push events.
   * @param {String} events An array of event name.
   * @return {Promise} resolve server response.
   *  - const result = await ws.subscribe(['hoehoge', 'foobar'])
   *    result -> {hogehoge: 'ok', foobar: 'Event not found.'}
   */
  async subscribe(events) {
    const result = await this._processEvents(events, true)
    Object.keys(result).forEach(e => this._eventSet.add(e))
    return result
  }

  /**
   * Unubscribes server-push events.
   * @param {String} events An array of event name.
   * @return {Promise} resolve server response.
   *  - const result = await ws.unsubscribe(['hoehoge', 'foobar'])
   *    result -> {hogehoge: 'ok', foobar: 'Event not found.'}
   */
  async unsubscribe(events) {
    const result = await this._processEvents(events, false)
    events.forEach(e => {
      this._eventSet.delete(e)
      if (this._options.removeListenersOnUnsubscribe) {
        this.removeAllListeners(e)
      }
    })
    return result
  }

  /**
   * @override
   */
  _onInit() {
    super._onInit()
    this._eventSet.clear()
  }

  /**
   * @override
   */
  _onDestroy() {
    super._onDestroy()
    this._eventSet.clear()
  }

  /**
   * @override
   */
  _onNotification(message) {
    this.emit(message.notification, message.params)
  }

  // ---> routines

  /**
   * @private
   * @method _processEvents
   * @param events {Array<string>} - array of event name
   * @param on {boolen} - true=subscribe, false=unsubscribe
   */
  async _processEvents(events, on) {
    const method = on ? 'rpc.on' : 'rpc.off'
    if (!Array.isArray(events) || !events.length) {
      throw new ClientError('Invalid argument: Empty events is not allowed.',
        'EVENT', undefined, {
          method: method,
          params: events
        })
    }
    // events maybe more than thousand.
    const result = await this.call(method, events,
      this._options.responseTimeout + events.length * 5)
    if (this._options.raiseErrorOnSubscribe) {
      const errors = events.filter(e => result[e] !== 'ok')
        .map((e) => ({ event: e, error: result[e] }))
      if (errors.length) {
        throw new ClientError(
          `Event ${on ? '' : 'un'}subscribe error.`,
          'EVENT', undefined, errors)
      }
    }
    return result
  }
}

module.exports = {
  EventClient: EventClient
}
