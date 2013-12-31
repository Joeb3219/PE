package com.charredgames.protectionsextended;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.charredgames.protectionsextended.listeners.InteractionType;
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
	private static File configFile;
	
	public void onLoad(){
		restrictedItems.clear();
		allowedTouches.clear();
		super.onLoad();
	}
	
	public void onEnable(){
		 // To make the plugin reload safe, config loading must be handled in onEnable instead of onLoad.
		configFile = new File(getDataFolder() + File.separator + "config.txt");
		if(!configFile.exists()) {
			configFile.mkdirs();
			try {
				configFile.createNewFile();
				File defaultConfig = new File(this.getClass().getResourceAsStream("config.txt").toString());
				copyFile(defaultConfig, configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//FileUtils.
		}
		
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
		xmlFile = new File(configFile.toURI());
		try {
				Document document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				
				//Handles loading configuration values (non-item stuff)
				Element configuration = rootNode.getChild("configuration");
				modPack = configuration.getChildText("modPack");
				protectionsPlugin = configuration.getChildText("protectionsPlugin");
				enableWorldguard = Boolean.parseBoolean(configuration.getChildText("enableWorldguard"));
				debugLevel = Integer.parseInt(configuration.getChildText("debugLevel"));
				targetBlockDistance = Integer.parseInt(configuration.getChildText("targetBlockDistance"));
				
				//Handles loading allowedTouches
				Element allowedToTouch = rootNode.getChild("allowedToTouch");
				List<Element> allowedTouches = allowedToTouch.getChildren("item");
				for(int i = 0; i < allowedTouches.size(); i ++){
					Element node = (Element) allowedTouches.get(i);
					int id = 0;
					int data = 0;
					String idString = node.getChildText("id");
					if(idString.contains(":")){
						String[] split = idString.split(":");
						id = Integer.parseInt(split[0]);
						data = Integer.parseInt(split[1]);
					}else{
						id = Integer.parseInt(idString);
					}
					new AllowedBlock(node.getChildText("modPack"), id, data);
				}
		
				//Handles loading restrictedItems
				Element restrictedItems = rootNode.getChild("restrictedItems");
				List<Element> items = restrictedItems.getChildren("item");
				
				for(int i = 0; i < items.size(); i ++){
					Element node = (Element) items.get(i);
					int id = 0;
					int data = 0;
					String idString = node.getChildText("id");
					if(idString.contains(":")){
						String[] split = idString.split(":");
						id = Integer.parseInt(split[0]);
						data = Integer.parseInt(split[1]);
					}else{
						id = Integer.parseInt(idString);
					}
					
					int scanRadius = Integer.parseInt(node.getChild("scan").getChildText("radius"));
					boolean lineOfSight = Boolean.parseBoolean(node.getChild("scan").getChildText("lineOfSight"));
					Element trigger = node.getChild("trigger");
					InteractionType type;
					boolean leftOnly = Boolean.parseBoolean(trigger.getAttributeValue("leftOnly"));
					boolean rightOnly = Boolean.parseBoolean(trigger.getAttributeValue("rightOnly"));
					boolean leftOrRight = Boolean.parseBoolean(trigger.getAttributeValue("leftOrRight"));
					if(leftOnly) type = InteractionType.leftClicksOnly;
					else if(rightOnly) type = InteractionType.rightClicksOnly;
					else type = InteractionType.AnyClicks;
					
					new RestrictedItem(node.getChildText("modPack"), id, data, node.getChildText("name"), scanRadius, lineOfSight,
						type, Integer.parseInt(node.getChildText("disableAfterSeconds")));
				}
				
		} catch (IOException e) {e.printStackTrace();} catch (JDOMException e) {e.printStackTrace();  }
	}
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) return null;
	    return (WorldGuardPlugin) plugin;
	}
	
	private static void copyFile(File source, File dest){
	    FileChannel inputChannel = null;
	    FileChannel outputChannel = null;
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
				inputChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        try {
				outputChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
}