package com.moscichowski.wonders

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class WondersTests {
    @Test
    fun takeCard() {
        val card = Card("TestCard", Resource(gold = 2))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(6), Player(6), board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(board.cards.count(), 0)
        assertEquals(game.player1.cards.first(), card)
        assertEquals(game.currentPlayer, 1)
        assertEquals(game.player1.gold, 4)
    }

    @Test
    fun takeCard2() {
        val card = Card("TestCard", Resource(gold = 4))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(6), Player(6), board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(board.cards.count(), 0)
        assertEquals(game.player1.cards.first(), card)
        assertEquals(game.currentPlayer, 1)
        assertEquals(game.player1.gold, 2)
    }

    @Test
    fun secondPlayerTakeCard() {
        val card = Card("TestCard", Resource(gold = 4))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(6), Player(6), board, currentPlayer = 1)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(board.cards.count(), 0)
        assertEquals(game.player2.cards.first(), card)
        assertEquals(game.currentPlayer, 0)
        assertEquals(game.player2.gold, 2)
    }

    @Test
    fun takeCardTooExpensive() {
        val card = Card("TestCard", Resource(gold = 7))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(6), Player(6), board)
        val startingState = game.copy()
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(startingState, wonders.gameState)
    }

    @Test
    fun secondPlayerTakeCardTooExpensive() {
        val card = Card("TestCard", Resource(gold = 7))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(8), Player(6), board, currentPlayer = 1)
        val startingState = game.copy()
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(startingState, wonders.gameState)
    }

    @Test
    fun takeUnavailableCard() {
        val availableCard = Card("Av Card")
        val parentCard = Card("Parent")
        val availableNode = BoardNode(availableCard)
        val unavailableNode = BoardNode(parentCard, mutableListOf(availableCard))
        val board = Board(mutableListOf(availableNode, unavailableNode))
        val game = Game(Player(1), Player(2), board)
        val startingState = game.copy()
        val wonders = Wonders(game)
        assertFails { wonders.takeAction(TakeCard(Card("Parent"))) }
        assertEquals(startingState, wonders.gameState)
    }

    @Test
    fun takeCardCustomCost() {                         // 7         12         15        16           15
        val card = Card("TestCard", Resource(wood = 1, clay = 2, stone = 3, glass = 4, papyrus = 5))
        val board = Board(mutableListOf(BoardNode(card)))
        val opponent = Player(0)
        opponent.cards.add(Card("OpponentCard", features = mutableListOf(ProvideResource(Resource(wood = 5, clay = 4, stone = 3, glass = 2, papyrus = 1)))))
        val game = Game(Player(70), opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(board.cards.count(), 0)
        assertEquals(game.player1.cards.first(), card)
        assertEquals(game.currentPlayer, 1)
        assertEquals(5, game.player1.gold)
    }

    @Test
    fun currentPlayerChanges() {
        val board = Board(mutableListOf(BoardNode(Card("One")), BoardNode(Card("Two"))))
        val game = Game(Player(0), Player(0), board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(Card("One")))
        wonders.takeAction(TakeCard(Card("Two")))
        assertEquals(0, board.cards.count())
        assertEquals(game.player1.cards.first(), Card("One"))
        assertEquals(game.player2.cards.first(), Card("Two"))
        assertEquals(game.currentPlayer, 0)
    }

    @Test
    fun hiddenCard() {
        val availableCard = Card("Av Card")
        val parentCard = Card("Hidden Parent")
        val availableNode = BoardNode(availableCard)
        val unavailableNode = BoardNode(parentCard, mutableListOf(availableCard), hidden = true)
        val board = Board(mutableListOf(availableNode, unavailableNode))
        val game = Game(Player(1), Player(2), board)
        val wonders = Wonders(game)
        val hiddenCard = wonders.gameState.board.cards.firstOrNull { boardNode -> boardNode.card == null }
        val visibleCard = wonders.gameState.board.cards.firstOrNull { boardNode -> boardNode.card == availableCard }
        assertNotNull(hiddenCard)
        assertNotNull(visibleCard)
        wonders.takeAction(TakeCard(availableCard))
        assertEquals(parentCard, wonders.gameState.board.cards[0].card)
    }

    @Test
    fun hasResourcesBuysCard() {
        val card = Card("Card", cost = Resource(2, 4, 6, 8, 10, 7))
        val availableNode = BoardNode(card)
        val board = Board(mutableListOf(availableNode))
        val player = Player(40)
        val providedResources = Resource(1, 2, 3, 4, 5, 0)
        player.cards.add(Card("ProvidingResourcesCard", features = mutableListOf(ProvideResource(providedResources))))
        val game = Game(player, Player(6), board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(3, game.player1.gold)
    }

    @Test
    fun hasWarehouseBuysCard() {
        val card = Card("Wood Card", Resource(wood = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val player1 = Player(6)
        val warehouseFeatures = mutableListOf<CardFeature>(WoodWarehouse)
        player1.cards.add(Card("Wood warehouse", features = warehouseFeatures))
        val opponent = Player(6)
        opponent.cards.add(Card("Wood provider", features = mutableListOf(ProvideResource(Resource(wood = 2)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(2, player1.gold)
    }
}