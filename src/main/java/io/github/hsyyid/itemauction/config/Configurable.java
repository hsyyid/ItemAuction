package io.github.hsyyid.itemauction.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * Template used for all config files
 */
public interface Configurable
{
	void setup();

	void load();

	void save();

	void populate();

	CommentedConfigurationNode get();
}
