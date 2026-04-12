package com.narxoz.rpg.engine;

import com.narxoz.rpg.boss.DungeonBoss;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.observer.GameEvent;
import com.narxoz.rpg.observer.GameEventType;
import com.narxoz.rpg.strategy.CombatStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DungeonEngine {

    private final List<Hero> heroes;
    private final DungeonBoss boss;
    private final int maxRounds;
    private final Map<Integer, List<StrategyShift>> scheduledShifts = new HashMap<>();
    private final Set<String> lowHpAnnouncements = new HashSet<>();

    public DungeonEngine(List<Hero> heroes, DungeonBoss boss, int maxRounds) {
        this.heroes = new ArrayList<>(heroes);
        this.boss = boss;
        this.maxRounds = maxRounds;
    }

    public void scheduleHeroStrategyShift(int round, Hero hero, CombatStrategy strategy) {
        scheduledShifts
                .computeIfAbsent(round, ignored -> new ArrayList<>())
                .add(new StrategyShift(hero, strategy));
    }

    public EncounterResult runEncounter() {
        int roundsPlayed = 0;

        while (roundsPlayed < maxRounds && boss.isAlive() && hasLivingHeroes()) {
            roundsPlayed++;
            System.out.println();
            System.out.println("=== Round " + roundsPlayed + " ===");

            applyScheduledShifts(roundsPlayed);
            runHeroPhase(roundsPlayed);

            if (!boss.isAlive() || !hasLivingHeroes()) {
                break;
            }

            runBossPhase();
        }

        return new EncounterResult(!boss.isAlive(), roundsPlayed, countLivingHeroes());
    }

    private void runHeroPhase(int roundsPlayed) {
        for (Hero hero : heroes) {
            if (!hero.isAlive() || !boss.isAlive()) {
                continue;
            }

            int damage = Math.max(1, hero.getEffectiveDamage() - boss.getStrategy().calculateDefense(boss.getDefense()));
            int bossHpAfterHit = Math.max(0, boss.getHp() - damage);

            System.out.println(hero.getName() + " uses " + hero.getStrategy().getName()
                    + " and strikes " + boss.getName() + " for " + damage
                    + " damage. Boss HP: " + bossHpAfterHit + "/" + boss.getMaxHp());

            boss.publishEvent(new GameEvent(GameEventType.ATTACK_LANDED, hero.getName(), damage));
            boss.takeDamage(damage);

            if (!boss.isAlive()) {
                boss.publishEvent(new GameEvent(GameEventType.BOSS_DEFEATED, boss.getName(), roundsPlayed));
            }
        }
    }

    private void runBossPhase() {
        for (Hero hero : heroes) {
            if (!hero.isAlive()) {
                continue;
            }

            int damage = Math.max(1, boss.getStrategy().calculateDamage(boss.getAttackPower()) - hero.getEffectiveDefense());
            hero.takeDamage(damage);

            System.out.println(boss.getName() + " uses " + boss.getStrategy().getName()
                    + " against " + hero.getName() + " for " + damage
                    + " damage. " + hero.getName() + " HP: " + hero.getHp() + "/" + hero.getMaxHp());

            boss.publishEvent(new GameEvent(GameEventType.ATTACK_LANDED, boss.getName(), damage));

            if (hero.isAlive() && isBelowThirtyPercent(hero) && lowHpAnnouncements.add(hero.getName())) {
                boss.publishEvent(new GameEvent(GameEventType.HERO_LOW_HP, hero.getName(), hero.getHp()));
            }

            if (!hero.isAlive()) {
                boss.publishEvent(new GameEvent(GameEventType.HERO_DIED, hero.getName(), 0));
            }
        }
    }

    private void applyScheduledShifts(int round) {
        List<StrategyShift> shifts = scheduledShifts.get(round);
        if (shifts == null) {
            return;
        }

        for (StrategyShift shift : shifts) {
            Hero hero = shift.hero();
            if (!hero.isAlive()) {
                continue;
            }

            String previousStrategy = hero.getStrategy().getName();
            hero.setStrategy(shift.strategy());
            System.out.println("[Tactics] " + hero.getName() + " switches from "
                    + previousStrategy + " to " + hero.getStrategy().getName() + ".");
        }
    }

    private boolean isBelowThirtyPercent(Hero hero) {
        return hero.getHp() * 10 < hero.getMaxHp() * 3;
    }

    private boolean hasLivingHeroes() {
        return countLivingHeroes() > 0;
    }

    private int countLivingHeroes() {
        int livingHeroes = 0;
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                livingHeroes++;
            }
        }
        return livingHeroes;
    }

    private record StrategyShift(Hero hero, CombatStrategy strategy) {
    }
}
