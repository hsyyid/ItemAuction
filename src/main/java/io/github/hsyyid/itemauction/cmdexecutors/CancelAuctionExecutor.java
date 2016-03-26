package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.util.Auction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CancelAuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Optional<Auction> auction = ItemAuction.auctions.stream().filter(a -> a.getSender().getUniqueId() == player.getUniqueId()).findAny();

			if (auction.isPresent())
			{
				ItemStack stack = auction.get().getItemStack();
				ItemAuction.auctions.remove(auction.get());
				MessageChannel.TO_ALL.send(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.RED, player.getName(), TextColors.GOLD, " is no longer auctioning " + stack.getQuantity() + " ", stack.getItem().getTranslation().get(), " for " + auction.get().getPrice() + " dollars."));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are not auctioning anything!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /cancelauction!"));
		}

		return CommandResult.success();
	}
}
