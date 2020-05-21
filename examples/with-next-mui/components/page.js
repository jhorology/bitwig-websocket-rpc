import React, { useEffect } from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'
import Container from '@material-ui/core/Container'
import Hidden from '@material-ui/core/Hidden'

// components
import Navbar from './navbar'
import Footer from './footer'
import { BwsConnectionProvider } from './bws-contexts'

const useStyles = makeStyles(theme => ({
  root: {
    position: 'relative',
    display: 'flex',
    flexDirection: 'column',
    flex: '1 1 0%',
    margin: '0 auto',
    overflowY: 'auto',
    width: '100%',
    height: '100vh'
  },
  pageContainer: {
    height: '100vh',
    alignItems: 'center'
  }
}))

export default function Page({ children, config, merge }) {
  // styles
  const classes = useStyles()
  useEffect(() => {
    document.body.style.overflow = 'unset'
    // Specify how to clean up after this effect:
  }, [])
  return (
    <div className={classes.root}>
      <Navbar />
      <BwsConnectionProvider config={config} merge={merge}>
        <Container fixed disableGutters className={classes.pageContainer}>
          {children}
        </Container>
      </BwsConnectionProvider>
      <Hidden smDown>
        <Footer />
      </Hidden>
    </div>
  )
}
