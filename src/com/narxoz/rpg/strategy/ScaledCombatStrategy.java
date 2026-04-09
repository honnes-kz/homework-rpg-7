package com.narxoz.rpg.strategy;

abstract class ScaledCombatStrategy implements CombatStrategy {

    private final String name;
    private final double damageScale;
    private final double defenseScale;

    protected ScaledCombatStrategy(String name, double damageScale, double defenseScale) {
        this.name = name;
        this.damageScale = damageScale;
        this.defenseScale = defenseScale;
    }

    @Override
    public int calculateDamage(int basePower) {
        return Math.max(1, (int) Math.round(basePower * damageScale));
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return Math.max(0, (int) Math.round(baseDefense * defenseScale));
    }

    @Override
    public String getName() {
        return name;
    }
}
