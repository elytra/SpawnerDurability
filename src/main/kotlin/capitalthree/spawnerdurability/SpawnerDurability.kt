package capitalthree.spawnerdurability

import capitalthree.spawnerdurability.rulesengine.SpawnCTX
import capitalthree.spawnerdurability.rulesengine.SpawnerDuraRules
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityMinecartMobSpawner
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraft.tileentity.MobSpawnerBaseLogic
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

lateinit var rulesEngine: SpawnerDuraRules

@Mod(modid = "spawnerdurability", version = "1.0", acceptableRemoteVersions="*")
class SpawnerDurability {
    @Mod.EventHandler
    fun preInit (event: FMLPreInitializationEvent) {
        rulesEngine = SpawnerDuraRules(SpawnerDuraConfig(event.modConfigurationDirectory).accumulators.asIterable())
        MinecraftForge.EVENT_BUS.register(EventHandler)
    }
}

object EventHandler {
    @SubscribeEvent
    fun specialSpawnEvent(e: LivingSpawnEvent.SpecialSpawn) {
        if (e.entity == null) return
        doStuff(e.spawner)
    }

    @SubscribeEvent
    fun trySpawnEvent(e: LivingSpawnEvent.CheckSpawn) {
        if (!e.isSpawner) return
        if (e.entity == null) return
        doStuff(e.spawner, e)
    }

    private fun doStuff(spawner: MobSpawnerBaseLogic?, event: LivingSpawnEvent.CheckSpawn? = null) {
        if (spawner == null) return
        val entity = spawner.spawnerEntity
        if (entity != null) {
            entitySpawnerCTX(spawner, entity, event)
        } else {
            blockSpawnerCTX(spawner, event)
        } ?.let(rulesEngine::act)
    }

    fun blockSpawnerCTX(spawner: MobSpawnerBaseLogic, event: LivingSpawnEvent.CheckSpawn?): SpawnCTX? {
        val te = spawner.spawnerWorld.getTileEntity(spawner.spawnerPosition)
        return when (te) {
            is TileEntityMobSpawner ->
                SpawnCTX(true, spawner, getOrCreateDuraTag(te.tileData), event)
            else -> null
        }
    }

    fun entitySpawnerCTX(spawner: MobSpawnerBaseLogic, ent: Entity, event: LivingSpawnEvent.CheckSpawn?) = when (ent) {
        is EntityMinecartMobSpawner ->
            SpawnCTX(false, spawner, getOrCreateDuraTag(ent.entityData), event)
        else -> null
    }

    @SubscribeEvent
    fun tryDespawnEvent(e: LivingSpawnEvent.AllowDespawn) {

    }
}