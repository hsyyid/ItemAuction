package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.events.AuctionEvent;
import org.spongepowered.api.Sponge;
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

public class AuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		double price = ctx.<Double> getOne("price").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;
			if (price < 0)
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot create an auction with a negative price!"));
				return CommandResult.success();
			}

			if (!ItemAuction.auctions.stream().filter(a -> a.getSender().getUniqueId() == player.getUniqueId()).findAny().isPresent())
			{
				if (player.getItemInHand().isPresent())
				{
					ItemStack stack = player.getItemInHand().get();
					Sponge.getEventManager().post(new AuctionEvent(player, stack, price));
					MessageChannel.TO_ALL.send(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.RED, player.getName(), TextColors.GOLD, " is now auctioning " + stack.getQuantity() + " ", stack.getItem().getTranslation().get(), " for " + price + " dollars."));
				}
				else
				{
					src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You aren't holding anything!"));
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You are already auctioning something!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /auction!"));
		}

		return CommandResult.success();
	}
}
