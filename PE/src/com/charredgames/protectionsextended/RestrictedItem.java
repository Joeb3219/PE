package com.charredgames.protectionsextended;

import org.bukkit.ChatColor;

import com.charredgames.protectionsextended.listeners.InteractionType;


public class RestrictedItem {

	private String pack = "ftb_ultimate", name = "stone";
	private int itemId = 1, itemData = 0;
	private int scanRadius = 0, eventLength = 0;
	private boolean lineOfSight = false;
	private InteractionType interactionType;
	
	/*
	 * RestrictedItem arguments in order:
	 * ModPack name, item ID, item Data, item Name
	 * Enable area scan (player)? radius?
	 * Enable line of sight scan? (Will scan target block w/ radius of area scan, disables area scan).
	 * Check on trigger of RIGHT_CLICK_BLOCK || LEFT_CLICK_BLOCK ?
	 * Check on trigger of RIGHT_CLICK_BLOCK || RIGHT_CLICK_AIR ?
	 * Check on trigger of LEFT_CLICK_BLOCK || LEFT_CLICK_AIR ?
	 * Disable the event after x seconds? Seconds?
	 */

	public RestrictedItem(String pack, int itemId, int itemData, String name, int scanRadius, boolean lineOfSight, InteractionType interactionType, int eventLength){
		this.pack = pack;
		this.name = name;
		this.itemId = itemId;
		this.itemData = itemData;
		this.scanRadius = scanRadius;
		this.eventLength = eventLength;
		this.lineOfSight = lineOfSight;
		this.interactionType = interactionType;
	}
	
	public String getModPack(){
		return pack;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNameForPrint(){
		return ChatColor.RED + name + ChatColor.WHITE;
	}
	
	public int getId(){
		return itemId;
	}
	
	public int getData(){
		return itemData;
	}
	
	public String getIdForPrint(){
		String str = "";
		str += itemId;
		if(itemData != 0) str += ":" + itemData;
		return ChatColor.GOLD + str + ChatColor.WHITE;
	}
	
	public boolean isScanNeeded(){
		if(scanRadius > 0) return true;
		return false;
	}
	
	public boolean isLineOfSightScan(){
		return lineOfSight;
	}
	
	public int getScanRadius(){
		return scanRadius;
	}
	
	public boolean isEventShortened(){
		if(eventLength > 0) return true;
		return false;
	}
	
	public int getEventLength(){
		return eventLength;
	}
	
	public InteractionType getInteractionType(){
		return interactionType;
	}
	
}
