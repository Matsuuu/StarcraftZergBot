package Zerg.Units;

import Zerg.Bot;
import Zerg.Structures.Builder;
import Zerg.Utility.NearestUtility;
import Zerg.Utility.ZergMineralCosts;
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
import java.util.concurrent.CompletableFuture;

public class Drone {
    private static boolean unitIsBecomingAHatchery = false;

    public static void handleIdle(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        bot.actions().unitCommand(unit, Abilities.SMART, NearestUtility.findNearestMineralPatch(unit, observation), false);
    }

    public static void handleCreated(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        int foodUsed = observation.getFoodUsed();
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);

        handleExpansionNeed(bot, observation, unit, foodUsed, hatcheryCount);
        if (hatcheryCount >= 2 && foodUsed >= 16) {
            handleSpawningPoolNeed(bot, observation, unit);
        }
    }

    private static void handleExpansionNeed(Bot bot, ObservationInterface observation, Unit unit, int foodUsed, int hatcheryCount) {
        System.out.println("FOOD USED: " + foodUsed);
        if (shouldNaturalBeTaken(observation)) {
            unitIsBecomingAHatchery = true;
            bot.setAllowMakingUnits(false);
            System.out.println("I WILL BECOME A HATCH");
            Point2d naturalExpansion = getNaturalExpansion(bot);
            bot.actions().unitCommand(unit, Abilities.MOVE, naturalExpansion, false);
            CompletableFuture.runAsync(() -> {
                boolean hasBecomeAHatchery = false;
                while (!hasBecomeAHatchery) {
                    if (observation.getMinerals() >= ZergMineralCosts.ZERG_HATCHERY) {
                        bot.actions().unitCommand(unit, Abilities.BUILD_HATCHERY, naturalExpansion, false);
                        hasBecomeAHatchery = true;
                    }
                }
                bot.setAllowMakingUnits(true);
                unitIsBecomingAHatchery = false;
            });
        }
    }

    private static boolean shouldNaturalBeTaken(ObservationInterface observation) {
        return observation.getFoodUsed() == 17 &&
                ZergUnitCounter.getHatcheryCount(observation) < 2 &&
                !unitIsBecomingAHatchery;
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
