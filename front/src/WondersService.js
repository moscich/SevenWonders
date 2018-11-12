class WondersService {
	constructor() {
		console.log("Im being made mate")
		this.smth = 42
	}
	
	getGames() {
		console.log("token = " + this.token)
		const token = this.token
		return new Promise(function(resolve, reject) {
			console.log("token " + token)
			if (token == null) {
				console.log("token2 " + token)
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
		const token = this.token
		return new Promise(function(resolve, reject) {
			console.log("token " + token)
			if (token == null) {
				console.log("token2 " + token)
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