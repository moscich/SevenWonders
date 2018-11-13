class WondersService {
	constructor() {
		console.log("Im being made mate")
		this.smth = 42
	}
	
	createGame() {
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			if (token == null) {
				reject("No token")
				return
			}
  		fetch("http://localhost:8080/games", {
  			method: 'POST',
			headers: {
				'Accept': 'application/json',
			    'Content-Type': 'application/json',
			    'Authorization': 'Bearer ' + token
			}
		})
		.then(function(res) {
    		if (!res.ok) {
    			reject()
    		}
    		resolve(res.json())
    	})
	})
	}

	getGames() {
		console.log("token = " + this.token)
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			console.log("token " + token)
			if (token == null) {
				reject("No token")
				return
			}
  		fetch("http://localhost:8080/games", {
			headers: {
				'Accept': 'application/json',
			    'Content-Type': 'application/json',
			    'Authorization': 'Bearer ' + token
			}
		})
    	.then(function(res) {
    		if (!res.ok) {
    			reject()
    		}
    		return res.json()
    	})
    	.then(function(res) {
    		console.log(res)
    		console.log(JSON.stringify(res))
    		
    		resolve(res) 
    	})
	});
	}

	getGame(gameNo) {
		console.log("token = " + this.token)
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			console.log("token " + token)
			if (token == null) {
				reject("No token")
				return
			}
  		fetch("http://localhost:8080/games/"+gameNo, {
			headers: {
				'Accept': 'application/json',
			    'Content-Type': 'application/json',
			    'Authorization': 'Bearer ' + token
			}
		})
    	.then(function(res) {
    		if (!res.ok) {
    			reject()
    		}
    		return res.json()
    	})
    	.then(function(res) {
    		resolve(res) 
    	})
	});
	}
	
}

const wondersService = new WondersService()

export default wondersService