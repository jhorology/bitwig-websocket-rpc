# bitwig-websocket-rpc
JSON-RPC 2.0 implementation over WebSockets for Bitwig Studio.

## Installation
In your project directory:
```sh
npm install bitwig-websocket-rpc --save
```
Install Bitwig Studio Extension
```sh
npx install-bitwig-extension [options]
```
### Options
    -e, --extensionDir <path>   Bitwig Studio's Extensions folder path.
                                (default: /[platform specific]/Extensions)
    -v, --version               x.x.x
    -V

    -h, --help                  output usage information

## Configuration and Helper Tool
```sh
npx bws-rpc [options]
```
### Options
    -u, --url <url>         URL of websockets server. (default: ws://localhost:8887)
    -c, --config <path>     config file(.js|.json) path.
    -a, --all               enable all RPC methods and events.
                            this option is ignored by -c, --config option.
    -b, --abbrev            enable abbreviated method and event name.(experimental)
                            this option is ignored by -c, --config option.
    -p, --printConfig       dry run, print current or intended configuration as JSON.
    -r, --report            report accessible RPC methods and evnets as JSON.
    -k, --actions           report result of Application#getActions() as JSON.
    -e, --events            monitor events.
    -l, --logs              trace control script logs.
    -v, --version           x.x.x
    -V
    -h, --help              output usage information

## Module Use
### Usage
```js
const bitwig = require('bitwig-websocket-rpc');
const WebSocket = require('rpc-websockets').Client;

const wait = millis => {
  return new Promise(resolve => setTimeout(resolve, millis));
};
      
(async() => {
  // configure interesting modules.
  // this function trigger restart of extension.
  // so all client connections will be closed by server.
  await bitwig('ws://localhost:8887', {
    useTransport: true
  });
  // recommended client library.
  // https://github.com/elpheria/rpc-websockets
  const ws = new WebSocket('ws://localhost:8887', {
    autoconnect: true,
    reconnect: true
  });
  
  ws.on('open', async () => {
    // host module is accessible without configuration.
    ws.notify('host.showPopupNotification', ['Hello Bitwig Studio!']);

    try {
      // SettableBooleanValue Transport#isPlaying()
      // this calling will causes error. this is a limitation of Bitwig API.
      const isPlaying0 = await ws.call('transport.isPlaying');
      console.log('isPlaying0:', isPlaying0);
    } catch (err) {
      // { code: -32603,
      //   message: 'Internal error',
      //   data: 'Trying to get a value while not being subscribed.' }
      console.log(err);
    }
    
    // start subscribing interesting events
    ws.subscribe([
      'transport.getPosition',
      'transport.isPlaying'
    ]);

    // now you can read Transport#isPlaying()
    // Value#subscribe() is invoked internally because of subscribing event.
    
    // SettableBooleanValue Transport#isPlaying()
    const isPlaying1 = await ws.call('transport.isPlaying');
    // boolean Transport#isPlaying().get()
    const isPlaying2 = await ws.call('transport.isPlaying.get');
    // Both values are same boolean value.
    // API's value objects (inherited Value class) are serialized via custom serializer.
    // see com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters
    console.log('isPlaying1:', isPlaying1, ', isPlaying2:', isPlaying2);

    // handling events
    ws.on('transport.getPosition', position => {
      console.log("position:", position);
    });
    ws.on('transport.isPlaying', playing => {
      console.log("playing:", playing ? 'start' : 'stop');
    });

    await wait(1000);
    ws.notify('transport.play');
    await wait(3000);
    ws.notify('transport.stop');
    await wait(1000);
    // unsbscrive events
    ws.unsubscribe([
      'transport.getPosition',
      'transport.isPlaying'
    ]);
    // close a connection
    ws.close();
  });
})();

```
### API
```js
const bitwig = require('bitwig-websocket-rpc');
bitwig(url, config).then(() => {
    // configuration is done
});
// or inside async function
(async () => {
    await bitwig(url, config);
})();

```
### bitwig(url, config)

Configure RPC modules. 

Return:
* {`Promise`}: resolve value is undefined.

Parameters:
* `url` {`String`}: The URL of the WebSocket server.
* `config` {`Object`}: The configuration of RPC modules. see section below for details.

This configuration is not session scoped. It's stored as JSON file in your home diretory.
```sh
${HOME}/.bitwig.extension.WebSocket\ RPC-x.x.x
```
### Configuration Defaults
```JSON
{
  "webSocketPort": 8887,
  "useAbbreviatedMethodNames": false,
  "useProject": false,
  "useApplication": false,
  "useTransport": false,
  "useArranger": false,
  "arrangerCueMarkerSize": 16,
  "useGroove": false,
  "useMixer": false,
  "useArrangerCursorClip": false,
  "arrangerCursorClipGridWidth": 16,
  "arrangerCursorClipGridHeight": 16,
  "useLauncherCursorClip": false,
  "launcherCursorClipGridWidth": 16,
  "launcherCursorClipGridHeight": 16,
  "useCursorTrack": false,
  "cursorTrackNumSends": 2,
  "cursorTrackNumScenes": 8,
  "cursorTrackShouldFollowSelection": true,
  "useSiblingsTrackBank": false,
  "siblingsTrackBankNumTracks": 8,
  "siblingsTrackBankIncludeEffectTracks": false,
  "siblingsTrackBankIncludeMasterTrack": false,
  "useChildTrackBank": false,
  "childTrackBankNumTracks": 8,
  "childTrackBankHasFlatList": false,
  "useCursorDevice": false,
  "cursorDeviceNumSends": 2,
  "cursorDeviceFollowMode": "FOLLOW_SELECTION",
  "useChainSelector": false,
  "useCursorDeviceLayer": false,
  "useCursorRemoteControlsPage": false,
  "cursorRemoteControlsPageParameterCount": 8,
  "useDeviceLayerBank": false,
  "deviceLayerBankNumChannels": 8,
  "useDrumPadBank": false,
  "drumPadBankNumPads": 16,
  "useSiblingsDeviceBank": false,
  "siblingsDeviceBankNumDevices": 4,
  "useChainDeviceBank": false,
  "chainDeviceBankNumDevices": 4,
  "useSceneBank": false,
  "sceneBankNumScenes": 8,
  "useMainTrackBank": false,
  "mainTrackBankFollowCursorTrack": true,
  "mainTrackBankNumTracks": 8,
  "mainTrackBankNumSends": 2,
  "mainTrackBankNumScenes": 8,
  "useEffectTrackBank": false,
  "effectTrackBankNumTracks": 2,
  "effectTrackBankNumScenes": 8,
  "useMasterTrack": false,
  "masterTrackNumScenes": 8,
  "useBrowser": false,
  "browserSmartCollectionRows": 32,
  "browserLocationRows": 32,
  "browserDeviceRows": 32,
  "browserCategoryRows": 32,
  "browserTagRows": 32,
  "browserDeviceTypeRows": 16,
  "browserFileTypeRows": 16,
  "browserCreatorRows": 32,
  "browserResultsRows": 32
}
```
You can get a default configuration as follows:
 * Push [reset to default] button in preferences panel.
 * Execute command.
```sh
npx bws-rpc --printConfig
```

## Notes
- `useAbbreviatedMethodNames` option is experimental. I don't gurantee to maintain the same method and event names for future.

- Currently calling `Bank#setSizeOfBank()` method doesn't support extending or reducing fire events.

- WebsocketRpcServer.bwextension calls following API methods from outside of Control Surface Session thread. It maybe dangerous.
    - `Host#println()`
    - `Host#errorln()`
    - `Host#requestFlush()`

## License
All source codes of this git repository are licensed under [MIT](LICENSE).

Bitwig Extension module contains following libraries:
- [google/gson](https://github.com/google/gson)
- [TooTallNate/Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
- [qos-ch/slf4j](https://github.com/qos-ch/slf4j)
