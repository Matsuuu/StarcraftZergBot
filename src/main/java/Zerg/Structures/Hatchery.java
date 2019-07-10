package Zerg.Structures;

import Zerg.Bot;
import Zerg.Utility.NearestUtility;
import Zerg.Utility.ZergPredicates;
import Zerg.Utility.ZergUnitCollector;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;

import java.util.List;
import java.util.Optional;

public class Hatchery {
    public static void changeWorkerRally(Bot bot, Unit unit) {
        ObservationInterface observation = bot.observation();
        List<UnitInPool> hatcheries = observation.getUnits(Alliance.SELF, ZergPredicates.isHatchery());

        for (UnitInPool hatchery : hatcheries) {
            Unit nearestMineralPatch = NearestUtility.findNearestMineralPatch(unit, bot.observation());
            Unit hatcheryAsUnit = hatchery.unit();
            bot.actions().unitCommand(hatcheryAsUnit, Abilities.RALLY_WORKERS, nearestMineralPatch, false);
        }
    }

    public static void checkForOverSaturation(Bot bot) {
        bot.observation().getUnits(Alliance.SELF, ZergPredicates.isHatchery())
                .forEach(hatch -> handleOverSaturation(bot, hatch.unit()));
    }

    private static void handleOverSaturation(Bot bot, Unit unit) {
        Optional<Integer> assignedHarvesters = unit.getAssignedHarvesters();
        Optional<Integer> idealHarvesters = unit.getIdealHarvesters();
        Tag baseTag = unit.getTag();
        if (assignedHarvesters.isPresent() && idealHarvesters.isPresent()) {
            Integer harvesterCount = assignedHarvesters.get();
            Integer idealHarvesterCount = idealHarvesters.get();

            if (harvesterCount > idealHarvesterCount) {
                List<Unit> allWorkers = ZergUnitCollector.getAllWorkers(bot);
                int movedWorkers = 0;
                int workersNeededToMove = harvesterCount - idealHarvesterCount;
                for (Unit worker : allWorkers) {
                    List<UnitOrder> orders = worker.getOrders();
                    if (orders == null || orders.size() < 1) {
                        continue;
                    }
                    Optional<Tag> targetedUnitTag = orders.get(0).getTargetedUnitTag();
                    if (targetedUnitTag.isPresent()) {
                        if (targetedUnitTag.get().equals(baseTag)) {
                            moveWorkerToUnSaturatedBase(bot, worker);
                            movedWorkers++;
                        }
                    }
                    if (movedWorkers >= workersNeededToMove) {
                        break;
                    }
                }
            }
        }
    }

    private static void moveWorkerToUnSaturatedBase(Bot bot, Unit worker) {
        Unit nearestBase = NearestUtility.getNearestUnit(worker, bot.observation(), ZergUnitCollector.getAllUnSaturatedBases(bot));
        if (nearestBase != null) {
            bot.actions().unitCommand(worker, Abilities.SMART, nearestBase, false);
        }
    }
}
