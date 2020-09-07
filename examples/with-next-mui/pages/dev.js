import React, { useState } from 'react'

// material-ui core component
import Typography from '@material-ui/core/Typography'
import Divider from '@material-ui/core/Divider'
import Box from '@material-ui/core/Box'
import Slider from '@material-ui/core/Slider'
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
  const [value, setValue] = useState(0)
  const handleChange = (e, v) => setValue(v)
  return (
    <Page config={config}>
      <div className={classes.root}>
        <Slider
          style={{ width: '200px' }}
          min={-1}
          max={1}
          value={value}
          onChange={handleChange}
          step={0.01}
        />
        <Typography variant="subtitle1">{'ArcMeter'}</Typography>
        {['inherit', 'primary', 'secondary'].map((color, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter
              min={-1}
              max={1}
              startAngle={-90}
              endAngle={180}
              color={color}
              value={value}
            />
          </Box>
        ))}
        <Typography variant="subtitle1">{'ArcMeter - zero origin'}</Typography>
        {['inherit', 'primary', 'secondary'].map((color, i) => (
          <Box key={i} border={1} style={{ display: 'inline-block' }}>
            <ArcMeter zeroOrigin min={-1} max={1} color={color} value={value} />
          </Box>
        ))}
      </div>
    </Page>
  )
}
