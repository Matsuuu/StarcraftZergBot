package Zerg.Units;

import Zerg.Bot;
import Zerg.Structures.Builder;
import Zerg.Utility.NearestUtility;
import Zerg.Utility.ZergPredicates;
import Zerg.Utility.ZergUnitCounter;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.List;

public class Drone {
    public static void handleIdle(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        bot.actions().unitCommand(unit, Abilities.SMART, NearestUtility.findNearestMineralPatch(unit, observation), false);
    }

    public static void handleCreated(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        int foodUsed = observation.getFoodUsed();

        boolean expansionNeeded = handleExpansionNeed(bot, observation, unit);
        if (!expansionNeeded && foodUsed >= 16) {
            handleSpawningPoolNeed(bot, observation, unit);
        }
    }

    private static boolean handleExpansionNeed(Bot bot, ObservationInterface observation, Unit unit) {
        boolean expansionNeeded = false;
        int droneCount = ZergUnitCounter.getDroneCount(observation);
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);
        System.out.println("Drone count: " + droneCount);
        System.out.println("Hatchery count" + hatcheryCount);
        if (droneCount > 16 && hatcheryCount < 2) {
            System.out.println("I SHOULD BECOME A HATCHERY");
            bot.actions().unitCommand(unit, Abilities.BUILD_HATCHERY, getNaturalExpansion(bot),false);
            expansionNeeded = true;
        }
        return expansionNeeded;
    }

    private static void handleSpawningPoolNeed(Bot bot, ObservationInterface observation, Unit unit) {
        List<UnitInPool> units = observation.getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.ZERG_SPAWNING_POOL));
        if (units.size() < 1) {
            Builder.tryBuilding(Units.ZERG_SPAWNING_POOL, bot, unit);
        }
    }

    private static Point2d getNaturalExpansion(Bot bot) {
        List<Point> expansionLocations = bot.getExpansionLocations();
        Point2d firstBaseLocation = bot.getFirstBaseLocation();
        Point2d target = null;
        double d = Double.MAX_VALUE;
        for (Point exp : expansionLocations) {
            double distanceToFirstBase = exp.toPoint2d().distance(firstBaseLocation);
            if (distanceToFirstBase < d) {
                d = distanceToFirstBase;
                target = exp.toPoint2d();
            }
        }
        return target;
    }
}
