package com.moscichowski.wonders.builder

import com.moscichowski.wonders.model.*
import org.junit.Test
import kotlin.test.assertEquals

class WonderBuildingTest {
    @Test
    fun buildWondersFromJson() {
        val wonderBuilder = WonderBuilder()
        val wonders = wonderBuilder.getWonders()

        assertEquals(12, wonders.count())
        assertEquals(Wonder("Kolos Rodyjski", Resource(clay = 3, glass = 1), listOf(Military(2), VictoryPoints(3))), wonders[0])
        assertEquals(Wonder("Via Appia", Resource(clay = 2, stone = 2, papyrus = 1), listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))), wonders[1])
        assertEquals(Wonder("Pireus", Resource(clay = 1, stone = 1, wood = 2), listOf(ProvideSilverResource, ExtraTurn, VictoryPoints(2))), wonders[2])
        assertEquals(Wonder("Piramida Cheopsa", Resource(stone = 3, papyrus = 1), listOf(VictoryPoints(9))), wonders[3])
        assertEquals(Wonder("Posąg Zeusa w Olimpii", Resource(wood = 1, clay = 1, stone = 1, papyrus = 2), listOf(DestroyBrownCard, Military(1), VictoryPoints(3))), wonders[4])
        assertEquals(Wonder("Circus Maximus", Resource(wood = 1, stone = 2, glass = 1), listOf(DestroySilverCard, Military(1), VictoryPoints(3))), wonders[5])
        assertEquals(Wonder("Świątynia Artemidy w Efezie", Resource(wood = 1, stone = 1, glass = 1, papyrus = 1), listOf(AddGold(12), ExtraTurn)), wonders[6])
        assertEquals(Wonder("Latarnia morska na Faros", Resource(wood = 1, stone = 1, papyrus = 2), listOf(ProvideBrownResource, VictoryPoints(4))), wonders[7])
        assertEquals(Wonder("Sfinks", Resource(clay = 1, stone = 1, glass = 2), listOf(ExtraTurn, VictoryPoints(6))), wonders[8])
        assertEquals(Wonder("Wiszące ogrody Semiramidy", Resource(wood = 2, papyrus = 1, glass = 1), listOf(AddGold(6), ExtraTurn, VictoryPoints(3))), wonders[9])
    }
}