package com.moscichowski.wonders

class Wonders {
    val gameState: Game

    constructor(state: Game) {
        this.gameState = state
    }

    fun takeAction(action: Action) {
        when (action) {
            is TakeCard -> {
                val wantedNode = gameState.board.cards.find { node ->
                    node.card == action.card
                }
                if (wantedNode?.descendants?.isEmpty()!!) {
                    val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2
                    val opponent = if (gameState.currentPlayer == 1) gameState.player1 else gameState.player2

                    val opponentResource = opponent.resources()

                    val requiredGold = action.card.cost.clay * (opponentResource.clay + 2) +
                            action.card.cost.wood * (opponentResource.wood + 2) +
                            action.card.cost.stone * (opponentResource.stone + 2) +
                            action.card.cost.papyrus * (opponentResource.papyrus + 2) +
                            action.card.cost.glass * (opponentResource.glass + 2) +
                            action.card.cost.gold

                    if (player.gold >= requiredGold) {
                        gameState.board.cards.remove(wantedNode)
                        gameState.board.cards.forEach { node: BoardNode ->
                            node.descendants.remove(wantedNode.card)
                        }
                        player.cards.add(action.card)
                        player.gold -= requiredGold
                        gameState.currentPlayer = (gameState.currentPlayer + 1) % 2
                    }
                } else {
                    throw Exception("Trututu")
                }
            }
            is BuildWonder -> print(action.wonderNo)
        }

    }
}

sealed class Action
data class TakeCard(val card: Card) : Action()
data class BuildWonder(val wonderNo: Int) : Action()

fun main(args: Array<String>) {
    val availableCard = Card("Av Card")
    val parentCard = Card("Hidden Parent")
    val availableNode = BoardNode(availableCard)
    val unavailableNode = BoardNode(parentCard, mutableListOf(availableCard))
    val board = Board(mutableListOf(availableNode, unavailableNode))
    val game = Game(Player(1), Player(2), board)
    val wonders = Wonders(game)
    print(wonders.gameState.board.cards)
}