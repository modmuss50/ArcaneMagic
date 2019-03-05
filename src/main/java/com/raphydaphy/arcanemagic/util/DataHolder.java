package com.raphydaphy.arcanemagic.util;

import net.minecraft.nbt.CompoundTag;

public interface DataHolder
{
	CompoundTag getAdditionalData();

	void setAdditionalData(CompoundTag tag);

	default void markAdditionalDataDirty() {}
}
