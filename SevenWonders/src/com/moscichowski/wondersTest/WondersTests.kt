package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
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
        opponent.cards.add(Card("Providing silver", features = listOf(ProvideResource(Resource(papyrus = 1, glass = 2)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        wonders.takeAction(TakeCard(card))
        assertEquals(0, board.cards.count())
        assertEquals(5, player1.gold)
    }

    @Test
    fun properCardCostWhenPlayerHasProvideOneBrownResource() {
        val card = Card("Some card", Resource(wood = 1, clay = 1, stone = 1))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val wonder = Wonder("Some wonder", features = listOf(ProvideBrownResource))
        val player1 = Player(8)
        player1.wonders = mutableListOf(Pair(true, wonder))
        val opponent = Player(6)
        opponent.cards.add(Card("Providing brown", features = listOf(ProvideResource(Resource(wood = 1, clay = 2, stone = 3)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(TakeCard(card))
            assertEquals(0, board.cards.count())
            assertEquals(1, player1.gold)
        } catch (e: Error) {
            fail()
        }
    }

    @Test
    fun combineAllResourceProviders() {
        val card = Card("Some card", Resource(wood = 1, clay = 1, stone = 1, glass = 1, papyrus = 1))
        val node = BoardNode(card)
        val board = Board(mutableListOf(node))
        val provideBrownWonder = Wonder("Some wonder", features = listOf(ProvideBrownResource))
        val provideSilverWonder = Wonder("Some wonder", features = listOf(ProvideSilverResource))
        val player1 = Player(6)
        player1.wonders = mutableListOf(Pair(true, provideBrownWonder), Pair(true, provideSilverWonder))
        player1.cards.add(Card("Brown Card", features = listOf(ProvideBrownResource)))
        player1.cards.add(Card("Silver Card", features = listOf(ProvideSilverResource)))
        val opponent = Player(6)
        opponent.cards.add(Card("Lot of resources", features = listOf(ProvideResource(Resource(wood = 1, clay = 2, stone = 3, papyrus = 4, glass = 5)))))
        val game = Game(player1, opponent, board)
        val wonders = Wonders(game)
        try {
            wonders.takeAction(TakeCard(card))
            assertEquals(0, board.cards.count())
            assertEquals(3, player1.gold)
        } catch (e: Error) {
            fail()
        }
    }

    @Test
    fun removeGoldFeature() {
        val (player1, wonder) = player(wonderWithFeature(RemoveGold))
        val (wonders, card, opponent) = game(player1)

        wonders.takeAction(BuildWonder(card, wonder))

        assertEquals(3, opponent.gold)
    }

    @Test
    fun removeGoldFeatureDonGoNegative() {
        val (player1, wonder) = player(wonderWithFeature(RemoveGold))
        val (wonders, card, opponent) = game(player1, Player(1))

        wonders.takeAction(BuildWonder(card, wonder))

        assertEquals(0, opponent.gold)
    }

    @Test
    fun addGoldFeature() {
        val (player1, wonder) = player(wonderWithFeature(AddGold(5)))
        val (wonders, card) = game(player1, Player(1))

        wonders.takeAction(BuildWonder(card, wonder))

        assertEquals(11, player1.gold)
    }

    @Test
    fun addGoldBuyCardFeature() {
        val player1 = Player(6)
        val (wonders, card) = game(player1, Player(6), cardWithFeature(AddGold(3)))

        wonders.takeAction(TakeCard(card))

        assertEquals(9, player1.gold)
    }

    @Test
    fun militarySimple() {
        val player1 = Player(6)
        val (wonders, card) = game(player1, Player(6), cardWithFeature(Military(2)))
        wonders.takeAction(TakeCard(card))
        assertEquals(2, wonders.gameState.military)
    }

    @Test
    fun militaryOpponent() {
        val player1 = Player(6)
        val (wonders, card) = game(player1, Player(6), cardWithFeature(Military(2)), 1)
        wonders.takeAction(TakeCard(card))
        assertEquals(-2, wonders.gameState.military)
    }

    @Test
    fun militaryTakeGold() {
        val (wonders, cards, opponent) = game((0..10).map { Military(2) }, Player(10))

        wonders.gameState.military = 2
        wonders.takeAction(TakeCard(cards[0]))

        assertEquals(8, opponent.gold)

        wonders.gameState.military = 2
        wonders.gameState.currentPlayer = 0

        wonders.takeAction(TakeCard(cards[1]))

        assertEquals(8, opponent.gold)
        wonders.gameState.military = 4
        wonders.gameState.currentPlayer = 0
        wonders.takeAction(TakeCard(cards[1]))

        assertEquals(3, opponent.gold)

        wonders.gameState.military = -2
        wonders.gameState.currentPlayer = 1

        wonders.takeAction(TakeCard(cards[2]))

        assertEquals(4, wonders.gameState.player1.gold)

        wonders.gameState.player1.gold = 6
        wonders.gameState.military = -5
        wonders.gameState.currentPlayer = 1
        wonders.takeAction(TakeCard(cards[3]))
        assertEquals(1, wonders.gameState.player1.gold)
        assertEquals(0, wonders.gameState.militaryThresholds.count())
    }

    @Test
    fun militaryActivateMultipleAtOnce() {

    }

    @Test
    fun sellCard() {
        val (wonders, card, player) = game()

        wonders.takeAction(SellCard(card))

        assertEquals(8, player.gold)
    }

    @Test
    fun sellCardGoldCards() {
        val (wonders, card, player) = game()

        player.cards.add(cardWithColor(CardColor.GOLD))
        player.cards.add(cardWithColor(CardColor.GOLD))

        wonders.takeAction(SellCard(card))

        assertEquals(10, player.gold)
    }

    @Test
    fun takeCardForFree() {
        val (wonders, card, player) = gameWithCard(Card("Test", cost = Resource(wood = 2, gold = 1), freeSymbol = CardFreeSymbol.SWORD))
        player.cards.add(Card("Free symbol", features = listOf(FreeSymbol(CardFreeSymbol.SWORD))))

        wonders.takeAction(TakeCard(card))

        assertEquals(6, player.gold)
    }

    @Test
    fun takeScience() {
        val (wonders, card, player) = gameWithCard(Card("Test", features = listOf(Science(ScienceSymbol.WHEEL))))
        player.cards.add(Card("Science", features = listOf(Science(ScienceSymbol.WHEEL))))

        wonders.takeAction(TakeCard(card))

        assertEquals(GameState.CHOOSE_SCIENCE, wonders.gameState.state)
    }

    @Test
    fun cantTakeCardWhenNotInRegularState() {
        val (wonders, card) = gameWithCard(Card("Test"))
        wonders.gameState.state = GameState.CHOOSE_SCIENCE

        assertFails { wonders.takeAction(TakeCard(card)) }
    }

    @Test
    fun chooseScience() {
        val wonders = Wonders(Game(Player(6), Player(6), Board(listOf())))
        wonders.gameState.state = GameState.CHOOSE_SCIENCE
        wonders.gameState.scienceTokens.add(Pair(null, ScienceToken.ENGINEERING))

        wonders.takeAction(ChooseScience(ScienceToken.ENGINEERING))

        assertEquals(0, wonders.gameState.scienceTokens[0].first)
        assertEquals(ScienceToken.ENGINEERING, wonders.gameState.scienceTokens[0].second)
    }

    @Test
    fun architecture() {
        val (wonders, card, player) = game()
        val wonder = Wonder("Some wonder", Resource(1, 1, 1, 1, 1))
        wonders.gameState.player2.cards.add(Card("Providing", features = listOf(ProvideResource(Resource(1, 2, 3, 4, 5)))))
        wonders.gameState.scienceTokens.add(Pair(0, ScienceToken.ARCHITECTURE))
        player.wonders = listOf(Pair(false, wonder))
        player.gold = 16

        wonders.takeAction(BuildWonder(card, wonder))

        assertEquals(4, player.gold)
    }

    @Test
    fun goldCantGetNegative() {
        val player = Player(6)
        player.gold -= 7
        assertEquals(0, player.gold)
    }

    @Test
    fun resourceCombiner() {
        val one = Resource(wood = 1, clay = 1)
        val two = Resource(glass = 1, papyrus = 1)

        val combined = one.combine(two)

        val expected = listOf(Resource(wood = 1, clay = 1, glass = 1), Resource(wood = 1, clay = 1, papyrus = 1))
        val filtered = expected.filter { !combined.contains(it) }
        assertEquals(0, filtered.count())
    }

    @Test
    fun testing() {
        val one = Resource(wood = 1, clay = 1, stone = 1, glass = 1, papyrus = 1)
        val two = Resource(wood = 1)

        val combined = one.combine(two)
        println(combined)
//        val expected = listOf(Resource(wood = 1, glass = 1), Resource(wood = 1, papyrus = 1), Resource(clay = 1, papyrus = 1), Resource(clay = 1, papyrus = 1))
//        val filtered = expected.filter { !combined.contains(it) }
//        assertEquals(filtered, listOf())
//        assertSame(expected, combined)
    }
}

fun cardWithColor(color: CardColor): Card {
    return Card("Color card test", color)
}

fun cardWithFeature(feature: CardFeature): Card {
    return Card("Fixture Card", features = listOf(feature))
}

fun player(wonder: Wonder): Pair<Player, Wonder> {
    val player1 = Player(6)
    player1.wonders = mutableListOf(Pair(false, wonder))
    return Pair(player1, wonder)
}

fun wonderWithFeature(feature: CardFeature): Wonder {
    return Wonder("Fixture wonder", features = listOf(feature))
}

fun game(features: List<CardFeature>, opponent: Player = Player(6)): Triple<Wonders, List<Card>, Player> {
    val cards = features.map { Card("Test", features = listOf(it)) }// listOf(Card("Test"), )
    val nodes = cards.map { BoardNode(it) }
    val board = Board(nodes)
    val game = Game(Player(6), opponent, board)
    val wonders = Wonders(game)
    return Triple(wonders, cards, opponent)
}

fun gameWithCard(card: Card = Card("Some card")): Triple<Wonders, Card, Player> {
    val node = BoardNode(card)
    val board = Board(mutableListOf(node))

    val player1 = Player(6)
    val game = Game(player1, Player(6), board)
    val wonders = Wonders(game)
    return Triple(wonders, card, player1)
}

fun game(player: Player, opponent: Player = Player(6), card: Card = Card("Some card"), currentPlayer: Int = 0): Triple<Wonders, Card, Player> {
    val node = BoardNode(card)
    val board = Board(mutableListOf(node))

    val game = Game(player, opponent, board)
    game.currentPlayer = currentPlayer
    val wonders = Wonders(game)
    return Triple(wonders, card, opponent)
}

fun game(): Triple<Wonders, Card, Player> {
    val card = Card("Test")
    val node = BoardNode(card)
    val board = Board(mutableListOf(node))

    val player1 = Player(6)
    val game = Game(player1, Player(6), board)
    val wonders = Wonders(game)
    return Triple(wonders, card, player1)
}

data class Wonder(val name: String, val cost: Resource = Resource(), val features: List<CardFeature> = mutableListOf())

//@SuppressWarnings("lowercase")
fun Card(name: String, cost: Resource = Resource(), features: List<CardFeature> = mutableListOf(), freeSymbol: CardFreeSymbol? = null): Card = Card(name, CardColor.BROWN, cost, features, freeSymbol)