package com.nao20010128nao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.Warning.WarningState;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;

public class WorldScriptPluginLoader implements PluginLoader {
	Server server;

	public WorldScriptPluginLoader(Server server) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.server = server;
	}

	@Override
	public Plugin loadPlugin(File file) throws InvalidPluginException,
			UnknownDependencyException {
		// TODO 自動生成されたメソッド・スタブ
		Objects.requireNonNull(file);
		if (!file.exists()) {
			throw new InvalidPluginException(file.getAbsolutePath()
					+ " not found");
		}
		if (file.isFile()) {
			throw new InvalidPluginException(file.getAbsolutePath()
					+ " is a file");
		}
		File yml = new File(file, "plugin.yml");
		if (!yml.exists()) {
			throw new InvalidPluginException("plugin.yml not found");
		}
		PluginDescriptionFile desc;
		try {
			desc = getPluginDescription(file);
		} catch (InvalidDescriptionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			throw new InvalidPluginException(
					"An error occured while loading the description", e);
		}
		Set<File> scripts = new HashSet<>(1);
		Collections.addAll(scripts, file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// TODO 自動生成されたメソッド・スタブ
				return name.endsWith(".js") | name.endsWith(".js.gz");
			}
		}));
		return new WorldScriptChild(file, yml, scripts, this, desc);
	}

	@Override
	public PluginDescriptionFile getPluginDescription(File file)
			throws InvalidDescriptionException {
		// TODO 自動生成されたメソッド・スタブ
		Objects.requireNonNull(file);
		if (!file.exists()) {
			throw new InvalidDescriptionException(file.getAbsolutePath()
					+ " not found");
		}
		if (file.isFile()) {
			throw new InvalidDescriptionException(file.getAbsolutePath()
					+ " is a file");
		}
		File yml = new File(file, "plugin.yml");
		if (!yml.exists()) {
			throw new InvalidDescriptionException("plugin.yml not found");
		}
		try {
			return new PluginDescriptionFile(new FileInputStream(yml));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			return null;
		}
	}

	@Override
	public Pattern[] getPluginFileFilters() {
		// TODO 自動生成されたメソッド・スタブ
		return new Pattern[] { Pattern.compile("\\.zip$") };
	}

	@Override
	public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
			Listener listener, Plugin plugin) {
		// TODO 自動生成されたメソッド・スタブ
		Validate.notNull(plugin, "Plugin can not be null");
		Validate.notNull(listener, "Listener can not be null");

		boolean useTimings = server.getPluginManager().useTimings();
		Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			Method[] privateMethods = listener.getClass().getDeclaredMethods();
			methods = new HashSet<Method>(publicMethods.length
					+ privateMethods.length, 1.0f);
			Collections.addAll(methods, publicMethods);
			Collections.addAll(methods, privateMethods);
		} catch (NoClassDefFoundError e) {
			plugin.getLogger().severe(
					"Plugin " + plugin.getDescription().getFullName()
							+ " has failed to register events for "
							+ listener.getClass() + " because "
							+ e.getMessage() + " does not exist.");
			return ret;
		}

		for (final Method method : methods) {
			final EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null)
				continue;
			// Do not register bridge or synthetic methods to avoid event
			// duplication
			// Fixes SPIGOT-893
			if (method.isBridge() || method.isSynthetic()) {
				continue;
			}
			final Class<?> checkClass;
			if (method.getParameterTypes().length != 1
					|| !Event.class.isAssignableFrom(checkClass = method
							.getParameterTypes()[0])) {
				plugin.getLogger()
						.severe(plugin.getDescription().getFullName()
								+ " attempted to register an invalid EventHandler method signature \""
								+ method.toGenericString() + "\" in "
								+ listener.getClass());
				continue;
			}
			final Class<? extends Event> eventClass = checkClass
					.asSubclass(Event.class);
			method.setAccessible(true);
			Set<RegisteredListener> eventSet = ret.get(eventClass);
			if (eventSet == null) {
				eventSet = new HashSet<RegisteredListener>();
				ret.put(eventClass, eventSet);
			}

			for (Class<?> clazz = eventClass; Event.class
					.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
				// This loop checks for extending deprecated events
				if (clazz.getAnnotation(Deprecated.class) != null) {
					Warning warning = clazz.getAnnotation(Warning.class);
					WarningState warningState = server.getWarningState();
					if (!warningState.printFor(warning)) {
						break;
					}
					plugin.getLogger()
							.log(Level.WARNING,
									String.format(
											"\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated."
													+ " \"%s\"; please notify the authors %s.",
											plugin.getDescription()
													.getFullName(),
											clazz.getName(),
											method.toGenericString(),
											(warning != null && warning
													.reason().length() != 0) ? warning
													.reason()
													: "Server performance will be affected",
											Arrays.toString(plugin
													.getDescription()
													.getAuthors().toArray())),
									warningState == WarningState.ON ? new AuthorNagException(
											null) : null);
					break;
				}
			}

			EventExecutor executor = new EventExecutor() {
				@Override
				public void execute(Listener listener, Event event)
						throws EventException {
					try {
						if (!eventClass.isAssignableFrom(event.getClass())) {
							return;
						}
						method.invoke(listener, event);
					} catch (InvocationTargetException ex) {
						throw new EventException(ex.getCause());
					} catch (Throwable t) {
						throw new EventException(t);
					}
				}
			};
			if (useTimings) {
				eventSet.add(new TimedRegisteredListener(listener, executor, eh
						.priority(), plugin, eh.ignoreCancelled()));
			} else {
				eventSet.add(new RegisteredListener(listener, executor, eh
						.priority(), plugin, eh.ignoreCancelled()));
			}
		}
		return ret;
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
