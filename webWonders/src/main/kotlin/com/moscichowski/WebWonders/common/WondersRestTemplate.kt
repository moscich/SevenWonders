package com.moscichowski.WebWonders.common

import com.moscichowski.WebWonders.security.FacebookAuth
import com.moscichowski.WebWonders.security.OAuth2
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Component
class WondersRestTemplate {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplateBuilder().rootUri("http://localhost:8080").build()
    }

    @Bean
    fun auth(): OAuth2 {
        return FacebookAuth()
    }
}

data class GameCreated(val id: String, val inviteCode: String)