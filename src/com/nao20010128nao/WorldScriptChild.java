package com.nao20010128nao;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import com.avaje.ebean.EbeanServer;

public class WorldScriptChild extends PluginBase {
	File dir, yml;
	File[] scripts;

	public WorldScriptChild(File dir, File yml, File[] scripts) {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public File getDataFolder() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public PluginDescriptionFile getDescription() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public FileConfiguration getConfig() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public InputStream getResource(String filename) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void saveConfig() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void saveDefaultConfig() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void saveResource(String resourcePath, boolean replace) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void reloadConfig() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public PluginLoader getPluginLoader() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Server getServer() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void onDisable() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onLoad() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean isNaggable() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void setNaggable(boolean canNag) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public EbeanServer getDatabase() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
