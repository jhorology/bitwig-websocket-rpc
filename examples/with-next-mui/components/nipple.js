//  bitwig-websocket-rpc/example/with-next-mui
//   (c) 2020 Masaafumi Fujimaru
//    Released under the MIT license.
//
//  Portions are using react-nipple as reference:
//    https://github.com/loopmode/react-nipple
//    Released under MIT license.
//
import React, { useRef, useState, useEffect, useMemo } from 'react'
import { makeStyles } from '@material-ui/core/styles'
import nipplejs from 'nipplejs'
const useStyles = makeStyles(theme => ({
  nipple: {
    position: 'relative',
    width: '100%',
    height: '100%'
  }
}))

/**
 * Component propTypes
 *
 * Any additional (unknown) props will be passed along as attributes of the created DOM element.
 *
 * @property {string} className - A css classname for the DOM element
 * @property {object} options - An object with nipplejs options, see https://github.com/yoannmoinet/nipplejs#options
 * @property {function} onCreated - Callback that is invoked with the created instance
 * @property {function} onDestroy - Callback that is invoked with the instance that is going to be destroyed
 * @property {function} onStart - Callback for the 'start' event handler, see https://github.com/yoannmoinet/nipplejs#start
 * @property {function} onEnd - Callback for the 'end' event handler, see https://github.com/yoannmoinet/nipplejs#end
 * @property {function} onMove - Callback for the 'move' event handler, see https://github.com/yoannmoinet/nipplejs#move
 * @property {function} onDir - Callback for the 'dir' event handler, see https://github.com/yoannmoinet/nipplejs#dir
 * @property {function} onPlain - Callback for the 'plain' event handler, see https://github.com/yoannmoinet/nipplejs#plain
 * @property {function} onShown - Callback for the 'shown' event handler, see https://github.com/yoannmoinet/nipplejs#shown
 * @property {function} onHidden - Callback for the 'hidden' event handler, see https://github.com/yoannmoinet/nipplejs#hidden
 * @property {function} onPressure - Callback for the 'pressure' event handler, see https://github.com/yoannmoinet/nipplejs#pressure
 */
export default function Nipple({
  options,
  onStart,
  onEnd,
  onMove,
  onDir,
  onPlain,
  onShown,
  onHidden,
  onPressure,
  onCreated,
  ...other
}) {
  const classes = useStyles()
  const handleElement = useRef(null)
  const [joystick, setJoystick] = useState()
  const createJoystick = () => {
    const opts = Object.assign(
      {
        zone: handleElement.current,
        mode: 'static',
        position: {
          top: '50%',
          left: '50%'
        },
        restOppacity: 1
      },
      options
    )
    const js = nipplejs.create(opts)
    onStart && joystick.on('start', onStart)
    onEnd && js.on('end', onEnd)
    onMove && js.on('move', onMove)
    onDir && js.on('dir', onDir)
    onPlain && js.on('plain', onPlain)
    onShown && js.on('shown', onShown)
    onHidden && js.on('hidden', onHidden)
    onPressure && js.on('pressure', onPressure)
    setJoystick(js)
    onCreated && onCreated(js)
  }
  const properties = useMemo(() => {}, [])

  useEffect(() => {
    const unmount = () => {
      if (joystick) {
        joystick.destroy()
      }
    }
    createJoystick()
    return unmount
  }, [])
  return <div {...other} ref={handleElement} className={classes.nipple} />
}
