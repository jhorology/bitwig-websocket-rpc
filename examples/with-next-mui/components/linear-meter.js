//  bitwig-websocket-rpc/example/with-next-mui
//   (c) 2020 Masaafumi Fujimaru
//    Released under the MIT license.
import React from 'react'

import clsx from 'clsx'
import PropTypes from 'prop-types'
import { makeStyles } from '@material-ui/core/styles'
import { fade } from '@material-ui/core/styles/colorManipulator'

function capitalize(string) {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

const useStyles = makeStyles(theme => ({
  root: {
    display: 'inline-block'
  },
  meter: {
    display: 'inline-block',
    position: 'absolute',
    '&::-webkit-meter-bar': {
      backgroundSize: '100% 100%',
      borderRadius: 2
    },
    '&::-webkit-meter-optimum-value': {
      backgroundSize: '100% 100%',
      borderRadius: 2
    }
  },
  vertical: {
    transform: `rotate(-90deg)`
  },
  colorPrimary: {
    '&::-webkit-meter-bar': {
      background: 'none', // Required to get rid of the default background property
      backgroundColor: fade(theme.palette.primary.main, 0.38)
    },
    '&::-webkit-meter-optimum-value': {
      backgroundImage: 'none', // Required to get rid of the default background property
      backgroundColor: theme.palette.primary.main
    }
  },
  colorSecondary: {
    // color: theme.palette.secondary.main,
    '&::-webkit-meter-bar': {
      background: 'none', // Required to get rid of the default background property
      backgroundColor: fade(theme.palette.secondary.main, 0.38)
    },
    '&::-webkit-meter-optimum-value': {
      backgroundImage: 'none', // Required to get rid of the default background property
      backgroundColor: theme.palette.secondary.main
    }
  }
}))

export default function LinearMeter({
  className,
  color = 'primary',
  size = 80,
  style,
  thickness = 20,
  orientation = 'horizontal',
  ...other
}) {
  const classes = useStyles()
  const isVertical = false
  // const isVertical = orientation !== 'vertical'
  return (
    <div
      className={classes.root}
      style={{
        width: isVertical ? thickness : size,
        height: isVertical ? size : thickness
      }}>
      <meter
        className={clsx(
          classes.meter,
          {
            [classes[`color${capitalize(color)}`]]: color !== 'inherit',
            [classes.vertical]: isVertical
          },
          className
        )}
        style={{
          width: isVertical ? thickness : size,
          height: isVertical ? size : thickness
        }}
        {...other}
      />
    </div>
  )
}

LinearMeter.propTypes = {
  classes: PropTypes.object,
  className: PropTypes.string,
  color: PropTypes.oneOf(['inherit', 'primary', 'secondary']),
  size: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  style: PropTypes.object,
  thickness: PropTypes.number,
  value: PropTypes.number,
  orientation: PropTypes.oneOf(['horizontal', 'vertical'])
}
