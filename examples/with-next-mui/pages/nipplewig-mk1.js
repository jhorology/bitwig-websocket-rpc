import React from 'react'
import dynamic from 'next/dynamic'

// material-ui core components
import { makeStyles } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import Container from '@material-ui/core/Container'
import Grid from '@material-ui/core/Grid'
import pink from '@material-ui/core/colors/pink'
import grey from '@material-ui/core/colors/grey'
import Page from '../components/page'

// dynamic no SSR for nipplejs doesn't work on error 'windows is undefined.'
const Nipple = dynamic(() => import('../components/nipple'), { ssr: false })

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    alignItems: 'center',
    height: '80vh'
  },
  gridContainer: {},
  outerBox: {
    [theme.breakpoints.down('sm')]: boxCss('sm'),
    [theme.breakpoints.up('md')]: boxCss('md'),
    [theme.breakpoints.up('lg')]: boxCss('lg')
  },
  innerBox: {
    borderRadius: '50%',
    backgroundColor: pink[100],
    width: '100%',
    height: '100%',
  },
  nippleContainer: {}
}))

function boxCss(size) {
  const gridSize =
    {
      sm: 600,
      md: 960,
      lg: 1280
    }[size] / 6 | 0
  const padding = gridSize * 0.15 | 0
  return {
    padding: `${padding}px`,
    width: `${gridSize}px`,
    height: `${gridSize}px`
  }
}

function NippleMk1() {
  const classes = useStyles()
  return (
    <Box className={classes.outerBox}>
      <Box className={classes.innerBox} background={pink[100]}>
        <Nipple
          options={{
            mode: 'dynamic',
            color: 'red',
            position: { top: '50%', left: '50%' },
            restOpacity: 0.5,
            restJoystick: true
          }}
        />
      </Box>
    </Box>
  )
}


export default function NipplewigMK1Page() {
  const classes = useStyles()
  return (
    <Page>
      <div className={classes.root}>
        <Grid container justify="center" alignItems="center" className={classes.gridContainer}>
          {[0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1].map((v, i) => (
            <Grid container item justify="center" alignItems="center" xs={2}>
              {(v === 1) && <NippleMk1 key={i}/>}
            </Grid>
          ))}
        </Grid>
      </div>
    </Page>
  )
}
