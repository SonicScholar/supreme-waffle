package com.matrixugly.magicwand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboid implements Iterable<Block>{

	Location min;
	Location max;
	
	public Cuboid(Location l1, Location l2)
	{
		World w = l1.getWorld();
		
		//derive min and max locations
		double minX = Math.min(l1.getBlockX(), l2.getBlockX());
		double minY = Math.min(l1.getBlockY(), l2.getBlockY());
		double minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
		
		double maxX = Math.max(l1.getBlockX(), l2.getBlockX());;
		double maxY = Math.max(l1.getBlockY(), l2.getBlockY());;
		double maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());;
		
		min = new Location(w, minX, minY, minZ);
		max = new Location(w, maxX, maxY, maxZ);
		
		Bukkit.getConsoleSender()
		.sendMessage("min x: " + min.getBlockX() + 
				" y: " + min.getBlockY() + 
				" z: " + min.getBlockZ() +
				" type: " + w.getBlockAt(min).getType());
		
		
		Bukkit.getConsoleSender()
		.sendMessage("max x: " + max.getBlockX() + 
				" y: " + max.getBlockY() + 
				" z: " + max.getBlockZ() +
				" type: " + max.getBlock().getType());
		
	}
	
	public Location getMin()
	{
		return min;
	}
	
	public Location getMax()
	{
		return max;
	}
	
	
	//search
	public List<Block> search(List<Material> materials)
	{
		ArrayList<Block> foundBlocks = new ArrayList<Block>();
		if(materials == null)
			return foundBlocks;
		if(materials.size() == 0)
			return foundBlocks;
		
//		for(int x = min.getBlockX(); x <= max.getBlockX(); x++)
//		{
//			for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++)
//			{
//				for(int y = min.getBlockY(); y <= max.getBlockY(); y++)
//				{
//					Block b = w.getBlockAt(x,y,z);
		for(Block b : this)
		{
					Material m = b.getType();
					Bukkit.getConsoleSender()
					.sendMessage("search x: " + b.getX() + 
							" y: " + b.getY() + 
							" z: " + b.getZ() +
							" type: " + m);
					materials.stream()
					.filter(it -> it == m)
					.findAny().ifPresent(it -> foundBlocks.add(b));
					
//					boolean tru = true;
//					if(tru)return foundBlocks;
//				}
//			}
//		}
		}
		
		return foundBlocks;
		
	}

	@Override
	public Iterator<Block> iterator() {
		
		return new CuboidIterator(this);
	}
	
	
	//iterate
	
}
