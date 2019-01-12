#!/bin/sh
BITWIG_VERSION="2.5 Beta 3"
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
    cp "${BITWIG_PREFS}/${BITWIG_VERSION}.prefs.clean" "${BITWIG_PREFS}/${BITWIG_VERSION}.prefs"
fi

BITWIG_DEBUG_PORT=8989 ${HOME}/Applications/Bitwig\ Studio.app/Contents/MacOS/BitwigStudio
