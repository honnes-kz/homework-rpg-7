package com.narxoz.rpg;

import com.narxoz.rpg.boss.DungeonBoss;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.engine.DungeonEngine;
import com.narxoz.rpg.engine.EncounterResult;
import com.narxoz.rpg.observer.AchievementTracker;
import com.narxoz.rpg.observer.BattleLogger;
import com.narxoz.rpg.observer.GameEventBus;
import com.narxoz.rpg.observer.HeroStatusMonitor;
import com.narxoz.rpg.observer.LootDropper;
import com.narxoz.rpg.observer.PartySupport;
import com.narxoz.rpg.strategy.AggressiveStrategy;
import com.narxoz.rpg.strategy.BalancedStrategy;
import com.narxoz.rpg.strategy.DefensiveStrategy;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Hero alric = new Hero("Alric", 150, 28, 14, new DefensiveStrategy());
        Hero lyra = new Hero("Lyra", 118, 31, 9, new AggressiveStrategy());
        Hero seren = new Hero("Seren", 130, 26, 11, new BalancedStrategy());
        List<Hero> heroes = List.of(alric, lyra, seren);

        GameEventBus eventBus = new GameEventBus();
        DungeonBoss boss = new DungeonBoss("Malgrath the Cursed", 420, 26, 12, eventBus);

        boss.registerObserver(new BattleLogger());
        boss.registerObserver(new AchievementTracker(heroes));
        boss.registerObserver(new PartySupport(heroes, 18));
        boss.registerObserver(new HeroStatusMonitor(heroes));
        boss.registerObserver(new LootDropper());

        DungeonEngine engine = new DungeonEngine(heroes, boss, 12);
        engine.scheduleHeroStrategyShift(3, alric, new AggressiveStrategy());

        System.out.println("The party enters the cursed dungeon.");
        System.out.println("Boss: " + boss.getName() + " (" + boss.getStrategy().getName() + ")");
        System.out.println("Heroes:");
        for (Hero hero : heroes) {
            System.out.println("- " + hero.getName() + " starts with " + hero.getStrategy().getName());
        }

        EncounterResult result = engine.runEncounter();

        System.out.println();
        System.out.println("=== Encounter Result ===");
        System.out.println("Heroes won: " + result.isHeroesWon());
        System.out.println("Rounds played: " + result.getRoundsPlayed());
        System.out.println("Surviving heroes: " + result.getSurvivingHeroes());
    }
}
