package Zerg.Structures;

import Zerg.Bot;
import Zerg.Utility.ZergPredicates;
import Zerg.Utility.ZergUnitCollector;
import Zerg.Utility.ZergUnitCounter;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;

import java.util.List;
import java.util.function.Predicate;

public class Larva {

    public static void handleIdle(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        int foodUsed = observation.getFoodUsed();
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);

        if(!bot.isAllowMakingUnits()) {
            return;
        }

        int foodCap = observation.getFoodCap();

        if (bot.getUnitQueue().size() > 0) {
            doQueuedUnitTrainings(bot, unit);
            return;
        }
        checkOverlordNeed(bot, unit, foodCap, foodUsed, observation);
        checkDroneNeed(bot, unit, hatcheryCount, observation);
        doAttackingUnits(bot, unit, observation);
    }

    private static boolean aboutToGetSupplyCapped(int foodCap, int foodUsed, int hatcheryCount) {
        return foodUsed >= foodCap - (2 * hatcheryCount);
    }

    private static void doQueuedUnitTrainings(Bot bot, Unit unit) {
        List<Abilities> unitQueue = bot.getUnitQueue();
        Abilities unitToBuild = unitQueue.get(0);

        bot.actions().unitCommand(unit, unitToBuild, false);

        unitQueue.remove(0);
        bot.setUnitQueue(unitQueue);
    }

    private static void checkOverlordNeed(Bot bot, Unit unit, int foodCap, int foodUsed, ObservationInterface observation) {
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);
        if (aboutToGetSupplyCapped(foodCap, foodUsed, hatcheryCount) && !isOverlordMorphingFromEgg(observation)) {
            if (hatcheryCount > 1) {
                bot.addToUnitQueue(Abilities.TRAIN_OVERLORD, hatcheryCount - 1);
            }
            bot.actions().unitCommand(unit, Abilities.TRAIN_OVERLORD, false);
        }
    }

    private static void checkDroneNeed(Bot bot, Unit unit, int hatcheryCount, ObservationInterface observation) {
        int droneCount = observation.getFoodWorkers();
        if (getMineralGatherersNeededCount(bot) > 0) {
            bot.actions().unitCommand(unit, Abilities.TRAIN_DRONE, false);
        }
    }

    private static int getMineralGatherersNeededCount(Bot bot) {
        List<Unit> allBases = ZergUnitCollector.getAllBases(bot);
        int gatherersNeeded = 0;
        for (Unit base : allBases) {
            gatherersNeeded += base.getIdealHarvesters().get() - base.getAssignedHarvesters().get();
        }
        return gatherersNeeded;
    }

    private static boolean isOverlordMorphingFromEgg(ObservationInterface observation) {
        List<UnitInPool> units = observation.getUnits(Alliance.SELF, ZergPredicates.isEgg());
        return units.stream().anyMatch(unit -> unit.unit().getOrders().get(0).getAbility().equals(Abilities.TRAIN_OVERLORD));
    }

    private static void doAttackingUnits(Bot bot, Unit unit, ObservationInterface observation) {
        if (ZergUnitCounter.getRoachWarrenCount(observation) > 0) {
            doRoaches(bot, unit);
        }
        if (ZergUnitCounter.getSpawningPoolCount(observation) > 0) {
            doLings(bot, unit);
        }
    }

    private static void doLings(Bot bot, Unit unit) {
        bot.actions().unitCommand(unit, Abilities.TRAIN_ZERGLING, false);
    }

    private static void doRoaches(Bot bot, Unit unit) {
        bot.actions().unitCommand(unit, Abilities.TRAIN_ROACH, false);
    }
}
