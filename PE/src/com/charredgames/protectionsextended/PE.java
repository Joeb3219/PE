package com.charredgames.protectionsextended;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.charredgames.protectionsextended.listeners.PlayerInteractionListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class PE extends JavaPlugin{

	//All of the stuff for config.
	public static final String _PLUGINNAMESTRING = "ProtectionsExtended";
	public static ArrayList<RestrictedItem> restrictedItems = new ArrayList<RestrictedItem>();
	public static ArrayList<AllowedBlock> allowedTouches = new ArrayList<AllowedBlock>();
	public static String modPack = "ftb_ultimate";
	public static String protectionsPlugin = "towny";
	public static boolean enableWorldguard = true;
	public static int debugLevel = 0;
	public static int targetBlockDistance = 150;
	
	public void onLoad(){
		restrictedItems.clear();
		allowedTouches.clear();
		super.onLoad();
	}
	
	public void onEnable(){
		 // To make the plugin reload safe, config loading must be handled in onEnable instead of onLoad.
		loadConfig();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PlayerInteractionListener(), this);
		
		if(protectionsPlugin.equalsIgnoreCase("towny")) pm.getPlugin("towny");
		if(enableWorldguard) getWorldGuard();
		
		super.onEnable();
	}
	
	public void onDisable(){
		restrictedItems.clear(); //Will allow the plugin to reload - onDisable fires before onEnable on reload.
		allowedTouches.clear(); //Will allow the plugin to reload - onDisable fires before onEnable on reload.
		super.onDisable();
	}
	
	private void loadConfig(){
		SAXBuilder builder = new SAXBuilder();
		File xmlFile;
		try {
			xmlFile = new File((this.getClass().getResource("testificate")).toURI());
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			
			//Handles loading configuration values (non-item stuff)
			List<Element> list = rootNode.getChildren("configuration");
			for (int i = 0; i < list.size(); i++) {				
				Element node = (Element) list.get(i);
				List<Element> inv = node.getChildren("item");
				for(int j = 0; j < inv.size(); j++){
					Element invNode = (Element) inv.get(j);
				}
			}
		} catch (IOException e) {e.printStackTrace();} catch (JDOMException e) {e.printStackTrace();  }} catch (URISyntaxException e) {e.printStackTrace();}
	}
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) return null;
	    return (WorldGuardPlugin) plugin;
	}
	
}