package com.narxoz.rpg.boss;

import com.narxoz.rpg.observer.GameEvent;
import com.narxoz.rpg.observer.GameEventBus;
import com.narxoz.rpg.observer.GameEventType;
import com.narxoz.rpg.observer.GameObserver;
import com.narxoz.rpg.strategy.BossPhaseOneStrategy;
import com.narxoz.rpg.strategy.BossPhaseThreeStrategy;
import com.narxoz.rpg.strategy.BossPhaseTwoStrategy;
import com.narxoz.rpg.strategy.CombatStrategy;

import java.util.Objects;

public class DungeonBoss implements GameObserver {

    private final String name;
    private final int maxHp;
    private final int attackPower;
    private final int defense;
    private final GameEventBus eventBus;
    private final CombatStrategy phaseOneStrategy = new BossPhaseOneStrategy();
    private final CombatStrategy phaseTwoStrategy = new BossPhaseTwoStrategy();
    private final CombatStrategy phaseThreeStrategy = new BossPhaseThreeStrategy();
    private int hp;
    private int currentPhase;
    private CombatStrategy strategy;

    public DungeonBoss(String name, int hp, int attackPower, int defense, GameEventBus eventBus) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.currentPhase = 1;
        this.strategy = phaseOneStrategy;
        this.eventBus.registerObserver(this);
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public CombatStrategy getStrategy() {
        return strategy;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void registerObserver(GameObserver observer) {
        eventBus.registerObserver(observer);
    }

    public void publishEvent(GameEvent event) {
        eventBus.publish(event);
    }

    public void takeDamage(int amount) {
        if (amount <= 0 || !isAlive()) {
            return;
        }

        int previousHp = hp;
        hp = Math.max(0, hp - amount);
        firePhaseChanges(previousHp, hp);
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.BOSS_PHASE_CHANGED) {
            return;
        }

        if (!name.equals(event.getSourceName())) {
            return;
        }

        int newPhase = event.getValue();
        if (newPhase <= currentPhase) {
            return;
        }

        currentPhase = newPhase;
        strategy = switch (newPhase) {
            case 2 -> phaseTwoStrategy;
            case 3 -> phaseThreeStrategy;
            default -> phaseOneStrategy;
        };
    }

    private void firePhaseChanges(int previousHp, int currentHp) {
        int previousPhase = determinePhase(previousHp);
        int newPhase = determinePhase(currentHp);

        for (int phase = previousPhase + 1; phase <= newPhase; phase++) {
            publishEvent(new GameEvent(GameEventType.BOSS_PHASE_CHANGED, name, phase));
        }
    }

    private int determinePhase(int hpValue) {
        double ratio = (double) hpValue / maxHp;
        if (ratio > 0.60) {
            return 1;
        }
        if (ratio > 0.30) {
            return 2;
        }
        return 3;
    }
}
