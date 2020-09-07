import React, { useState } from 'react'
import Head from 'next/head'
import { ThemeProvider, createMuiTheme } from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'
import Hidden from '@material-ui/core/Hidden'

import { dark, light } from '../components/theme'
import { BwsLocationProvider } from '../components/bws-contexts'
import Navbar from '../components/navbar'
import Footer from '../components/footer'
// import { red } from '@material-ui/core/colors'

export default function MyApp(props) {
  const { Component, pageProps } = props
  const [theme, setTheme] = useState(dark)
  const handleTogglePaletteType = () => {
    setTheme(createMuiTheme(theme.palette.type === 'dark' ? light : dark))
  }
  React.useEffect(() => {
    // Remove the server-side injected CSS.
    const jssStyles = document.querySelector('#jss-server-side')
    if (jssStyles) {
      jssStyles.parentElement.removeChild(jssStyles)
    }
  }, [])

  return (
    <React.Fragment>
      <Head>
        <title>bitwig-websocket-rpc:examples</title>
        <meta name="viewport" content="minimum-scale=1, initial-scale=1, width=device-width" />
      </Head>
      <ThemeProvider theme={theme}>
        {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />
        <Navbar onTogglePaletteType={handleTogglePaletteType} />
        <BwsLocationProvider>
          <Component {...pageProps} />
        </BwsLocationProvider>
        <Hidden smDown>
          <Footer />
        </Hidden>
      </ThemeProvider>
    </React.Fragment>
  )
}
