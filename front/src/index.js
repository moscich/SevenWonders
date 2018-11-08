import React from 'react';
import ReactDOM from 'react-dom';
import './bulma.css';
import './index.css';

class Board extends React.Component {

  render() {
  	var cards = [];
  	var descendants = {}
  	for(var i = 0; i < this.props.elements.length; i++) {
  		var elem = this.props.elements[i]
		cards.push(<Card onClick={(id) => this.props.onClick(id)} name={elem.card && elem.card.name} row={elem.position.row} column={elem.position.column} key={elem.id}/>)
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
  	if (!this.props.hidden) {
  	return (  
  	  <div onClick={() => this.props.onClick(this.props.name)} className="card" style={{left:""+(column+bonus)*16.66+"%", top:(this.props.row-1)*100}}>
  	    <div>{this.props.name}</div>
  	  </div>
  	);
  } else {
  	return (  
  	  <div className="card" style={{left:""+(column+bonus)*16.66+"%", top:this.props.row*100}}>
  	    <div>?</div>o
  	  </div>
  	);
  }
  }
}

class Game extends React.Component {

  constructor(props) {
  	super(props);
  	this.state = {
      elements: Array(0).fill(null),
    };
	fetch("http://localhost:8080/games/66")
    .then(result=>result.json())
    .then((res) => 
    	this.setState({
    		elements: res.board.elements,
    		currentPlayer: res.currentPlayer,
    		player1gold: res.player1.gold,
    		player2gold: res.player2.gold,
    	})
    ).catch(function(e) {
        console.log("error " + e);
    });

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

  cardAction(id, action) {
	fetch('http://localhost:8080/games/66/actions', {
    method: 'POST',
    headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      type: action,
      name: id,
    })
  }).then(result=>result.json())
    .then((res) => 
    	this.setState({
    		elements: res.board.elements,
    		currentPlayer: res.currentPlayer,
    		player1gold: res.player1.gold,
    		player2gold: res.player2.gold,
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
    return (
      <div>
      <ActionSelection 
        selectedCard={this.state.selectedCard} 
        onClose={() => this.closeModal()}
        onTake={() => this.takeCard(this.state.selectedCard)}
        onSell={() => this.sellCard(this.state.selectedCard)}/>
      <Military />
      <Science />
      <div className="game">
        <div className="game-board">
          <Board onClick={(id) => this.handleClick(id)} elements={this.state.elements}/>
        </div>
      </div>
      <DebugDetails 
            currentPlayer={this.state.currentPlayer}
            player1gold={this.state.player1gold}
            player2gold={this.state.player2gold}/>
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
  			  <div className="game">
  				<div className="box">
  					<h2>Test</h2>
  					<div className="button is-primary" onClick={() => this.props.onTake()}>Take</div>
  					<div className="button is-warning" onClick={() => this.props.onSell()}>Sell</div>
  					<div className="button is-primary" onClick={() => this.props.onClose()} >Cancel</div>
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

// ========================================

ReactDOM.render(
  <Game />,
  document.getElementById('root')
);

