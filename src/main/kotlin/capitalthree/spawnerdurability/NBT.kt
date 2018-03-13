package capitalthree.spawnerdurability

import net.minecraft.nbt.NBTTagCompound

fun NBTTagCompound.setNewTagCompound(tagname: String): NBTTagCompound {
    val fresh = NBTTagCompound()
    setTag(tagname, fresh)
    return fresh
}

fun NBTTagCompound.getOrCreateCompoundTag(tagname: String) =
        getTag(tagname) as? NBTTagCompound ?: setNewTagCompound(tagname)

val FORGEDATA_TAG = "ForgeData"
val ENCAPSULATION_TAGS = arrayOf("WrappedTE", "WrappedEnt")
val DURA_TAG = "SpawnerDura"
fun getOrCreateDuraTag(root: NBTTagCompound): NBTTagCompound {
    val forgedat = root.getOrCreateCompoundTag(FORGEDATA_TAG)
    (forgedat.getTag(DURA_TAG) as? NBTTagCompound)?.let { return it }
    ENCAPSULATION_TAGS.forEach {
        (forgedat.getCompoundTag(it).getTag(DURA_TAG) as? NBTTagCompound)?.let { return it }
    }
    return root.setNewTagCompound(DURA_TAG)
}