package com.narxoz.rpg.observer;

import java.util.Random;

public class LootDropper implements GameObserver {

    private static final String[] PHASE_TWO_LOOT = {
            "Ashen Rune",
            "Reinforced Shield Charm",
            "Smoldering Bomb Satchel"
    };

    private static final String[] PHASE_THREE_LOOT = {
            "Bloodforged Elixir",
            "Phoenix Ember",
            "Dreadfang Spearhead"
    };

    private static final String[] FINAL_LOOT = {
            "Crown of the Cursed King",
            "Ancient Dragonheart",
            "Mythic Vault Key"
    };

    private final Random random = new Random(19L);

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case BOSS_PHASE_CHANGED -> System.out.println("[LootDropper] "
                    + event.getSourceName() + " drops "
                    + pickPhaseLoot(event.getValue()) + " while transforming.");
            case BOSS_DEFEATED -> System.out.println("[LootDropper] Final reward: "
                    + pickRandom(FINAL_LOOT));
            default -> {
            }
        }
    }

    private String pickPhaseLoot(int phase) {
        if (phase == 2) {
            return pickRandom(PHASE_TWO_LOOT);
        }
        return pickRandom(PHASE_THREE_LOOT);
    }

    private String pickRandom(String[] items) {
        return items[random.nextInt(items.length)];
    }
}
