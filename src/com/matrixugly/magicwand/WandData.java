package com.matrixugly.magicwand;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class stores the levels of a magic wand in a 16 bit storage underneath
 * There are 4 attributes of a magic wand. Search Distance, Item Detection,
 * Mining, and Cooldowns. The maximum level for each attribute is level 6.
 * Therefore, to make most efficient use of the storage, the 16 bit number will
 * be converted to a base 6 number, and the 4 digits of that number will denote
 * the various levels.
 * 
 * @author collin.tewalt
 *
 */
public class WandData {

	
	/**
	 * Digit storage from left to right first - distance level, second -
	 * detection level, third - mining level, fourth - cooldown
	 */
	short rawData;
	ItemStack blazeRod;

	public WandData() {
		blazeRod = new ItemStack(Material.BLAZE_ROD);
		rawData = 0;
	}

	/**
	 * 
	 * @param blazeRod
	 *            - An existing blaze rod to use. The amount will be set to 1,
	 *            and name set to "Magic Wand"
	 * @throws Exception
	 *             if the item passed in is not a blaze rod
	 */
	public WandData(ItemStack blazeRod) throws Exception {
		if (blazeRod.getType() != Material.BLAZE_ROD)
			throw new Exception("Item must be a blaze rod.");

		blazeRod.setAmount(1);
		ItemMeta rodMeta = blazeRod.getItemMeta();
		rodMeta.setDisplayName("Magic Wand");
		blazeRod.setItemMeta(rodMeta);
		List<String> lore = rodMeta.getLore();

		// create lore for wand levels if it doesn't exist
		if (lore == null)
			lore = new ArrayList<String>();
		if (lore.size() == 0) {
			// create level lore with heximal string of 0
			rawData = 0;
			String heximal = this.toHeximalString();
			lore.add(heximal);
			rodMeta.setLore(lore);
		} else if (lore.size() == 1) {
			// assume lore has been created
			String levelLore = lore.get(0);
			rawData = Short.parseShort(levelLore, 6);
		} else {
			throw new Exception("This blaze rod has multiple lore. It can only have 1 for magic wand.");
		}

	}

	public static int SEARCH_DISTANCE_DIGIT = 0;
	public static int SEARCH_DETECTION_DIGIT = 1;
	public static int MINING_DIGIT = 2;
	public static int COOLDOWN_DIGIT = 3;

	private static int[] DISTANCE_LEVEL = { 3, 4, 6, 9, 13, 18 };
	private static int[] MINING_LEVEL = { 1, 2, 4, 7, 11, 16 };
	private static int[] COOLDOWN_LEVEL = { 15, 13, 11, 9, 7, 4 };

	public static Material[] MATERIAL_LEVEL = {
			Material.COAL_ORE, 
			Material.IRON_ORE, 
			Material.REDSTONE_ORE,
			Material.GOLD_ORE, 
			Material.EMERALD_ORE, 
			Material.DIAMOND_ORE };

	public static int[] XP_PER_LEVEL = { 100, 110, 121, 133, 146, 161, 177, 194, 214, 235, 259, 285, 313, 345, 379, 417,
			451, 505, 555, 611, 672, 740, 814, 895 };

	public int getSearchDistance() {
		return DISTANCE_LEVEL[this.getSearchDistanceLevel() - 1];
	}

	public int getMiningDistance() {
		return MINING_LEVEL[this.getMiningLevel() - 1];
	}

	public int getCooldown() {
		return COOLDOWN_LEVEL[this.getCooldownLevel() - 1];
	}

	public List<Material> getDetectableMaterials() {
		List<Material> materials = new ArrayList<Material>();
		int detectionLevel = this.getItemDetectionLevel();
		for (int i = 0; i < detectionLevel; i++) {
			materials.add(MATERIAL_LEVEL[i]);
		}

		return materials;
	}

	public short getRaw() {
		return rawData;
	}

	/**
	 * @return That is, a string representing number in base 6
	 */
	public String toHeximalString() {
		String heximal = Integer.toString(rawData, 6);
		int length = heximal.length();
		for (int i = length; i < 4; i++) {
			heximal = "0" + heximal;
		}
		return heximal;
	}

	// search distance
	public short getSearchDistanceLevel() {
		String heximal = toHeximalString();
		char c = heximal.charAt(SEARCH_DISTANCE_DIGIT);
		return (short) (Short.parseShort("" + c, 6) + 1);

	}

	public void setSearchDistanceLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != SEARCH_DISTANCE_DIGIT)
				s.append(heximal.charAt(i));
			else
				s.append(validLevel);
		}
		short data = Short.parseShort(s.toString(), 6);
		rawData = data;
	}

	// item detection
	public short getItemDetectionLevel() {
		String heximal = toHeximalString();
		char c = heximal.charAt(SEARCH_DETECTION_DIGIT);
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setItemDetectionLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != SEARCH_DETECTION_DIGIT)
				s.append(heximal.charAt(i));
			else
				s.append(validLevel);
		}
		short data = Short.parseShort(s.toString(), 6);
		rawData = data;
	}

	// mining
	public short getMiningLevel() {
		String heximal = toHeximalString();
		char c = heximal.charAt(MINING_DIGIT);
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setMiningLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != MINING_DIGIT)
				s.append(heximal.charAt(i));
			else
				s.append(validLevel);
		}
		short data = Short.parseShort(s.toString(), 6);
		rawData = data;
	}

	// cooldown
	public short getCooldownLevel() {
		String heximal = toHeximalString();
		char c = heximal.charAt(COOLDOWN_DIGIT);
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setCooldownLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != COOLDOWN_DIGIT)
				s.append(heximal.charAt(i));
			else
				s.append(validLevel);
		}
		short data = Short.parseShort(s.toString(), 6);
		rawData = data;
	}

	public void saveData() {
		String levels = this.toHeximalString();
		List<String> lore = new ArrayList<String>();
		lore.add(levels);
		blazeRod.getItemMeta().setLore(lore);
	}

	public static void doTests() {
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		WandData empty;
		try {
			empty = new WandData(new ItemStack(Material.BLAZE_ROD, 1));

			console.sendMessage("Starting Magic Wand Tests");
			console.sendMessage("empty wand: " + empty.toHeximalString() + " raw: " + empty.rawData);

			//
			empty.setCooldownLevel((short) 6);
			console.sendMessage("wand 1: " + empty.toHeximalString() + " raw: " + empty.rawData);

			empty.setCooldownLevel((short) 1);
			console.sendMessage("wand 1: " + empty.toHeximalString() + " raw: " + empty.rawData);

			//
			empty.setMiningLevel((short) 6);
			console.sendMessage("wand 5: " + empty.toHeximalString() + " raw: " + empty.rawData);

			empty.setMiningLevel((short) 2);
			console.sendMessage("wand 6: " + empty.toHeximalString() + " raw: " + empty.rawData);

			//
			empty.setItemDetectionLevel((short) 6);
			console.sendMessage("wand 7: " + empty.toHeximalString() + " raw: " + empty.rawData);

			empty.setItemDetectionLevel((short) 3);
			console.sendMessage("wand 8: " + empty.toHeximalString() + " raw: " + empty.rawData);
			//
			empty.setSearchDistanceLevel((short) 6);
			console.sendMessage("wand 9: " + empty.toHeximalString() + " raw: " + empty.rawData);

			empty.setSearchDistanceLevel((short) 4);
			console.sendMessage("wand 10: " + empty.toHeximalString() + " raw: " + empty.rawData);

			console.sendMessage("getLevels: " + empty.getCooldownLevel() + " " + empty.getMiningLevel() + " "
					+ empty.getItemDetectionLevel() + " " + empty.getSearchDistanceLevel());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			console.sendMessage(e.getMessage());
		}
	}
}
