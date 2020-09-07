import React, { useState, useEffect } from 'react'

import InputLabel from '@material-ui/core/InputLabel'
import MenuItem from '@material-ui/core/MenuItem'
import FormControl from '@material-ui/core/FormControl'
import Select from '@material-ui/core/Select'
import Grid from '@material-ui/core/Grid'

import { makeStyles } from '@material-ui/core/styles'
import { useBwsConnection, useBwsEventParams } from './bws-contexts'

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 180,
    maxWidth: 180
  },
  inputLabel: {
    whiteSpace: 'nowrap'
  }
}))

function EnumSelect({ label, id, event, slotIndexes, enumFilter, ...other }) {
  const classes = useStyles()
  const bws = useBwsConnection()
  const [enumValues, setEnumValues] = useState()
  const params = useBwsEventParams(event, slotIndexes)
  const selectLabelId = label && id ? `${id}-label` : undefined
  const handleChange = e => {
    bws.notify(event + '.set', [e.target.value])
  }
  useEffect(() => {
    bws
      .call(event + '.enumDefinition')
      .then(result => setEnumValues(enumFilter ? result.filter(enumFilter) : result))
  }, [])
  return (
    <FormControl className={classes.formControl}>
      {label && (
        <InputLabel id={selectLabelId} maring="dense" shrink className={classes.inputLabel}>
          {label}
        </InputLabel>
      )}
      {enumValues && params && (
        <Select labelId={selectLabelId} value={params[0]} onChange={handleChange} {...other}>
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
      label="Default launch quantization"
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
      label="Record quantization"
      event="application.recordQuantizationGrid"
      {...props}
    />
  )
}

export function TransportSettings(props) {
  return (
    <Grid container spacing={2}>
      <AutomationWriteModeSelect />
      <PreRollSelect />
      <DefaultLaunchQuantizationSelect />
      <ClipLauncherPostRecordingActionSelect />
      <RecordQuantizationSelect />
    </Grid>
  )
}
