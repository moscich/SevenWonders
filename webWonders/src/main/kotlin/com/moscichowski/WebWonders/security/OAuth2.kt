package com.moscichowski.WebWonders.security

data class AuthUser(val user_id: String?, val is_valid: Boolean)

interface OAuth2 {
    fun getUserForToken(token: String): String
}