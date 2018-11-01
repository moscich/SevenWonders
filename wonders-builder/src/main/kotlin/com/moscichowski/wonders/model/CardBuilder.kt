package com.moscichowski.wonders.model

class CardBuilder {
    fun getCards(): List<Card> {
        return listOf(Card("", CardColor.RED))
    }
}