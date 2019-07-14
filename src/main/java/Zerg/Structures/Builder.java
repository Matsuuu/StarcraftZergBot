package Zerg.Structures;

import Zerg.Bot;
import Zerg.Utility.ZergCosts;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Builder {
    public static void tryBuilding(Units unitType, Bot bot, Unit unit) {
        switch (unitType) {
            case ZERG_SPAWNING_POOL:
                tryBuild(bot, unit, Abilities.BUILD_SPAWNING_POOL);
                break;
            case ZERG_ROACH_WARREN:
                tryBuild(bot, unit, Abilities.BUILD_ROACH_WARREN);
        }
    }

    private static void tryBuild(Bot bot, Unit unit, Abilities building) {
        bot.setAllowMakingUnits(false);
        CompletableFuture.runAsync(() -> {
            boolean canBuild = false;
            Point2d buildingPosition = null;
            while (!canBuild) {
                buildingPosition = getRandomBuildingPosition(unit);
                boolean canPlace = bot.query().placement(building, buildingPosition);
                boolean hasCreep = bot.observation().hasCreep(buildingPosition);
                if (canPlace && hasCreep && canAfford(bot, building)) {
                    canBuild = true;
                }
            }
            bot.actions().unitCommand(unit, building, buildingPosition, false);
            bot.setAllowMakingUnits(true);
        });
    }

    private static boolean canAfford(Bot bot, Abilities building) {
        ZergCosts cost = ZergCosts.getByAbility(building);
        if (cost == null) {
            return false;
        }
        return bot.observation().getMinerals() >= cost.getMineralCost() &&
                bot.observation().getVespene() >= cost.getGasCost();
    }

    private static Point2d getRandomBuildingPosition(Unit unit) {
        return unit.getPosition().toPoint2d().add(getRandomOffset(), getRandomOffset());
    }

    private static float getRandomOffset() {
        Random r = new Random();
        return r.nextInt((7 - -7) + 1) + -7;
    }
}
