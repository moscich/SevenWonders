package com.moscichowski.wonders

import kotlin.math.max

class GuildFeatureResolver {

    fun pointsForGuild(guild: Guild, game: Game): Int {
        return when (guild.type) {
            GuildType.BROWN_SILVER -> {
                val playersResourceCards = game.player1.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                val opponentResourceCards = game.player2.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                max(playersResourceCards, opponentResourceCards)
            }
            GuildType.WONDERS -> {
                val player1Wonders = game.player1.wonders.count { it.first }
                val player2Wonders = game.player2.wonders.count { it.first }
                2 * max(player1Wonders, player2Wonders)
            }
            GuildType.GOLD -> {
                max(game.player1.gold, game.player2.gold) / 3
            }
            else -> {
                val playersResourceCards = game.player1.cards.count { it.color == guild.cardColor() }
                val opponentResourceCards = game.player2.cards.count { it.color == guild.cardColor() }
                max(playersResourceCards, opponentResourceCards)
            }
        }
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