import { useState, useEffect, useContext, createContext } from 'react'
import { BitwigClient } from 'bitwig-websocket-rpc'

const Context = createContext()

export default function BwsConnectionProvider({ children }) {
  const context = createBwsContext()
  return (
    <Context.Provider value={context}>
      {context ? children : null}
    </Context.Provider>
  )
}

function createBwsContext(url = 'ws://localhost:8887') {
  const [context, setContext] = useState()
  useEffect(() => {
    const ctx = new BwsContext(url)
    const unmount = () => {
      ctx.destroy()
    }
    ctx.connect()
      .then(() => {
        setContext(ctx)
      })
    return unmount
  }, [])
  return context
}

class BwsContext {
  constructor(url = 'ws://localhost:8887') {
    this._bws = new BitwigClient(url)
    this._url = url
  }

  get bws() {
    return this._bws
  }

  get error() {
    return this._error
  }

  get isConnected() {
    return this._bws.isOpen
  }

  async connect() {
    try {
      await this._bws.connect()
      this._error = undefined
    } catch (err) {
      this._error = err
    }
  }

  destroy() {
    this._bws.close()
  }
}
