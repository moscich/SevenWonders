package com.moscichowski.WebWonders.security

interface OAuth2 {
    fun getUserIdForToken(token: String): String
    fun getUserNameForToken(userId: String): String
}