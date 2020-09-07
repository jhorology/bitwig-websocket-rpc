import { createMuiTheme } from '@material-ui/core/styles'

// Create a theme instance.
export const dark = createMuiTheme({
  palette: {
    type: 'dark',
    primary: {
      main: '#546e7a'
      // main: '#ff5a00'
    },
    secondary: {
      main: '#f44336'
    }
    // error: {
    //   main: red.A400
    // },
    // background: {
    //   default: '#fff'
    // }
  }
})

export const light = createMuiTheme({
  palette: {
    type: 'light',
    primary: {
      main: '#546e7a'
      // main: '#ff5a00'
    },
    secondary: {
      main: '#f44336'
    }
    // error: {
    //   main: red.A400
    // },
    // background: {
    //   default: '#fff'
    // }
  }
})
