package com.nao20010128nao;

import java.io.File;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.mozilla.javascript.Context;

public class WorldScript extends JavaPlugin {

	public WorldScript() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public WorldScript(JavaPluginLoader loader,
			PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onLoad() {
		// TODO 自動生成されたメソッド・スタブ
		Context.enter();
		getServer().getPluginManager().registerInterface(
				WorldScriptPluginLoader.class);
	}
}
