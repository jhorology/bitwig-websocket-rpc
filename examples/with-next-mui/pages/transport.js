import React from 'react'

// material-ui core component
import Typography from '@material-ui/core/Typography'
import { makeStyles } from '@material-ui/core/styles'

// sources
import Page from '../components/page'
import { BwsConnectionProvider } from '../components/bws-contexts'
import { ArrangerButtonGroup } from '../components/bws-buttons'

const useStyles = makeStyles(theme => ({
  root: {
    marginTop: theme.spacing(2)
  }
}))

// RPC remote host configuration
const config = {
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
            Arranger button group
          </Typography>
          <ArrangerButtonGroup />
        </div>
      </BwsConnectionProvider>
    </Page>
  )
}
