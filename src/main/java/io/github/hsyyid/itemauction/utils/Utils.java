package io.github.hsyyid.itemauction.utils;

import java.io.IOException;

import io.github.hsyyid.itemauction.Main;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Utils
{
	public static boolean isPlayerInConfig(String UUID)
	{
		ConfigurationNode valueNode = Main.config.getNode((Object[]) ("players." + UUID + ".money").split("\\."));
		Object inConfig = valueNode.getValue();
		if (inConfig != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static void addPlayerToConfig(String UUID)
	{
		Utils.setBalance(UUID, 1000);
	}
	
	public static double getBalance(String UUID)
	{
		ConfigurationNode valueNode = Main.config.getNode((Object[]) ("players." + UUID + ".money").split("\\."));
		return valueNode.getDouble();
	}
	
	public static void setBalance(String UUID, double newBalance)
	{
		ConfigurationLoader<CommentedConfigurationNode> configManager = Main.getConfigManager();
		Main.config.getNode("players", UUID, "money").setValue(newBalance);
		try
		{
			configManager.save(Main.config);
			configManager.load();
		}
		catch (IOException e)
		{
			System.out.println("[ItemAuction]: Failed to set new balance!");
		}
	}
}
