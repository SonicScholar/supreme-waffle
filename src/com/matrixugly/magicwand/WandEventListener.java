package com.matrixugly.magicwand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
		
		if(cooldownTime.isAfter(now))
			return false;
		else
			return true;
	}
	
	public static void resetCooldown(Player player)
	{
		cooldowns.put(player, LocalDateTime.now());
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) throws Exception
	{
		//must be using main hand (not off hand)
		if(e.getHand() != EquipmentSlot.HAND)
			return;
		
		Player p = e.getPlayer();
		PlayerInventory inv = p.getInventory();
		ItemStack currentItem = inv.getItemInMainHand();
		Material itemMaterial = currentItem.getType();
		
		//this event handles only blaze rod, with right and left clicks only
		boolean isBlazeRod = itemMaterial == Material.BLAZE_ROD;
		boolean isRightOrLeftClickBlock = e.getAction() == Action.RIGHT_CLICK_BLOCK || 
				e.getAction() == Action.LEFT_CLICK_BLOCK;
		
		if(!isBlazeRod || !isRightOrLeftClickBlock)
			return;
			
		//validation/error checking
			
		//can only have 1 magic wand equipped
		int amount = currentItem.getAmount();
		if(amount != 1)
			return;
		
		WandData wandData = new WandData(currentItem);
		
		//blaze rod must be named properly
		String itemName = currentItem.getItemMeta().getDisplayName();
		if(!"Magic Wand".equalsIgnoreCase(itemName))
			return;
		
		//search for stuff
		Location playerLoc = p.getEyeLocation();
		int x = playerLoc.getBlockX();
		int y = playerLoc.getBlockY();
		int z = playerLoc.getBlockZ();
		
		int radius = wandData.getSearchDistance();
		
		BlockFace wandDirection = e.getBlockFace().getOppositeFace();
		Location l1 = playerLoc;
		Location l2 = playerLoc;
		
		Block clicked = e.getClickedBlock();
		p.sendMessage("block: " + clicked.getType() + " " + clicked.getX() + clicked.getY() + clicked.getZ());
		
		switch(wandDirection)
		{
		//east - positive x
		// startblock = get block directly east of player (at eye location)
		// 3x3xRadius cuboid that player is facing
		// corner1: startblock.x,y+1,z+1
		// corner2: startblock.x + radius, startblock.y-1, z-1
		case EAST:
			p.sendMessage("EAST");
			l1 = new Location(p.getWorld(), x+1, y+1, z+1);
			l2 = new Location(p.getWorld(), x+radius, y-1, z-1);
			break;
		//north - negative z
		//startblock = get block directly north of player (at eye location)
		//3x3xRadius cuboid that player is facing
		//corner1:  x+1, y+1, startblock.z
		//corner2:  x-1, y-1, startblock.z - radius
		case NORTH:
			p.sendMessage("NORTH");
			l1 = new Location(p.getWorld(), x+1, y+1, z-1);
			l2 = new Location(p.getWorld(), x-1, y-1, z-radius);
			break;
		//south - positive z
		//startblock = get block directly south of player (at eye location)
		//3x3xRadius cuboid that player is facing
		//corner1:  x+1, y+1, startblock.z
		//corner2:  x-1, y-1, startblock.z + radius
		case SOUTH:
			p.sendMessage("SOUTH");
			l1 = new Location(p.getWorld(), x+1, y+1, z+1);
			l2 = new Location(p.getWorld(), x-1, y-1, z+radius);
			break;
		//west - negative x
		// startblock = get block directly west of player (at eye location)
		// 3x3xRadius cuboid that player is facing
		// corner1: startblock.x, y+1, z+1
		// corner2: startblock.x - radius, startblock.y-1, z-1
		case WEST:
			p.sendMessage("WEST");
			l1 = new Location(p.getWorld(), x-1, y+1, z+1);
			l2 = new Location(p.getWorld(), x-radius, y-1, z-1);
			break;
		default:
			p.sendMessage("DEFAULT: " + wandDirection.toString());
			break;
		}
		//todo: materials to search for
		List<Material> searchMaterials = new ArrayList<Material>();
		searchMaterials.add(Material.COAL_ORE);
		searchMaterials.add(Material.DIAMOND_ORE);
		
		Cuboid c = new Cuboid(l1, l2);
		if(e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			for(Block b : c)
			b.breakNaturally();
			return;
		}
		List<Block> searchResults = c.search(searchMaterials);

		for(Block b : searchResults)
		{
			Location bLoc = b.getLocation();
			
			p.sendMessage("found block: " + b.getType() + " at x:" + (bLoc.getBlockX()) 
					+ " y:" + bLoc.getBlockY() + " z:" + bLoc.getBlockZ());
		}
	}
}
