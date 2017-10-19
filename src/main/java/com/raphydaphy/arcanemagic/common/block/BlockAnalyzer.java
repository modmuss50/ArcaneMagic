package com.raphydaphy.arcanemagic.common.block;

import java.util.ArrayList;
import java.util.List;

import com.raphydaphy.arcanemagic.api.ArcaneMagicAPI;
import com.raphydaphy.arcanemagic.api.notebook.NotebookCategory;
import com.raphydaphy.arcanemagic.common.tileentity.TileEntityAnalyzer;
import com.raphydaphy.arcanemagic.common.util.IHasRecipe;
import com.raphydaphy.arcanemagic.common.util.RecipeHelper;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

public class BlockAnalyzer extends BlockBase implements IHasRecipe
{
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(1d * (1d / 16d), 0.0D, 1d * (1d / 16d),
			15d * (1d / 16d), 10d * (1d / 16d), 15d * (1d / 16d));
	
	public int recipeID = -1;

	public BlockAnalyzer()
	{
		super("analyzer", Material.WOOD, 2.5f);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return AABB;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityAnalyzer te = (TileEntityAnalyzer) world.getTileEntity(pos);

		if (!te.getStack(0).isEmpty())
		{
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getStack(0).copy());
		}

		if (!te.getStack(1).isEmpty())
		{
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getStack(1).copy());
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityAnalyzer();
	}

	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		TileEntity teUnchecked = world.getTileEntity(pos);
		if (!(teUnchecked instanceof TileEntityAnalyzer))
		{
			return false;
		}
		TileEntityAnalyzer te = (TileEntityAnalyzer) teUnchecked;
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.isEmpty() && !player.isSneaking())
		{
			ItemStack insertStack = stack.copy();
			if (stack.getItem().equals(Items.PAPER))
			{
				List<NotebookCategory> unlockable = ArcaneMagicAPI.getFromAnalysis(te.getStack(0).copy(),
						new ArrayList<>());
				if (!te.getStack(0).isEmpty() && unlockable.size() > 0 && te.getStack(1).isEmpty())
				{

					stack.shrink(1);
					te.setStack(1, new ItemStack(Items.PAPER));
				}
			} else if (te.getStack(0).isEmpty())
			{
				stack.shrink(1);
				insertStack.setCount(1);
				player.setHeldItem(hand, stack);
				te.setStack(0, insertStack);
				((TileEntityAnalyzer) te).setPlayer(player);
				te.markDirty();
				world.playSound(player, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 1, 1);

			}
		} else if (player.isSneaking())
		{
			ItemStack toExtract = te.getStack(0).copy();
			if (!toExtract.isEmpty())
			{
				if (!world.isRemote)
				{
					if (player.addItemStackToInventory(toExtract.copy()))
					{
						te.setStack(0, ItemStack.EMPTY);
						te.markDirty();
					}
				} else
				{
					world.playSound(player, pos, SoundEvents.ENTITY_ITEMFRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1, 1);
				}
			}
		}

		return true;
	}

	@Override
	public void initRecipes(Register<IRecipe> e)
	{
		RecipeHelper.addShaped(this, 3, 3, null, "enderpearl", null, "plankWood", "blockGlass", "plankWood",
				"plankWood", null, "plankWood");
		recipeID = RecipeHelper.getJ();
	}

	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
}