export default props => {
  return (
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
  )
}

const format = props => {
  return `${('' + props.bars).padStart(3, '0')}.${props.beats}.${props.ticks}.${('' + props.remainder).padStart(2, '0')}`
}
