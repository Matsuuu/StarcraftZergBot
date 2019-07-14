package Zerg.Utility;

import com.github.ocraft.s2client.protocol.data.Abilities;

public enum ZergCosts {

    ZERG_SPAWNING_POOL(Abilities.BUILD_SPAWNING_POOL, 200, 0),
    ZERG_HATCHERY(Abilities.BUILD_HATCHERY, 300, 0),
    ZERG_ROACH_WARREN(Abilities.BUILD_ROACH_WARREN, 150, 0);

    private Abilities buildingName;
    private int mineralCost;
    private int gasCost;

    private ZergCosts(Abilities buildingName, int mineralCost, int gasCost) {
        this.buildingName = buildingName;
        this.mineralCost = mineralCost;
        this.gasCost = gasCost;
    }

    public Abilities getBuildingName() {
        return buildingName;
    }

    public int getMineralCost() {
        return mineralCost;
    }

    public int getGasCost() {
        return gasCost;
    }

    public static ZergCosts getByAbility(Abilities ability) {
        for (ZergCosts c : ZergCosts.values()) {
            if (c.getBuildingName().equals(ability)) {
                return c;
            }
        }
        return null;
    }
}
