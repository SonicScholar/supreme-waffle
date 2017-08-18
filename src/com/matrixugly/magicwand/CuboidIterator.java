package com.matrixugly.magicwand;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.block.Block;

public class CuboidIterator implements Iterator<Block> {

	World world;
	
	int currX;
	int currY;
	int currZ;
	
	int minX;
	int minY;
	int minZ;
	
	int maxX;
	int maxY;
	int maxZ;
	
	public CuboidIterator(Cuboid c)
	{
		if(c == null)
			throw new IllegalArgumentException("cuboid must be non-null");
		
		world = c.min.getWorld();
		
		minX = c.min.getBlockX();
		minY = c.min.getBlockY();
		minZ = c.min.getBlockZ();
		
		currX = minX;
		currY = minY;
		currZ = minZ;
		
		maxX = c.max.getBlockX();
		maxY = c.max.getBlockY();
		maxZ = c.max.getBlockZ();
		
		
	}
	
	@Override
	public boolean hasNext() {
		if(currZ > maxZ) //this is the only component out of x,y,z that can be over its max
			return false;
		else
			return true;
	}

	@Override
	public Block next() {
		
		//get the block
		Block result = world.getBlockAt(currX, currY, currZ);
		
		//increment the current values
		
		//if x is not maxed, ++x then done
		//else set x back to xmin
		if(currX < maxX) {
			currX++;
			return result;
		}
		currX = minX;
		
		//if y is not maxed, ++y then done
		//else set y back to ymin
		if(currY < maxY) {
			currY++;
			return result;
		}
		currY = minY;
		
		//always ++z.
		//when z is greater than maxZ, we've hit the last block
		currZ++;
		return result;
	}

}
