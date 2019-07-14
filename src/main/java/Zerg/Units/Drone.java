package Zerg.Units;

import Zerg.Bot;
import Zerg.Structures.Builder;
import Zerg.Utility.NearestUtility;
import Zerg.Utility.ZergCosts;
import Zerg.Utility.ZergUnitCollector;
import Zerg.Utility.ZergUnitCounter;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Buffs;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Drone {
    private static boolean unitIsBecomingAHatchery = false;
    private static boolean unitIsBecomingASpawningPool = false;
    private static boolean unitIsBecomingARoachWarren = false;
    private static boolean unitIsBecomingAExtractor = false;

    public static void handleIdle(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        bot.actions().unitCommand(unit, Abilities.SMART, NearestUtility.findNearestMineralPatch(unit, observation), false);
    }

    public static void handleCreated(Bot bot, Unit unit) {
        if (unit.getBuffs().contains(Buffs.CARRY_HARVESTABLE_VESPENE_GEYSER_GAS_ZERG)) {
            return;
        }
        ObservationInterface observation = bot.observation();
        int foodUsed = observation.getFoodUsed();
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);

        handleExpansionNeed(bot, observation, unit, foodUsed, hatcheryCount);
        if (hatcheryCount >= 2 && foodUsed >= 17) {
            boolean built = handleSpawningPoolNeed(bot, observation, unit);
            if (built) return;
        }
        if (hatcheryCount > 1) {
            boolean built = handleGases(bot, unit);
            if(built) return;
        }
        if (hatcheryCount >= 2 && foodUsed >= 35) {
            boolean built = handleDefensiveTech(bot, unit);
            if (built) return;
        }

    }

    private static boolean handleDefensiveTech(Bot bot, Unit unit) {
        return doRoachWarren(bot, unit);
    }

    private static boolean doRoachWarren(Bot bot, Unit unit) {
        if (ZergUnitCounter.getRoachWarrenCount(bot.observation()) < 1 && !unitIsBecomingARoachWarren) {
            unitIsBecomingARoachWarren = true;
            Builder.tryBuilding(Units.ZERG_ROACH_WARREN, bot, unit);
            return true;
        }
        return false;
    }

    private static void handleExpansionNeed(Bot bot, ObservationInterface observation, Unit unit, int foodUsed, int hatcheryCount) {
        System.out.println("FOOD USED: " + foodUsed);
        if (expansionIsNeeded(observation) && !unitIsBecomingAHatchery) {
            unitIsBecomingAHatchery = true;
            bot.setAllowMakingUnits(false);
            Point2d expansion = getNextExpansionLocation(bot);
            bot.actions().unitCommand(unit, Abilities.MOVE, expansion, false);
            CompletableFuture.runAsync(() -> {
                boolean hasBecomeAHatchery = false;
                while (!hasBecomeAHatchery) {
                    if (observation.getMinerals() >= ZergCosts.ZERG_HATCHERY.getMineralCost()) {
                        bot.actions().unitCommand(unit, Abilities.BUILD_HATCHERY, expansion, false);
                        hasBecomeAHatchery = true;
                    }
                }
                bot.setAllowMakingUnits(true);
                unitIsBecomingAHatchery = false;
                bot.markExpansionAsTaken(expansion);
                bot.setLatestBase(expansion);
            });
        }
    }

    private static boolean handleGases(Bot bot, Unit unit) {
        Unit nearestBase = NearestUtility.getNearestUnit(unit, ZergUnitCollector.getAllBases(bot));
        if (nearestBase.getAssignedHarvesters().get() >= nearestBase.getIdealHarvesters().get() && needMoreGas(bot) && !unitIsBecomingAExtractor) {
            List<Unit> allGasGeysers = ZergUnitCollector.getAllGasGeysers(bot);
            unitIsBecomingAExtractor = true;

            allGasGeysers = allGasGeysers.stream()
                    .filter(g -> g.getPosition().toPoint2d().distance(nearestBase.getPosition().toPoint2d()) < 15)
                    .collect(Collectors.toList());
            for (Unit g : allGasGeysers) {
                if (g.getAssignedHarvesters().isPresent()) {
                    allGasGeysers.remove(g);
                }
            }

            Unit nearestGas = NearestUtility.getNearestUnit(nearestBase, allGasGeysers);
            if (nearestGas != null) {
                bot.actions().unitCommand(unit, Abilities.BUILD_EXTRACTOR, nearestGas, false);
                unitIsBecomingAExtractor = false;
            }
            return true;
        }
        return false;
    }

    private static boolean needMoreGas(Bot bot) {
        boolean needMoreGas = false;
        ObservationInterface observation = bot.observation();
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);
        int droneCount = observation.getFoodWorkers();
        int gasCount = ZergUnitCounter.getGasCount(observation);
        int defensiveTechCount = ZergUnitCounter.getRoachWarrenCount(observation) + ZergUnitCounter.getBanelingNestCount(observation);

        if (hatcheryCount >= 1 && gasCount == 0 && droneCount >= 16) {
            needMoreGas = true;
        }
        if (hatcheryCount == 2 && gasCount <= 1 && defensiveTechCount > 0) {
            needMoreGas = true;
        }

        return needMoreGas;
    }

    private static boolean expansionIsNeeded(ObservationInterface observation) {
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);
        boolean isNeeded = false;
        if (observation.getFoodUsed() >= 17 && hatcheryCount < 2 && !unitIsBecomingAHatchery) {
            isNeeded = true;
        }
        /*if (observation.getFoodWorkers() >= 30 && hatcheryCount == 2 && !unitIsBecomingAHatchery) {
            isNeeded = true;
        }*/
        return isNeeded;
    }

    private static boolean handleSpawningPoolNeed(Bot bot, ObservationInterface observation, Unit unit) {
        if (ZergUnitCounter.getSpawningPoolCount(observation) < 1 && !unitIsBecomingASpawningPool) {
            unitIsBecomingASpawningPool = true;
            Builder.tryBuilding(Units.ZERG_SPAWNING_POOL, bot, unit);
            return true;
        }
        return false;
    }


    private static Point2d getNextExpansionLocation(Bot bot) {
        Map<Point2d, Boolean> expansionLocations = bot.getExpansionLocations();
        List<Point2d> locations = new ArrayList<>();
        for (Map.Entry<Point2d, Boolean> locEntry : expansionLocations.entrySet()) {
            if (!locEntry.getValue()) {
                locations.add(locEntry.getKey());
            }
        }
        return NearestUtility.getNearestPoint(bot.getLatestBase(), locations);
    }
}
