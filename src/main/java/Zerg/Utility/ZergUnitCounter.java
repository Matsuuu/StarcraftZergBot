package Zerg.Utility;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.unit.Alliance;

public class ZergUnitCounter {
    public static int getDroneCount(ObservationInterface observation) {
        return observation.getFoodWorkers();
    }

    public static int getHatcheryCount(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, ZergPredicates.isHatchery()).size();
    }
}
