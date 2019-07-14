package Zerg;

import Zerg.Units.Drone;
import Zerg.Units.Egg;
import Zerg.Utility.ZergUnitCollector;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.List;

public class UnitActions {
    public static void handleAggression(Bot bot) {
        ObservationInterface observation = bot.observation();
        int armyCount = observation.getArmyCount();
        if (armyCount > 30) {
            doAttack(bot, observation);
        }
    }

    private static void doAttack(Bot bot, ObservationInterface observation) {
        List<Unit> armyUnits = ZergUnitCollector.getArmyUnitsNotAttacking(bot);
        Point2d enemyBasePosition = bot.getEnemyBasePosition();
        for (Unit armyUnit : armyUnits) {
            bot.actions().unitCommand(armyUnit, Abilities.ATTACK, enemyBasePosition, false);
        }
    }

    public void handleUnitIdle(Bot bot, Unit unit, Units unitType) {

    }

    public void handleUnitCreated(Bot bot, Unit unit, Units unitType) {
        switch (unitType) {
            case ZERG_DRONE:
                Drone.handleCreated(bot, unit);
                break;
            case ZERG_ZERGLING:

                break;
            case ZERG_ROACH:

                break;
        }
    }
}
