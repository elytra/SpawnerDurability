package capitalthree.spawnerdurability.rulesengine

import com.elytradev.concrete.rulesengine.Effect
import com.elytradev.concrete.rulesengine.EvaluationContext
import com.elytradev.concrete.rulesengine.RulesEngine
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.MobSpawnerBaseLogic
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.fml.common.eventhandler.Event
import java.util.function.Predicate

class SpawnerDuraRules(val confAccumulators: Collection<String>): RulesEngine<SpawnCTX>() {
    override fun getDomainPredicates(): Map<Char, (String) -> com.elytradev.concrete.common.Either<Predicate<SpawnCTX>, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val effectSlots = (listOf(-1) + (1..confAccumulators.size)).toSet()
    override fun getEffectSlots() = effectSlots

    override fun parseEffect(str: String): com.elytradev.concrete.common.Either<Iterable<Effect<SpawnCTX>>, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun genInterestingNumbers(x: SpawnCTX): DoubleArray {
        val pos = x.logic.spawnerPosition
        val tag = x.duratag
        return (
            listOf(
                if (x.tile) 0.0 else 1.0,
//                x.event?.entity?.posX?:0.0, x.event?.entity?.posY?:0.0, x.event?.entity?.posZ?:0.0,
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                x.event?.entity?.dimension?.toDouble()?:0.0
            ) + confAccumulators.map {tag.getDouble(it)}
            ).toDoubleArray()
    }

    override fun interestingNumberList() = listOf("entity",
            "x", "y", "z",
            "dim") + confAccumulators
}

class SpawnCTX(val tile: Boolean, val logic: MobSpawnerBaseLogic, val duratag: NBTTagCompound,
               val event: LivingSpawnEvent.CheckSpawn? = null): EvaluationContext()

val DENY = CheckSpawnEffect(Event.Result.DENY)
val DEFAULT = CheckSpawnEffect(Event.Result.DEFAULT)
val ALLOW = CheckSpawnEffect(Event.Result.ALLOW)

class CheckSpawnEffect(val result: Event.Result): Effect<SpawnCTX> {
    override fun getSlot() = -1
    override fun accept(ctx: SpawnCTX) {
        ctx.event?.result = result
    }
}

class AccumulatorEffect(private val slot: Int, val tag: String, val inc: Boolean, val amount: Double): Effect<SpawnCTX> {
    override fun getSlot() = slot
    override fun accept(ctx: SpawnCTX) =
        ctx.duratag.setDouble(tag, amount + (if (inc) ctx.duratag.getDouble(tag) else 0.0))
}