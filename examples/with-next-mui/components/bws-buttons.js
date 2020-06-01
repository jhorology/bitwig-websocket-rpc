import React from 'react'

import { makeStyles } from '@material-ui/core/styles'
// material-ui core component
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'

// material-ui icons
import PlayIcon from '@material-ui/icons/PlayArrow'
import StopIcon from '@material-ui/icons/Stop'
import RecordIcon from '@material-ui/icons/FiberManualRecord'

import clsx from 'clsx'

// sources
import { useBwsConnection, useBwsEventParams } from './bws-contexts'
import GrooveIcon from './icons/groove'
import OverdubIcon from './icons/overdub'
import AbletonIcon from './icons/ableton'
import AutomationWriteIcon from './icons/automation-write'

const useStyles = makeStyles(theme => ({
  button: {},
  buttonSelected: {}
}))

// base for two-state button
function TwoStateButton({
  color = 'default',
  selectedColor = 'primary',
  event,
  slotIndexes,
  modifier,
  onClick,
  ...other
}) {
  const classes = useStyles()
  const params = useBwsEventParams(event, slotIndexes)
  const selected = params && (modifier ? modifier(params) : params[0])
  return (
    <Button
      className={clsx(classes.button, {
        [classes.buttonSelected]: selected
      })}
      color={selected ? selectedColor : color}
      onClick={e => onClick && onClick(selected, params, e)}
      {...other}
    />
  )
}

export function PlayButton(props) {
  const bws = useBwsConnection()
  return (
    <TwoStateButton
      event="transport.isPlaying"
      startIcon={<PlayIcon />}
      onClick={() => bws.notify('transport.isPlaying.toggle')}
      {...props}>
      Play
    </TwoStateButton>
  )
}

export function StopButton(props) {
  const bws = useBwsConnection()
  const classes = useStyles()
  return (
    <Button
      className={classes.button}
      startIcon={<StopIcon />}
      onClick={() => bws.notify('transport.stop')}
      {...props}>
      Stop
    </Button>
  )
}

export function ArrangerRecordButton(props) {
  const bws = useBwsConnection()
  return (
    <TwoStateButton
      selectedColor="secondary"
      event="transport.isArrangerRecordEnabled"
      startIcon={<RecordIcon />}
      onClick={() => bws.notify('transport.isArrangerRecordEnabled.toggle')}
      {...props}>
      Record
    </TwoStateButton>
  )
}

export function ArrangerAutomationWriteButton(props) {
  const bws = useBwsConnection()
  return (
    <TwoStateButton
      selectedColor="secondary"
      event="transport.isArrangerAutomationWriteEnabled"
      startIcon={<AutomationWriteIcon />}
      onClick={() => bws.notify('transport.isArrangerAutomationWriteEnabled.toggle')}
      {...props}>
      Automation
    </TwoStateButton>
  )
}

export function ArrangerOverdubButton(props) {
  const bws = useBwsConnection()
  return (
    <TwoStateButton
      event="transport.isArrangerOverdubEnabled"
      startIcon={<OverdubIcon />}
      onClick={() => bws.notify('transport.isArrangerOverdubEnabled.toggle')}
      {...props}>
      Overdub
    </TwoStateButton>
  )
}

export function GrooveButton(props) {
  const bws = useBwsConnection()
  return (
    <TwoStateButton
      event="groove.getEnabled.value"
      startIcon={<GrooveIcon />}
      onClick={params =>
        bws.notify('groove.getEnabled.value.set', [params && params[0] ? 0 : 1])
      }
      {...props}>
      Groove
    </TwoStateButton>
  )
}

export function AbletonLinkButton(props) {
  // TODO
  // can't control remotely
  // doesn't exists a 'transport.tempo.name.set' method
  return (
    <TwoStateButton
      event="transport.tempo.name"
      modifier={params => params[0] === 'AbletonLinkTempo'}
      startIcon={<AbletonIcon />}
      {...props}>
      Link
    </TwoStateButton>
  )
}

export function ArrangerButtonGroup(props) {
  return (
    <ButtonGroup variant="outlined" {...props}>
      <PlayButton />
      <StopButton />
      <ArrangerRecordButton />
      <ArrangerAutomationWriteButton />
      <ArrangerOverdubButton />
    </ButtonGroup>
  )
}
