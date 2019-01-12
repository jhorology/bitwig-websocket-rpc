WebSocket = (require 'rpc-websockets').Client
beautify  = require 'js-beautify'
fs        = require 'fs'

$ =
  reportFile: 'rpc-implementation-spec.json'
  url: 'ws://localhost:8887'
  config:
    useProject: on
    useApplication: on
    useTransport: on
    useArranger: on
    useGroove: on
    useMixer: on
    useArrangerCursorClip: on
    useLauncherCursorClip: on
    useCursorTrack: on
    useSiblingsTrackBank: on
    useChildTrackBank: on
    useCursorDevice: on
    useChainSelector: on
    useCursorDeviceLayer: on
    useCursorRemoteControlsPage: on
    useDeviceLayerBank: on
    useDrumPadBank: on
    useSiblingsDeviceBank: on
    useChainDeviceBank: on
    useSceneBank: on
    useMainTrackBank: on
    useEffectTrackBank: on
    useMasterTrack: on
    useBrowser: on


configure = (config) ->
  return new Promise (resolve, reject) ->
    error = undefined
    ws = new WebSocket $.url,
      autoconnect: on
      reconnect: off
    ws.once 'open', ->
      ws.notify 'rpc.config', $.config
    ws.once 'error', (err) ->
      ws.close()
      error = err;
    ws.once 'close', ->
      if error
        reject error
      else
        ws = new WebSocket $.url,
          autoconnect: on
          reconnect: on
        ws.once 'open', ->
          resolve ws
        ws.once 'error', (err) ->
          error = err;
          ws.close()
        ws.once 'close', -> 
         if error
            reject error
  
conn = undefined
(configure $.config)
  .then (ws) ->
    conn = ws
    error = undefined
    ws.call 'rpc.report'
  .then (result) ->
    report = beautify (JSON.stringify result), indent_size: 2
    console.info report
    fs.writeFileSync $.reportFile, report
  .catch (err) ->
    console.error err
  .finally ->
    conn.close() if conn 
