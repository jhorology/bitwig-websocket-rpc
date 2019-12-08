import React from 'react'
import Link from 'next/link'
import { inject, observer } from 'mobx-react'
import Transport from './Transport'

@inject('store')
@observer
class Page extends React.Component {
  componentDidMount() {
    this.props.store.start()
  }

  componentWillUnmount() {
    this.props.store.stop()
  }

  render() {
    return (
      <div>
        <h1>{this.props.title}</h1>
        <Transport
          bars={this.props.store.bars}
          beats={this.props.store.beats}
          ticks={this.props.store.ticks}
          remainder={this.props.store.remainder}
        />
        <nav>
          <Link href={this.props.linkTo}>
            <a>Navigate</a>
          </Link>
        </nav>
      </div>
    )
  }
}

export default Page
