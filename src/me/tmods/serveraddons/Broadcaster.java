package me.tmods.serveraddons;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.tmods.serverutils.Methods;

public class Broadcaster extends JavaPlugin implements Listener{
	public File maincfgfile = new File("plugins/TModsServerUtils", "config.yml");
	public FileConfiguration maincfg = YamlConfiguration.loadConfiguration(maincfgfile);
	public boolean broadcastEntity = true;
	public boolean broadcastBlock = true;
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		try {
		if (broadcastBlock && maincfg.getBoolean("broadcastBlockBreak." + event.getBlock().getType().toString())) {
			Bukkit.broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + " has found " + event.getBlock().getType().toString() + ".");
			broadcastBlock = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					broadcastBlock = true;
				}
			}, maincfg.getInt("broadcasterDelay") * 20);
		}
		} catch (Exception e) {
			Methods.log(e);
		}
	}
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		try {
		if (broadcastEntity && maincfg.getBoolean("broadcastEntityDamage." + event.getEntity().getType().name())) {
			if (event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player) {
				Bukkit.broadcastMessage(((Player)((Arrow)event.getDamager()).getShooter()).getName() + " has attacked " + event.getEntity().getType().name() + ".");
			} else {
				if (event.getDamager() instanceof Player) {
					Bukkit.broadcastMessage(event.getDamager().getName() + " has attacked " + event.getEntity().getType().name() + ".");
				}
			}
			broadcastEntity = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					broadcastEntity = true;
				}
			}, maincfg.getInt("broadcasterDelay") * 20);
		}
		} catch (Exception e) {
			Methods.log(e);
		}
	}
}
