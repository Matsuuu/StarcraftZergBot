package Zerg.Utility;

import Zerg.Bot;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.List;
import java.util.stream.Collectors;

public class ZergUnitCollector {
    public static List<Unit> getAllWorkers(Bot bot) {
        return bot.observation().getUnits(Alliance.SELF, ZergPredicates.isDrone())
                .stream()
                .map(UnitInPool::unit)
                .collect(Collectors.toList());
    }

    public static List<Unit> getAllBases(Bot bot) {
        return bot.observation().getUnits(Alliance.SELF, ZergPredicates.isHatchery())
                .stream()
                .map(UnitInPool::unit)
                .collect(Collectors.toList());
    }

    public static List<Unit> getAllUnSaturatedBases(Bot bot) {
        return bot.observation().getUnits(Alliance.SELF, ZergPredicates.isHatchery())
                .stream()
                .map(UnitInPool::unit)
                .filter(unit -> unit.getIdealHarvesters().get() > unit.getAssignedHarvesters().get())
                .collect(Collectors.toList());
    }

    public static List<Unit> getAllMinerals(Bot bot) {
        return bot.observation().getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.NEUTRAL_MINERAL_FIELD))
                .stream()
                .map(UnitInPool::unit)
                .collect(Collectors.toList());
    }
}
