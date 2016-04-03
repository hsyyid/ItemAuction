package io.github.hsyyid.itemauction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.github.hsyyid.itemauction.cmdexecutors.AcceptBidExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.AuctionExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.BidExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.CancelAuctionExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.IgnoreAuctionExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.ItemAuctionExecutor;
import io.github.hsyyid.itemauction.cmdexecutors.ListAuctionExecutor;
import io.github.hsyyid.itemauction.events.AuctionEvent;
import io.github.hsyyid.itemauction.events.BidEvent;
import io.github.hsyyid.itemauction.util.Auction;
import io.github.hsyyid.itemauction.util.Bid;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Plugin(id = "io.github.hsyyid.itemauction", name = "ItemAuction", version = "0.6.3")
public class ItemAuction
{
	public static EconomyService economyService;
	public static List<Auction> auctions = Lists.newArrayList();
	public static Set<UUID> ignorePlayers = Sets.newHashSet();

	@Inject
	private Logger logger;

	public Logger getLogger()
	{
		return logger;
	}

	@Listener
	public void init(GameInitializationEvent event)
	{
		getLogger().info("ItemAuction loading...");

		HashMap<List<String>, CommandSpec> subcommands = Maps.newHashMap();

		subcommands.put(Arrays.asList("auction"), CommandSpec.builder()
			.description(Text.of("Auction Command"))
			.permission("itemauction.command.auction")
			.arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("price"))))
			.executor(new AuctionExecutor())
			.build());
		
		subcommands.put(Arrays.asList("ignoreauction", "ignoreauctions"), CommandSpec.builder()
			.description(Text.of("Ignore Auction Command"))
			.permission("itemauction.command.ignoreauction")
			.executor(new IgnoreAuctionExecutor())
			.build());

		subcommands.put(Arrays.asList("list", "listauctions"), CommandSpec.builder()
			.description(Text.of("List Auctions Command"))
			.permission("itemauction.command.list")
			.executor(new ListAuctionExecutor())
			.build());

		subcommands.put(Arrays.asList("cancelauction", "cauc", "cancelauc"), CommandSpec.builder()
			.description(Text.of("Cancel Auction Command"))
			.permission("itemauction.command.cancelauction")
			.executor(new CancelAuctionExecutor())
			.build());

		subcommands.put(Arrays.asList("acceptbid"), CommandSpec.builder()
			.description(Text.of("Accept Bid Command"))
			.permission("itemauction.command.acceptbid")
			.executor(new AcceptBidExecutor())
			.build());

		subcommands.put(Arrays.asList("bid"), CommandSpec.builder()
			.description(Text.of("Bid Command"))
			.permission("itemauction.command.bid")
			.arguments(GenericArguments.seq(GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))), GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("price"))))
			.executor(new BidExecutor())
			.build());

		CommandSpec itemAuctionCommandSpec = CommandSpec.builder()
			.description(Text.of("ItemAuction Command"))
			.permission("itemauction.command.use")
			.executor(new ItemAuctionExecutor())
			.children(subcommands)
			.build();

		Sponge.getCommandManager().register(this, itemAuctionCommandSpec, "ia", "itemauction");

		getLogger().info("-----------------------------");
		getLogger().info("ItemAuction was made by HassanS6000!");
		getLogger().info("Have fun, and enjoy! :D");
		getLogger().info("-----------------------------");
		getLogger().info("ItemAuction loaded!");
	}

	@Listener
	public void postInit(GamePostInitializationEvent event)
	{
		Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);

		if (optionalEconomyService.isPresent())
		{
			economyService = optionalEconomyService.get();
		}
		else
		{
			getLogger().error("Error! This plugin requires an economy plugin to be installed!");
		}
	}

	@Listener
	public void onAuction(AuctionEvent event)
	{
		Auction auction = new Auction(event.getSender(), new BigDecimal(event.getPrice()), event.getItemStack().getQuantity(), event.getItemStack());
		auctions.add(auction);
	}

	@Listener
	public void onBid(BidEvent event)
	{
		Auction auction = event.getAuction();
		Player bidder = event.getBidder();
		Bid bid = new Bid(bidder, new BigDecimal(event.getPrice()), auction);
		ItemAuction.auctions.remove(auction);

		// Remove old bids
		auction.getBids().removeIf(b -> b.getBidder().getUniqueId() == bidder.getUniqueId());
		// Add new bid
		auction.addBid(bid);
		ItemAuction.auctions.add(auction);

		MutableMessageChannel messageChannel = MessageChannel.TO_ALL.asMutable();

		for (UUID uuid : ItemAuction.ignorePlayers)
		{
			if (Sponge.getServer().getPlayer(uuid).isPresent())
			{
				messageChannel.removeMember(Sponge.getServer().getPlayer(uuid).get());
			}
		}

		messageChannel.send(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, bidder.getName() + " has raised the bid to " + event.getPrice() + " " + ItemAuction.economyService.getDefaultCurrency().getPluralDisplayName().toPlain() + " for " + auction.getSender().getName() + "'s " + auction.getQuantity() + " " + auction.getItemStack().getTranslation().get()));
	}
}
