package com.narxoz.rpg.combatant;

import com.narxoz.rpg.strategy.BalancedStrategy;
import com.narxoz.rpg.strategy.CombatStrategy;

import java.util.Objects;

/**
 * Represents a player-controlled hero participating in the dungeon encounter.
 * Adapted from Homework 6.
 *
 * Students: you may extend this class as needed for your implementation.
 */
public class Hero {

    private final String name;
    private int hp;
    private final int maxHp;
    private final int attackPower;
    private final int defense;
    private CombatStrategy strategy;

    public Hero(String name, int hp, int attackPower, int defense) {
        this(name, hp, attackPower, defense, new BalancedStrategy());
    }

    public Hero(String name, int hp, int attackPower, int defense, CombatStrategy strategy) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public String getName()             { return name; }
    public int getHp()                  { return hp; }
    public int getMaxHp()               { return maxHp; }
    public int getAttackPower()         { return attackPower; }
    public int getDefense()             { return defense; }
    public CombatStrategy getStrategy() { return strategy; }
    public boolean isAlive()            { return hp > 0; }

    public void setStrategy(CombatStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public int getEffectiveDamage() {
        return strategy.calculateDamage(attackPower);
    }

    public int getEffectiveDefense() {
        return strategy.calculateDefense(defense);
    }

    /**
     * Reduces this hero's HP by the given amount, clamped to zero.
     *
     * @param amount the damage to apply; must be non-negative
     */
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    /**
     * Restores this hero's HP by the given amount, clamped to maxHp.
     *
     * @param amount the HP to restore; must be non-negative
     */
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }
}
