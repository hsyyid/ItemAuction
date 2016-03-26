package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.util.Auction;
import io.github.hsyyid.itemauction.util.Bid;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class AcceptBidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Player bidder = ctx.<Player> getOne("player").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;

			Optional<Auction> auction = ItemAuction.auctions.stream().filter(a -> a.getSender().getUniqueId() == player.getUniqueId()).findAny();

			if (auction.isPresent())
			{
				Optional<Bid> bid = auction.get().getBids().stream().filter(b -> b.getBidder().getUniqueId() == bidder.getUniqueId()).findAny();

				if (bid.isPresent() && player.getItemInHand().isPresent() && player.getItemInHand().get().getQuantity() == auction.get().getItemStack().getQuantity() && player.getItemInHand().get().getItem() == auction.get().getItemStack().getItem())
				{
					TransactionResult transactionResult = ItemAuction.economyService.getOrCreateAccount(bidder.getUniqueId()).get().transfer(ItemAuction.economyService.getOrCreateAccount(player.getUniqueId()).get(), ItemAuction.economyService.getDefaultCurrency(), bid.get().getPrice(), Cause.of(NamedCause.source(player)));

					if (transactionResult.getResult() == ResultType.SUCCESS)
					{
						ItemAuction.auctions.remove(auction.get());
						MessageChannel.TO_ALL.send(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, player.getName() + " auction for " + auction.get().getQuantity() + " " + auction.get().getItemStack().getItem().getTranslation().get() + " has ended."));
						bidder.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Your bid was accepted by " + player.getName() + "."));
						player.setItemInHand(null);
						bidder.getInventory().offer(auction.get().getItemStack());
						src.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Bid accepted."));
					}
					else
					{
						src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Bidder does not have enough money!"));
					}
				}
				else if (bid.isPresent())
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not holding the item(s) for the auction!"));
				}
				else
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Bid not found!"));
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not auctioning anything!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}

		return CommandResult.success();
	}
}
