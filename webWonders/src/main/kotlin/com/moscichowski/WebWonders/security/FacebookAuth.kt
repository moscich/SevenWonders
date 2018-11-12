package com.moscichowski.WebWonders.security

import com.moscichowski.WebWonders.InvalidTokenError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate

data class FacebookAuthUserWrapper(val data: AuthUser)

class FacebookAuth: OAuth2 {

    @Autowired
    lateinit var restTemplate: RestTemplate

    override fun getUserForToken(token: String): String {
        val entity = restTemplate.getForEntity("https://graph.facebook.com/debug_token?input_token=$token&access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", FacebookAuthUserWrapper::class.java)
        val userId = entity.body?.data?.user_id
        if (entity.statusCode.is2xxSuccessful && entity.body?.data?.is_valid == true && userId != null) {
            return userId
        } else {
            throw InvalidTokenError()
        }
    }
}