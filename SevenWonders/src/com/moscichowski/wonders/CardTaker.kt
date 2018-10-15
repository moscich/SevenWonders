package com.moscichowski.wonders

class CardTaker : ActionPerformer() {
    private lateinit var game: Game
    private lateinit var card: Card
    override fun hasPromo(): Boolean {
        return game.doesCurrentPlayerHaveScience(ScienceToken.CONSTRUCTION) && card.color == CardColor.BLUE
    }

    override fun additionalMilitaryPoints(): Int {
        return if (game.doesCurrentPlayerHaveScience(ScienceToken.STRATEGY)) { 1 } else { 0 }
    }

    fun takeCard(game: Game, action: TakeCard) {
        this.game = game
        this.card = action.card
        val (player, wantedNode) = boardCheck(game, action.card)

        var requiredGold = required2(game, player, action.card.cost)

        if (player.hasFreeSymbol(action.card.freeSymbol)) {
            if (game.doesCurrentPlayerHaveScience(ScienceToken.CITY_PLANNING)) {
                player.gold += 4
            }
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

}