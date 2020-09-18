//  bitwig-websocket-rpc
//   (c) 2020 Masaafumi Fujimaru
//    Released under the MIT license.
const path = require('path'),
  fs = require('fs'),
  os = require('os'),
  execSync = require('child_process').execSync,
  glob = require('glob'),
  rimraf = require('rimraf'),
  ENABLE_INSTALL_ALL = false

/**
 * install extension(s).
 * @public
 * @function
 * @param {options} options
 * @typedef options
 * @property {string} extensionsDir - the absolute path of Bitwig Studio Extensions directory
 * @property {number} api - the version of Controller Surface API.
 * @return {Array<string>} array of installed file path.
 */
function installExtension(options) {
  _checkDir(options.extensionsDir)
  const srcDir = path.resolve(__dirname, '../../'),
    results = []
  if (ENABLE_INSTALL_ALL && options.api === 'all') {
    const files = glob.sync(`${srcDir}/WebSocketRpcServer-API*.bwextension`)
    if (files.length) {
      rimraf.sync(`${options.extensionsDir}/**/WebSocketRpcServer*.bwextension`)
      files.forEach(src => {
        const dest = path.join(options.extensionsDir, path.basename(src))
        fs.copyFileSync(src, dest)
        results.push(dest)
      })
      console.info('Installation completed successfully.')
    } else {
      throw new Error('could not find extenson file!')
    }
  } else {
    const src = `${srcDir}/WebSocketRpcServer-API${options.api}.bwextension`,
      dest = path.join(options.extensionsDir, path.basename(src))
    _checkFile(src)
    rimraf.sync(`${options.extensionsDir}/**/WebSocketRpcServer*.bwextension`)
    fs.copyFileSync(src, dest)
    results.push(dest)
  }
  return results
}

/**
 * Returns default extsion folder path.
 * @public
 * @function
 * @param {object} options
 *  - {String} options.extensionsDir
 *  - {String} options.api
 * @return {string} default extension folder path.
 */
function defaultExtensionsDir() {
  switch (process.platform) {
    case 'win32':
      return path.join(os.homedir(), 'Documents', 'Bitwig Studio', 'Extensions')
    case 'darwin':
      return path.join(os.homedir(), 'Documents', 'Bitwig Studio', 'Extensions')
    case 'linux':
      if (_isWSL()) {
        return path.join(
          _wslWinHomeDir(),
          'Documents',
          'Bitwig Studio',
          'Extensions'
        )
      } else {
        return path.join(os.homedir(), 'Bitwig Studio', 'Extensions')
      }
    default:
      throw new Error(`Unsupported Platform:[${process.platform}].`)
  }
}

function _isWSL() {
  // WSL2 report different words
  return (
    process.platform === 'linux' &&
    os.release().toLowerCase().includes('microsoft') &&
    fs.readFileSync('/proc/version', 'utf8').toLowerCase().includes('microsoft')
  )
}

function _wslWinHomeDir() {
  // Full path cmd.exe for /etc/wsl.conf [interop] appendWindowsPath = false
  return execSync(
    'wslpath $(/mnt/c/Windows/system32/cmd.exe /C "echo %USERPROFILE%") 2> /dev/null',
    {
      stdio: ['pipe', 'pipe', 'ignore']
    }
  )
    .toString()
    .replace(/[\r\n]+$/g, '')
}

function _checkDir(dir) {
  if (!fs.existsSync(dir)) {
    throw new Error(`no such directory, "${dir}"`)
  }
  if (!fs.statSync(dir).isDirectory()) {
    throw new Error(`no such directory, "${dir}"`)
  }
}

function _checkFile(file) {
  if (!fs.existsSync(file)) {
    throw new Error(`no such file, "${file}"`)
  }
  if (!fs.statSync(file).isFile()) {
    throw new Error(`no such file, "${file}"`)
  }
}

module.exports = {
  installExtension,
  defaultExtensionsDir
}
