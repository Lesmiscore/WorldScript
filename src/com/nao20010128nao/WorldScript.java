package com.nao20010128nao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.mozilla.javascript.Context;

public class WorldScript extends JavaPlugin {
	public static WeakReference<WorldScript> instance = new WeakReference<WorldScript>(
			null);
	public Map<String, String> config = new HashMap<>(10);

	public WorldScript() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public WorldScript(JavaPluginLoader loader,
			PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
		// TODO 自動生成されたコンストラクター・スタブ
		instance = new WeakReference<WorldScript>(this);

	}

	@Override
	public void onLoad() {
		// TODO 自動生成されたメソッド・スタブ
		Context.enter();
		getServer().getPluginManager().registerInterface(
				WorldScriptPluginLoader.class);
	}

	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ
		loadSettings();
		if (config.size() == 0) {
			config.put("default-charset", "UTF-8");
		}
	}

	@Override
	public void onDisable() {
		// TODO 自動生成されたメソッド・スタブ
		saveSettings();
	}

	private void loadSettings() {
		File dir = new File(getDataFolder(), "config.txt");
		if (!dir.exists()) {
			return;
		}
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(dir));
			String s;
			while (null != (s = read.readLine())) {
				String[] data = s.split("\\:");
				if (data.length != 2) {
					continue;
				}
				config.put(data[0], data[1]);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private void saveSettings() {
		File dir = new File(getDataFolder(), "config.txt");
		Writer write = null;
		try {
			write = new FileWriter(dir);
			for (Map.Entry<String, String> d : config.entrySet()) {
				write.write(d.getKey() + ":" + d.getValue() + "\n");
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				write.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
