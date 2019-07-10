package Zerg.Structures;

import Zerg.Bot;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class SpawningPool {
    public static void handleUnitCreated(Bot bot, Unit unit) {
        bot.addToUnitQueue(Abilities.TRAIN_ZERGLING, 3);
    }
}
