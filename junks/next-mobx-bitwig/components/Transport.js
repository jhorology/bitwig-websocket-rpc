export default props => {
  return (
    <div>
      <div>
        {format(props)}
        <style jsx>{`
          div {
            padding: 15px;
            color: #82fa58;
            display: inline-block;
            font: 50px menlo, monaco, monospace;
            background-color: #000;
          }
          `}</style>
      </div>
      <div>
        <button className='play' onClick={props.onClickPlay}>
          Play
        </button>
        <button onClick={props.onClickStop} >Stop</button>
        <style jsx>{`
          button.play {
            background-color: ${props.playing ? '#FD8713' : 'none'};
          }
        `}</style>)
      </div>
    </div>
  )
}

const format = props => {
  const {position} = props
  return position
    ? `${('' + position.bars).padStart(3, '0')}.${position.beats}.${position.ticks}.${('' + position.remainder).padStart(2, '0')}`
    : '---.-.-.--'
}
