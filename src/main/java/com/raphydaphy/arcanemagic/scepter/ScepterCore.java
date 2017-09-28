package com.raphydaphy.arcanemagic.scepter;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.api.scepter.ScepterPart;

import net.minecraft.util.ResourceLocation;

public class ScepterCore extends ScepterPart
{

	public static final ScepterCore WOOD = new ScepterCore("wood");
	public static final ScepterCore GREATWOOD = new ScepterCore("greatwood");
	public static final ScepterCore SILVERWOOD = new ScepterCore("silverwood");

	private final String UNLOC_NAME;
	private final ResourceLocation TEXTURE;
	private final ResourceLocation REGNAME;

	public ScepterCore(String name)
	{
		super(PartCategory.CORE);
		UNLOC_NAME = ArcaneMagic.MODID + ".core." + name;
		TEXTURE = new ResourceLocation(ArcaneMagic.MODID, "textures/items/scepter/core_" + name);
		REGNAME = new ResourceLocation(ArcaneMagic.MODID, name);
	}

	@Override
	public String getUnlocalizedName()
	{
		return UNLOC_NAME;
	}

	@Override
	public ResourceLocation getTexture()
	{
		return TEXTURE;
	}

	@Override
	public ResourceLocation getRegistryName()
	{
		return REGNAME;
	}

}