class WondersService {
	
	checkSession() {
		const token = sessionStorage.getItem('secret')
		if (token == null) {
			return new Promise(function(resolve, reject) {
				reject("no token")
			})
		} 
		const now = new Date()
		var expireDate = new Date(0)
		expireDate.setUTCSeconds(sessionStorage.getItem('secret_expiration'))

		if (expireDate < now) {
			return new Promise(function(resolve, reject) {
				reject("expired")
			})
		}
		return null
	}

	joinGame(inviteCode) {
		const check = this.checkSession()
		if (check != null) {
			return check
		}
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			
			const body = {inviteCode: inviteCode}

  		fetch("http://localhost:8080/games", {
  			method: 'PUT',
			headers: {
				'Accept': 'application/json',
			    'Content-Type': 'application/json',
			    'Authorization': 'Bearer ' + token
			},
			body: JSON.stringify(body)
		})
		.then(function(res) {
    		if (!res.ok) {
    			reject()
    		}
    		resolve(res.json())
    	})
	})
	}

	takeAction(gameNo, id, action, param1, playerNo) {

		const check = this.checkSession()
			if (check != null) {
				return check
			}
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
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

			fetch('http://localhost:8080/games/'+gameNo+'/actions', {
			    method: 'POST',
			    headers: {
				    'Accept': 'application/json',
				    'Content-Type': 'application/json',
				    'Authorization': 'Bearer ' + token
			    },
			    body: JSON.stringify(body)
	  		})
			.then(function(res) {
	    		if (!res.ok) {
	    			reject()
	    		}
    			resolve(res.json())
    		})
	 	})
	}

	createGame() {
		const check = this.checkSession()
		if (check != null) {
			return check
		}
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			
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
		const check = this.checkSession()
		if (check != null) {
			return check
		}
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			
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
    		resolve(res) 
    	})
	});
	}

	getGame(gameNo) {
		console.log("reloading")
		const check = this.checkSession()
		if (check != null) {
			return check
		}
		const token = sessionStorage.getItem('secret')
		return new Promise(function(resolve, reject) {
			
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