package com.elseytd.theaurorian.Entities.Hostile;

import java.util.List;

import javax.annotation.Nullable;

import com.elseytd.theaurorian.TABlocks;
import com.elseytd.theaurorian.TAConfig;
import com.elseytd.theaurorian.TAItems;
import com.elseytd.theaurorian.TAMod;
import com.elseytd.theaurorian.Util.EntityHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoonAcolyte_Entity extends EntityMob {

	private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(MoonAcolyte_Entity.class, DataSerializers.BOOLEAN);

	public static final String EntityName = "moonacolyte";
	public static final ResourceLocation LOOT = new ResourceLocation(TAMod.MODID, "entities/" + EntityName);
	public static final float MobScale = 1F;
	public int maxNearby = 4 * TAConfig.Config_MoonTempleMobDensity;

	public MoonAcolyte_Entity(World worldIn) {
		super(worldIn);
		ItemStack sword = new ItemStack(TAItems.Registry.AURORIANSTONESWORD.getItem());
		sword.addEnchantment(Enchantments.KNOCKBACK, 2);
		this.setHeldItem(EnumHand.MAIN_HAND, sword);
		this.setSize(0.6F * MobScale, 1.95F * MobScale);
	}

	@Override
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		List<EntityLivingBase> entities = EntityHelper.getEntitiesAround(this.world, this.posX, this.posY, this.posZ, 64, 30, false);
		int maxcount = this.maxNearby;
		int count = 0;
		for (EntityLivingBase e : entities) {
			if (e instanceof MoonAcolyte_Entity) {
				count++;
			}
		}
		return count <= maxcount && this.world.getBlockState(blockpos.down()).getBlock() == TABlocks.Registry.DUNGEONSTONEMOONTEMPLE.getBlock() && this.dimension == TAConfig.Config_AurorianDimID && super.getCanSpawnHere();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(ARMS_RAISED, Boolean.valueOf(false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((TAConfig.Config_NIGHTMAREMODE ? 40 * TAConfig.Config_NIGHTMAREMODE_Multiplier : 20));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((TAConfig.Config_NIGHTMAREMODE ? 0.5D * TAConfig.Config_NIGHTMAREMODE_Multiplier : 0.25D));
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((TAConfig.Config_NIGHTMAREMODE ? 4D * TAConfig.Config_NIGHTMAREMODE_Multiplier : 2D));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
	}

	public void setArmsRaised(boolean armsRaised) {
		this.getDataManager().set(ARMS_RAISED, Boolean.valueOf(armsRaised));
	}

	@SideOnly(Side.CLIENT)
	public boolean isArmsRaised() {
		return this.getDataManager().get(ARMS_RAISED).booleanValue();
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		//Drop nothing held
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new MoonAcolyte_AIAttack(this, 1.0D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (super.attackEntityAsMob(entityIn)) {
			if (entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 20));
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return this.maxNearby;
	}

}
