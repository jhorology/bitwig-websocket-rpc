import React, { useState, useEffect } from 'react'

import InputLabel from '@material-ui/core/InputLabel'
import FormControl from '@material-ui/core/FormControl'
import { makeStyles } from '@material-ui/core/styles'
import ArcMeter from './arc-meter'
import Nipple from './nipple'

const useStyles = makeStyles(theme => ({
  formControl: {
    minWidth: 180,
    maxWidth: 180
  },
  inputLabel: {
    whiteSpace: 'nowrap'
  }
}))

export default function knob({ label, id }) {
  const classes = useStyles()
  const inputLabelId = label && id ? `${id}-label` : undefined
  return (
    <FormControl className={classes.formControl}>
      <InputLabel id={inputLabelId} maring="dense" shrink className={classes.inputLabel}>
        {label}
      </InputLabel>
    </FormControl>
  )
}
