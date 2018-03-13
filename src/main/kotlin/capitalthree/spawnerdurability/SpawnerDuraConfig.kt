package capitalthree.spawnerdurability

import net.minecraftforge.common.config.Configuration
import java.io.File

class SpawnerDuraConfig(file: File) {
    val accumulators: Array<String>

    init {
        val config = Configuration(file.resolve("spawnerdurability.cfg"))
        accumulators = config.getStringList("accumulators", "spawnerdurability", arrayOf("dura"), "You can edit this list if you want more accumulators for use in your ruleset than the default dura and builtin time.  Note that removing accumulators that are in use by your ruleset will cause the rules file to become invalid.")
    }
}