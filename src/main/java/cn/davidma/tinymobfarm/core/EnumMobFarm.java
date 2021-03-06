package cn.davidma.tinymobfarm.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.davidma.tinymobfarm.core.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;;

public enum EnumMobFarm {

	WOOD("wood_farm", Blocks.CRAFTING_TABLE, false, new int[] {2, 3, 3}),
	STONE("stone_farm", Blocks.STONE, false, new int[] {1, 2, 3}),
	IRON("iron_farm", Blocks.IRON_BLOCK, true, new int[] {1, 2}),
	GOLD("gold_farm", Blocks.GOLD_BLOCK, true, new int[] {1, 1, 2}),
	DIAMOND("diamond_farm", Blocks.DIAMOND_BLOCK, true, new int[] {1}),
	EMERALD("emerald_farm", Blocks.EMERALD_BLOCK, true, new int[] {0, 1, 1}),
	INFERNAL("inferno_farm", Blocks.OBSIDIAN, true, new int[] {0, 0, 1}),
	ULTIMATE("ultimate_farm", Blocks.OBSIDIAN, true, new int[] {0});
	
	private String registryName;
	private Block baseBlock;
	private boolean canFarmHostile;
	private int[] damageChance;
	private Map<Integer, Integer> normalizedChance;
	
	private EnumMobFarm(String registryName, Block baseBlock, boolean canFarmHostile, int[] damageChance) {
		this.registryName = registryName;
		this.baseBlock = baseBlock;
		this.canFarmHostile = canFarmHostile;
		this.damageChance = damageChance;
		
		this.normalizedChance = new HashMap<Integer, Integer>();
		for (int i: this.damageChance) {
			if (!this.normalizedChance.containsKey(i)) this.normalizedChance.put(i, 0);
			this.normalizedChance.put(i, this.normalizedChance.get(i) + 1);
		}
		int denominator = this.damageChance.length;
		for (int i: this.normalizedChance.keySet()) {
			this.normalizedChance.put(i, (int) (this.normalizedChance.get(i) * 100.0 / denominator));
		}
	}
	
	public String getRegistryName() {
		return this.registryName;
	}
	
	public String getUnlocalizedName() {
		return String.format("tile.%s.%s.name", Reference.MOD_ID, this.registryName);
	}
	
	public Block getBaseBlock() {
		return this.baseBlock;
	}
	
	public boolean isLassoValid(ItemStack lasso) {
		return NBTHelper.hasMob(lasso) && (this.canFarmHostile || !NBTHelper.hasHostileMob(lasso));
	}
	
	public int getMaxProgress() {
		return (int) (ConfigTinyMobFarm.MOB_FARM_SPEED[this.ordinal()] * 20);
	}
	
	public int getRandomDamage(Random rand) {
		return this.damageChance[rand.nextInt(this.damageChance.length)];
	}
	
	public void addTooltip(List<String> tooltip) {
		if (!this.canFarmHostile) {
			tooltip.add(I18n.format(TextFormatting.RED + "tinymobfarm.tooltip.no_hostile"));
		}
		tooltip.add(I18n.format("tinymobfarm.tooltip.farm_rate", ConfigTinyMobFarm.MOB_FARM_SPEED[this.ordinal()]));
		tooltip.add(I18n.format("tinymobfarm.tooltip.durability_info"));
		for (int i: this.normalizedChance.keySet()) {
			if (i == 0) tooltip.add(I18n.format("tinymobfarm.tooltip.no_durability", this.normalizedChance.get(i)));
			else tooltip.add(I18n.format("tinymobfarm.tooltip.default_durability", this.normalizedChance.get(i), i));
		}
	}
}
