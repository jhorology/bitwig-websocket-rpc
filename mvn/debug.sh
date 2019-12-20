#!/bin/bash

CWD=$(cd $(dirname $0); pwd)
BITWIG_VERSION="3.1"

wslenv() {
  # Full path cmd.exe for /etc/wsl.conf [interop] appendWindowsPath = false
  /mnt/c/Windows/system32/cmd.exe /C "echo %$1%"  2> /dev/null | sed -e "s/[\r\n]\+//g"
}

# path conversion win -> wsl
# https://github.com/sgraf812/wslpath/blob/master/wslpath
wslpath() {
    set -- "${1:-$(</dev/stdin)}" "${@:2}"
    echo $1 | sed -e 's/\\/\//g' -e 's/^\(.*\):/\/mnt\/\L\1/'
}

for arg in "$@"; do
  case $arg in
    -clean) clean=true ;;
    *) ;;
  esac
done

case "`uname`" in
    Linux*)
        if grep -q Microsoft /proc/version; then
            PLATFORM="WSL"
            BITWIG_STUDIO="${CWD}/wsl-bitwig-studio"
            USER_HOME="$(wslpath "$(wslenv "USERPROFILE")")"
            LOCALAPPDATA="$(wslpath "$(wslenv "LOCALAPPDATA")")"
            BITWIG_STUDIO_PREFS="${LOCALAPPDATA}/Bitwig Studio/prefs/${BITWIG_VERSION}.prefs"
        else
            # TODO not tested yet
            PLATFORM="Linux"
            BITWIG_STUDIO="/usr/bin/bitwig-studio"
            USER_HOME="$HOME"
            BITWIG_STUDIO_PREFS="${HOME}/.BitwigStudio/prefs/${BITWIG_VERSION}.prefs"
        fi
        ;;
    Darwin*)
        PLATFORM="Mac"
        if [[ $BITWIG_VERSION == *"Beta"* ]]; then
            BITWIG_STUDIO="${HOME}/Applications/Bitwig Studio/${BITWIG_VERSION}/Bitwig Studio.app/Contents/MacOS/BitwigStudio"
        else
            BITWIG_STUDIO="/Applications/Bitwig Studio.app/Contents/MacOS/BitwigStudio"
        fi
        BITWIG_STUDIO_PREFS="${HOME}/Library/Application Support/Bitwig/Bitwig Studio/prefs/${BITWIG_VERSION}.prefs"
        USER_HOME="$HOME"
        ;;
    *)
        echo "Unsupported Platform" 1>&2
        exit 1
        ;;
esac

if [[ -n $clean ]]; then
    # overwrite clean preferences file
    echo "${BITWIG_STUDIO_PREFS}.clean"
    if [ -f "${BITWIG_STUDIO_PREFS}.clean" ]; then
        cp "${BITWIG_STUDIO_PREFS}.clean" "${BITWIG_STUDIO_PREFS}"
        echo 'preferences file has bean overwrited'
    fi
    
    # clean RC file
    rm -f "${USER_HOME}"/.bitwig.extension.*
fi

BITWIG_DEBUG_PORT=8989 BITWIG_VERSION=${BITWIG_VERSION} "${BITWIG_STUDIO}"
