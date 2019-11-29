# bitwig-websocket-rpc
JSON-RPC 2.0 implementation over WebSockets for Bitwig Studio.

## Installation
In your project directory:
```sh
npm install bitwig-websocket-rpc --save
```
Install Bitwig Studio Extension
```sh
npx bws-rpc install [options]
```

## Configuration and Helper Tool
```sh
npx bws-rpc <cmd> [options]
```
### Options
    $ npx bws-rpc
    Usage: bws-rpc [options] [command]

    Configuration and helper tools for bitwig-websocket-rpc.

    Options:
      -V, --version      output the version number
      -h, --help         output usage information

    Commands:
      install [options]  Install Bitwig Studio WebSockets RPC server extension.
      config [options]   Configure RPC modules.
      report [options]   Report accessible RPC methods and evnets of current configuration as JSON.
      actions [options]  Report result of Application#getActions() as JSON.
      events [options]   Monitor events.
      logs [options]     Trace controller script logs.

    $ npx bws-rpc install --help
 
    Usage: install [options]

    Install Bitwig Studio WebSockets RPC server extension.

    Options:
      -e, --extension-dir <path>  Bitwig Studio Extension directory
                                  (default: "<Platfom Specific>/Extensions")
      -a, --api <version>         Bitwig Studio Extension API version.
                                  version=8, 9 or 10 (default: "8")
      -h, --help                  output usage information

    $ npx bws-rpc config --help
    Usage: config [options]

    Configure RPC modules.

    Options:
      -u, --url <URL>    Bitwig Studio WebSockets URL (default: "ws://localhost:8887")
      -r, --reset        reset configuration to defaults.
      -f, --file <path>  config file(.js|.json) path.
                         this option is ignored by -r option.
      -a, --all          enable all RPC methods and events.
                         this option is ignored by -r or -f option.
      -b, --abbrev       enable abbreviated method and event name (experimental),
                         this option is ignored by -r, -f or -a option.
      -p, --print        print current or result configuration as JSON.
      -h, --help         output usage information

## Module Use
### Usage
```js
const { BitwigClient } = require('bitwig-websocket-rpc')

const wait = millis => {
  return new Promise(resolve => setTimeout(resolve, millis))
}

async function main() {
  const ws = new BitwigClient('ws://localhost:8887', {
    traceLog: undefined
  })

  // connect to server
  await ws.connect()

  // configure interest modules.
  // this function may trigger restart of extension.
  // so all client connections will maybe closed by server.

  // ws.config(settings, merge, reconnect)
  const config = await ws.config({
    useTransport: true
  }, true, true)
  console.log('config:', config)

  // host module is accessible without configuration.
  ws.notify('host.showPopupNotification', ['Hello Bitwig Studio!'])

  try {
    // SettableBooleanValue Transport#isPlaying()
    // this calling will causes error. this is a limitation of Bitwig API.
    const isPlaying0 = await ws.call('transport.isPlaying')
    console.log('isPlaying0:', isPlaying0)
  } catch (err) {
    // { code: -32603,
    //   message: 'Internal error',
    //   data: 'Trying to get a value while not being subscribed.' }
    console.log(err)
  }

  // handling & subscribe interest events

  // if initial value is needed, event listeners should be registered before subscribe.
  ws.on('transport.getPosition', params => {
    console.log('position:', params)
  })
  ws.on('transport.isPlaying', params => {
    console.log('playing:', params[0] ? 'play' : 'stop')
  })
  const subscribeResult = await ws.subscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  console.log('subscribe result:', subscribeResult)

  // now you can read Transport#isPlaying()
  // Value#subscribe() is invoked internally because of subscribing event.

  // batch request can reduce communication costs and thread dispatching costs of server-side.
  const batchResult = await ws.batchRequest(context => {
    // SettableBooleanValue Transport#isPlaying()
    context.call('transport.isPlaying')
    // boolean Transport#isPlaying().get()
    context.call('transport.isPlaying.get')
  })
  // Both values are same boolean value.
  // API's value objects (inherited Value class) are serialized via custom serializer.
  // see com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters
  console.log('isPlaying1:', batchResult[0], ', isPlaying2:', batchResult[1])

  ws.notify('transport.play')
  await wait(6000)
  ws.notify('transport.stop')
  await wait(1000)

  // unsubscribe events
  await ws.unsubscribe([
    'transport.getPosition',
    'transport.isPlaying'
  ])
  // close a connection
  await ws.close()
}

main()
  .then(() => console.log('done!'))
  .catch((err) => console.log('done with error!', err))

```
## API
　Work in progress.

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
  "useCursorDeviceDirectParameter": false,
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
  "browserResultsRows": 32,
  "vuMeterUsedFor": "NONE",
  "vuMeterRange": 64,
  "vuMeterChannelMode": "MONO",
  "vuMeterPeakMode": "RMS"
}
```
You can get a default configuration as follows:
```sh
npx bws-rpc config --reset --print
```

### RPC Module Dependencies
```
bitwig-websocket-rpc
├── application
├── arranger
├── arrangerCursorClip
├── browser
├── cursorTrack
│   ├── childTrackBank
│   ├── cursorDevice
│   │   ├── chainDeviceBank
│   │   ├── chainSelector
│   │   ├── cursorDeviceLayer
│   │   ├── cursorRemoteControlsPage
│   │   ├── deviceLayerBank
│   │   ├── drumPadBank
│   │   └── siblingsDeviceBank
│   └── siblingsTrackBank
├── effectTrackBank
├── groove
├── host
├── launcherCursorClip
├── mainTrackBank
├── masterTrack
├── mixer
├── rpc         (core module for tools and supporting client library)
├── sceneBank
├── test        (for testing)
└── transport

```

## Notes
- `useAbbreviatedMethodNames` option is experimental. I don't gurantee to maintain the same method and event names for future.

- Currently calling `Bank#setSizeOfBank()` method doesn't support extending or reducing fire events.

- WebsocketRpcServer.bwextension calls following API methods from outside of Control Surface Session thread. It maybe dangerous.
    - `Host#requestFlush()`

## License
All source codes of this git repository are licensed under [MIT](LICENSE).

Bitwig Extension module contains following libraries:
- [google/gson](https://github.com/google/gson)
- [TooTallNate/Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
- [qos-ch/slf4j](https://github.com/qos-ch/slf4j)
