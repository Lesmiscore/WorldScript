package com.nao20010128nao;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;

public class WorldScriptPluginLoader implements PluginLoader {

	public WorldScriptPluginLoader() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public Plugin loadPlugin(File file) throws InvalidPluginException,
			UnknownDependencyException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public PluginDescriptionFile getPluginDescription(File file)
			throws InvalidDescriptionException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Pattern[] getPluginFileFilters() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
			Listener listener, Plugin plugin) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void disablePlugin(Plugin plugin) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
