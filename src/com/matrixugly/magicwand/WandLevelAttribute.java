package com.matrixugly.magicwand;

public enum WandLevelAttribute {
	
	SEARCH_DISTANCE(0),
	SEARCH_DETECTION(1),
	MINING_DISTANCE(2),
	COOLDOWN(3);
	
	private final int value;
	private WandLevelAttribute(int value)
	{
		this.value = value;
	}
//	public static int SEARCH_DISTANCE_DIGIT = 0;
//	public static int SEARCH_DETECTION_DIGIT = 1;
//	public static int MINING_DIGIT = 2;
//	public static int COOLDOWN_DIGIT = 3;
//	
	public int getDigit()
	{
		return value;
	}
}