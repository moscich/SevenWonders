package com.moscichowski.wondersTest

import com.moscichowski.wonders.Board
import com.moscichowski.wonders.BoardBuilder
import com.moscichowski.wonders.*
import org.junit.Test

class TestBoardBuilder: BoardBuilder {
    override fun build1Board(): Board { return Board(listOf()) }
    override fun build2Board(): Board { return Board(listOf()) }
    override fun build3Board(): Board { return Board(listOf()) }
}

class BoardBuildingTests {
    @Test
    fun `build first board after game changes state from select wonders`() {
//        Game(listOf(), TestBoardBuilder())
//        Game(Board(listOf()), EmptyBoardBuilder())

//        val game = Game(Board(listOf()))
    }
}