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
		console.log(props.history)
	}

  responseFacebook(response, history) {
	 console.log(history)
	 wondersService.token = response.accessToken
	 wondersService.getGames()
	 history.push('/game')
  } 

  render() {
    return(
      <FacebookLogin
    	appId="1559710037462455"
    	autoLoad={true}
    	callback={(res) => this.responseFacebook(res, this.props.history)} />
    )
  }
}


export default Login