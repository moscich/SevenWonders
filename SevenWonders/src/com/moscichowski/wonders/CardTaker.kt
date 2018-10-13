package com.moscichowski.wonders

class CardTaker : ActionPerformer() {
    private lateinit var game: Game
    private lateinit var card: Card
    override fun hasPromo(): Boolean {
        return hasConstruction() && card.color == CardColor.BLUE
    }

    private fun hasConstruction() =
            game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.CONSTRUCTION } != null

    fun takeCard(game: Game, action: TakeCard) {
        this.game = game
        this.card = action.card
        val (player, wantedNode) = boardCheck(game, action.card)

        var requiredGold = required2(game, player, action.card.cost)

        if (player.hasFreeSymbol(action.card.freeSymbol)) {
            requiredGold = 0
        }

        if (player.gold < requiredGold) {
            throw Error()
        }

        game.board.cards.remove(wantedNode)
        game.board.cards.forEach { node: BoardNode ->
            node.descendants.remove(wantedNode.card)
        }

        player.gold -= requiredGold

        if (doesOpponentHaveEconomy(game)) {
            game.opponent.gold += (requiredGold - action.card.cost.gold)
        }

        resolveCommonFeatures(game, action.card.features, player)

        if (player.alreadyHasThisScienceSymbol(action.card.features)) {
            game.state = GameState.CHOOSE_SCIENCE
        }

        player.cards.add(action.card)
        game.currentPlayer = (game.currentPlayer + 1) % 2
    }

    private fun doesOpponentHaveEconomy(game: Game): Boolean {
        val opponentIndex = if (game.currentPlayer == 0) { 1 } else { 0 }
            return game.scienceTokens.find { it.first == opponentIndex && it.second == ScienceToken.ECONOMY } != null
}
}