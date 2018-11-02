package com.moscichowski.wonders

class Requires8WondersError: Error()
class WrongNumberOfCards(val cards: List<List<Any>>): Error("expected [20, 20, 20] cards, received ${cards.map { it.count() }}")
