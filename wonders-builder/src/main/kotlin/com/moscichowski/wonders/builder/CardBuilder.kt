package com.moscichowski.wonders.builder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.model.*
import java.io.File
import java.nio.file.Paths


class CardBuilder {
    fun getCards(epoch: Int): List<Card> {
        val resource = this.javaClass.classLoader.getResource("cards.json")
        val json = resource.openStream().bufferedReader().use { it.readText() }
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CardJsonModule())
        val readValue: List<List<Card>> = objectMapper.readValue(json, object : TypeReference<List<List<Card>>>() {})
        return readValue[epoch]
    }

    fun tests() {
        val cards = listOf(
                Card("sawmill", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(2)))),
                Card("brickyard", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(clay = 2)))),
                Card("shelf quarry", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(stone = 2)))),
                Card("glass blower", CardColor.SILVER, features = listOf(ProvideResource(Resource(glass = 1)))),
                Card("drying room", CardColor.SILVER, features = listOf(ProvideResource(Resource(papyrus = 1)))),
                Card("walls", CardColor.RED, Resource(stone = 2), features = listOf(Military(2))),
                Card("forum", CardColor.YELLOW, Resource(clay = 1, gold = 3), features = listOf(ProvideSilverResource)),
                Card("caravansery", CardColor.YELLOW, Resource(glass = 1, papyrus = 1, gold = 2), features = listOf(ProvideBrownResource)),
                Card("customs house", CardColor.YELLOW, Resource(gold = 4), features = listOf(Customs)),
                Card("tribunal", CardColor.BLUE, Resource(wood = 2, glass = 1), features = listOf(VictoryPoints(5))),
                Card("horse brereders", CardColor.RED, Resource(clay = 2, wood = 1), features = listOf(Military(1)), freeSymbol = CardFreeSymbol.HORSESHOE),
                Card("barracks", CardColor.RED, Resource(gold = 3), features = listOf(Military(1)), freeSymbol = CardFreeSymbol.SWORD),
                Card("archery range", CardColor.RED, Resource(wood = 1, stone = 1, papyrus = 1), features = listOf(Military(2), FreeSymbol(CardFreeSymbol.TARGET))),
                Card("parade ground", CardColor.RED, Resource(clay = 2, glass = 1), features = listOf(Military(2), FreeSymbol(CardFreeSymbol.HELMET))),
                Card("library", CardColor.GREEN, Resource(wood = 1, stone = 1, glass = 1), features = listOf(Science(ScienceSymbol.INK), VictoryPoints(2)), freeSymbol = CardFreeSymbol.BOOK),
                Card("dispensary", CardColor.GREEN, Resource(clay = 2, stone = 1), features = listOf(Science(ScienceSymbol.HERBS), VictoryPoints(2)), freeSymbol = CardFreeSymbol.COG),
                Card("school", CardColor.GREEN, Resource(wood = 1, papyrus = 2), features = listOf(Science(ScienceSymbol.WHEEL), VictoryPoints(1), FreeSymbol(CardFreeSymbol.HARF))),
                Card("laboratory", CardColor.GREEN, Resource(wood = 1, glass = 2), features = listOf(Science(ScienceSymbol.COMPASS), VictoryPoints(1), FreeSymbol(CardFreeSymbol.LAMP))),
                Card("statue", CardColor.BLUE, Resource(clay = 2), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.COLUMN)), freeSymbol = CardFreeSymbol.MASK),
                Card("temple", CardColor.BLUE, Resource(wood = 1, papyrus = 1), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.SUN)), freeSymbol = CardFreeSymbol.MOON),
                Card("aqueduct", CardColor.BLUE, Resource(stone = 3), features = listOf(VictoryPoints(5)), freeSymbol = CardFreeSymbol.TEARDROP),
                Card("rostrum", CardColor.BLUE, Resource(wood = 1, stone = 1), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.COURT))),
                Card("brewery", CardColor.YELLOW, features = listOf(AddGold(6), FreeSymbol(CardFreeSymbol.BARREL)))
        )
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CardJsonModule())
        val writeValueAsString = objectMapper.writeValueAsString(cards)
        println("writeValueAsString = ${writeValueAsString}")
    }
}

fun main(args: Array<String>) {
    val path = Paths.get("").toAbsolutePath().toString()
    println("Working Directory = $path")
    File(".").list().forEach { println(it) }
    val fileName = "wonders-builder/src/main/resources/cards.json"

    val lines: List<String> = File(fileName).readLines()

    lines.forEach { line -> println(line) }
}