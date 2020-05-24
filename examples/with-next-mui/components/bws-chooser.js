import React, { useState, useRef } from 'react'
import { makeStyles } from '@material-ui/core/styles'

// material-ui components
import Alert from '@material-ui/lab/Alert'
import Button from '@material-ui/core/Button'
import CircularProgress from '@material-ui/core/CircularProgress'
import Dialog from '@material-ui/core/Dialog'
import DialogTitle from '@material-ui/core/DialogTitle'
import DialogContent from '@material-ui/core/DialogContent'
import DialogActions from '@material-ui/core/DialogActions'
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import TextField from '@material-ui/core/TextField'
import Grid from '@material-ui/core/Grid'
import Divider from '@material-ui/core/Divider'
import IconButton from '@material-ui/core/IconButton'
import Typography from '@material-ui/core/Typography'
import Tooltip from '@material-ui/core/Tooltip'
// icons
import PlatformIcon from './icons/platform'
import RefreshIcon from '@material-ui/icons/Refresh'
import BitwigIcon from './icons/bitwig'

import Paper from '@material-ui/core/Paper'

import { parse } from 'url'

import { useBwsLocation } from './bws-contexts'

const useStyles = makeStyles(theme => ({
  title: {
    paddingTop: '8px',
    paddingBottom: '0px'
  },
  tableDivider: {
    marginTop: '16px',
    marginBottom: '8px'
  },
  tableToolbar: {
    display: 'flex'
  },
  tableToolbarTitle: {
    flexGrow: 1
  },
  tableContainer: {
    maxHeight: '120px'
  },
  tableRow: {
    cursor: 'pointer'
  },
  tableCell: {
    padding: '6px 12px 6px 8px'
  },
  buttonWrapper: {
    position: 'relative'
  },
  buttonProgress: {
    position: 'absolute',
    top: '50%',
    left: '50%',
    marginTop: -12,
    marginLeft: -12
  }
}))

function ChooserTable({ rpcServices, onSelectUrl, onRefreshServices }) {
  const classes = useStyles()
  return (
    <>
      <Divider className={classes.tableDivider} />
      <div className={classes.tableToolbar}>
        <Typography variant="subtitle2" className={classes.tableToolbarTitle}>
          {rpcServices && rpcServices.length ? rpcServices.length : 'No'} services are detected
        </Typography>
        <Tooltip title="Refresh">
          <IconButton size="small" onClick={onRefreshServices}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </div>
      <Paper variant="outlined">
        <TableContainer className={classes.tableContainer}>
          <Table className={classes.table} size="small">
            <TableHead></TableHead>
            <TableBody className={classes.tableRow}>
              {rpcServices &&
                rpcServices.map((row, i) => (
                  <TableRow key={i} hover onClick={() => onSelectUrl(row.location)}>
                    <TableCell align="center" className={classes.tableCell}>
                      <PlatformIcon platform={row.platform} fontSize="small" />
                    </TableCell>
                    <TableCell className={classes.tableCell}>{row.location}</TableCell>
                    <TableCell className={classes.tableCell}>{row.bitwigVersion}</TableCell>
                    <TableCell
                      className={classes.tableCell}>{`API-${row.apiVersion}`}</TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </>
  )
}

/**
 * Bitwig Studio connection chooser Dialog
 * @property open {boolean} - open state of this dialog
 * @property isConnecting {boolean} - connecting state
 * @property errorText {String} - error text
 * @propperty onConnect {func(url, password)} -  connect button hanndler
 */
export default function BwsChooser({ open, isConnecting, errorText, onConnect }) {
  const classes = useStyles()
  const loc = useBwsLocation()
  const passwordInput = useRef()
  // input field values
  const [values, setValues] = useState({
    hostname: '',
    hostnameErr: 'hostname is required',
    port: '8887',
    portErr: undefined,
    password: ''
  })
  const [rpcServices, setRpcServices] = useState(loc && loc.services)
  // handles the 'CONNECT' Button
  const handleConnect = () => {
    onConnect(`ws://${values.hostname}:${values.port}`, values.password)
  }

  // handles the list select
  const handleSelectUrl = url => {
    const u = parse(url)
    setValues({
      ...values,
      hostname: u.hostname,
      hostnameErr: validate('hostname', u.hostname),
      port: u.port,
      portErr: validate('port', u.port)
    })
    passwordInput.current.focus()
  }
  // handle the refresh button
  const handleRefresh = () => {
    loc && loc.fetchServices().then(services => setRpcServices(services))
  }

  // handles the field value change
  const handleValueChange = name => event => {
    const value = event.target.value
    setValues({
      ...values,
      [name]: value,
      [name + 'Err']: validate(name, event.target.value)
    })
  }

  // field has error ?
  const hasErr = name => {
    if (name) {
      return !!values[name + 'Err']
    } else {
      return Object.keys(values)
        .filter(key => key.endsWith('Err'))
        .some(key => !!values[key])
    }
  }

  // validate field values
  const validate = (name, value) => {
    let err
    if (name === 'hostname') {
      if (!value.length) {
        err = 'hostname is required'
      }
    } else if (name === 'port') {
      if (!value.length) {
        err = 'port is required'
      } else if (!/^[0-9]+$/.exec(value)) {
        err = 'port should be number'
      }
    }
    return err
  }

  return (
    <Dialog
      disableBackdropClick
      disableEscapeKeyDown
      maxWidth="xs"
      aria-labelledby="confirmation-dialog-title"
      open={open}>
      <DialogTitle id="confirmation-dialog-title" className={classes.title}>
        <Grid container spacing={2} justify="center" alignItems="center">
          <Grid item>
            <BitwigIcon color="primary" fontSize="large" />
          </Grid>
          <Grid item>
            <Typography variant="h6" gutterBottom>
              Connect to Bitwig Studio
            </Typography>
          </Grid>
        </Grid>
      </DialogTitle>
      <form>
        <DialogContent dividers>
          <Grid container spacing={2}>
            <Grid item xs={8}>
              <TextField
                fullWidth
                autoFocus
                label="hostname or address"
                error={hasErr('hostname')}
                helperText={values.hostnameErr}
                value={values.hostname}
                onChange={handleValueChange('hostname')}
              />
              <TextField
                inputRef={passwordInput}
                fullWidth
                type="password"
                label="password"
                value={values.password}
                onChange={handleValueChange('password')}
              />
            </Grid>
            <Grid item xs={4}>
              <TextField
                label="port"
                value={values.port}
                error={hasErr('port')}
                helperText={values.portErr}
                inputProps={{ maxLength: 5, width: '30px' }}
                onChange={handleValueChange('port')}
              />
            </Grid>
          </Grid>
          {loc && (
            <ChooserTable
              rpcServices={rpcServices}
              onSelectUrl={handleSelectUrl}
              onRefreshServices={handleRefresh}
            />
          )}
          {errorText && <Alert severity="error">{errorText}</Alert>}
        </DialogContent>
        <DialogActions>
          <div className={classes.buttonWrapper}>
            <Button
              type="submit"
              color="primary"
              onClick={handleConnect}
              disabled={hasErr() || isConnecting}>
              Connect
            </Button>
            {isConnecting && (
              <CircularProgress size={24} color="primary" className={classes.buttonProgress} />
            )}
          </div>
        </DialogActions>
      </form>
    </Dialog>
  )
}
