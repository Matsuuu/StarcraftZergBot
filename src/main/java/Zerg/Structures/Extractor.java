package Zerg.Structures;

import Zerg.Bot;
import Zerg.Utility.ZergUnitCollector;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Extractor {
    public static void saturateGas(Bot bot, Unit unit) {
        List<Unit> allWorkers = ZergUnitCollector.getAllWorkers(bot);
        List<Unit> allGasGeysers = ZergUnitCollector.getAllGasGeysers(bot);
        List<Tag> ownedGasGeysers = allGasGeysers.stream().map(Unit::getTag).collect(Collectors.toList());
        List<Unit> mineralWorkers = allWorkers.stream()
                .filter(u -> u.getOrders().size() > 0)
                .collect(Collectors.toList());
        int movedWorkerCount = 0;
        for (Unit w : mineralWorkers) {
            List<UnitOrder> orders = w.getOrders();
            if (orders.size() < 1) {
                continue;
            }
            Optional<Tag> targetedUnitTag = orders.get(0).getTargetedUnitTag();
            if (!targetedUnitTag.isPresent()) {
                continue;
            }
            if (ownedGasGeysers.contains(targetedUnitTag.get())) {
                continue;
            }
            bot.actions().unitCommand(w, Abilities.SMART, unit, false);
            movedWorkerCount++;
            if (movedWorkerCount >= 3) {
                break;
            }
        }
    }
}
