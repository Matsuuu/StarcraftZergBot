package Zerg.Utility;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;

public class ZergUnitCounter {
    public static int getDroneCount(ObservationInterface observation) {
        return observation.getFoodWorkers();
    }

    public static int getHatcheryCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isHatchery()).size();
    }

    public static int getSpawningPoolCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.ZERG_SPAWNING_POOL)).size();
    }

    public static int getRoachWarrenCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.ZERG_ROACH_WARREN)).size();
    }

    public static int getGasCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.ZERG_EXTRACTOR)).size();
    }

    public static int getBanelingNestCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isUnitByType(Units.ZERG_BANELING_NEST)).size();
    }
}
