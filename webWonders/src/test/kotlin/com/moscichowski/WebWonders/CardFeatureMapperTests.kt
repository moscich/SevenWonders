package com.moscichowski.WebWonders

import com.moscichowski.wonders.Board
import com.moscichowski.wonders.BoardNode
import com.moscichowski.wonders.model.*
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class CardFeatureMapperTests {

    val objectMapper = WondersMapper()

    @Test
    fun saneDependenciesJson() {
        val boardNode = BoardNode(1, Card("Test 1", CardColor.RED))
        val boardNode1 = BoardNode(2, Card("Test 2", CardColor.RED, Resource(clay = 2)))
        val rootNode = BoardNode(3, Card("Test 2", CardColor.RED), mutableListOf(boardNode, boardNode1), true)
        val board = Board(listOf(rootNode, boardNode, boardNode1))
        val string = objectMapper.writeValueAsString(board)

        assertEquals("{\"cards\":[{\"id\":3,\"descendants\":[1,2]},{\"id\":1,\"card\":{\"name\":\"Test 1\",\"color\":\"RED\",\"features\":[]}},{\"id\":2,\"card\":{\"name\":\"Test 2\",\"color\":\"RED\",\"cost\":{\"clay\":2},\"features\":[]}}]}", string)
    }
}