package com.matrixugly.magicwand;

import java.util.ArrayList;
import java.util.List;

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
		if(command.getName().equalsIgnoreCase("wandxp")){
			Player p = ((Player)sender);
			PlayerInventory inv = p.getInventory();
			ItemStack currentItem = inv.getItemInMainHand();

			try {
				WandData wandData = new WandData(currentItem);
				int totalXp = wandData.getXp();
				sender.sendMessage("total xp: " + totalXp);
				return true;
			} catch (Exception e) {
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
		return new ArrayList<String>();
	}

}

