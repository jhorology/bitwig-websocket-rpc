import React from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'
import Link from '@material-ui/core/Link'
import Typography from '@material-ui/core/Typography'
import Divider from '@material-ui/core/Divider'
import GitHubIcon from '@material-ui/icons/GitHub'
const useStyles = makeStyles(theme => ({
  root: {
    position: 'absolute',
    textAlign: 'center',
    verticalAlign: 'middle',
    bottom: '0',
    width: '100%'
    // color: theme.palette.primary.contrastText
  }
}))

export default function Footer(props) {
  const classes = useStyles()
  return (
    <div className={classes.root}>
      <Divider/>
      <footer>
        <Typography variant="body2" color="textSecondary">
          Powered by <GitHubIcon fontSize="small"/>&nbsp;&nbsp;
          <Link color="inherit" href="https://github.com/jhorology/bitwig-websocket-rpc">
            bitwig-websocket-rpc
          </Link>&nbsp;|&nbsp;
          <Link color="inherit" href="https://github.com/zeit/next.js/">
            Next.js
          </Link>&nbsp;|&nbsp;
          <Link color="inherit" href="https://github.com/mui-org/material-ui">
            material-ui
          </Link>
        </Typography>
      </footer>
    </div>
  )
}
