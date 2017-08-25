package com.matrixugly.magicwand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
	int xp;
	ItemStack blazeRod;

	public WandData() {
		blazeRod = new ItemStack(Material.BLAZE_ROD);
		rawData = 0;
		xp = 0;
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
			lore.add("0"); //0 initial xp
			rodMeta.setLore(lore);
		} 
		else {
			if (lore.size() >= 1) {
				String levelLore = lore.get(0);
				rawData = Short.parseShort(levelLore, 6); 
			}
			if (lore.size() >= 2) {
				String xpLore = lore.get(1);
				xp = Integer.parseInt(xpLore);
			}
		}
		this.blazeRod = blazeRod;
	}

//	public static int SEARCH_DISTANCE_DIGIT = 0;
//	public static int SEARCH_DETECTION_DIGIT = 1;
//	public static int MINING_DIGIT = 2;
//	public static int COOLDOWN_DIGIT = 3;

	private static int[] DISTANCE_LEVEL = { 3, 4, 6, 9, 13, 18 };
	private static int[] MINING_LEVEL = { 1, 2, 4, 7, 11, 16 };
	private static int[] COOLDOWN_LEVEL = { 15, 13, 11, 9, 7, 4 };

	private static final ConcurrentHashMap<Material, Integer> xpValues;
	static
	{
		xpValues = new ConcurrentHashMap<Material, Integer>();
		xpValues.put(Material.COAL_ORE, 1);
		xpValues.put(Material.IRON_ORE, 2);
		xpValues.put(Material.REDSTONE_ORE, 3);
		xpValues.put(Material.LAPIS_ORE, 3);
		xpValues.put(Material.GOLD_ORE, 5);
		xpValues.put(Material.EMERALD_ORE, 20);
		xpValues.put(Material.DIAMOND_ORE, 15);

	}
	
	public static Material[] MATERIAL_LEVEL = {
			Material.COAL_ORE, 
			Material.IRON_ORE, 
			Material.REDSTONE_ORE,
			Material.GOLD_ORE, 
			Material.EMERALD_ORE, 
			Material.DIAMOND_ORE };

	public static int[] XP_PER_LEVEL = { 100, 110, 121, 133, 146, 161, 177, 194, 214, 235, 259, 285, 313, 345, 379, 417,
			451, 505, 555, 611, 672, 740, 814, 895 };

	public int getUsedLevels()
	{
		int result = getSearchDistanceLevel()
				+ getItemDetectionLevel()
				+ getMiningLevel()
				+ getCooldownLevel() -4; //all levels start at 1.
		
		return result;
	}
	
	public int getAvailableLevels()
	{
		//figure out how much xp is not usable by the used levels
		int usedLevels = this.getUsedLevels();
		int xpLeft = xp;
		for(int i=0; i< usedLevels; i++)
		{
			xpLeft -= XP_PER_LEVEL[i];
		}
		
		int availableLevels = 0;
		do
		{
			if(availableLevels + usedLevels == XP_PER_LEVEL.length)
				break;
			
			xpLeft -= XP_PER_LEVEL[availableLevels + usedLevels];
			if(xpLeft >= 0)
				availableLevels++;
		}while(xpLeft > 0);
		
		return availableLevels;
	}
	
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
		char c = heximal.charAt(WandLevelAttribute.SEARCH_DISTANCE.getDigit());
		return (short) (Short.parseShort("" + c, 6) + 1);

	}

	public void setSearchDistanceLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != WandLevelAttribute.SEARCH_DISTANCE.getDigit())
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
		char c = heximal.charAt(WandLevelAttribute.SEARCH_DETECTION.getDigit());
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setItemDetectionLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != WandLevelAttribute.SEARCH_DETECTION.getDigit())
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
		char c = heximal.charAt(WandLevelAttribute.MINING_DISTANCE.getDigit());
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setMiningLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != WandLevelAttribute.MINING_DISTANCE.getDigit())
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
		char c = heximal.charAt(WandLevelAttribute.COOLDOWN.getDigit());
		return (short) (Short.parseShort("" + c, 6) + 1);
	}

	public void setCooldownLevel(short level) {
		level--;
		String validLevel = Integer.toString(level, 6);
		String heximal = toHeximalString();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != WandLevelAttribute.COOLDOWN.getDigit())
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
		lore.add(new Integer(xp).toString());
		ItemMeta meta = blazeRod.getItemMeta();
		meta.setLore(lore);
		blazeRod.setItemMeta(meta);
	}
	
	public static int getXpForMaterial(Material blockMaterial) {
		if(!xpValues.containsKey(blockMaterial))
			return 0;
		return xpValues.get(blockMaterial);
	}
	
	public int getLevelFromXp()
	{
		int currentXpLevel = 0;
		
		int currLevelXP = this.xp;
		for(int i=0; i< XP_PER_LEVEL.length; i++)
		{
			currLevelXP -= XP_PER_LEVEL[i];
			if(currLevelXP < 0)
				break;
			else
				currentXpLevel++;
		}
		
		return currentXpLevel;
	}
	
	public int getXp()
	{
		return xp;
	}
	
	/**
	 * 
	 * @param xp
	 * @return true, if by adding this xp, it causes a level to be gained
	 */
	public boolean addXp(int xp)
	{
		int oldXpLevel = getLevelFromXp();
		
		int maxXP = Arrays.stream(XP_PER_LEVEL).sum();
		this.xp += xp;
		
		int newXpLevel = getLevelFromXp();
		
		
		//make sure added xp doesn't go past the max
		this.xp = Math.min(this.xp, maxXP);
		
		return newXpLevel > oldXpLevel;
	}
	
	public void levelup(WandLevelAttribute levelAttribute) throws Exception {
		int availableLevels = this.getAvailableLevels();
		if(availableLevels < 1)
			throw new Exception("No levels available to spend!");
		
		int maxLevel = 6;
		Exception e = new Exception("Already at max level (" + maxLevel +")!");
		switch(levelAttribute)
		{
		case SEARCH_DISTANCE:
			int search_dist = getSearchDistanceLevel();
			if(search_dist >= maxLevel)
				throw e;
			setSearchDistanceLevel((short) (search_dist+1));
			break;
		case SEARCH_DETECTION:
			int search_detect = getItemDetectionLevel();
			if(search_detect >= maxLevel)
				throw e;
			setItemDetectionLevel((short) (search_detect+1));
			break;
		case MINING_DISTANCE:
			int mine_dist = getMiningLevel();
			if(mine_dist >= maxLevel)
				throw e;
			setMiningLevel((short) (mine_dist+1));
			break;
		case COOLDOWN:
			int cooldown = getCooldownLevel();
			if(cooldown >= maxLevel)
				throw e;
			setCooldownLevel((short) (cooldown+1));
			break;
		}
		this.saveData();
	}

	public static void doTests() {
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		WandData empty;
		try {
			ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
			empty = new WandData(item);

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
			
			empty.addXp(90);
			empty.saveData();
			
			WandData other = new WandData(item);
			for(String lore : item.getItemMeta().getLore())
				console.sendMessage("lore: " + lore);
			

			console.sendMessage("getLevels: " + empty.getCooldownLevel() + " " + empty.getMiningLevel() + " "
					+ empty.getItemDetectionLevel() + " " + empty.getSearchDistanceLevel());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			String message = "";
			e.printStackTrace();
			console.sendMessage("unit test error: " + message);
		}
	}




}
