package io.github.hsyyid.itemauction.util;

import io.github.hsyyid.itemauction.config.Config;
import io.github.hsyyid.itemauction.config.Configs;
import io.github.hsyyid.itemauction.config.Configurable;

public class Utils
{
	private static Configurable mainConfig = Config.getConfig();

	public static boolean shouldRegisterAsSubcommands()
	{
		return Configs.getConfig(mainConfig).getNode("commands", "subcommands").getBoolean();
	}
}
