package Zerg.Structures;

import Zerg.Bot;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Random;

public class Builder {
    public static void tryBuilding(Units unitType, Bot bot, Unit unit) {
        switch (unitType) {
            case ZERG_SPAWNING_POOL:
                tryBuildingSpawningPool(bot, unit);
                break;
        }
    }

    private static void tryBuildingSpawningPool(Bot bot, Unit unit) {
        boolean hasCreep = false;
        Point2d buildingPosition = null;
        while (!hasCreep) {
            buildingPosition = getRandomBuildingPosition(unit);
            hasCreep = bot.observation().hasCreep(buildingPosition);
        }
        bot.actions().unitCommand(unit, Abilities.BUILD_SPAWNING_POOL, buildingPosition, false);
    }

    private static Point2d getRandomBuildingPosition(Unit unit) {
        return unit.getPosition().toPoint2d().add(getRandomOffset(), getRandomOffset());
    }

    private static float getRandomOffset() {
        Random r = new Random();
        return r.nextInt((10 -  -10) + 1) + -10;
    }
}
