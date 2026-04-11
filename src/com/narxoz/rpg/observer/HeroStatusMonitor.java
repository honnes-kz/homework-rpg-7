package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;

import java.util.List;

public class HeroStatusMonitor implements GameObserver {

    private final List<Hero> heroes;

    public HeroStatusMonitor(List<Hero> heroes) {
        this.heroes = heroes;
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.HERO_LOW_HP && event.getType() != GameEventType.HERO_DIED) {
            return;
        }

        StringBuilder summary = new StringBuilder("[HeroStatusMonitor] Party status: ");
        for (int index = 0; index < heroes.size(); index++) {
            Hero hero = heroes.get(index);
            if (index > 0) {
                summary.append(" | ");
            }
            summary.append(hero.getName())
                    .append(" ")
                    .append(hero.getHp())
                    .append("/")
                    .append(hero.getMaxHp())
                    .append(hero.isAlive() ? " (alive)" : " (dead)");
        }

        System.out.println(summary);
    }
}
