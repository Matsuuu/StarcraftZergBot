package Zerg.Structures;

import Zerg.Bot;
import Zerg.Utility.ZergPredicates;
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

        int foodCap = observation.getFoodCap();
        int foodUsed = observation.getFoodUsed();
        int hatcheryCount = ZergUnitCounter.getHatcheryCount(observation);

        if (bot.getUnitQueue().size() > 0) {
            doQueuedUnitTrainings(bot, unit);
            return;
        }
        checkOverlordNeed(bot, unit, foodCap, foodUsed, observation);
        if (foodUsed > foodCap - 2) {
            return;
        }
        checkDroneNeed(bot, unit, hatcheryCount, observation);
    }

    private static void doQueuedUnitTrainings(Bot bot, Unit unit) {
        List<Abilities> unitQueue = bot.getUnitQueue();
        Abilities unitToBuild = unitQueue.get(0);

        bot.actions().unitCommand(unit, unitToBuild, false);

        unitQueue.remove(0);
        bot.setUnitQueue(unitQueue);
    }

    private static void checkOverlordNeed(Bot bot, Unit unit, int foodCap, int foodUsed, ObservationInterface observation) {
        if (foodUsed >= foodCap - 2 && !isOverlordMorphingFromEgg(observation)) {
            bot.actions().unitCommand(unit, Abilities.TRAIN_OVERLORD, false);
        }
    }

    private static void checkDroneNeed(Bot bot, Unit unit, int hatcheryCount, ObservationInterface observation) {
        int droneCount = ZergUnitCounter.getDroneCount(observation);
        bot.actions().unitCommand(unit, Abilities.TRAIN_DRONE, false);
    }

    private static boolean isOverlordMorphingFromEgg(ObservationInterface observation) {
        List<UnitInPool> units = observation.getUnits(Alliance.SELF, ZergPredicates.isEgg());
        return units.stream().anyMatch(unit -> unit.unit().getOrders().get(0).getAbility().equals(Abilities.TRAIN_OVERLORD));
    }
}
