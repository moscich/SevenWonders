package com.moscichowski.WebWonders.security

import com.moscichowski.WebWonders.InvalidTokenError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import java.time.Instant

data class FacebookAuthUserWrapper(val data: AuthUser)
data class AuthUser(val user_id: String?, val is_valid: Boolean, val expires_at: Long)
data class AuthUserDetails(val name: String, val id: String)

class FacebookAuth: OAuth2 {
    override fun getUserNameForToken(userId: String): String {
        val entity = restTemplate.getForEntity("https://graph.facebook.com/$userId?access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", AuthUserDetails::class.java)
        return entity.body!!.name
    }

    @Autowired
    lateinit var restTemplate: RestTemplate

    val cache = hashMapOf<String, Pair<Instant, String>>()

    override fun getUserIdForToken(token: String): String {
        println("cached = ${cache[token]?.first}")
        if (cache[token]?.first ?: Instant.ofEpochSecond(0) > Instant.now()) {
            return cache[token]!!.second
        }
        val entity = restTemplate.getForEntity("https://graph.facebook.com/debug_token?input_token=$token&access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", FacebookAuthUserWrapper::class.java)
        val userId = entity.body?.data?.user_id
        if (entity.statusCode.is2xxSuccessful && entity.body?.data?.is_valid == true && userId != null) {
            println("Instant.ofEpochMilli(entity.body!!.data.expires_at) = ${Instant.ofEpochSecond(entity.body!!.data.expires_at)}")
            cache[token] = Pair(Instant.ofEpochSecond(entity.body!!.data.expires_at), userId)
            return userId
        } else {
            throw InvalidTokenError()
        }
    }
}