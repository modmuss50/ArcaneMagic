package com.raphydaphy.arcanemagic.core.common;

import com.raphydaphy.arcanemagic.init.ArcaneMagicConstants;
import com.raphydaphy.arcanemagic.init.ModEvents;
import com.raphydaphy.arcanemagic.init.ModRegistry;
import com.raphydaphy.arcanemagic.item.ICrystalEquipment;
import com.raphydaphy.arcanemagic.parchment.DiscoveryParchment;
import com.raphydaphy.arcanemagic.util.ArcaneMagicUtils;
import com.raphydaphy.arcanemagic.util.DataHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	@Shadow protected int playerHitTimer;

	@Shadow private int lastAttackedTime;

	@Shadow public int hurtTime;

	@Shadow public abstract void setAttacker(LivingEntity livingEntity_1);

	@Shadow protected boolean dead;

	protected LivingEntityMixin(EntityType<? extends LivingEntity> type, World world)
	{
		super(type, world);
	}

	@Inject(at = @At(value = "HEAD"), method = "method_16077", cancellable = true) // dropLoot
	private void method_16077(DamageSource source, boolean killedByPlayer, CallbackInfo info)
	{
		if (source == ModRegistry.DRAINED_DAMAGE || ModEvents.shouldLivingEntityDropLoot(this, source))
		{
			info.cancel();
		}
	}

	@Inject(at = @At(value ="RETURN"), method="damage")
	private void damage(DamageSource source, float float_1, CallbackInfoReturnable<Boolean> info)
	{
		Entity entity = source.getAttacker();
		if (entity instanceof LivingEntity)
		{
			ItemStack stack = ((LivingEntity)entity).getMainHandStack();
			CompoundTag tag;
			if (stack.getItem() instanceof ICrystalEquipment && (tag = stack.getTag()) != null)
			{
				ArcaneMagicUtils.ForgeCrystal active = ArcaneMagicUtils.ForgeCrystal.getFromID(tag.getString(ArcaneMagicConstants.ACTIVE_CRYSTAL_KEY));
				int timer = tag.getInt(ArcaneMagicConstants.ACTIVE_TIMER_KEY);
				if (active == ArcaneMagicUtils.ForgeCrystal.GOLD && timer > 0)
				{
					// TODO: Disable hit cooldown.. how?
					this.playerHitTimer = 0;
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method="onDeath")
	private void onDeath(DamageSource source, CallbackInfo info)
	{
		if (this.getType() == EntityType.DROWNED && !this.dead && !world.isClient)
		{
			Entity attacker = source.getAttacker();
			if (attacker instanceof PlayerEntity)
			{
				int kills = ((DataHolder)attacker).getAdditionalData().getInt(ArcaneMagicConstants.DROWNED_KILLS_KEY);
				int paper = -1;
				boolean giveParchment = false;

				if (kills < 5)
				{
					if (kills < 2)
					{
						for (int i = 0; i < ((PlayerEntity)attacker).inventory.getInvSize(); i++)
						{
							if (((PlayerEntity)attacker).inventory.getInvStack(i).getItem() == Items.PAPER)
							{
								paper = i;
								break;
							}
						}

						if (kills == 0)
						{
							String message = "message.arcanemagic.drowned_first_kill";
							if (paper != -1)
							{
								message = "message.arcanemagic.drowned_paper_first";
								kills++;
								giveParchment = true;
							}
							((PlayerEntity)attacker).addChatMessage(new TranslatableTextComponent(message).setStyle(new Style().setColor(TextFormat.DARK_PURPLE)), false);
						} else if (kills == 1 && paper != -1)
						{
							giveParchment = true;
							((PlayerEntity)attacker).addChatMessage(new TranslatableTextComponent("message.arcanemagic.drowned_paper_second").setStyle(new Style().setColor(TextFormat.DARK_PURPLE)), false);
						}
					}

					if (giveParchment)
					{
						ItemStack parchment = new ItemStack(ModRegistry.WRITTEN_PARCHMENT);
						parchment.getOrCreateTag().putString(ArcaneMagicConstants.PARCHMENT_TYPE_KEY, DiscoveryParchment.NAME);

						ItemStack paperStack = ((PlayerEntity) attacker).inventory.getInvStack(paper);
						if (!paperStack.isEmpty())
						{
							paperStack.subtractAmount(1);
						}
						if (!((PlayerEntity)attacker).method_7270(parchment.copy())) // addItemStackToInventory
						{
							world.spawnEntity(new ItemEntity(world, attacker.x, attacker.y + 0.5, attacker.z, parchment.copy()));
						}
					}
					if (kills != 1 || paper != -1)
					{
						((DataHolder)attacker).getAdditionalData().putInt(ArcaneMagicConstants.DROWNED_KILLS_KEY, kills + 1);
						((DataHolder)attacker).markAdditionalDataDirty();
					}
				}
			}
		}
	}
}
