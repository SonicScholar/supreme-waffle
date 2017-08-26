package com.matrixugly.magicwand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WandEventListener implements Listener {

	static ConcurrentHashMap<Player, LocalDateTime> cooldowns = new ConcurrentHashMap<Player, LocalDateTime>();
	
	static boolean isCooldownReady(Player player, int seconds)
	{
		if(!cooldowns.containsKey(player))
			return true;
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastUsage = cooldowns.get(player);
		LocalDateTime cooldownTime = lastUsage.plusSeconds(seconds);
		
		long secondsLeft = now.until(cooldownTime, ChronoUnit.SECONDS);
		
		if(cooldownTime.isAfter(now))
		{
			player.sendMessage("You have " + secondsLeft + " second(s) left on the cooldown");
			return false;
		}
		else
			return true;
	}
	
	public static void resetCooldown(Player player)
	{
		cooldowns.put(player, LocalDateTime.now());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) throws Exception
	{
		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();
		ItemStack currentItem = inv.getItemInMainHand();
		
		Material blockType = e.getBlock().getType();
		if(blockType == Material.OBSIDIAN && currentItem.getType() == Material.BLAZE_ROD)
		{
			if(currentItem.hasItemMeta() && 
					currentItem.getItemMeta().getDisplayName().equalsIgnoreCase("Magic Wand"))
				return;
			
			Random RNG = new Random();
			float f = RNG.nextFloat();
			if(f < .10)
			{
				p.sendMessage("Sorry, your magic blaze rod was turned to dust... bad RNG :(");
				inv.setItemInMainHand(new ItemStack(Material.BLAZE_POWDER,1));
			}
			else
			{
				WandData newMagicWand = new WandData(currentItem);
				newMagicWand.saveData();
				p.sendMessage("Your blaze rod's character is true! It is now a magic wand!");
			}
				
		}
		
		
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) throws Exception
	{
		boolean wandIsValid = validateMagicWand(e);
		if(!wandIsValid)
			return;
		
		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();
		ItemStack currentItem = inv.getItemInMainHand();

		WandData wandData = new WandData(currentItem);

		//search for stuff
		Block clicked = e.getClickedBlock();
		Location playerLoc = clicked.getLocation();
//		p.sendMessage("block: " + clicked.getType() + " " + clicked.getX() + " "+ clicked.getY() + " "+ clicked.getZ());
		
		int radius = 1;
		if(e.getAction() == Action.LEFT_CLICK_BLOCK)
			radius = wandData.getMiningDistance() -1;
		else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
			radius = wandData.getSearchDistance() -1;
		
		int x = playerLoc.getBlockX();
		//y should be no less than player's eye height
		int y = (int) Math.max(clicked.getY(), p.getLocation().getBlockY()+1);
		int z = playerLoc.getBlockZ();
		
		
		BlockFace wandDirection = e.getBlockFace().getOppositeFace();
		Location l1 = playerLoc;
		Location l2 = playerLoc;
		
		
		switch(wandDirection)
		{
		//east - positive x
		// startblock = get block directly east of player (at eye location)
		// 3x3xRadius cuboid that player is facing
		// corner1: startblock.x,y+1,z+1
		// corner2: startblock.x + radius, startblock.y-1, z-1
		case EAST:
			//p.sendMessage("EAST");
			l1 = new Location(p.getWorld(), x, y+1, z+1);
			l2 = new Location(p.getWorld(), x+radius, y-1, z-1);
			break;
		//north - negative z
		//startblock = get block directly north of player (at eye location)
		//3x3xRadius cuboid that player is facing
		//corner1:  x+1, y+1, startblock.z
		//corner2:  x-1, y-1, startblock.z - radius
		case NORTH:
			//p.sendMessage("NORTH");
			l1 = new Location(p.getWorld(), x+1, y+1, z);
			l2 = new Location(p.getWorld(), x-1, y-1, z-radius);
			break;
		//south - positive z
		//startblock = get block directly south of player (at eye location)
		//3x3xRadius cuboid that player is facing
		//corner1:  x+1, y+1, startblock.z
		//corner2:  x-1, y-1, startblock.z + radius
		case SOUTH:
			//p.sendMessage("SOUTH");
			l1 = new Location(p.getWorld(), x+1, y+1, z);
			l2 = new Location(p.getWorld(), x-1, y-1, z+radius);
			break;
		//west - negative x
		// startblock = get block directly west of player (at eye location)
		// 3x3xRadius cuboid that player is facing
		// corner1: startblock.x, y+1, z+1
		// corner2: startblock.x - radius, startblock.y-1, z-1
		case WEST:
			//p.sendMessage("WEST");
			l1 = new Location(p.getWorld(), x, y+1, z+1);
			l2 = new Location(p.getWorld(), x-radius, y-1, z-1);
			break;
		case UP:
		case DOWN:
		default:
			//todo: get direction of player
			//see if they are within a 30 degree angle tolerance of facing N, S, E, or W
			//p.sendMessage("DEFAULT: " + wandDirection.toString());
			break;
		}
		//todo: materials to search for
		
		List<Material> searchMaterials = wandData.getDetectableMaterials();
		
		Cuboid c = new Cuboid(l1, l2);
		if(e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			for(Block b : c)
			{
				Material blockMaterial = b.getType();
				int xp = WandData.getXpForMaterial(blockMaterial);
				boolean newLevel = wandData.addXp(xp);
				if(newLevel)
					p.sendMessage("You gained a level! Use the '/wandlevelup' command to upgrade your wand!");
				
				if(blockMaterial == Material.IRON_ORE && currentItem.getType() == Material.BLAZE_ROD)
				{
					b.setType(Material.AIR);
					World w = p.getWorld();
					w.dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
				}
				else if(blockMaterial == Material.GOLD_ORE && currentItem.getType() == Material.BLAZE_ROD)
				{
					b.setType(Material.AIR);
					World w = p.getWorld();
					w.dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
				}
				else
					b.breakNaturally();
			}
			wandData.saveData();
			resetCooldown(p);
			return;
		}
		else if(e.getAction() == Action.RIGHT_CLICK_BLOCK);
		{
			List<Block> searchResults = c.search(searchMaterials);
			
			for(Block b : searchResults)
			{
				Location bLoc = b.getLocation();	
				p.sendMessage("found block: " + b.getType() + " at x:" + (bLoc.getBlockX()) 
						+ " y:" + bLoc.getBlockY() + " z:" + bLoc.getBlockZ());
			}
			resetCooldown(p);
			
		}
	}
	
	public boolean validateMagicWand(PlayerInteractEvent e)
	{
		//must be using main hand (not off hand)
		if(e.getHand() != EquipmentSlot.HAND)
			return false;
		
		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();
		ItemStack currentItem = inv.getItemInMainHand();
		Material itemMaterial = currentItem.getType();
		
		//this event handles only blaze rod, with right and left clicks only
		boolean isBlazeRod = itemMaterial == Material.BLAZE_ROD;
		boolean isRightOrLeftClickBlock = e.getAction() == Action.RIGHT_CLICK_BLOCK || 
				e.getAction() == Action.LEFT_CLICK_BLOCK;
		
		if(!isBlazeRod || !isRightOrLeftClickBlock)
			return false;
			
		//validation/error checking
			
		//can only have 1 magic wand equipped
		int amount = currentItem.getAmount();
		if(amount != 1)
			return false;
		
		try {
			//blaze rod must be named properly
			String itemName = currentItem.getItemMeta().getDisplayName();
			if(!"Magic Wand".equalsIgnoreCase(itemName))
				return false;

			WandData wandData = new WandData(currentItem);
			int cooldown = wandData.getCooldown();
			boolean isCooldownReady = isCooldownReady(p, cooldown);
			if(!isCooldownReady)
			{
				return false;
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void handleLeftClick(PlayerInteractEvent e)
	{
		
	}
	
	public void handleRightClick(PlayerInteractEvent e)
	{
		
	}
}
