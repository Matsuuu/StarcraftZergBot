package Zerg.Utility;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import static com.github.ocraft.s2client.protocol.data.Units.*;

import java.util.function.Predicate;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_LARVA;

public class ZergPredicates {

    public static Predicate<UnitInPool> isHatchery() {
        return isUnitByType(ZERG_HATCHERY);
    }

    public static Predicate<UnitInPool> isDrone() {
        return isUnitByType(ZERG_DRONE);
    }

    public static Predicate<UnitInPool> isEgg() {
        return isUnitByType(ZERG_EGG);
    }

    public static Predicate<UnitInPool> isLarva() {
        return isUnitByType(ZERG_LARVA);
    }

    public static Predicate<UnitInPool> isUnitByType(Units unitType) {
        return unitInPool -> unitInPool.unit().getType().equals(unitType);
    }
}
