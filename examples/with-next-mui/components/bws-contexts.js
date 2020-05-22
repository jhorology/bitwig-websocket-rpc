import React, { useState, useEffect, useMemo, useContext, createContext } from 'react'
import { BitwigClient } from 'bitwig-websocket-rpc'
import fetch from 'node-fetch'

import BwsChooser from './bws-chooser'

const Location = createContext()
const Connection = createContext()

/**
 * Bitwig Studio location provider
 * @property config {object} - configuration
 * @property merge {boolean} -
 */
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

/**
 * Bitwig Studio connection provider
 * @property config {object} - configuration
 * @property merge {boolean} -
 */
export function BwsConnectionProvider({ children, config, merge }) {
  const loc = useContext(Location)
  const context = useMemo(() => new ConnectionContext(config, merge), [])
  // readyState
  //  0: not connected
  //  1: auto connecting
  //  2: manual connecting
  //  3: connected
  const [readyState, setReadyState] = useState(loc.url ? 1 : 0)
  const [rpcServices, setRpcServices] = useState(loc.services)
  // connect to bitwig studio
  const connect = (s, url, password) => {
    setReadyState(s) // 1: auto connecting 2: manual connecting
    context.connect(url, password).then(connected => {
      if (connected) {
        loc.url = url
        loc.password = password
        setReadyState(3) // 3: connected
      } else {
        setReadyState(0) // 0: not connected
      }
    })
  }
  // handles the chooser connect button
  const handleConnect = (url, password) => {
    connect(2, url, password) // 2: manual connecting
  }

  // handles the chooser refresh button
  const handleRefreshServices = () => {
    loc.fetchServices().then(() => {
      setRpcServices(loc.services)
    })
  }

  useEffect(() => {
    const unmount = () => {
      context.destroy()
    }
    if (loc.url) {
      connect(1, loc.url, loc.password) // 1: auto connecting
    }
    return unmount
  }, [])

  return (
    <Connection.Provider value={context}>
      {readyState === 3 && children}
      <BwsChooser
        open={readyState === 0 || readyState === 2}
        isConnecting={readyState === 2}
        rpcServices={loc.services}
        onConnect={handleConnect}
        onRefreshServices={handleRefreshServices}
        errorText={context.error && 'Connection refused !'}
      />
    </Connection.Provider>
  )
}

export function useBwsConnectionContext() {
  return useContext(Connection)
}

class ConnectionContext {
  constructor(config, merge, state) {
    this._config = config
    this._merge = merge
  }

  get bws() {
    return this._bws
  }

  get error() {
    return this._error
  }

  async connect(url, password) {
    try {
      this._error = undefined
      this._bws = new BitwigClient(url)
      await this._bws.connect(password)
      if (this._config) {
        await this._bws.config(this._config, this._merge)
      }
      return true
    } catch (err) {
      this._error = err
      this.destroy()
    }
    return false
  }

  destroy() {
    this._bws &&
      this._bws.close().catch(() => {
        // ignore
      })
  }
}
