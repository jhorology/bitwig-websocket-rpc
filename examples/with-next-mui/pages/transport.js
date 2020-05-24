import React, { useState, useEffect, useContext } from 'react'
// material-ui core component
import Toolbar from '@material-ui/core/Toolbar'
import Button from '@material-ui/core/Button'
import Divider from '@material-ui/core/Divider'
import { makeStyles } from '@material-ui/core/styles'

// material-ui iconsi
import PlayIcon from '@material-ui/icons/PlayArrow'
import StopIcon from '@material-ui/icons/Stop'
import RecordIcon from '@material-ui/icons/FiberManualRecord'
import AutomationWriteIcon from '../components/icons/automation-write'
import GrooveIcon from '../components/icons/groove'
import OverdubIcon from '../components/icons/overdub'
import AbletonIcon from '../components/icons/ableton'

import Page from '../components/page'
import { useBwsConnection, useBwsEventParams } from '../components/bws-contexts'

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    alignItems: 'center',
    height: '80vh'
  },
  button: {},
  buttonDivider: {
    width: theme.spacing(2)
  }
}))

// RPC remote host configuration
const config = {
  useTransport: true,
  useGroove: true
}
// base styles for button
function BwsButton({ size = 'large', variant = 'outlined', ...other }) {
  const classes = useStyles()
  return <Button className={classes.button} size={size} variant={variant} {...other} />
}

// base for two-state button
function BwsTwoStateButton({
  color = 'default',
  enabledColor = 'primary',
  event,
  modifier,
  onClick,
  ...other
}) {
  const params = useBwsEventParams(event)
  const enabled = params && (modifier ? modifier(params) : params[0])
  const handleClick = e => {
    if (onClick) {
      onClick(params, e)
    }
  }
  return (
    <BwsButton
      variant={enabled ? 'contained' : 'outlined'}
      color={enabled ? enabledColor : color}
      onClick={handleClick}
      {...other}
    />
  )
}

function BwsPlayButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsTwoStateButton
      event="transport.isPlaying"
      startIcon={<PlayIcon />}
      onClick={() => bws.call('transport.togglePlay')}
      {...props}>
      Play
    </BwsTwoStateButton>
  )
}

function BwsStopButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsButton startIcon={<StopIcon />} onClick={() => bws.call('transport.stop')} {...props}>
      Stop
    </BwsButton>
  )
}

function BwsArrangerRecordButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsTwoStateButton
      enabledColor="secondary"
      event="transport.isArrangerRecordEnabled"
      startIcon={<RecordIcon />}
      onClick={() => bws.call('transport.isArrangerRecordEnabled.toggle')}
      {...props}>
      Record
    </BwsTwoStateButton>
  )
}

function BwsArrangerAutomationWriteButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsTwoStateButton
      enabledColor="secondary"
      event="transport.isArrangerAutomationWriteEnabled"
      startIcon={<AutomationWriteIcon />}
      onClick={() => bws.call('transport.isArrangerAutomationWriteEnabled.toggle')}
      {...props}>
      Automation
    </BwsTwoStateButton>
  )
}

function BwsArrangerOverdubButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsTwoStateButton
      event="transport.isArrangerOverdubEnabled"
      startIcon={<OverdubIcon />}
      onClick={() => bws.call('transport.isArrangerOverdubEnabled.toggle')}
      {...props}>
      Overdub
    </BwsTwoStateButton>
  )
}

function BwsGrooveButton(props) {
  const bws = useBwsConnection()
  return (
    <BwsTwoStateButton
      event="groove.getEnabled.value"
      startIcon={<GrooveIcon />}
      onClick={params => bws.call('groove.getEnabled.value.set', [params[0] ? 0 : 1])}
      {...props}>
      Groove
    </BwsTwoStateButton>
  )
}

function BwsAbletonLinkButton(props) {
  // TODO
  // can't control remotely
  // doesn't exists a 'transport.tempo.name.set' method
  return (
    <BwsTwoStateButton
      event="transport.tempo.name"
      modifier={params => params[0] === 'AbletonLinkTempo'}
      startIcon={<AbletonIcon />}
      {...props}>
      Link
    </BwsTwoStateButton>
  )
}

export default function TransportPage() {
  const classes = useStyles()
  return (
    <Page config={config}>
      <div className={classes.root}>
        <Toolbar size="large">
          <BwsPlayButton />
          <BwsStopButton />
          <BwsArrangerRecordButton />
          <BwsArrangerAutomationWriteButton />
          <BwsArrangerOverdubButton />
          <div className={classes.buttonDivider} />
          <BwsGrooveButton />
          <div className={classes.toolbarDivider} />
          <BwsAbletonLinkButton />
        </Toolbar>
      </div>
    </Page>
  )
}
