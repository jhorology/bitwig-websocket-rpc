import { action, observable } from 'mobx'
import { useStaticRendering } from 'mobx-react'
import { BitwigClient } from 'bitwig-websocket-rpc'

const isServer = typeof window === 'undefined'
// eslint-disable-next-line react-hooks/rules-of-hooks
useStaticRendering(isServer)

export class Store {
  @observable.shallow transport = {
    postion: undefined,
    playing: false
  }
  @observable connected = false
  
  hydrate(serializedStore) {
  }

  connect = async () => {
    if (this.bws) {
      return
    }
    const bws = new BitwigClient('ws://localhost:8887', {
      // password: 'hogehoge'
      // traceLog: (objs) => console.log.apply(undefined, objs),
    })
    this.bws = bws
    while(this.bws) {
      await bws.connect(-1)
      await bws.config({
        useTransport: true
      })
      this.connected = true
      bws.subscribe([
        'transport.getPosition',
        'transport.isPlaying'
      ])
      bws.on('transport.getPosition', params => this.transport.position = params)
      bws.on('transport.isPlaying', params => this.transport.playing = params[0])
      await bws.event('close').occur().asPromised()
      this.transport.position = undefined
      this.connected = false
    }
  }

  togglePlay = () => {
    if (this.bws && this.bws.isOpen) {
      this.bws.notify(this.playing ? 'transport.pause' : 'transport.continuePlayback')
    }
  }
  
  stop = () => {
    if (this.bws && this.bws.isOpen) {
      this.bws.notify(this.playing ? 'transport.pause' : 'transport.stop')
    }
  }

  disconnect = () => {
  }
}

export async function fetchInitialStoreState() {
  return {}
}
