import React from 'react'
import Link from 'next/link'
import { inject, observer } from 'mobx-react'
import Transport from './Transport'

@inject('store')
@observer
class Page extends React.Component {
  componentDidMount() {
    this.props.store.connect()
  }

  componentWillUnmount() {
    this.props.store.disconnect()
  }

  render() {
    return (
      <div>
        <h1>{this.props.title}</h1>
        <Transport
          onClickPlay={this.props.store.togglePlay}
          onClickStop={this.props.store.stop}
          {...this.props.store.transport}/>
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
