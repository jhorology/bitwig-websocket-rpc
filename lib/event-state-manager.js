module.exports = class EventStateManager {
  constructor(events) {
    const store = {}
    events.reduce((bankDimesionIndex, e) => {
      const chain = e.event.split('.')
      const toObject = (chain, parent) => {
        chain.reduce((o, e) => {
          if (e === 'getItemAt' || e === 'getParameter') {
            const array = o.parent[e] ? o[e] : (o[e] = [])
          }
          return {
            parent: o.parent[e] ? o[e] : (o[e] = {}),
            chain: o.chain.shift
          }
        }, {
          parent: store,
          chain: chain
        })
      }
  }

  get store() {
    return this._store
  }
}
