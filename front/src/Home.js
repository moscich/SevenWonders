import React from 'react'
import { Route } from 'react-router-dom'
import wondersService from './WondersService'

const Home = () => (
  <div>
    <h1>Game list and shit</h1>
    <Route render={({ history }) => (
    	<XDD history = {history}/>
  )} />
  </div>
)

export default Home

class XDD extends React.Component {
	constructor(props) {
		super(props)
		
		wondersService.getGames()
		.then(function(res) {
    		console.log(res)
    	})
		.catch(function(e) {
    		console.log("err = " + e)
    		props.history.push('/login')
    	});
  }

  render() {
    return(
      <p>ih a ha</p>
    )
  }
}

