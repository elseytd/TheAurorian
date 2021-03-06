package com.elseytd.theaurorian.Blocks;

import com.elseytd.theaurorian.TAItems;
import com.elseytd.theaurorian.TAMod;
import com.elseytd.theaurorian.TAUtil;
import com.elseytd.theaurorian.Items.TAItem_Special_DungeonKey;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TABlock_DungeonStoneGateKeyhole extends Block {

	public static final String BLOCKNAME_RUNESTONE = "runestonegatekeyhole";
	public static final String BLOCKNAME_RUNESTONELOOT = "runestonelootgatekeyhole";
	public static final String BLOCKNAME_MOONTEMPLE = "moontemplegatekeyhole";
	public static final String BLOCKNAME_MOONTEMPLECELL = "moontemplecellgatekeyhole";
	public static final String BLOCKNAME_DARK = "darkstonegatekeyhole";

	private boolean Lockpickable = false;
	private TABlock_DungeonStoneGate gateBlock = null;
	private TAItem_Special_DungeonKey keyItem = null;
	private int maxBlocksFromKeyhole = 2;

	public TABlock_DungeonStoneGateKeyhole(String blockname) {
		super(Material.ROCK);
		this.setCreativeTab(TAMod.CREATIVE_TAB);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName(TAMod.MODID + "." + blockname);
		this.setRegistryName(blockname);
	}

	public TABlock_DungeonStoneGateKeyhole(String blockname, String gateblockname, TAItem_Special_DungeonKey.Keys keyitemname) {
		this(blockname);
		this.setGate(new TABlock_DungeonStoneGate(gateblockname));
		this.setKey(new TAItem_Special_DungeonKey(keyitemname));
	}

	public TABlock_DungeonStoneGateKeyhole(String blockname, String gateblockname, TAItem_Special_DungeonKey.Keys keyitemname, int maxdistance) {
		this(blockname, gateblockname, keyitemname);
		this.setMaxGateDistance(maxdistance);
	}

	public TABlock_DungeonStoneGateKeyhole(String blockname, String gateblockname, TAItem_Special_DungeonKey.Keys keyitemname, boolean islockpickable) {
		this(blockname, gateblockname, keyitemname);
		this.setLockpickable(islockpickable);
	}

	public TABlock_DungeonStoneGateKeyhole(String blockname, String gateblockname, TAItem_Special_DungeonKey.Keys keyitemname, int maxdistance, boolean islockpickable) {
		this(blockname, gateblockname, keyitemname, maxdistance);
		this.setLockpickable(islockpickable);
	}

	public void setLockpickable(boolean islockpickable) {
		this.Lockpickable = islockpickable;
	}

	public boolean isLockpickable() {
		return this.Lockpickable;
	}

	public int getMaxGateDistance() {
		return this.maxBlocksFromKeyhole;
	}

	public void setMaxGateDistance(int dist) {
		this.maxBlocksFromKeyhole = dist;
	}

	public TABlock_DungeonStoneGate getGate() {
		return this.gateBlock;
	}

	public void setGate(TABlock_DungeonStoneGate gateblock) {
		this.gateBlock = gateblock;
	}

	public boolean isGate(Block bcheck) {
		if (this.getGate().getRegistryName().equals(bcheck.getRegistryName())) {
			return true;
		} else {
			return false;
		}
	}

	public TAItem_Special_DungeonKey getKey() {
		return this.keyItem;
	}

	public void setKey(TAItem_Special_DungeonKey keyitem) {
		this.keyItem = keyitem;
	}

	public boolean isKey(Item icheck) {
		if (this.getKey().getRegistryName().equals(icheck.getRegistryName())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!playerIn.isSneaking() && this.isKey(playerIn.getHeldItem(hand).getItem())) {
			playerIn.getHeldItem(hand).damageItem(2, playerIn);
			this.breakGateBlocks(worldIn, pos);
			if (!worldIn.isRemote) {
				worldIn.destroyBlock(pos, false);
			}
			playerIn.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, 1F, 1F);
		} else if (!playerIn.isSneaking() && playerIn.getHeldItem(hand).getItem() == TAItems.Registry.LOCKPICKS.getItem() && this.isLockpickable()) {
			playerIn.getHeldItem(hand).damageItem(1, playerIn);
			if (TAUtil.randomChanceOf(0.33F)) {
				this.breakGateBlocks(worldIn, pos);
				if (!worldIn.isRemote) {
					worldIn.destroyBlock(pos, false);
				}
				playerIn.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, 1F, 1F);
			} else {
				playerIn.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.5F, 1F);
			}
		} else {
			playerIn.sendStatusMessage(new TextComponentString(I18n.format("string.theaurorian.block.dungeonstonekeyhole1") + (this.isLockpickable() ? " " + I18n.format("string.theaurorian.block.dungeonstonekeyhole2") : "")), true);
		}
		return true;
	}

	public void breakGateBlocks(World worldIn, BlockPos pos) {
		int gated = this.getMaxGateDistance();
		for (int x = -gated; x <= gated; x++) {
			for (int y = -gated; y <= gated; y++) {
				BlockPos blkpos = new BlockPos(pos.getX() - x, pos.getY() - y, pos.getZ());
				IBlockState blk = worldIn.getBlockState(blkpos);
				if (this.isGate(blk.getBlock())) {
					if (!worldIn.isRemote) {
						worldIn.destroyBlock(blkpos, false);
					}
				}
			}
		}

		for (int z = -gated; z <= gated; z++) {
			for (int y = -gated; y <= gated; y++) {
				BlockPos blkpos = new BlockPos(pos.getX(), pos.getY() - y, pos.getZ() - z);
				IBlockState blk = worldIn.getBlockState(blkpos);
				if (this.isGate(blk.getBlock())) {
					if (!worldIn.isRemote) {
						worldIn.destroyBlock(blkpos, false);
					}
				}
			}
		}
	}

}