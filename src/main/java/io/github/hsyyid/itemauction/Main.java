package io.github.hsyyid.itemauction;

import io.github.hsyyid.itemauction.cmdexecutors.AcceptBidExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.AuctionExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.BidExecutor;
import io.github.hsyyid.itemauction.events.AuctionEvent;
import io.github.hsyyid.itemauction.events.BidEvent;
import io.github.hsyyid.itemauction.utils.Auction;
import io.github.hsyyid.itemauction.utils.Bid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.TeleportHelper;

import com.google.inject.Inject;

@Plugin(id = "ItemAuction", name = "ItemAuction", version = "0.2", dependencies = "required-after:TotalEconomy")
public class Main
{
	public static Game game = null;
	public static ConfigurationNode config = null;
	public static ConfigurationLoader<CommentedConfigurationNode> configurationManager;
	public static TeleportHelper helper;
	public static ArrayList<Auction> auctions = new ArrayList<Auction>();

	@Inject
	private Logger logger;

	public Logger getLogger()
	{
		return logger;
	}

	@Inject
	@DefaultConfig(sharedRoot = true)
	private File dConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> confManager;

	@Subscribe
	public void onServerStart(ServerStartedEvent event)
	{
		getLogger().info("ItemAuction loading...");
		game = event.getGame();
		helper = game.getTeleportHelper();
		// Config File
		try
		{
			if (!dConfig.exists())
			{
				dConfig.createNewFile();
				config = confManager.load();
				confManager.save(config);
			}
			configurationManager = confManager;
			config = confManager.load();

		}
		catch (IOException exception)
		{
			getLogger().error("The default configuration could not be loaded or created!");
		}

		CommandSpec auctionCommandSpec = CommandSpec.builder()
			.description(Texts.of("Auction Command"))
			.permission("auction.use")
			.arguments(GenericArguments.onlyOne(GenericArguments.integer(Texts.of("price"))))
			.executor(new AuctionExecutor())
			.build();

		game.getCommandDispatcher().register(this, auctionCommandSpec, "auction");
		
		CommandSpec acceptBidCommandSpec = CommandSpec.builder()
			.description(Texts.of("Accept Bid Command"))
			.permission("bid.accept")
			.arguments(GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)))
			.executor(new AcceptBidExecutor())
			.build();

		game.getCommandDispatcher().register(this, acceptBidCommandSpec, "acceptbid");

		CommandSpec bidCommandSpec = CommandSpec.builder()
			.description(Texts.of("Bid Command"))
			.permission("bid.use")
			.arguments(GenericArguments.seq(
						GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game))),
						GenericArguments.onlyOne(GenericArguments.integer(Texts.of("price"))))
						.executor(new BidExecutor())
						.build();

			game.getCommandDispatcher().register(this, bidCommandSpec, "bid");

			getLogger().info("-----------------------------");
			getLogger().info("ItemAuction was made by HassanS6000!");
			getLogger().info("Have fun, and enjoy! :D");
			getLogger().info("-----------------------------");
			getLogger().info("ItemAuction loaded!");
	}

	@Subscribe
	public void auctionEventHandler(AuctionEvent event)
	{
		Auction auction = new Auction(event.getSender(), event.getPrice(), event.getItemStack().getQuantity(), event.getItemStack());
		auctions.add(auction);
	}
	
	@Subscribe
	public void bidEventHandler(BidEvent event)
	{
		Auction auction = event.getAuction();
		Player bidder = event.getBidder();
		int price = event.getPrice();
		
		Bid bid = new Bid(bidder, price, auction);
		auctions.remove(auction);
		auction.addBid(bid);
		auctions.add(auction);
		
		auction.getSender().sendMessage(Texts.of(TextColors.GREEN, "[ItemAuction] ", TextColors.YELLOW, bidder.getName() + " has bid " + price + " dollars for your " + auction.getQuantity() + " " + auction.getItemStack().getItem().getName()));
		auction.getSender().sendMessage(Texts.of(TextColors.GREEN, "[ItemAuction] ", TextColors.YELLOW, "Do /acceptbid " + bidder.getName() + " to accept this bid."));
	}
	
	public static ConfigurationLoader<CommentedConfigurationNode> getConfigManager()
	{
		return configurationManager;
	}
}
