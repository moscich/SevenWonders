package com.moscichowski.wonders

class CardSeller: ActionPerformer() {
    fun sellCard(game: Game, action: SellCard) {
        val player = if (game.currentPlayer == 0) game.player1 else game.player2
        val wantedNode = game.board.cards.find { node ->
            node.card == action.card
        } ?: throw Error()

        game.board.cards.remove(wantedNode)
        game.board.cards.forEach { node: BoardNode ->
            node.descendants.remove(wantedNode.card)
        }

        player.gold += 2 + player.goldCardsCount()
    }
}