package com.matrixugly.magicwand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable(){
	    //enabled
	    this.getServer().getPluginManager().registerEvents(new WandEventListener(), this);
	    
	    //debug - unit tests
	    WandData.doTests();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	 
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("wandhelp"))
		{
			sender.sendMessage(this.getCommand("wandxp").getUsage());
			sender.sendMessage(this.getCommand("wandlevelup").getUsage());
		}
		
		Player p = ((Player)sender);
		PlayerInventory inv = p.getInventory();
		ItemStack currentItem = inv.getItemInMainHand();
		
		if(command.getName().equalsIgnoreCase("wandxp")){

			try {
				WandData wandData = new WandData(currentItem);
				int totalXp = wandData.getXp();
				sender.sendMessage("total xp: " + totalXp);
				int availableLevels = wandData.getAvailableLevels();
				if(availableLevels > 0)
					sender.sendMessage("You have " + availableLevels + " available level(s) to spend!");
				return true;
			} catch (Exception e) {
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		else if(command.getName().equalsIgnoreCase("wandlevelup"))
		{
			if(args.length != 1)
			{
				sender.sendMessage(command.getUsage());
				return true;
			}
			try {
				WandData wandData = new WandData(currentItem);
				WandLevelAttribute levelAttribute = WandLevelAttribute.valueOf(args[0]);
				wandData.levelup(levelAttribute);
			}catch(Exception e) {
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
			
		}
		//todo command xp show progress of xp
		//need to verify xp is getting stored in wand
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		 ArrayList<String> result = new ArrayList<String>();
		 
		 if(command.getName().equalsIgnoreCase("wandlevelup"))
		 {
			 return Stream.of(WandLevelAttribute.values())
					 .map(Enum::name)
					 .collect(Collectors.toList());
		 }
		 
		 return result;
	}

}

