package com.moscichowski.wonders

import kotlin.math.max

class GuildFeatureResolver {

    fun pointsForGuild(guild: Guild, game: Game): Int {
        if (guild.type == GuildType.BROWN_SILVER) {
            val playersResourceCards = game.player.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
            val opponentResourceCards = opponent(game).cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
            return max(playersResourceCards, opponentResourceCards)
        } else {
            val playersResourceCards = game.player.cards.count { it.color == CardColor.YELLOW }
            val opponentResourceCards = opponent(game).cards.count { it.color == CardColor.YELLOW }
            return max(playersResourceCards, opponentResourceCards)
        }
    }

    private fun opponent(game: Game): Player {
        return if (game.currentPlayer == 1) game.player1 else game.player2
    }

}