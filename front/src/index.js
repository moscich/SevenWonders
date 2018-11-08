import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';

class Board extends React.Component {

  render() {
	return (
		<div>

		<div className="card" style={{left:"44%", top:200}}/>
		<div className="card" style={{left:"56%", top:200}}/>
		
		<div className="card" style={{left:"38%", top:300}}/>
		<div className="card" style={{left:"50%", top:300}}/>
		<div className="card" style={{left:"62%", top:300}}/>

        <div className="card" style={{left:"32%", top:400}}/>
		<div className="card" style={{left:"44%", top:400}}/>
		<div className="card" style={{left:"56%", top:400}}/>
		<div className="card" style={{left:"68%", top:400}}/>

		<div className="card" style={{left:"26%", top:500}}/>
		<div className="card" style={{left:"38%", top:500}}/>
		<div className="card" style={{left:"50%", top:500}}/>
		<div className="card" style={{left:"62%", top:500}}/>
		<div className="card" style={{left:"74%", top:500}}/>

		<div className="card" style={{left:"20%", top:600}}/>
        <div className="card" style={{left:"32%", top:600}}/>
		<div className="card" style={{left:"44%", top:600}}/>
		<div className="card" style={{left:"56%", top:600}}/>
		<div className="card" style={{left:"68%", top:600}}/>
		<div className="card" style={{left:"80%", top:600}}/>

		</div>
    );
  }
}

class Game extends React.Component {
  render() {
    return (
      <div className="game">
        <div className="game-board">
          <Board />
        </div>
      </div>
    );
  }
}

// ========================================

ReactDOM.render(
  <Game />,
  document.getElementById('root')
);

