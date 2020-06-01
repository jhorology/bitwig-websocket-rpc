import React from 'react'

// @material-ui/core components
import { makeStyles } from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
import Drawer from '@material-ui/core/Drawer'
import Hidden from '@material-ui/core/Hidden'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'

// @material-ui/icons
import AdjustIcon from '@material-ui/icons/Adjust'
import PlayCircleOutlineIcon from '@material-ui/icons/PlayCircleOutline'
import MenuIcon from '@material-ui/icons/Menu'
import InfoOutlinedIcon from '@material-ui/icons/InfoOutlined'
import DeveloperBoardIcon from '@material-ui/icons/DeveloperBoard'

import Link from './link'

const useStyles = makeStyles(theme => ({
  toolBar: {
    [theme.breakpoints.down('sm')]: {
      minHeight: 'fit-content!important'
    }
  },
  title: {
    flexGrow: 1
  }
}))

const pages = [
  { href: '/start', title: 'Start', icon: InfoOutlinedIcon },
  { href: '/transport', title: 'Transport', icon: PlayCircleOutlineIcon },
  { href: '/nipplewig-mk1', title: 'Nipplewig Mk1', icon: AdjustIcon },
  { href: '/dev', title: 'Dev', icon: DeveloperBoardIcon }
  // TODO
  // Mix, RemoteControls, CLiplauncher etc...
]

const getPage = href => {
  var page = pages.find(p => href.includes(p.href))
  return page ? page : pages[0]
}

const LinkButton = ({ page }) => {
  return (
    <Box borderBottom={location.pathname.includes(page.href) ? 2 : 0}>
      <Button naked color="inherit" component={Link} href={page.href} startIcon={<page.icon />}>
        {page.title}
      </Button>
    </Box>
  )
}

export default function Navbar() {
  const classes = useStyles()
  const [open, setOpen] = React.useState(false)
  const handleDrawerToggle = () => {
    setOpen(!open)
  }
  const activePage = getPage(location.pathname)
  return (
    <AppBar position="static">
      <Toolbar variant="dense" className={classes.toolBar}>
        <Hidden smDown>
          <Typography variant="h6" className={classes.title}>
            <Box fontWeight="fontWeightLight">
              bitwig-websocket-rpc examples : {activePage.title}
            </Box>
          </Typography>
        </Hidden>
        <Hidden mdUp>
          <Typography variant="h6" className={classes.title}>
            <Box fontWeight="fontWeightLight">{activePage.title}</Box>
          </Typography>
        </Hidden>
        <Hidden smDown>
          {pages.map((p, i) => (
            <LinkButton key={i} page={p} />
          ))}
        </Hidden>
        <Hidden mdUp>
          <Button color="inherit" aria-label="open drawer" onClick={handleDrawerToggle}>
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
                    <LinkButton page={page} />
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
