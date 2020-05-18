import React from 'react'
import { useRouter } from 'next/router'

import cx from 'classnames'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
import Drawer from '@material-ui/core/Drawer'
import Hidden from '@material-ui/core/Hidden'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'

// @material-ui/icons
import PlayCircleOutlineIcon from '@material-ui/icons/PlayCircleOutline'
import MenuIcon from '@material-ui/icons/Menu'
import InfoOutlinedIcon from '@material-ui/icons/InfoOutlined'

import Link from './link'

const useStyles = makeStyles((theme) => ({
  title: {
    flexGrow: 1
  }
}))

const pages = [
  { href: '/start', title: 'Start', icon: InfoOutlinedIcon },
  { href: '/transport', title: 'Transport', icon: PlayCircleOutlineIcon }
  // TODO
  // Mix, RemoteControls, CLiplauncher etc...
]

const getPage = href => {
  var page = pages.find(p => href.includes(p.href))
  return page ? page : pages[0]
}

const LinkButton = ({ page }) => {
  return (
    <Button naked color="inherit" outlinedPrimary component={Link} href={page.href} startIcon={<page.icon/>}>
      {page.title}
    </Button>
  )
}

export default function Navbar() {
  const classes = useStyles()
  const router = useRouter()
  const [open, setOpen] = React.useState(false)
  const handleDrawerToggle = () => {
    setOpen(!open)
  }
  const activePage = getPage(router.pathname)
  return (
    <AppBar position="static">
      <Toolbar variant="dense">
        <Hidden smDown>
          <Typography variant="h6" className={classes.title}>
            <Box fontWeight="fontWeightLight">
              bitwig-websocket-rpc examples : {activePage.title}
            </Box>
          </Typography>
        </Hidden>
        <Hidden mdUp>
          <Typography variant="h6" className={classes.title}>
            <Box fontWeight="fontWeightLight">
              {activePage.title}
            </Box>
          </Typography>
        </Hidden>
        <Hidden smDown>
          {pages.map((p, i) => <LinkButton key={i} page={p}/>)}
        </Hidden>
        <Hidden mdUp>
          <Button
            color="inherit"
            justIcon
            aria-label="open drawer"
            onClick={handleDrawerToggle}>
            <MenuIcon />
          </Button>
        </Hidden>
        <Hidden mdUp>
          <Hidden mdUp>
            <Drawer
              variant="temporary"
              anchor={'right'}
              open={open}
              onClose={handleDrawerToggle}
              ModalProps={{
                keepMounted: true // Better open performance on mobile.
              }}>
              <List>
                {pages.map((page, i) => (
                  <ListItem key={i}>
                    <LinkButton page={page}/>
                  </ListItem>
                ))}
              </List>
            </Drawer>
          </Hidden>
        </Hidden>
      </Toolbar>
    </AppBar>
  )
}
