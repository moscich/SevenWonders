import React from 'react'
import FacebookLogin from 'react-facebook-login';
import { Route } from 'react-router-dom'
import wondersService from './WondersService'

const Login = () => (
  <div>
  	<Route render={({ history }) => (
    	<XD history = {history} />
  	 )} />
  </div>
)

class XD extends React.Component {
	constructor(props) {
		super(props)
	}

  responseFacebook(response, history) {
  	console.log(response)
	 wondersService.token = response.accessToken
	 sessionStorage.setItem('secret', response.accessToken)
	 const now = new Date()
	 now.setUTCSeconds(response.expiresIn)
	 sessionStorage.setItem('secret_expiration', now)
	 history.push('/')
  } 

  render() {
    return(
      <FacebookLogin
    	appId="1559710037462455"
    	autoLoad={false}
    	callback={(res) => this.responseFacebook(res, this.props.history)} />
    )
  }
}


export default Login