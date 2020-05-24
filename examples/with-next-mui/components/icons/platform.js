import React from 'react'

import AppleIcon from '@material-ui/icons/Apple'
import WindowsIcon from './windows'
import LinuxIcon from './ubuntu'

export default function PlatformIcon({ platform, ...other }) {
  const Icon = {
    MAC: AppleIcon,
    WINDOWS: WindowsIcon,
    LINUX: LinuxIcon
  }[platform]
  return <Icon {...other} />
}
