import React from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'

const useStyles = makeStyles(theme => ({
  root: {
    position: 'absolute',
    bottom: '0',
    width: '100%',
    background: theme.palette.primary.main,
    color: theme.palette.primary.contrastText
  }
}))

export default function Footer(props) {
  const classes = useStyles()
  return (
    <div className={classes.root}>
      <footer>
        <Typography variant="caption" display="block">
          &copy; {1900 + new Date().getYear()} Masafumi Fujimaru.
        </Typography>
      </footer>
    </div>
  )
}
