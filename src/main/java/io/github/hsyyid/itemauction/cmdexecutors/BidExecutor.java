package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.events.BidEvent;
import io.github.hsyyid.itemauction.utils.Auction;
import io.github.hsyyid.itemauction.utils.Bid;
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

public class BidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Player auctioner = ctx.<Player> getOne("player").get();
		int price = ctx.<Integer> getOne("price").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;
			Auction bidOnAuction = null;

			if (price < 0)
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot bid a negative number!"));
				return CommandResult.success();
			}

			for (Auction auction : ItemAuction.auctions)
			{
				boolean alreadyBid = false;
				for (Bid bid : auction.getBids())
				{
					if (bid.getBidder() == player)
					{
						alreadyBid = true;
						break;
					}
				}
				boolean isPlayerBidingOnOwnAuction = (auctioner == player);
				if (!alreadyBid && auction.getSender() == auctioner && !isPlayerBidingOnOwnAuction)
				{
					bidOnAuction = auction;
					break;
				}
				else if (alreadyBid)
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot bid multiple times!"));
					return CommandResult.success();
				}
				else if (isPlayerBidingOnOwnAuction)
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot bid on your own auction!"));
					return CommandResult.success();
				}
			}

			boolean hasEnoughMoney = ItemAuction.economyService.getOrCreateAccount(player.getUniqueId()).get().getBalance(ItemAuction.economyService.getDefaultCurrency()).compareTo(BigDecimal.valueOf(price)) >= 0;

			if (hasEnoughMoney && bidOnAuction != null)
			{
				Sponge.getEventManager().post(new BidEvent(player, price, bidOnAuction));
				src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.WHITE, "Bid sent."));
			}
			else if (!hasEnoughMoney)
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You do not have enough money to create a bid for that sum of money!"));
				return CommandResult.success();
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Auction not found!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /bid!"));
		}

		return CommandResult.success();
	}
}
