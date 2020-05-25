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

/**
 * use Bitwig Studio remote location and location service
 */
export function useBwsLocation() {
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
    return this._services
  }
}

/**
 * Bitwig Studio connection provider
 * @property config {object} - configuration
 * @property merge {boolean} - config
 */
export function BwsConnectionProvider({ children, config, merge = true }) {
  const loc = useBwsLocation()
  // readyState
  //  0: not connected
  //  1: auto connecting
  //  2: manual connecting
  //  3: connected
  const [states, setStates] = useState({
    readyState: 0,
    bws: undefined,
    error: undefined
  })
  // connect to bitwig studio
  // handles the chooser connect button
  const handleConnect = (url, password) => {
    setStates({ ...states, readyState: 2 })
    connect(url, password, config, merge, setStates, loc)
  }

  useEffect(() => {
    const unmount = () => {
      states.bws && states.bws.close()
    }
    // auto connect
    if (loc && loc.url) {
      setStates({ ...states, readyState: 1 })
      connect(loc.url, loc.password, config, merge, setStates)
    }
    return unmount
  }, [])

  return (
    <Connection.Provider value={states.bws}>
      {states.readyState === 3 && children}
      <BwsChooser
        open={states.readyState === 0 || states.readyState === 2}
        isConnecting={states.readyState === 2}
        onConnect={handleConnect}
        errorText={states.error}
      />
    </Connection.Provider>
  )
}

/**
 * use Bitwig Studio connection
 */
export function useBwsConnection() {
  const bws = useContext(Connection)
  return bws
}

/**
 * use Bitwig Studio event params
 */
export function useBwsEventParams(event) {
  const bws = useBwsConnection()
  const [params, setParams] = useState()
  useEffect(() => {
    const handleEvent = params => setParams(params)
    const handleUnmount = () => {
      bws.off(event, handleEvent)
    }
    if (!bws.isSubscribed(event)) {
      bws.subscribe([event])
    }
    bws.on(event, handleEvent)
    return handleUnmount
  }, [])
  return params
}

async function connect(url, password, config, merge, setStates, loc) {
  let bws
  try {
    bws = new BitwigClient(url)
    await bws.connect(password)
    if (config) {
      await bws.config(config, merge)
    }
    if (loc) {
      loc.url = url
      loc.password = password
    }
    bws.once('close', () => {
      setStates({
        readyState: 0,
        bws: undefined,
        error: 'Connection closed by remote host !'
      })
    })
    setStates({
      readyState: 3,
      bws: bws,
      error: undefined
    })
  } catch (err) {
    console.error(err)
    if (bws) {
      bws.close()
    }
    setStates({
      readyState: 0,
      bws: undefined,
      error: 'Connection refused !'
    })
  }
}
