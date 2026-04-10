package com.narxoz.rpg.observer;

public class BattleLogger implements GameObserver {

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case ATTACK_LANDED ->
                    System.out.println("[BattleLogger] " + event.getSourceName()
                            + " lands a hit for " + event.getValue() + " damage.");
            case HERO_LOW_HP ->
                    System.out.println("[BattleLogger] " + event.getSourceName()
                            + " is in critical condition with " + event.getValue() + " HP left.");
            case HERO_DIED ->
                    System.out.println("[BattleLogger] " + event.getSourceName() + " has fallen.");
            case BOSS_PHASE_CHANGED ->
                    System.out.println("[BattleLogger] " + event.getSourceName()
                            + " enters phase " + event.getValue()
                            + " and adopts " + phaseStrategyName(event.getValue()) + ".");
            case BOSS_DEFEATED ->
                    System.out.println("[BattleLogger] " + event.getSourceName()
                            + " is defeated after " + event.getValue() + " rounds.");
        }
    }

    private String phaseStrategyName(int phase) {
        return switch (phase) {
            case 2 -> "Ravaging Assault";
            case 3 -> "Cataclysmic Frenzy";
            default -> "Measured Tyranny";
        };
    }
}
