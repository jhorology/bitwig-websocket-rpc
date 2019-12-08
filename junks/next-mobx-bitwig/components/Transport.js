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
          {props.playing && (
            <style jsx>{`
              button.play {
                background-color: #FD8713;
              }
           `}</style>)
          }
          Play
        </button>
        <button onClick={props.onClickStop} >Stop</button>
      </div>
    </div>
  )
}

const format = props => {
  return `${('' + props.bars).padStart(3, '0')}.${props.beats}.${props.ticks}.${('' + props.remainder).padStart(2, '0')}`
}
