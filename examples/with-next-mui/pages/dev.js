import React from 'react'

// material-ui core component
import Typography from '@material-ui/core/Typography'
import Divider from '@material-ui/core/Divider'
import Box from '@material-ui/core/Box'
import { makeStyles } from '@material-ui/core/styles'

// sources
import Page from '../components/page'
import { BwsConnectionProvider } from '../components/bws-contexts'
import ArcMeter from '../components/arc-meter'
import LinerMeter from '../components/linear-meter'

const useStyles = makeStyles(theme => ({
  root: {
    marginTop: theme.spacing(2)
  }
}))

// RPC remote host configuration
const config = {
  useApplication: true,
  useTransport: true,
  useGroove: true
}

export default function TestPage() {
  const classes = useStyles()
  return (
    <Page config={config}>
      <div className={classes.root}>
        <Typography variant="subtitle1">ArcMeter</Typography>
        {[0, 0.2, 0.4, 0.6, 0.8, 1].map((value, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={value} />
          </Box>
        ))}
        <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
          ArcMeter - no rail
        </Typography>
        {[0, 0.2, 0.4, 0.6, 0.8, 1].map((value, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={value} noRail />
          </Box>
        ))}
        <Typography variant="subtitle1">{'ArcMeter - size -> small, normal, big'}</Typography>
        {['small', 'normal', 'big'].map((size, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={0.5} size={size} />
          </Box>
        ))}
        <Typography variant="subtitle1">
          {'ArcMeter - color -> inherit, primary, secondary'}
        </Typography>
        {['inherit', 'primary', 'secondary'].map((color, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={0.5} color={color} />
          </Box>
        ))}
        <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
          {'ArcMeter - thickness -> 2, 4, 6, 8, 10, 12'}
        </Typography>
        {[2, 4, 6, 8, 10, 12].map((thickness, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={0.5} thickness={thickness} />
          </Box>
        ))}
        <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
          {
            'ArcMeter - startAngle = -180, -135, -90, -45, 0, 45, 90, 135, 180 endAngle = startAngle + 270'
          }
        </Typography>
        {[-180, -135, -90, -45, 0, 45, 90, 135, 180].map((angle, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={0.5} startAngle={angle} endAngle={angle + 270} />
          </Box>
        ))}
        <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
          ArcMeter - startAngle=-n, endAngle=n
        </Typography>
        {[30, 60, 90, 120, 150].map((angle, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter value={0.5} startAngle={-angle} endAngle={angle} />
          </Box>
        ))}
      </div>
    </Page>
  )
}
