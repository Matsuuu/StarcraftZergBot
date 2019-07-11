package Zerg;

import Zerg.Units.Drone;
import Zerg.Units.Egg;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class UnitActions {
    public void handleUnitIdle(Bot bot, Unit unit, Units unitType) {

    }

    public void handleUnitCreated(Bot bot, Unit unit, Units unitType) {
        switch (unitType) {
            case ZERG_DRONE:
                Drone.handleCreated(bot, unit);
                break;
        }
    }
}
