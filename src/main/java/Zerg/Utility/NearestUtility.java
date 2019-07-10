package Zerg.Utility;

import Zerg.Bot;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.List;

public class NearestUtility {
    public static Unit findNearestMineralPatch(Unit unit, ObservationInterface observation) {
        Point2d droneLocation = unit.getPosition().toPoint2d();
        List<UnitInPool> neutralUnits = observation.getUnits(Alliance.NEUTRAL);
        double distance = Double.MAX_VALUE;
        Unit target = null;
        for (UnitInPool unitInPool : neutralUnits) {
            Unit neutralUnitInPool = unitInPool.unit();
            if (neutralUnitInPool.getType().equals(Units.NEUTRAL_MINERAL_FIELD)) {
                double d = neutralUnitInPool.getPosition().toPoint2d().distance(droneLocation);
                if (d < distance) {
                    distance = d;
                    target = neutralUnitInPool;
                }
            }
        }
        return target;
    }

    public static Unit getNearestUnit(Unit unit, ObservationInterface observation, List<Unit> targetUnits) {
        Point2d start = unit.getPosition().toPoint2d();
        Unit target = null;
        Double distance = Double.MAX_VALUE;
        for (Unit u : targetUnits) {
            Double d = u.getPosition().toPoint2d().distance(start);
            if (d < distance) {
                distance = d;
                target = u;
            }
        }
        return target;
    }
}
