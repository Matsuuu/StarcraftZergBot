package Zerg;

import Zerg.Structures.Hatchery;
import Zerg.Utility.ZergPredicates;
import Zerg.Utility.ZergUnitCounter;
import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.action.ActionChat;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.game.Race;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.ArrayList;
import java.util.List;

public class Bot extends S2Agent {
    public final Race race = Race.ZERG;
    private StructureActions structureActions;
    private UnitActions unitActions;
    private List<Point> expansionLocations;
    private Point2d firstBaseLocation;
    private List<Abilities> unitQueue;

    public Bot() {
        this.structureActions = new StructureActions();
        this.unitActions = new UnitActions();
        this.unitQueue = new ArrayList<>();
    }

    @Override
    public void onGameStart() {
        //
        this.expansionLocations = query().calculateExpansionLocations(observation());
        setFirstBaseLocation();
        actions().sendChat("You can, and SHOULD suck my left nut", ActionChat.Channel.BROADCAST);
    }

    private void setFirstBaseLocation() {
        List<UnitInPool> units = observation().getUnits(Alliance.SELF, ZergPredicates.isHatchery());
        if (units.size() > 0) {
            UnitInPool firstHatch = units.get(0);
            this.firstBaseLocation = firstHatch.unit().getPosition().toPoint2d();
        }
    }

    @Override
    public void onStep() {
        structureActions.handleLarva(this);
        Hatchery.checkForOverSaturation(this);
    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {
        Unit unit = unitInPool.unit();
        Units unitType = (Units) unit.getType();
        if (structureActions.isStructureAction(unitType)) {
            structureActions.handleUnitIdle(this, unit, unitType);
        } else {
            unitActions.handleUnitIdle(this, unit, unitType);
        }
    }

    @Override
    public void onUnitCreated(UnitInPool unitInPool) {
        Unit unit = unitInPool.unit();
        Units unitType = (Units) unit.getType();
        if (structureActions.isStructureAction(unitType)) {
            structureActions.handleUnitCreated(this, unit, unitType);
        } else {
            unitActions.handleUnitCreated(this, unit, unitType);
        }
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unitInPool) {
        Unit unit = unitInPool.unit();
        Units unitType = (Units) unit.getType();
        structureActions.handleConstructionComplete(this, unit, unitType);
    }

    public List<Point> getExpansionLocations() {
        return expansionLocations;
    }

    public Point2d getFirstBaseLocation() {
        return firstBaseLocation;
    }

    public List<Abilities> getUnitQueue() {
        return unitQueue;
    }

    public void setUnitQueue(List<Abilities> unitQueue) {
        this.unitQueue = unitQueue;
    }

    public void addToUnitQueue(Abilities unitToBuild) {
        this.unitQueue.add(unitToBuild);
    }

    public void addToUnitQueue(Abilities unitToBuild, int multiplier) {
        for (int i = 0; i < multiplier; i++) {
            this.unitQueue.add(unitToBuild);
        }
    }
}
