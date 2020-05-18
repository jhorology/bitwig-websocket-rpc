import React, { useEffect } from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'

// components
import BwsConnectionProvider from './bws-connection-provider'
import Navbar from './navbar'
import Footer from './footer'


const useStyles = makeStyles(theme => ({
  page: {
  }
}))

export default function Layout({ children }) {
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
        <BwsConnectionProvider>
          {children}
        </BwsConnectionProvider>
        <Footer/>
      </div>
    </>
  )
}
