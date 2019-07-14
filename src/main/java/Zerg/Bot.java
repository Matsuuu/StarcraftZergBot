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
import com.github.ocraft.s2client.protocol.game.raw.StartRaw;
import com.github.ocraft.s2client.protocol.response.ResponseGameInfo;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Bot extends S2Agent {
    public final Race race = Race.ZERG;
    private StructureActions structureActions;
    private UnitActions unitActions;
    private Map<Point2d, Boolean> expansionLocations;
    private Point2d firstBaseLocation;
    private List<Abilities> unitQueue;
    private boolean allowMakingUnits;
    private Point2d latestBase;
    private Point2d enemyBasePosition;

    public Bot() {
        this.structureActions = new StructureActions();
        this.unitActions = new UnitActions();
        this.unitQueue = new ArrayList<>();
        allowMakingUnits = true;
    }

    @Override
    public void onGameStart() {
        //
        mapExpansionLocations();
        setFirstBaseLocation();
        actions().sendChat("A", ActionChat.Channel.BROADCAST);
        setEnemyBasePosition();
    }

    private void setFirstBaseLocation() {
        List<UnitInPool> units = observation().getUnits(Alliance.SELF, ZergPredicates.isHatchery());
        if (units.size() > 0) {
            UnitInPool firstHatch = units.get(0);
            this.latestBase = firstHatch.unit().getPosition().toPoint2d();
            this.firstBaseLocation = firstHatch.unit().getPosition().toPoint2d();
        }
    }

    @Override
    public void onStep() {
        structureActions.handleLarva(this);
        Hatchery.checkForOverSaturation(this);
        UnitActions.handleAggression(this);
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

    public Map<Point2d, Boolean> getExpansionLocations() {
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

    public boolean isAllowMakingUnits() {
        return allowMakingUnits;
    }

    public void setAllowMakingUnits(boolean allowMakingUnits) {
        this.allowMakingUnits = allowMakingUnits;
    }

    public Point2d getLatestBase() {
        return latestBase;
    }

    public void setLatestBase(Point2d latestBase) {
        this.latestBase = latestBase;
    }

    private void mapExpansionLocations() {
        Map<Point2d, Boolean> locationMap = new HashMap<>();
        List<Point> locations = query().calculateExpansionLocations(observation());
        for (Point loc : locations) {
            locationMap.put(loc.toPoint2d(), false);
        }
        this.expansionLocations = locationMap;
    }

    public void markExpansionAsTaken(Point2d expansion) {
        Map<Point2d, Boolean> expansionLocations = this.getExpansionLocations();
        for (Map.Entry<Point2d, Boolean> expEntry : expansionLocations.entrySet()) {
            if (expEntry.getKey() == expansion) {
                expEntry.setValue(true);
            }
        }
        this.expansionLocations = expansionLocations;
    }

    private void setEnemyBasePosition() {
        ResponseGameInfo gameInfo = observation().getGameInfo();
        Optional<StartRaw> startRaw = gameInfo.getStartRaw();
        if (startRaw.isPresent()) {
            Set<Point2d> startLocations = startRaw.get().getStartLocations();
            if (startLocations.isEmpty()) {
                return;
            }
            this.enemyBasePosition = new ArrayList<>(startLocations).get(ThreadLocalRandom.current().nextInt(startLocations.size()));
        }
    }

    public Point2d getEnemyBasePosition() {
        return enemyBasePosition;
    }
}
