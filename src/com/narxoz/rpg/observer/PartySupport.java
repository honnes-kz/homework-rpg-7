package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PartySupport implements GameObserver {

    private final List<Hero> heroes;
    private final Random random;
    private final int healAmount;

    public PartySupport(List<Hero> heroes, int healAmount) {
        this.heroes = heroes;
        this.healAmount = healAmount;
        this.random = new Random(7L);
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.HERO_LOW_HP) {
            return;
        }

        List<Hero> candidates = new ArrayList<>();
        List<Hero> woundedCandidates = new ArrayList<>();
        Hero sourceHero = null;

        for (Hero hero : heroes) {
            if (hero.getName().equals(event.getSourceName())) {
                sourceHero = hero;
            } else if (hero.isAlive()) {
                candidates.add(hero);
                if (hero.getHp() < hero.getMaxHp()) {
                    woundedCandidates.add(hero);
                }
            }
        }

        if (!woundedCandidates.isEmpty()) {
            candidates = woundedCandidates;
        }

        if (candidates.isEmpty() && sourceHero != null && sourceHero.isAlive()) {
            candidates.add(sourceHero);
        }

        if (candidates.isEmpty()) {
            System.out.println("[PartySupport] No ally was available to receive emergency aid.");
            return;
        }

        Hero healedHero = candidates.get(random.nextInt(candidates.size()));
        int hpBefore = healedHero.getHp();
        healedHero.heal(healAmount);
        int restored = healedHero.getHp() - hpBefore;

        System.out.println("[PartySupport] " + event.getSourceName()
                + " cries for help, and " + healedHero.getName()
                + " receives " + restored + " emergency healing.");
    }
}
