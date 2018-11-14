import React from 'react'
import { Route } from 'react-router-dom'
import wondersService from './WondersService'
import {
  NavLink,
  HashRouter
} from "react-router-dom";

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

		const now = new Date()
		var expireDate = new Date(0)
		expireDate.setUTCSeconds(sessionStorage.getItem('secret_expiration'))
		
		console.log(expireDate)

		if (expireDate < now) {
			console.log('chyba expired')
		} else {
			console.log('a tu nie expired')
		}

		if(sessionStorage.getItem('secret') == null) {
    		props.history.push('login')
    	}

    	wondersService.getGames()
    	.then((res) => 
    		this.setState({
    			gameList: res,
    			inputValue: ""
    		})
    	)

    } 
     
    createGame() {
    	wondersService.createGame()
    	.then(function(res) {
    		alert("xd mate " + res.inviteCode + " " + res.id)
    	})
    	
    }

    joinGame() {
    	wondersService.joinGame(this.state.inputValue)
    	.then(function(res) {
    		alert(res)
    	})
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
    	<GamesList games={this.state.gameList}/>
    	</div>
    )
  }
}

class GamesList extends React.Component {
	render() {
		const listItems = this.props.games.map((game) =>
  			<li><NavLink to={"/games/" + game.id}>{game.id} {game.player1} {game.player2}</NavLink></li>
		);

		return(<HashRouter><ul>{listItems}</ul></HashRouter>)
	}
}
