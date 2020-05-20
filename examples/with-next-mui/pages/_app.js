import React from 'react'
import PropTypes from 'prop-types'
import Head from 'next/head'
import { ThemeProvider, createMuiTheme } from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'

import { BwsLocationProvider } from '../components/bws-contexts'
// import { red } from '@material-ui/core/colors'

const theme = createMuiTheme({
  palette: {
    type: 'dark'
    // primary: {
    //   main: '#556cd6'
    // },
    // secondary: {
    //   main: '#19857b'
    // },
    // error: {
    //   main: red.A400
    // },
    // background: {
    //   default: '#fff'
    // }
  }
})

export default function MyApp(props) {
  const { Component, pageProps } = props

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
        <title>My page</title>
        <meta name="viewport" content="minimum-scale=1, initial-scale=1, width=device-width" />
      </Head>
      <ThemeProvider theme={theme}>
        {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />
        <BwsLocationProvider>
          <Component {...pageProps} />
        </BwsLocationProvider>
      </ThemeProvider>
    </React.Fragment>
  )
}

MyApp.propTypes = {
  Component: PropTypes.elementType.isRequired,
  pageProps: PropTypes.object.isRequired
}
