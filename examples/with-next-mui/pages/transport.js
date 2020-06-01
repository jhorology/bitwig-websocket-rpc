import React from 'react'

// material-ui core component
import Typography from '@material-ui/core/Typography'
import Grid from '@material-ui/core/Grid'
import { makeStyles } from '@material-ui/core/styles'

// sources
import Page from '../components/page'
import { BwsConnectionProvider } from '../components/bws-contexts'
import { ArrangerButtonGroup } from '../components/bws-buttons'
import { TransportSettings } from '../components/bws-selects'

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

export default function TransportPage() {
  const classes = useStyles()
  return (
    <Page config={config}>
      <BwsConnectionProvider config={config}>
        <div className={classes.root}>
          <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
            Buttons
          </Typography>
          <ArrangerButtonGroup />
          <Typography variant="subtitle1" className={classes.tableToolbarTitle}>
            Settings
          </Typography>
          <TransportSettings />
        </div>
      </BwsConnectionProvider>
    </Page>
  )
}
