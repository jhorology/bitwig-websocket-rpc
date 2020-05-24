import { createMuiTheme } from '@material-ui/core/styles'

// Create a theme instance.
const theme = createMuiTheme({
  palette: {
    type: 'dark',
    primary: {
      main: '#ff5a00'
    }
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

export default theme
