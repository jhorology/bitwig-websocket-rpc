//  bitwig-websocket-rpc/example/with-next-mui
//   (c) 2020 Masaafumi Fujimaru
//    Released under the MIT license.
import React, { useMemo, useState, useEffect, useRef } from 'react'

import clsx from 'clsx'
import { makeStyles } from '@material-ui/core/styles'
import { capitalize, round } from './utils'
const RADIUS = 32
const INITIAL_VIEWBOX = [0, 0, 0, 0]
const useStyles = makeStyles(theme => ({
  root: {},
  colorPrimary: {
    color: theme.palette.primary.main
  },
  colorSecondary: {
    color: theme.palette.secondary.main
  },
  rail: {
    stroke: 'currentColor',
    strokeOpacity: 0.38
  },
  arc: {
    stroke: 'currentColor'
  }
}))

/**
 * @param radius - outer radius
 * @param startAngle - angle in degree (north=0 clockwise) -180..180
 * @param endAngle - angle in degree (north=0 clock direction) >= startAngle, < startAngle + 360
 * @param thickness - line width
 */
function calcArc(radius, startAngle, endAngle, thickness) {
  // degree to mathematical radian (east=0 counter-clockwise)
  const arcAngle = endAngle - startAngle
  const r = radius - thickness / 2
  const startRad = ((endAngle - 90) * Math.PI) / 180
  const endRad = ((startAngle - 90) * Math.PI) / 180
  // arc start/end points
  const x1 = r * Math.cos(startRad)
  const y1 = r * Math.sin(startRad)
  const x2 = r * Math.cos(endRad)
  const y2 = r * Math.sin(endRad)
  const largeArc = arcAngle > 180 ? 1 : 0
  // minimun/maximum x, y coordinate

  return {
    path: ['M', x1, y1, 'A', r, r, 0, largeArc, 0, x2, y2].join(' '),
    arcLength: (r * arcAngle * Math.PI) / 180
  }
}

/**
 * @property className - outer radius
 * @param startAngle - angle in degree (north=0 clockwise) -180..180
 * @param endAngle - angle in degree (north=0 clock direction) >= startAngle, < startAngle + 360
 * @param thickness - line width
 */
export default function ArcMeter({
  className,
  color = 'inherit',
  startAngle = -135,
  endAngle = 135,
  size = 'normal',
  style,
  thickness = 4,
  value = 0,
  max = 1,
  min = 0,
  noRail = false,
  zeroOrigin = false,
  linecap = 'butt',
  ...other
}) {
  const width =
    {
      large: 96,
      small: 40
    }[size] || 64

  const classes = useStyles()
  // initial placeholder group for getting arc bounds
  const boundsGroup = useRef(null)
  // inner/outer arc path for getting bounds
  const outerArc = useMemo(() => calcArc(RADIUS, startAngle, endAngle, 0), [])
  const innerArc = useMemo(() => calcArc(RADIUS - thickness, startAngle, endAngle, 0), [])
  const rail = !noRail && useMemo(() => calcArc(RADIUS, startAngle, endAngle, thickness), [])
  const [viewBox, setViewBox] = useState()
  const [arc, setArc] = useState()
  useEffect(() => {
    if (boundsGroup && boundsGroup.current) {
      const rect = boundsGroup.current.getBBox()
      setViewBox([rect.x, rect.y, rect.width, rect.height].map(n => round(n, 3)))
    }
  }, [])
  useEffect(() => {
    const k = (endAngle - startAngle) / (max - min)
    let v = value
    if (v > max) v = max
    if (v < min) v = min
    if (zeroOrigin) {
      if (v >= 0) {
        setArc(calcArc(RADIUS, startAngle - min * k, startAngle + (v - min) * k, thickness))
      } else {
        setArc(calcArc(RADIUS, startAngle + (v - min) * k, startAngle - min * k, thickness))
      }
    } else {
      setArc(calcArc(RADIUS, startAngle, startAngle + (v - min) * k, thickness))
    }
  }, [value])

  return (
    <svg
      className={clsx(
        classes.root,
        {
          [classes[`color${capitalize(color)}`]]: color !== 'inherit'
        },
        className
      )}
      style={{
        width: width,
        ...style
      }}
      viewBox={(viewBox || INITIAL_VIEWBOX).join(',')}
      {...other}
    >
      {!viewBox && (
        <g ref={boundsGroup} visibilty="hidden">
          <path d={innerArc.path} fill="none" />
          <path d={outerArc.path} fill="none" />
        </g>
      )}
      {rail && (
        <path
          className={classes.rail}
          d={rail.path}
          fill="none"
          strokeWidth={thickness}
          strokeLinecap={linecap}
        />
      )}
      {arc && (
        <path
          className={classes.arc}
          d={arc.path}
          fill="none"
          strokeWidth={thickness}
          strokeLinecap={linecap}
        />
      )}
    </svg>
  )
}
