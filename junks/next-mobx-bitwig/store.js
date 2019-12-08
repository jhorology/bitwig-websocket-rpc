import { action, observable } from 'mobx'
import { useStaticRendering } from 'mobx-react'
import { BitwigClient } from 'bitwig-websocket-rpc'

const isServer = typeof window === 'undefined'
// eslint-disable-next-line react-hooks/rules-of-hooks
useStaticRendering(isServer)

export class Store {
  @observable bars = 0
  @observable beats = 0
  @observable ticks = 0
  @observable remainder = 0

  hydrate(serializedStore) {
  }

  @action start = () => {
    const bws = new BitwigClient('ws://localhost:8887', {
      traceLog: (objs) => console.log.apply(undefined, objs),
    })
    bws.connect(1000)
      .then(() => {
        bws.subscribe(['transport.getPosition'])
        bws.on('transport.getPosition', params => {
          this.bars = params.bars
          this.beats = params.beats
          this.ticks = params.ticks
          this.remainder = params.remainder
        })
        this.bws = bws
      })
  }

  stop = () => {
    const bws = this.bws
    if (bws !== undefined) {
      bws.unsubscribe(['transport.getPosition'])
        .then(() => bws.close())
    }
  }
}

export async function fetchInitialStoreState() {
  return {}
}
