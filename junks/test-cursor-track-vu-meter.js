const { BitwigClient } = require('..'),
      readline = require('readline'),
      chalk = require('chalk'),
      METER_RANGE = 64,
      METER_LENGTH = 64,
      LEFT_RMS = { line: 1, title: 'L RMS', level: 0 },
      LEFT_PEAK = { line: 2, title: 'L Peak', level: 0 },
      RIGHT_PEAK = { line: 3, title: 'R Peak', level: 0 },
      RIGHT_RMS = { line: 4, title: 'R RMS', level: 0 }

let X_ORIGIN = 0,
    CURSOR_HOME_Y = 0
async function main() {
  setup(LEFT_RMS, LEFT_PEAK, RIGHT_PEAK, RIGHT_RMS)
  const bws = new BitwigClient('ws://localhost:8887')
  await bws.connect()
  await bws.config({
    useCursorTrack: true,
    cursorTrackShouldFollowSelection: true,
    vuMeterUsedFor: 'CURSOR_TRACK',
    vuMeterRange: METER_RANGE,
    vuMeterChannelMode: 'STEREO',
    vuMeterPeakMode: 'BOTH'
  })
  bws.subscribe([
    'cursorTrack.vuMeter',
    'cursorTrack.name'
  ])
  bws.on('cursorTrack.vuMeter', params => {
    if (params.ch === 0 && !params.peak) {
      updateMeter(LEFT_RMS, params.value)
    } else if (params.ch === 0 && params.peak) {
      updateMeter(LEFT_PEAK, params.value)
    } else if (params.ch === 1 && params.peak) {
      updateMeter(RIGHT_PEAK, params.value)
    } else if (params.ch === 1 && !params.peak) {
      updateMeter(RIGHT_RMS, params.value)
    }
  })
  bws.on('cursorTrack.name', params => updateTrackName(params[0]))
}

function setup(...meters) {
  console.clear()
  const maxTitleLength = meters.reduce((l, m) => Math.max(m.title.length, l), 'Track'.length)
  X_ORIGIN = maxTitleLength + 3
  readline.cursorTo(process.stdout, 0, 0)
  process.stdout.write('Track'.padEnd(maxTitleLength + 1) + '|')
  meters.forEach(m => {
    readline.cursorTo(process.stdout, 0, m.line)
    process.stdout.write(m.title.padEnd(maxTitleLength + 1) + '|')
    CURSOR_HOME_Y = CURSOR_HOME_Y >= m.line ? CURSOR_HOME_Y : m.line
  })
  CURSOR_HOME_Y++
}

function updateTrackName(name) {
  readline.cursorTo(process.stdout, X_ORIGIN, 0)
  readline.clearLine(process.stdout, 1)
  process.stdout.write(name)
  readline.cursorTo(process.stdout, 0, CURSOR_HOME_Y)
}

function updateMeter(meter, value) {
  const level = METER_LENGTH * value / METER_RANGE
  if (level > meter.level) {
    readline.cursorTo(process.stdout, X_ORIGIN + meter.level, meter.line)
    process.stdout.write(createBar(meter.level, level))
    readline.cursorTo(process.stdout, 0, CURSOR_HOME_Y)
  } else if (level < meter.level) {
    readline.cursorTo(process.stdout, X_ORIGIN + level, meter.line)
    readline.clearLine(process.stdout, 1)
    readline.cursorTo(process.stdout, 0, CURSOR_HOME_Y)
  }
  meter.level = level
}

function createBar(oldLevel, newLevel) {
  let bar = ''
  for (let i = oldLevel; i < newLevel; i++) {
    bar += color(i)('=')
  }
  return bar
}

function color(level) {
  if (level < METER_LENGTH / 2) {
    return chalk.green
  } else if (level < METER_LENGTH * 3 / 5) {
    return chalk.yellow
  }
  return chalk.red
}

main()
  .catch(err => console.log('done with error!', err))
