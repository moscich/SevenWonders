package com.moscichowski.wonders

class CardTaker : ActionPerformer() {
    fun takeCard(game: Game, action: TakeCard) {
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

        resolveCommonFeatures(game, action.card.features, player)

        if (player.alreadyHasThisScienceSymbol(action.card.features)) {
            game.state = GameState.CHOOSE_SCIENCE
        }

        player.cards.add(action.card)
        game.currentPlayer = (game.currentPlayer + 1) % 2
    }
}