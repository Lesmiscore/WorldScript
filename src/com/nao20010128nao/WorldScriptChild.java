package com.nao20010128nao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
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

public class WorldScriptChild extends PluginBase implements Listener {
	static String declareScript = "var Server,Plugin;";
	static String onCommandScript = "function(s,c,l,a){if(onCommand){onCommand(s,c,l,a);}}";
	static String setupScript;
	static {
		String ss = "";
		// arguments: a is Server, b is WorldScriptChild (this)
		ss += "function(a,b){";
		ss += "    Server={";
		ss += "        getServer:function(){";
		ss += "            return a;";
		ss += "        }";
		ss += "    };";
		ss += "";
		ss += "    Plugin={";
		ss += "        getMain:function(){";
		ss += "            return b;";
		ss += "        },";
		ss += "        registerEvent:function(func,event){";
		ss += "            Server.getServer().getPluginManager().registerEvent(Plugin.__convertClass(event),Plugin.getMain(),org.bukkit.event.EventPriority.NORMAL,Plugin__makeExecutor(func),Plugin.getMain(),true);";
		ss += "        },";
		ss += "        __convertClass:function(cl){";
		ss += "            var strc=java.lang.Class.forName(\"java.lang.CharSequence\");";
		ss += "            var clsc=java.lang.Class.forName(\"java.lang.Class\");";
		ss += "            if(strc.isInstance(cl)){";
		ss += "                return java.lang.Class.forName(c.toString());";
		ss += "            }";
		ss += "            if(clsc.isInstance(cl)){";
		ss += "                return c;";
		ss += "            }";
		ss += "            throw new Error(\"Incorrect object\");";
		ss += "        },";
		ss += "        __makeExecutor:function(fu){";
		ss += "            return new org.bukkit.event.EventExecutor({";
		ss += "                execute:function(listener,event){";
		ss += "                    fu(event);";
		ss += "                }";
		ss += "            });";
		ss += "        }";
		ss += "    };";
		ss += "}";
		/* delete all indents (all indents are from spaces, check it!) */
		ss = ss.replace("    ", "");
		setupScript = ss;
	}
	File dir;
	Set<String> scripts;
	WorldScriptPluginLoader loader;
	Scriptable objective;
	Context jsPlayer;
	PluginDescriptionFile desc;
	Server server;
	WorldScript parent;
	FileConfiguration newConfig = null;
	File configFile = null;
	boolean enabled = false;
	ZipFile zf;

	public WorldScriptChild(File pack, Set<String> scripts,
			WorldScriptPluginLoader loader, PluginDescriptionFile desc) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.server = parent.getServer();
		getServer().getLogger().info("Preparing...");
		this.dir = pack;
		this.scripts = Collections.unmodifiableSet(scripts);
		this.loader = loader;
		this.desc = desc;
		parent = WorldScript.instance.get();
		jsPlayer = Context.getCurrentContext();
		objective = jsPlayer.newObject(jsPlayer.initStandardObjects());
		jsPlayer.evaluateString(objective, declareScript, "file", 0, this);
		Function setup = jsPlayer.compileFunction(objective, setupScript,
				"file", 0, this);
		setup.call(jsPlayer, objective, objective, new Object[] { getServer(),
				this });
		zf = loader.openZipFile(pack);
		if (zf == null) {
			getServer().getLogger().info(pack + " is invalid");
			throw new RuntimeException("");
		}
		for (String script : scripts) {
			InputStream is;
			try {
				is = zf.getInputStream(zf.getEntry(script));
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
				getServer().getLogger().info(
						"An error occured while loading: " + script.toString());
				break;
			}
			getServer().getLogger().info("Evaluating: " + script.toString());
			try {
				jsPlayer.evaluateReader(objective,
						openReader(is, script.endsWith(".gz")),
						script.toString(), 0, this);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				getServer().getLogger().info(
						"An error occured while evaluating: "
								+ script.toString());
			}
		}
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
		String insideAssets = ("assets/" + filename).replace('\\', '/')
				.replace("//", "/");
		String insideResources = ("assets/" + filename).replace('\\', '/')
				.replace("//", "/");
		String raw = filename.replace('\\', '/').replace("//", "/");
		try {
			return zf.getInputStream(zf.getEntry(insideAssets));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			return zf.getInputStream(zf.getEntry(insideResources));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			return zf.getInputStream(zf.getEntry(raw));
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
			Function f = jsPlayer.compileFunction(objective, onCommandScript,
					"file", 0, objective);
			return (boolean) f.call(jsPlayer, objective, objective,
					new Object[] { sender, command, label, args });
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
	}

	private Reader openReader(File file) {
		String s = file.toString();
		Charset cs = Charset.forName(parent.config.get("default-charset"));
		if (s.endsWith(".js")) {
			try {
				return openReader(new FileInputStream(file), false);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		if (s.endsWith(".js.gz")) {
			try {
				return openReader(new FileInputStream(file), true);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return new StringReader("");
	}

	private Reader openReader(InputStream is, boolean gz) {
		Charset cs = Charset.forName(parent.config.get("default-charset"));
		if (!gz) {
			return new InputStreamReader(is, cs);
		} else {
			try {
				return new InputStreamReader(new GZIPInputStream(is), cs);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return new StringReader("");
	}
}
