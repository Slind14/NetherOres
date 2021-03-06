package powercrystals.netherores.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercrystals.netherores.NetherOresCore;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityArmedOre extends Entity
{
	private int _fuse;
	private Block _target;
	
	public EntityArmedOre(World world)
	{
		super(world);
		_fuse = 80;
		preventEntitySpawning = true;
		setSize(0.0F, 0.0F);
		yOffset = height / 2.0F;
	}

	public EntityArmedOre(World world, double x, double y, double z) { this(world, x, y, z, null); }
	
	public EntityArmedOre(World world, double x, double y, double z, Block block)
	{
		this(world);
		setPosition(x, y, z);
		motionX = 0.0F;
		motionY = 0.0F;
		motionZ = 0.0F;
		_fuse = 80;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		_target = block;
		setAir(Block.getIdFromBlock(_target));
	}

	@Override
	protected void entityInit() {}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}
	
	@Override
	public void onUpdate()
	{
		if(_fuse-- <= 0)
		{
			setDead();

			if(!worldObj.isRemote)
			{
				setInvisible(true);
				explode();
			}
		}
		else if (worldObj.isRemote)
		{
			if (isInvisible())
				setDead();
			Block block = worldObj.getBlock(MathHelper.floor_double(posX),
					MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
			if (Block.getIdFromBlock(block) == getAir())
				worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		}
	}

	private void explode()
	{
		Block blockId = worldObj.getBlock(MathHelper.floor_double(posX),
				MathHelper.floor_double(posY), MathHelper.floor_double(posZ));
		if (blockId == _target)
		{
			worldObj.newExplosion(null, this.posX, this.posY, this.posZ,
					NetherOresCore.explosionPower.getInt(), true, true);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setByte("Fuse", (byte)_fuse);
		tag.setString("STarget", Block.blockRegistry.getNameForObject(_target));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		_fuse = tag.getByte("Fuse");
		_target = Block.getBlockFromName(tag.hasKey("Target") ?
				Integer.toString(tag.getInteger("Target")) : tag.getString("STarget"));
		setAir(Block.getIdFromBlock(_target));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getShadowSize()
	{
		return 0.0F;
	}
}
