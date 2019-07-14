package Zerg;

import Zerg.Structures.Extractor;
import Zerg.Structures.Hatchery;
import Zerg.Structures.Larva;
import Zerg.Structures.SpawningPool;
import Zerg.Units.Drone;
import Zerg.Utility.NearestUtility;
import Zerg.Utility.ZergPredicates;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import static com.github.ocraft.s2client.protocol.data.Units.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class StructureActions {
    private List<Units> structureUnits = new ArrayList<>(Arrays.asList(
            ZERG_HATCHERY,ZERG_BANELING_NEST,ZERG_CREEP_TUMOR,ZERG_EVOLUTION_CHAMBER,ZERG_EXTRACTOR,ZERG_GREATER_SPIRE,ZERG_HIVE,
            ZERG_HYDRALISK_DEN,ZERG_INFESTATION_PIT,ZERG_LAIR,ZERG_LURKER_DEN_MP,ZERG_NYDUS_NETWORK,ZERG_NYDUS_CANAL,ZERG_ROACH,
            ZERG_SPAWNING_POOL,ZERG_SPINE_CRAWLER,ZERG_SPIRE,ZERG_SPORE_CRAWLER,ZERG_ULTRALISK_CAVERN,ZERG_LARVA));


    public boolean isStructureAction(Units action) {
        return structureUnits.contains(action);
    }

    public void handleUnitIdle(Bot bot, Unit unit, Units unitType) {
        switch (unitType) {
            case ZERG_HATCHERY:
                //
                break;
            case ZERG_DRONE:
                Drone.handleIdle(bot, unit);
                break;
        }
    }

    public void handleUnitCreated(Bot bot, Unit unit, Units unitType) {
        switch (unitType) {
            default:
            case ZERG_HATCHERY:
                Hatchery.handleUnitCreated(bot);
                break;
        }
    }

    public void handleLarva(Bot bot) {
        ObservationInterface observation = bot.observation();
        List<UnitInPool> units = observation.getUnits(Alliance.SELF, ZergPredicates.isLarva());
        for (UnitInPool u : units) {
            Larva.handleIdle(bot, u.unit());
        }
    }

    public void handleConstructionComplete(Bot bot, Unit unit, Units unitType) {
        switch (unitType) {
            case ZERG_HATCHERY:
                Hatchery.changeWorkerRally(bot, unit);
                break;
            case ZERG_SPAWNING_POOL:
                SpawningPool.handleUnitCreated(bot, unit);
                break;
            case ZERG_EXTRACTOR:
                Extractor.saturateGas(bot, unit);
                break;
        }
    }
}
