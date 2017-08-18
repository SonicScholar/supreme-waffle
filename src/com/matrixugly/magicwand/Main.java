package com.matrixugly.magicwand;

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
	 

}

