const WebSocket = require('rpc-websockets').Client;

/**
 * Configure accessible RPC modules of Bitwig Studio.
 * @method
 * @param {String} url - URL of WebSockets server.
 * @param {Object} config - Configuration settings for RPC modules of Bitwig Studio.
 * @return {Promise}
 */
module.exports = async (url, config) => {
  const _settings = Object.assign({useAbbreviatedMethodNames: false}, config);
  return new Promise((resolve, reject) => {
    var error,
        timer,
        open,
        ws = new WebSocket(url, {
          autoconnect: true,
          reconnect: false
        });
    ws.once('open', async () => {
      try {
        open = true;
        // assert connection is established. timeout 2000msec
        const echo = await ws.call('rpc.echo', ['hello'], 2000);
        // this method trigger restart extension, so connection will be closed by server.
        ws.notify('rpc.config', _settings);
        ws.once('close', () => {
          resolve();
        });
      } catch (err) {
        ws.close();
        reject(err);
      }
    });
    ws.once('error', (err) => {
      if (!open) {
        reject(err);
      }
    });
  });
};
