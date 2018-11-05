package com.moscichowski.WebWonders.repository

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Wonders

interface GameRepository {
    fun getWondersGame(id: String): Wonders
    fun storeWondersGame(wonders: Wonders)
    fun storeAction(gameId: String, action: Action)
    fun storeInitialState(settings: GameInitialSettings): Int
}