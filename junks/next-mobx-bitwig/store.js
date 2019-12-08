import { action, observable } from 'mobx'
import { useStaticRendering } from 'mobx-react'
import { BitwigClient } from 'bitwig-websocket-rpc'

const isServer = typeof window === 'undefined'
// eslint-disable-next-line react-hooks/rules-of-hooks
useStaticRendering(isServer)

const events = [
  'transport.getPosition',
  'transport.isPlaying'
]
export class Store {
  @observable transport = {
    bars: '',
    beats: '',
    ticks: '',
    remainder: '',
    playing: false
  }

  hydrate(serializedStore) {
    const {transport} = this
    transport.bars = '---'
    transport.beats = '-'
    transport.ticks = '-'
    transport.remainder = '--'
    transport.playing = false
  }

  @action
  connect = () => {
    const bws = new BitwigClient('ws://localhost:8887', {
      // traceLog: (objs) => console.log.apply(undefined, objs),
    })
    bws.connect(-1)
      .then(() => {
        bws.subscribe(events)
        bws.on('transport.getPosition', params => {
          const {transport} = this
          transport.bars = params.bars
          transport.beats = params.beats
          transport.ticks = params.ticks
          transport.remainder = params.remainder
        })
        bws.on('transport.isPlaying', params => {
          this.transport.playing = params[0]
        })
        this.bws = bws
      })
  }

  togglePlay = () => {
    if (this.bws && this.bws.isOpen) {
      this.bws.notify(this.playing ? 'transport.pause' : 'transport.play')
    }
  }
  
  stop = () => {
    if (this.bws && this.bws.isOpen) {
      this.bws.notify(this.playing ? 'transport.pause' : 'transport.stop')
    }
  }

  disconnect = () => {
    const bws = this.bws
    if (bws !== undefined) {
      this.bws = undefined
      if (bws.isOpen || bws.isConnecting) {
        bws.close()
      }
    }
  }
}

export async function fetchInitialStoreState() {
  return {}
}
