package com.raphydaphy.arcanemagic.common.network;

import com.raphydaphy.arcanemagic.api.essence.EssenceStack;
import com.raphydaphy.arcanemagic.common.ArcaneMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEssenceTransfer implements IMessage
{
	private Vec3d from;
	private Vec3d to;
	private Vec3d toCosmetic;
	private EssenceStack essence;
	private boolean spawnParticles;

	public PacketEssenceTransfer()
	{
	}

	public PacketEssenceTransfer(EssenceStack essence, Vec3d from, Vec3d to, Vec3d toCosmetic, boolean spawnParticles)
	{
		this.from = from;
		this.to = to;
		this.toCosmetic = toCosmetic;
		this.essence = essence;
	}

	public static class Handler implements IMessageHandler<PacketEssenceTransfer, IMessage>
	{
		@Override
		public IMessage onMessage(PacketEssenceTransfer message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketEssenceTransfer message, MessageContext ctx)
		{
			ArcaneMagic.proxy.sendEssenceSafe(message.essence, message.from, message.to, message.toCosmetic, message.spawnParticles);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		from = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		to = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		toCosmetic = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		essence = EssenceStack.readFromBuf(buf);
		spawnParticles = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(from.x);
		buf.writeDouble(from.y);
		buf.writeDouble(from.z);
		buf.writeDouble(to.x);
		buf.writeDouble(to.y);
		buf.writeDouble(to.z);
		buf.writeDouble(toCosmetic.x);
		buf.writeDouble(toCosmetic.y);
		buf.writeDouble(toCosmetic.z);
		EssenceStack.writeToBuf(buf, essence);
		buf.writeBoolean(spawnParticles);
	}
}