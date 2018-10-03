package com.moscichowski.wonders

import org.junit.Test
import kotlin.test.*

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
        assertFails { wonders.takeAction(TakeCard(card)) }
        assertEquals(startingState, wonders.gameState)
    }

    @Test
    fun secondPlayerTakeCardTooExpensive() {
        val card = Card("TestCard", Resource(gold = 7))
        val board = Board(mutableListOf(BoardNode(card)))
        val game = Game(Player(8), Player(6), board, currentPlayer = 1)
        val startingState = game.copy()
        val wonders = Wonders(game)
        assertFails { wonders.takeAction(TakeCard(card)) }
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
    fun hasWoodWarehouseBuysCard() {
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

    @Test
    fun hasClayWarehouseBuysCard() {
        val card = Card("Clay Card", Resource(clay = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val player1 = Player(6)
        val warehouseFeatures = mutableListOf<CardFeature>(ClayWarehouse)
        player1.cards.add(Card("Clay warehouse", features = warehouseFeatures))
        val opponent = Player(6)
        opponent.cards.add(Card("Clay provider", features = mutableListOf(ProvideResource(Resource(clay = 2)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(2, player1.gold)
    }

    @Test
    fun hasStoneWarehouseBuysCard() {
        val card = Card("Stone Card", Resource(stone = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val player1 = Player(6)
        val warehouseFeatures = mutableListOf<CardFeature>(StoneWarehouse)
        player1.cards.add(Card("Stone warehouse", features = warehouseFeatures))
        val opponent = Player(6)
        opponent.cards.add(Card("Stone provider", features = mutableListOf(ProvideResource(Resource(stone = 2)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(2, player1.gold)
    }

    @Test
    fun buildUnavailableWonder() {
        val card = Card("Stone Card", Resource(stone = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder")
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, Wonder("Different Wonder")))
        val game = Game(player1, Player(6), board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(BuildWonder(card, wonder))
            fail()
        } catch (err : WonderBuildFailed) {
            assertEquals("Test", err.something)
        }
        assertEquals(game.currentPlayer, 0)
    }

    @Test
    fun wonderAlreadyBuilt() {
        val card = Card("Stone Card", Resource(stone = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder")
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(true, wonder))
        val game = Game(player1, Player(6), board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(BuildWonder(card, wonder))
            fail()
        } catch (err : WonderBuildFailed) {
            assertEquals("Test", err.something)
        }
        assertEquals(game.currentPlayer, 0)
    }

    @Test
    fun wonderTooExpensive() {
        val card = Card("Stone Card", Resource(stone = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 7))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val game = Game(player1, Player(6), board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(BuildWonder(card, wonder))
            fail()
        } catch (err : Error) {
        }
        assertEquals(game.currentPlayer, 0)
    }

    @Test
    fun simpleWonderBuild() {
        val card = Card("Stone Card", Resource(stone = 4))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val game = Game(player1, Player(6), board)
        val wonders = Wonders(game)
        wonders.takeAction(BuildWonder(card, wonder))
        assertTrue(player1.wonders.first().first)
        assertEquals(4, player1.gold)
        assertEquals(game.currentPlayer, 1)
    }

    @Test
    fun removeBrownCardWonderFeature() {
        val card = Card("Some card", Resource())
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2), listOf(DestroyBrownCard))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val opponent = Player(6)
        val brownCard = Card("Brown card")
        opponent.cards.add(brownCard)
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(BuildWonder(card, wonder, brownCard))
        assertTrue(player1.wonders.first().first)
        assertEquals(4, player1.gold)
        assertEquals(0, board.cards.count())
        assertEquals(0, opponent.cards.count())
        assertEquals(game.currentPlayer, 1)
    }

    @Test
    fun failWhenRemovingNonBrownCard() {
        val card = Card("Some card", Resource())
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2), listOf(DestroyBrownCard))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val opponent = Player(6)
        val nonBrownCard = Card("Non Brown card", CardColor.SILVER)
        opponent.cards.add(nonBrownCard)
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(BuildWonder(card, wonder, nonBrownCard))
            fail()
        } catch (err : WonderBuildFailed) {
            assertEquals("Test", err.something)
        }

        assertFalse(player1.wonders.first().first)
        assertEquals(6, player1.gold)
        assertEquals(1, board.cards.count())
        assertEquals(1, opponent.cards.count())
        assertEquals(game.currentPlayer, 0)
    }

    @Test
    fun failWhenRemovingNonSilverCard() {
        val card = Card("Some card", Resource())
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2), listOf(DestroySilverCard))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val opponent = Player(6)
        val nonSilverCard = Card("Non Silver card", CardColor.BROWN)
        opponent.cards.add(nonSilverCard)
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(BuildWonder(card, wonder, nonSilverCard))
            fail()
        } catch (err : WonderBuildFailed) {
            assertEquals("Test", err.something)
        }

        assertFalse(player1.wonders.first().first)
        assertEquals(6, player1.gold)
        assertEquals(1, board.cards.count())
        assertEquals(1, opponent.cards.count())
    }

    @Test
    fun removeSilverCardWonderFeature() {
        val card = Card("Some card", Resource())
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2), listOf(DestroySilverCard))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val opponent = Player(6)
        val silverCard = Card("Silver card", CardColor.SILVER)
        opponent.cards.add(silverCard)
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(BuildWonder(card, wonder, silverCard))
        assertTrue(player1.wonders.first().first)
        assertEquals(4, player1.gold)
        assertEquals(0, board.cards.count())
        assertEquals(0, opponent.cards.count())
    }

    @Test
    fun buildWonderWithExtraTurnFeature() {
        val card = Card("Some card", Resource())
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", Resource(gold = 2), listOf(ExtraTurn))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(false, wonder))
        val game = Game(player1, Player(6), board)
        val wonders = Wonders(game)
        wonders.takeAction(BuildWonder(card, wonder))
        assertTrue(player1.wonders.first().first)
        assertEquals(4, player1.gold)
        assertEquals(0, board.cards.count())
        assertEquals(0, game.currentPlayer)
    }

    @Test
    fun properCardCostWhenPlayerHasProvideOneSilverResource() {
        val card = Card("Some card", Resource(papyrus = 1, glass = 1))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", features = listOf(ProvideSilverResource))
        val player1 = Player(8)
        player1.wonders = mutableListOf(Pair(true, wonder))
        val opponent = Player(6)
        opponent.cards.add(Card("Forum", features = listOf(ProvideResource(Resource(papyrus = 1, glass = 2)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(0, board.cards.count())
        assertEquals(5, player1.gold)
    }
}

data class Wonder(val name: String, val cost: Resource = Resource(), val features: List<CardFeature> = mutableListOf())

//@SuppressWarnings("lowercase")
fun Card(name: String, cost: Resource = Resource(), features: List<CardFeature> = mutableListOf()): Card = Card(name, CardColor.BROWN, cost, features)