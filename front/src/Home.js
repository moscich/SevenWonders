import React from 'react'
import { Route } from 'react-router-dom'
import wondersService from './WondersService'

const Home = () => (
  <div>
    <h1>Game list and shit</h1>
    <Route render={({ history }) => (
    	<HomeComponent history = {history}/>
  )} />
  </div>
)

export default Home

class HomeComponent extends React.Component {
	constructor(props) {
		super(props)

		const expDate = new Date(0)
		expDate.setUTCSeconds(sessionStorage.getItem('secret_expiration'));
		console.log(expDate)
		console.log(new Date())

		if(sessionStorage.getItem('secret') == null) {
    		props.history.push('login')
    	}

		this.state = {
        	inputValue: 'xd'
      	}

    } 
     
    createGame() {
    	alert("xd mate")
    }

    joinGame() {
    	alert(this.state.inputValue)
    }

    updateInputValue(evt) {
    this.setState({
      inputValue: evt.target.value
    });
  }

  render() {
  	if (this.state == null) {
  		return(<div></div>)
  	};
    return(
    	<div>
    	<div className="button is-primary" onClick={() => this.createGame()}>Create game</div>
    	<input value={this.state.inputValue} onChange={evt => this.updateInputValue(evt)} className="input" type="text" placeholder="Invitation code"/>
    	<div className="button is-primary" onClick={() => this.joinGame()}>Join game</div>
    	</div>
    )
  }
}

