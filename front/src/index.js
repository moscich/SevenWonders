import React from 'react';
import ReactDOM from 'react-dom';
import {
  Switch,
  Route,
  NavLink,
  HashRouter
} from "react-router-dom";
import Login from './Login'
import Home from './Home'
import wondersService from './WondersService'

import './bulma.css';
import './index.css';

class Board extends React.Component {

  render() {
  	var cards = [];
  	var descendants = {}
  	for(var i = 0; i < this.props.elements.length; i++) {
  		var elem = this.props.elements[i]
		cards.push(<Card onClick={(id) => this.props.onClick(id)} 
			card={elem.card} 
			row={elem.position.row} 
			column={elem.position.column} 
			key={elem.id}/>)
  	}

  	return (
  		<div>
  		{cards}
  		</div>
  		)
  }

  
}

class Card extends React.Component {
  render() {
  	const bonus = ((this.props.row + 1)% 2) * 0.5
  	const column = this.props.column - 1
  	const cardName = this.props.card && this.props.card.name


  	const cardCost = this.props.card && this.props.card.cost
  	var cardCostString = ""
  	if (cardCost) {

  		// const cardCostString = "G"+cardCost.gold
  		
  		const gold = (cardCost.gold === undefined) ? "" : "$"+cardCost.gold
  		const wood = (cardCost.wood === undefined) ? "" : "W"+cardCost.wood
  		const clay = (cardCost.clay === undefined) ? "" : "C"+cardCost.clay
  		const stone = (cardCost.stone === undefined) ? "" : "S"+cardCost.stone
  		const papyrus = (cardCost.papyrus === undefined) ? "" : "P"+cardCost.papyrus
  		const glass = (cardCost.glass === undefined) ? "" : "G"+cardCost.glass

        cardCostString = gold + wood + clay + stone + papyrus + glass
  	} else {
  		cardCostString = "Free"
  	}

  	if (cardName) {
  	return (  
  	  <div onClick={() => this.props.onClick(cardName)} className="card boardCard" style={{left:""+(column+bonus)*16.66+"%", top:(this.props.row-1)*100}}>
  	  <header className="card-header center">
    				<p className="card-header-title center">
      					{cardName}
    				</p>
    			</header>
    			<div className="card-content">
    			<div className="content">
    			{cardCostString}
	    	</div>
	    </div>
  	  </div>
  	);
  } else {
  	return (  
  	  <div className="boardCard card" style={{left:""+(column+bonus)*16.66+"%", top:(this.props.row-1)*100}}>
  	    <header className="card-header center">
    				<p className="card-header-title">
      					?
    				</p>
    			</header>
  	  </div>
  	);
  }
  }
}

class Game extends React.Component {

  constructor(props) {
  	super(props);

  wondersService.getGame(77)
  .then((res) => 
      this.setState({
        game: res,
        elements: res.board.elements,
        currentPlayer: res.currentPlayer,
        player1gold: res.player1.gold,
        player2gold: res.player2.gold,
        player1: res.player1,
        player2: res.player2,
      })
    )
  }

  handleClick(id) {
  	this.setState({
  		selectedCard: id
  	})
  }

  takeCard(id) {
  	this.cardAction(id, 'TAKE_CARD')
  }

  sellCard(id) {
  	this.cardAction(id, 'SELL_CARD')
  }

  buildWonder(id) {
  	this.cardAction(id, 'BUILD_WONDER', this.state.selectedCard)
  }

  selectPlayer(playerNo) {
  	this.cardAction(null, "CHOOSE_PLAYER", null, playerNo)
  }

  cardAction(id, action, param1, playerNo) {

  	var body = {
  		type: action
  	}

  	if (id) {
  		body["name"] = id
  	}

  	if (param1) {
  		body["card"] = param1
  	}

  	if (playerNo != null) {
  		body["playerNo"] = playerNo
  	}

	fetch('http://localhost:8080/games/77/actions', {
    method: 'POST',
    headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    },
    body: JSON.stringify(body)
  }).then(result=>result.json())
    .then((res) => 
    	this.setState({
    		game: res,
    		elements: res.board.elements,
    		currentPlayer: res.currentPlayer,
    		player1gold: res.player1.gold,
    		player2gold: res.player2.gold,
    		player1: res.player1,
    		player2: res.player2,
    		selectedCard: null
    	})
    )
    .catch(function(e) {
        console.log("error " + e);
    });

  }

  closeModal() {
  	this.setState({
  		selectedCard: null
  	})
  }

  render() {
    if (this.state == null) {
      return(<p>Loading...</p>)
    }
  	const player = this.state.currentPlayer == 0 ? this.state.player1 : this.state.player2
    return (
      <div>
      <PlayerSelection
        onPlayerSelection={(playerNo) => this.selectPlayer(playerNo)}
        state={this.state.game && this.state.game.state}/>
      <ActionSelection 
        selectedCard={this.state.selectedCard} 
        player={player}
        onClose={() => this.closeModal()}
        onTake={() => this.takeCard(this.state.selectedCard)}
        onSell={() => this.sellCard(this.state.selectedCard)}
        onBuildWonder={(wonder) => this.buildWonder(wonder)}/>
      <Military />
      <Science />
      <div className="center">
        <div className="game-board">
          <Board onClick={(id) => this.handleClick(id)} elements={this.state.elements}/>
        </div>
      </div>
      <div className="playerCards">
      <PlayerSummary class="player1Cards" current={this.state.currentPlayer == 0} player={this.state.player1}/>
      <PlayerSummary class="player2Cards" current={this.state.currentPlayer == 1} player={this.state.player2}/>
      </div>
      <div className="playerCards">
      <PlayerCards class="player1Cards" player={this.state.player1}/>
      <PlayerCards class="player2Cards" player={this.state.player2}/>
      </div>
      <div className="playerCards">
      <PlayerWonders class="player1Cards" player={this.state.player1}/>
      <PlayerWonders class="player2Cards" player={this.state.player2}/>
      </div>
	  </div>
    );
  }
}

class Science extends React.Component {
	render() {
		return (
			<div className="military">
				<p>Science</p>
			</div>
			)
	}
}

class ActionSelection extends React.Component {
	render() {
		return (
			<div className={"modal " + (this.props.selectedCard ? "is-active" : "")}>
  			<div className="modal-background"></div>
  			<div className="modal-content">
  			  <div className="center">
  				<div className="box">
  					<h2>{this.props.selectedCard}</h2>
  					<div className="button is-primary" onClick={() => this.props.onTake()}>Build</div>
  					<div className="button is-warning" onClick={() => this.props.onSell()}>Sell</div>
  					<div className="button is-primary" onClick={() => this.props.onClose()} >Cancel</div>
  					<SelectWondersToBuild onBuildWonder={this.props.onBuildWonder} player={this.props.player}/>
  				</div>
  			  </div>
  			</div>
  			<button onClick={() => this.props.onClose()} className="modal-close is-large" aria-label="close"></button>
		</div>
	
		)
	}
}

class PlayerSelection extends React.Component {
	render() {
		return (
			<div className={"modal " + (this.props.state == "CHOOSE_PLAYER" ? "is-active" : "")}>
  			<div className="modal-background"></div>
  			<div className="modal-content">
  			  <div className="center">
  				<div className="box">
  					<h2>New Age!</h2>
  					<div className="button is-primary" onClick={() => this.props.onPlayerSelection(0)}>Player 1</div>
  					<div className="button is-primary" onClick={() => this.props.onPlayerSelection(1)} >Player 2</div>
  				</div>
  			  </div>
  			</div>
  			<button onClick={() => this.props.onClose()} className="modal-close is-large" aria-label="close"></button>
		</div>
	
		)
	}
}

class Military extends React.Component {
	render() {
		return (
			<div className="military">
				<p>Military</p>
			</div>
			)
	}
}

class DebugDetails extends React.Component {
	render() {
		return (
			<div>
				<p>"Current Player:"{this.props.currentPlayer}</p>
				<p>"Player 1 gold:"{this.props.player1gold}</p>
				<p>"Player 2 gold::"{this.props.player2gold}</p>
			</div>
			)
	}
}

class PlayerSummary extends React.Component {
	render() {
		const gold = this.props.player && <h1 className={[this.props.class, (this.props.current && "currentPlayer")].join(' ')} >Gold {this.props.player.gold}</h1>
		return (
			<div>
				{gold}
			</div>
			)
	}	
}

class Main extends React.Component {
	render() {
		return (
			<HashRouter>
			<ul>
			<li><NavLink to="/">Home</NavLink></li>
            <li><NavLink to="/stuff">Stuff</NavLink></li>
            <li><NavLink to="/contact">Contact</NavLink></li>
            </ul>
			</HashRouter>
			)
	}
}

function cardString(card) {
	return card.name
}

class SelectWondersToBuild extends React.Component {
	render() {
		var wonders = ""
		if (this.props.player != null ) {
			wonders = 
			<table className="table" className={this.props.class}>
			<tbody>{this.props.player.wonders.map((it) => <tr key={it.wonder.name}><td onClick={(id) => this.props.onBuildWonder(it.wonder.name)}><a>{it.wonder.name}</a></td></tr>)}
			</tbody>
			</table>
		}

		return (
			
			<div>
			{wonders}
    		</div>
			

			)
	}
}

class PlayerWonders extends React.Component {
	render() {

		var wonders = ""
		if (this.props.player != null ) {
			wonders = 
			<table className="table" className={this.props.class}>
			<tbody>{this.props.player.wonders.map((it) => 
				<tr key={it.wonder.name}>
					<td className={it.built ? "wonderBuilt" : ""}>{it.wonder.name}{JSON.stringify()}</td>
				</tr>)}
			</tbody>
			</table>
		}

		return (
			
			<div>
			{wonders}
    		</div>
			

			)
	}
}

class PlayerCards extends React.Component {
	render() {
		
		var cards = ""
		if (this.props.player != null ) {
			cards = 
			<table className="table" className={this.props.class}>
			<tbody>{this.props.player.cards.map((it) => <tr key={it.name}>
				<PlayerCard 
					card={it} 
					cardHover={() => this.cardHover(it)}
					cardStopHover={() => this.cardStopHover()} />
					</tr>)}
			</tbody>
			</table>
		}

		return (
			<div>
			<CardDetail hoveredCard={this.state && this.state.hoveredCard}/>
			{cards}
    		</div>
			)
	}

	cardHover(card) {
		this.setState({
			hoveredCard: card
		}
			)

	}

	cardStopHover() {
		this.setState({
			hoveredCard: null
		}
			)

	}
}

class PlayerCard extends React.Component {
	render() {
		return (
			<td 
				onMouseEnter={() => this.props.cardHover()}
				onMouseLeave={() => this.props.cardStopHover()}
			>{this.props.card.name}</td>
			)
	}
}

class CardDetail extends React.Component {
	render() {
		const hidden = this.props.hoveredCard == null ? "hidden" : ""
		return (
			<div className={"card cardDetails " + hidden}  style={{left:"20%"}}>
		 		<header className="card-header">
    				<p className="card-header-title">
      					{this.props.hoveredCard && this.props.hoveredCard.name}
    				</p>
    			</header>
		 	<div className="card-content">
    			<div className="content">
    				ofoaewifjwe ofijwef oajewfefawoefjaefoijaw ea owefijawe oijaewf;ozsd; flaekfaw oijaef ojiawe oijisdf oawejfseflk aewofia jwe ji
      			</div>
      		</div>
		</div>
		)
	}
}

// ========================================

const GameXD = () => (
  <Game/>
)

ReactDOM.render(
   <HashRouter>
      <Switch>
      <Route path='/game' component={GameXD}/>
        <Route exact path='/login' component={Login}/>
        <Route exact path='/' component={Home}/>
      </Switch>
   </HashRouter>,
  document.getElementById('root')
);

