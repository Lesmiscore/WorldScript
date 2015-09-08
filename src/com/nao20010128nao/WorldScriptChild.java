package com.nao20010128nao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.avaje.ebean.EbeanServer;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class WorldScriptChild extends PluginBase {
	File dir, yml;
	Set<File> scripts;
	WorldScriptPluginLoader loader;
	Scriptable objective;
	Context jsPlayer;
	PluginDescriptionFile desc;
	Server server;
	WorldScript parent;
	EbeanServer ebean = null;
	FileConfiguration newConfig = null;
	File configFile = null;
	boolean enabled = false;
	private Scriptable thisObj;

	public WorldScriptChild(File dir, File yml, Set<File> scripts,
			WorldScriptPluginLoader loader, PluginDescriptionFile desc,
			Server server) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.dir = dir;
		this.yml = yml;
		this.scripts = Collections.unmodifiableSet(scripts);
		this.loader = loader;
		this.desc = desc;
		this.server = server;
		parent = (WorldScript) server.getPluginManager().getPlugin(
				"WorldScript");
		jsPlayer = Context.getCurrentContext();
		objective = jsPlayer.newObject(jsPlayer.initStandardObjects());
		// getServer().getLogger().info("");
	}

	@Override
	public File getDataFolder() {
		// TODO 自動生成されたメソッド・スタブ
		return new File(parent.getDataFolder(), "../" + getName())
				.getAbsoluteFile();
	}

	@Override
	public PluginDescriptionFile getDescription() {
		// TODO 自動生成されたメソッド・スタブ
		return desc;
	}

	@Override
	public FileConfiguration getConfig() {
		// TODO 自動生成されたメソッド・スタブ
		return newConfig;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void reloadConfig() {
		newConfig = YamlConfiguration.loadConfiguration(configFile);

		final InputStream defConfigStream = getResource("config.yml");
		if (defConfigStream == null) {
			return;
		}

		final YamlConfiguration defConfig;
		if (isStrictlyUTF8() || FileConfiguration.UTF8_OVERRIDE) {
			defConfig = YamlConfiguration
					.loadConfiguration(new InputStreamReader(defConfigStream,
							Charsets.UTF_8));
		} else {
			final byte[] contents;
			defConfig = new YamlConfiguration();
			try {
				contents = ByteStreams.toByteArray(defConfigStream);
			} catch (final IOException e) {
				getLogger().log(Level.SEVERE,
						"Unexpected failure reading config.yml", e);
				return;
			}

			final String text = new String(contents, Charset.defaultCharset());
			if (!text.equals(new String(contents, Charsets.UTF_8))) {
				getLogger()
						.warning(
								"Default system encoding may have misread config.yml from plugin jar");
			}

			try {
				defConfig.loadFromString(text);
			} catch (final InvalidConfigurationException e) {
				getLogger().log(Level.SEVERE,
						"Cannot load configuration from jar", e);
			}
		}

		newConfig.setDefaults(defConfig);
	}

	private boolean isStrictlyUTF8() {
		return getDescription().getAwareness().contains(
				PluginAwareness.Flags.UTF8);
	}

	@Override
	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {

		}
	}

	@Override
	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}
	}

	@Override
	public void saveResource(String resourcePath, boolean replace) {
		if (resourcePath == null || resourcePath.equals("")) {
			throw new IllegalArgumentException(
					"ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null) {
			throw new IllegalArgumentException("The resource '" + resourcePath
					+ "' cannot be found in " + dir);
		}

		File outFile = new File(getDataFolder(), resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(getDataFolder(), resourcePath.substring(0,
				lastIndex >= 0 ? lastIndex : 0));

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if (!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} else {

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public InputStream getResource(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}
		File insideAssets = new File(dir, "assets/" + filename);
		File insideResources = new File(dir, "assets/" + filename);
		File raw = new File(dir, filename);
		try {
			if (insideAssets.exists() || insideAssets.isFile()) {
				return new FileInputStream(insideAssets);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			if (insideResources.exists() || insideResources.isFile()) {
				return new FileInputStream(insideResources);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			if (raw.exists() || raw.isFile()) {
				return new FileInputStream(raw);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PluginLoader getPluginLoader() {
		// TODO 自動生成されたメソッド・スタブ
		return loader;
	}

	@Override
	public Server getServer() {
		// TODO 自動生成されたメソッド・スタブ
		return server;
	}

	@Override
	public boolean isEnabled() {
		// TODO 自動生成されたメソッド・スタブ
		return enabled;
	}

	@Override
	public void onDisable() {
		// TODO 自動生成されたメソッド・スタブ
		jsPlayer.evaluateString(objective, "onDisable()", getName(), 0, this);
	}

	@Override
	public void onLoad() {
		// TODO 自動生成されたメソッド・スタブ
		jsPlayer.evaluateString(objective, "onLoad()", getName(), 0, this);
	}

	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ
		jsPlayer.evaluateString(objective, "onEnable()", getName(), 0, this);
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
		return parent.getDatabase();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		// TODO 自動生成されたメソッド・スタブ
		return parent.getDefaultWorldGenerator(worldName, id);
	}

	@Override
	public Logger getLogger() {
		// TODO 自動生成されたメソッド・スタブ
		return parent.getLogger();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		return parent.onTabComplete(sender, command, alias, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		try {
			return (boolean) ((Function) jsPlayer.compileString("onCommand",
					"", 0, this)).call(jsPlayer, objective, objective,
					new Object[] { sender, command, label, args });
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
	}

}
