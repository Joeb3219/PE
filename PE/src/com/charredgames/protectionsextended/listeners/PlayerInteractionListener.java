package com.charredgames.protectionsextended.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.charredgames.protectionsextended.AllowedBlock;
import com.charredgames.protectionsextended.PE;
import com.charredgames.protectionsextended.RestrictedItem;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.bukkit.WGBukkit;

public class PlayerInteractionListener implements Listener{

	private static boolean violation = false;
	private static boolean blockClicked = false;
	Plugin ProtectionsExtended = Bukkit.getPluginManager().getPlugin("ProtectionsExtended");
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		blockClicked = false;
		violation = false;
		Player player = e.getPlayer();
		Material itemInHand = player.getItemInHand().getType();
		InteractionType type = InteractionType.AnyClicks;
		Block block = null;
		Location blockLoc;
		if(player.isOp()) return;
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			blockClicked = true;
			type = InteractionType.AnyClicks;
			block = e.getClickedBlock();
		}
		else if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			type = InteractionType.rightClicksOnly;
			block = player.getTargetBlock(null, PE.targetBlockDistance);
		}
		else if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){
			if(PE.debugLevel >= 1) System.out.println(PE._PLUGINNAMESTRING + ": Well, that was an interesting player interaction: LEFT CLICKED.");
		}
		
		blockLoc = block.getLocation();
		ItemStack realMat = new ItemStack(itemInHand);
		int realId = realMat.getTypeId();
		int realData = realMat.getDurability();
		
		
		
		
		for(RestrictedItem rItem : PE.restrictedItems){
			if(rItem.getModPack().equalsIgnoreCase(PE.modPack)){
				if(realId == rItem.getId() && ( realData == rItem.getData() || rItem.getData() == 99 || ((realData == -1 || realData == 0) && (rItem.getData() == 0 || rItem.getData() == -1)) )){
					if(blockClicked && rItem.getInteractionType() == InteractionType.AnyClicks){
						if(!canBuild(player,block.getLocation())){violation=true;}
					}
					else if(type == InteractionType.rightClicksOnly && rItem.getInteractionType() == type){
						if(rItem.isLineOfSightScan()){
							if(block.getType()!=Material.AIR){
								if(!canBuildScan(player, blockLoc, rItem.getScanRadius())) violation=true;
							} else violation=true;
						}
						else if(rItem.isScanNeeded()){
							if(!canBuildScan(player, player.getLocation(), rItem.getScanRadius())) violation=true;
						}
						else{
							if(!canBuild(player, player.getLocation())){violation = true;}
						}
						if(rItem.isEventShortened()){slotChange(player, realId, rItem.getEventLength());}
					}else{
						
					}
				}
			}
		}
		
		//Violation ticker
		if(violation || ( blockClicked && denyTouch(block) && !canBuild(player,blockLoc) ) ) e.setCancelled(true);
		
	}
	
	private boolean denyTouch(Block realBlock){
				
		for(AllowedBlock aBlock : PE.allowedTouches){
			if(!aBlock.getModPack().equalsIgnoreCase(PE.modPack)) continue;
			int blockId = realBlock.getTypeId();
			int blockData = realBlock.getState().getData().getData();
			if(aBlock.getItemId() == blockId && (blockData == aBlock.getItemData())) return false;
		}
				
		return true;
	}
	
	private boolean canBuild(Player realPlayer, Location loc){
		if(PE.enableWorldguard){
			if(!realPlayer.isOp() && !WGBukkit.getPlugin().canBuild(realPlayer, loc)) return true;
		}
		if(PE.protectionsPlugin.equalsIgnoreCase("towny")){
			String blockTown = TownyUniverse.getTownName(loc);
			Resident player;
			String playerTown=null;
				try {
					player = TownyUniverse.getDataSource().getResident(realPlayer.getName());
					playerTown = player.getTown().getName();
				} catch (NotRegisteredException e) {}
				if(blockTown != playerTown && blockTown != null && !realPlayer.isOp()) return true;
		}
		return false;
	}
	
	private boolean canBuildScan(Player realPlayer, Location loc, int radius){
		int playerX = loc.getBlockX();
		int playerY = loc.getBlockY();
		int playerZ = loc.getBlockZ();
		int playerXNegative = playerX - radius;
		int playerYNegative = playerY - radius;
		int playerZNegative = playerZ - radius;
		
		for (int scanX = playerXNegative; scanX <= (playerX + radius); scanX++) {
			for (int scanY = playerYNegative; scanY <= (playerY + radius); scanY++) {
				for (int scanZ = playerZNegative; scanZ <= (playerZ + radius); scanZ++) {
						
					Location scanLoc = new Location(realPlayer.getWorld(), scanX, scanY, scanZ);
					if(!canBuild(realPlayer,scanLoc)){return true;}
					
				}
			}
		}
		return false;
	}
	
	private void slotChange(final Player player, final int itemId, int seconds){
		Bukkit.getServer().getScheduler().runTaskLater(ProtectionsExtended, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				if(player.getItemInHand().getTypeId()==itemId){
					int currentSlot = player.getInventory().getHeldItemSlot();
						ItemStack currentItem = player.getInventory().getItem(currentSlot);
						player.getInventory().removeItem(currentItem);
						player.updateInventory();
						player.getInventory().setItem(currentSlot,currentItem);
						player.updateInventory();
				}
			}
		}, ((long)seconds * 20));
}

}
