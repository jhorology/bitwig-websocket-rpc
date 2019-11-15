#!/bin/bash

CWD=$(cd $(dirname $0); pwd)
BITWIG_VERSION="3.0.3"
BETA=false

wslenv() {
  cmd.exe /C "echo %$1%"  2> /dev/null | sed -e "s/[\r\n]\+//g"
}

# path conversion win -> wsl
# https://github.com/sgraf812/wslpath/blob/master/wslpath
wslpath() {
    set -- "${1:-$(</dev/stdin)}" "${@:2}"
    echo $1 | sed -e 's/\\/\//g' -e 's/^\(.*\):/\/mnt\/\L\1/'
}

case "`uname`" in
    Linux*)
        if grep -q Microsoft /proc/version; then
            PLATFORM="WSL"
            BITWIG_STUDIO="${CWD}/wsl-bitwig-studio"
            USER_HOME="$(wslpath "$(wslenv "USERPROFILE")")"
        else
            PLATFORM="Linux"
            BITWIG_STUDIO="/usr/bin/bitwig-studio"
            USER_HOME="$HOME"
        fi
        ;;
    Darwin*)
        PLATFORM="Mac"
        # BITWIG_STUDIO="/Applications/Bitwig Studio.app/Contents/MacOS/BitwigStudio"
        BITWIG_STUDIO="${HOME}/Applications/Bitwig Studio/3.1 Beta 1/Bitwig Studio.app/Contents/MacOS/BitwigStudio"
        USER_HOME="$HOME"
        ;;
    *)
        echo "Unsupported Platform" 1>&2
        exit 1
        ;;
esac

for arg in "$@"; do
  case $arg in
    -clean) clean=true ;;
    *) ;;
  esac
done

if [[ -n $clean ]]; then
    # clean RC file
    rm -f "${USER_HOME}"/.bitwig.extension.*
fi

BITWIG_DEBUG_PORT=8989 "${BITWIG_STUDIO}"
