package com.hbm.config;

import java.util.HashMap;
import java.util.HashSet;

import com.hbm.main.MainRegistry;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonConfig {

	public static boolean createConfigBool(Configuration config, String category, String name, String comment, boolean def) {
		Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getBoolean();
	}

	public static String createConfigString(Configuration config, String category, String name, String comment, String def) {
		Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getString();
	}

	public static String[] createConfigStringList(Configuration config, String category, String name, String comment, String[] defaultValues) {
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		return prop.getStringList();
	}

	public static HashSet<String> createConfigStringSet(Configuration config, String category, String name, String comment, String[] defaultValues) {
		HashSet<String> configSet = new HashSet<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			configSet.add(entry);
		}
		return configSet;
	}

		public static HashSet<Integer> createConfigIntSet(Configuration config, String category, String name, String comment, String[] defaultValues) {
		HashSet<Integer> configSet = new HashSet<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			try {
				configSet.add(Integer.parseInt(entry));
			} catch (NumberFormatException ignored) {
			}
		}
		return configSet;
	}

	public static HashMap<String, String> createConfigStringMap(Configuration config, String category, String name, String comment, String[] defaultValues, String splitReg) {
		HashMap<String, String> configDictionary = new HashMap<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			String[] pairs = entry.split(splitReg, 0);
			if (pairs.length >= 2) {
				configDictionary.put(pairs[0], pairs[1]);
			}
		}
		return configDictionary;
	}

		public static HashMap<Integer, Float> createConfigIntFloatMap(Configuration config, String category, String name, String comment, String[] defaultValues, String splitReg) {
		HashMap<Integer, Float> configDictionary = new HashMap<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			String[] pairs = entry.split(splitReg, 0);
			if (pairs.length >= 2) {
				try {
					configDictionary.put(Integer.parseInt(pairs[0]), Float.parseFloat(pairs[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}
		return configDictionary;
	}

		public static HashMap<Integer, Integer> createConfigIntMap(Configuration config, String category, String name, String comment, String[] defaultValues, String splitReg) {
		HashMap<Integer, Integer> configDictionary = new HashMap<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			String[] pairs = entry.split(splitReg, 0);
			if (pairs.length >= 2) {
				try {
					configDictionary.put(Integer.parseInt(pairs[0]), Integer.parseInt(pairs[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}
		return configDictionary;
	}

	public static HashMap<String, Integer> createConfigStringIntMap(Configuration config, String category, String name, String comment, String[] defaultValues, String splitReg) {
		HashMap<String, Integer> configDictionary = new HashMap<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			String[] pairs = entry.split(splitReg, 0);
			if (pairs.length >= 2) {
				try {
					configDictionary.put(pairs[0], Integer.parseInt(pairs[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}
		return configDictionary;
	}

	public static HashMap<String, Float> createConfigStringFloatMap(Configuration config, String category, String name, String comment, String[] defaultValues, String splitReg) {
		HashMap<String, Float> configDictionary = new HashMap<>();
		Property prop = config.get(category, name, defaultValues);
		prop.setComment(comment);
		for (String entry : prop.getStringList()) {
			String[] pairs = entry.split(splitReg, 0);
			if (pairs.length >= 2) {
				try {
					configDictionary.put(pairs[0], Float.parseFloat(pairs[1]));
				} catch (NumberFormatException ignored) {
				}
			}
		}
		return configDictionary;
	}

	public static int createConfigInt(Configuration config, String category, String name, String comment, int def) {
		Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getInt();
	}

	public static double createConfigDouble(Configuration config, String category, String name, String comment, double def) {
		Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getDouble();
	}

	public static int setDefZero(int value, int def) {
		if (value < 0) {
			MainRegistry.logger.error("Fatal error config: Randomizer value has been below zero, despite bound having to be positive integer!");
			MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW CONFIGURATION DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
			return def;
		}
		return value;
	}

	public static int setDef(int value, int def) {
		if (value <= 0) {
			MainRegistry.logger.error("Fatal error config: Randomizer value has been set to zero, despite bound having to be positive integer!");
			MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW CONFIGURATION DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
			return def;
		}
		return value;
	}
}
