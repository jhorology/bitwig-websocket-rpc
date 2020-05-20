import React, { useState, useEffect, useContext, createContext } from 'react'
import { BitwigClient } from 'bitwig-websocket-rpc'
import fetch from 'node-fetch'

import BwsChooser from './bws-chooser'

const Location = createContext()
const Connection = createContext()

export function BwsLocationProvider({ children }) {
  const [context, setContext] = useState()
  useEffect(() => {
    const ctx = new LocationContext()
    ctx.fetchServices().then(rpcServices => {
      setContext(ctx)
    })
  }, [])
  return <Location.Provider value={context}>{context && children}</Location.Provider>
}

export function useBwsLocationContext() {
  return useContext(Location)
}

class LocationContext {
  constructor() {
    this._url = undefined
    this._password = undefined
    this._services = undefined
  }

  get url() {
    return this._url
  }

  set url(url) {
    this._url = url
  }

  get password() {
    return this._password
  }

  set password(password) {
    this._password = password
  }

  get services() {
    return this._services
  }

  async fetchServices() {
    const res = await fetch(location.origin + '/rpc-services')
    this._services = await res.json()
  }
}

export function BwsConnectionProvider({ children, config, merge }) {
  const loc = useContext(Location)
  const [context, setContext] = useState(new ConnectionContext(config, merge, loc.url ? 1 : 0))
  const [state, setState] = useState(context.state)
  const handleConnect = (url, password) => {
    console.info('## handleConnect0 url:', url, 'password:', password)
    setState(1)
    context.connect(url, password).then(connected => {
      if (context.isConnected) {
        loc.url = url
        loc.password = password
      }
      setState(context.state)
    })
  }
  useEffect(() => {
    const unmount = () => {
      context.destroy()
    }
    if (loc.url) {
      setState(1)
      context.connect(loc.url, loc.password).then(connected => {
        setState(context.state)
      })
    }
    return unmount
  }, [])

  return (
    <Connection.Provider value={context}>
      {state === 2 && children}
      <BwsChooser state={state} onConnect={handleConnect} />
    </Connection.Provider>
  )
}

export function useBwsConnectionContext() {
  return useContext(Connection)
}

class ConnectionContext {
  constructor(config, merge, state) {
    this._state = state // 0: initial(not connected) 1: connecting 2:connected
    this._config = config
    this._merge = merge
  }

  get bws() {
    return this._bws
  }

  get error() {
    return this._error
  }

  get state() {
    return this._state
  }

  get isNotConnected() {
    return this._state === 0
  }

  get isConnecting() {
    return this._state === 1
  }

  get isConnected() {
    return this._state === 2
  }

  async connect(url, password) {
    this._state = 1 // 1: connecting
    this._error = undefined
    try {
      this._bws = new BitwigClient(url)
      await this._bws.connect(password)
      if (this._config) {
        await this._bws.config(this._config, this._merge)
      }
      this._state = 2 // connected
      return true
    } catch (err) {
      this._error = err
      this.destroy()
    }
    return false
  }

  destroy() {
    this._state = 0 // not connected
    this._bws &&
      this._bws.close().catch(() => {
        // ignore
      })
  }
}
