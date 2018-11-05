//package com.moscichowski.wonders
//
//import com.moscichowski.wonders.model.Card
//
//internal class BoardBuilder {
//    internal fun buildBoard(cards: List<Card>) {
//        this.firstEpoh = cards[0].toMutableList()
//
//        val hiddenIndexes = listOf(2, 3, 4, 9, 10, 11, 12, 13)
//
//        val nodes = (0 until 20).map {
//            val card = if (!hiddenIndexes.contains(it)) {
//                firstEpoh.pop()
//            } else {
//                null
//            }
//            BoardNode(it, card)
//        }
//
//        val dependencies = listOf(
//                Pair(2, 3),
//                Pair(3, 4),
//                Pair(5, 6),
//                Pair(6, 7),
//                Pair(7, 8),
//                Pair(9, 10),
//                Pair(10, 11),
//                Pair(11, 12),
//                Pair(12, 13),
//                Pair(14, 15),
//                Pair(15, 16),
//                Pair(16, 17),
//                Pair(17, 18),
//                Pair(18, 19)
//        )
//
//        for (index in 0 until dependencies.count()) {
//            val dependency = dependencies[index]
//            nodes[index].descendants.add(nodes[dependency.first])
//            nodes[index].descendants.add(nodes[dependency.second])
//        }
//
//        val board = Board(nodes)
//    }
//}