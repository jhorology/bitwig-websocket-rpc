//  bitwig-websocket-rpc
//   (c) 2019 Masaafumi Fujimaru
//    Realeased under the MIT license.
const { EventClient } = require('./server-push-model-client')

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
   * @public
   * @async
   * @method config - configure server settings.
   * @param {Object} settings
   * @param {boolean} [merge=false] -
   * @param {boolean} [reconnect=true] -
   * @return {Promise<object>} - present configuration after applying this method.
   */
  async config(settings, merge = false, reconnect = true) {
    const config = await this.call('rpc.config'),
          fulFilled = Object.keys(settings)
            .every(key => settings[key] === config[key])
    if (merge && fulFilled) {
      return config
    }
    const newSettings = merge
      ? Object.assign(config, settings)
      : Object.assign({ useAbbreviatedMethodNames: false }, settings)
    this.notify('rpc.config', newSettings)
    await this.promise('close')
    if (reconnect) {
      await this.connect()
      const newConfig = await this.call('rpc.config')
      return newConfig
    }
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
  msg(msg) {
    this.notify('host.showPopupNotification', [msg])
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
   * @param {...object} params
   */
  broadcast(...params) {
  }
}

module.exports = {
  BitwigClient: BitwigClient
}
