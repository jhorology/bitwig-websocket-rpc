import React, { useEffect } from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'

// components
import Navbar from './navbar'
import Footer from './footer'
import { BwsConnectionProvider } from './bws-contexts'

const useStyles = makeStyles(theme => ({
  page: {}
}))

export default function Page({ children, config, merge }) {
  // styles
  const classes = useStyles()
  useEffect(() => {
    document.body.style.overflow = 'unset'
    // Specify how to clean up after this effect:
  }, [])
  return (
    <>
      <Navbar />
      <div className={classes.page}>
        <BwsConnectionProvider config={config} merge={merge}>
          {children}
        </BwsConnectionProvider>
        <Footer />
      </div>
    </>
  )
}
