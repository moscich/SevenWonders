//
//  AppDelegate.swift
//  SevenWondersIOS
//
//  Created by Marek Mościchowski on 27/08/2018.
//  Copyright © 2018 Marek Mościchowski. All rights reserved.
//

import UIKit
import Base
import Wonders

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
//        let c = Won
        // Override point for customization after application launch.
        let card = BaseCard(name: "xD", cost: BaseResource(wood: 0, clay: 0, stone: 0, glass: 0, papyrus: 0, gold: 5), features: [])
        let node = BaseBoardNode(innerCard: card, descendants: [], hidden: false)
        let board = BaseBoard(cards: [node])
        let game = BaseGame(player1: BasePlayer(gold: 6), player2: BasePlayer(gold: 6), board: board, currentPlayer: 0)
        let wonders = BaseWonders(state: game)
        print("Count = \(wonders.gameState.board.cards.count)")

        wonders.takeAction(action: BaseTakeCard(card: card))
        print("Count = \(wonders.gameState.board.cards.count)")
        print("Gold = \(wonders.gameState.player1.gold)")
        print("Cards = \(wonders.gameState.player1.cards)")
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

