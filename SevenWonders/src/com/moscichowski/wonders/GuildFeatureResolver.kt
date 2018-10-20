package com.moscichowski.wonders

import kotlin.math.max

class GuildFeatureResolver {

    fun pointsForGuild(guild: Guild, game: Game): Int {
        return when (guild.type) {
            GuildType.BROWN_SILVER -> {
                val playersResourceCards = game.player.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                val opponentResourceCards = opponent(game).cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                max(playersResourceCards, opponentResourceCards)
            }
            else -> {
                val playersResourceCards = game.player.cards.count { it.color == guild.cardColor() }
                val opponentResourceCards = opponent(game).cards.count { it.color == guild.cardColor() }
                max(playersResourceCards, opponentResourceCards)
            }
        }
    }

    private fun opponent(game: Game): Player {
        return if (game.currentPlayer == 1) game.player1 else game.player2
    }

    private fun Guild.cardColor(): CardColor? {
        return when(this.type) {
            GuildType.YELLOW -> CardColor.YELLOW
            GuildType.RED -> CardColor.RED
            GuildType.GREEN -> CardColor.GREEN
            GuildType.BLUE -> CardColor.BLUE
            else -> null
        }
    }
}