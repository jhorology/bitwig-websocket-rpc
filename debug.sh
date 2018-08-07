#!/bin/sh
BITWIG_VERSION="2.4 Beta 2"
BITWIG_PREFS="${HOME}/Library/Application Support/Bitwig/Bitwig Studio/prefs"

for arg in "$@"; do
  case $arg in
    -clean) clean=true ;;
    *) ;;
  esac
done

if [[ -n $clean ]]; then
    # clear RC file
    rm -f ~/.bitwig.extension.*
    # clear preference
    rm "${BITWIG_PREFS}/${BITWIG_VERSION}.prefs"
fi

BITWIG_DEBUG_PORT=8989 ~/Applications/Bitwig\ Studio\ Beta/Bitwig\ Studio.app/Contents/MacOS/BitwigStudio
