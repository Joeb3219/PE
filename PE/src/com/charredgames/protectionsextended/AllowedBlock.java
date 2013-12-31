package com.charredgames.protectionsextended;

public class AllowedBlock {

	private String modPack;
	private int itemId;
	private int itemData;
	
	public AllowedBlock(String modPack, int itemId, int itemData){
		this.modPack = modPack;
		this.itemId = itemId;
		this.itemData = itemData;
	}
	
	public String getModPack(){
		return modPack;
	}
	
	public int getItemId(){
		return itemId;
	}
	
	public int getItemData(){
		return itemData;
	}
	
	public String getFormattedItemId(){
		return itemId + ":" + itemData;
	}
	
}
