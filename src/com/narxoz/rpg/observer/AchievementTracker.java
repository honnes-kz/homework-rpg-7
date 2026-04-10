package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AchievementTracker implements GameObserver {

    private final List<Hero> heroes;
    private final Set<String> unlockedAchievements = new LinkedHashSet<>();
    private int attacksLanded;

    public AchievementTracker(List<Hero> heroes) {
        this.heroes = heroes;
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case ATTACK_LANDED -> {
                attacksLanded++;
                if (attacksLanded == 1) {
                    unlock("First Blood");
                }
                if (attacksLanded == 10) {
                    unlock("Relentless Barrage");
                }
            }
            case HERO_DIED -> unlock("The Price of Failure");
            case BOSS_DEFEATED -> {
                unlock("Boss Slayer");
                if (countLivingHeroes() == heroes.size()) {
                    unlock("No One Left Behind");
                }
                if (countLivingHeroes() == 1) {
                    unlock("Last Stand");
                }
            }
            default -> {
            }
        }
    }

    private void unlock(String name) {
        if (unlockedAchievements.add(name)) {
            System.out.println("[AchievementTracker] Achievement unlocked: " + name);
        }
    }

    private int countLivingHeroes() {
        int living = 0;
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                living++;
            }
        }
        return living;
    }
}
