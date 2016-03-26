package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.events.BidEvent;
import io.github.hsyyid.itemauction.util.Auction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class BidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Player auctioneer = ctx.<Player> getOne("player").get();
		double price = ctx.<Double> getOne("price").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;
			Optional<Auction> auction = ItemAuction.auctions.stream().filter(a -> a.getSender().getUniqueId() == auctioneer.getUniqueId()).findAny();

			if (price < 0)
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot bid a negative number!"));
				return CommandResult.success();
			}

			if (auction.isPresent() && auctioneer.getUniqueId() != player.getUniqueId())
			{
				boolean hasEnoughMoney = ItemAuction.economyService.getOrCreateAccount(player.getUniqueId()).get().getBalance(ItemAuction.economyService.getDefaultCurrency()).compareTo(BigDecimal.valueOf(price)) >= 0;

				if (hasEnoughMoney)
				{
					Sponge.getEventManager().post(new BidEvent(player, price, auction.get()));
					src.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Bid sent."));
				}
				else
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You do not have enough money to create a bid for that sum of money!"));
				}

			}
			else if (!auction.isPresent())
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "No auction found!"));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot bid on your own auction!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /bid!"));
		}

		return CommandResult.success();
	}
}
