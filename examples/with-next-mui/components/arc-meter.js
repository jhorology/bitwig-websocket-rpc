//  bitwig-websocket-rpc/example/with-next-mui
//   (c) 2020 Masaafumi Fujimaru
//    Released under the MIT license.
import React, { useMemo, useState, useEffect, useRef } from 'react'

import clsx from 'clsx'
import PropTypes from 'prop-types'
import { makeStyles } from '@material-ui/core/styles'

const RADIUS = 40

function capitalize(string) {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

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
    strokeOpacity: 0.3
  },
  arc: {
    stroke: 'currentColor',
    transition: theme.transitions.create('stroke-dashoffset')
  }
}))
/**
 * @param radius - outer radius
 * @param startAngle - angle in degree (north=0 clockwise) -180..180
 * @param endAngle - angle in degree (north=0 clockwise) >= startAngle, < startAngle + 360
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
    path: ['M', x1, y1, 'A', r, r, 0, largeArc, 0, x2, y2]
      .map(n => (typeof n === 'number' ? Math.round(n, 3) : n))
      .join(' '),
    arcLength: (r * arcAngle * Math.PI) / 180
  }
}

export default function ArcMeter({
  className,
  color = 'inherit',
  startAngle = -135,
  endAngle = 135,
  size = 'normal',
  style,
  thickness = 6,
  value = 0,
  max = 1,
  noRail = false,
  linecap = 'butt',
  ...other
}) {
  const width =
    {
      big: 80,
      normal: 64,
      small: 40
    }[size] || 40

  const classes = useStyles()
  // initial placeholder group for getting arc bounds
  const boundsGroup = useRef(null)
  // inner/outer arc path for getting bounds
  const [states, setStates] = useState({
    viewBox: [-RADIUS, -RADIUS, 2 * RADIUS, 2 * RADIUS],
    outerArc: calcArc(RADIUS, startAngle, endAngle, 0),
    innerArc: calcArc(RADIUS - thickness, startAngle, endAngle, 0)
  })
  useEffect(() => {
    if (boundsGroup && boundsGroup.current) {
      const rect = boundsGroup.current.getBBox()
      setStates({
        viewBox: [rect.x, rect.y, rect.width, rect.height].map(n => Math.round(n, 3)),
        arc: calcArc(RADIUS, startAngle, endAngle, thickness)
      })
    }
  }, [])

  // TODO which is faster stroke-dashoffset or arc path ?

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
      viewBox={states.viewBox.join(',')}
      {...other}>
      {states.innerArc && states.outerArc && (
        <g ref={boundsGroup} visibilty="hidden">
          <path d={states.innerArc.path} fill="none" />
          <path d={states.outerArc.path} fill="none" />
        </g>
      )}
      {!noRail && states.arc && (
        <path
          className={classes.rail}
          d={states.arc.path}
          fill="none"
          strokeWidth={thickness}
          strokeLinecap={linecap}
        />
      )}
      {states.arc && (
        <path
          className={classes.arc}
          d={states.arc.path}
          fill="none"
          strokeDasharray={states.arc.arcLength}
          strokeDashoffset={-states.arc.arcLength * (1 - value / max)}
          strokeWidth={thickness}
          strokeLinecap={linecap}
        />
      )}
    </svg>
  )
}
