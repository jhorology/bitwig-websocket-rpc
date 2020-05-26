import React, { useState, useEffect, useCallback } from 'react'

import InputLabel from '@material-ui/core/InputLabel'
import MenuItem from '@material-ui/core/MenuItem'
import FormControl from '@material-ui/core/FormControl'
import Select from '@material-ui/core/Select'

import { makeStyles } from '@material-ui/core/styles'
import { useBwsConnection, useBwsEventParams } from './bws-contexts'

const useStyles = makeStyles(theme => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 600
  }
}))

function EnumSelect({ label, labelId, event, slotIndexes, enumFilter, ...other }) {
  const classes = useStyles()
  const bws = useBwsConnection()
  const [enumValues, setEnumValues] = useState()
  const params = useBwsEventParams(event, slotIndexes)
  const genLabelId = labelId || `${event}-id`
  const handleChange = e => {
    bws.call(event + '.set', [e.target.value])
  }
  useEffect(() => {
    bws
      .call(event + '.enumDefinition')
      .then(result => setEnumValues(enumFilter ? result.filter(enumFilter) : result))
  }, [])
  return (
    <FormControl className={classes.formControl}>
      <InputLabel id={genLabelId}>{label}</InputLabel>
      {enumValues && params && (
        <Select labelId={genLabelId} value={params[0]} onChange={handleChange} {...other}>
          {enumValues.map(e => (
            <MenuItem key={e.id} value={e.id}>
              {e.displayName}
            </MenuItem>
          ))}
        </Select>
      )}
    </FormControl>
  )
}

export function AutomationWriteModeSelect(props) {
  return (
    <EnumSelect label="Automation Write" event="transport.automationWriteMode" {...props} />
  )
}

export function PreRollSelect(props) {
  return <EnumSelect label="Pre roll" event="transport.preRoll" {...props} />
}

export function DefaultLaunchQuantizationSelect(props) {
  // enumDefinition include pointless 'default', maybe it's share with Clip's definition.
  return (
    <EnumSelect
      label="Default launch quantiztion"
      event="transport.defaultLaunchQuantization"
      enumFilter={e => e.id !== 'default'}
      {...props}
    />
  )
}

export function ClipLauncherPostRecordingActionSelect(props) {
  return (
    <EnumSelect
      label="Post recording action"
      event="transport.clipLauncherPostRecordingAction"
      {...props}
    />
  )
}

export function RecordQuantizationSelect(props) {
  return (
    <EnumSelect
      label="Record quantiztion"
      event="application.recordQuantizationGrid"
      {...props}
    />
  )
}

export function TransportSettings(props) {
  return (
    <>
      <AutomationWriteModeSelect />
      <PreRollSelect />
      <DefaultLaunchQuantizationSelect />
      <ClipLauncherPostRecordingActionSelect />
      <RecordQuantizationSelect />
    </>
  )
}
