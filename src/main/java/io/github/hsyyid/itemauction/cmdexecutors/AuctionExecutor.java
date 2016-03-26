package io.github.hsyyid.itemauction.cmdexecutors;

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
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class AuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		int price = ctx.<Integer> getOne("price").get();

		if (src instanceof Player)
		{
			if (price < 0)
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot create an auction with a negative number!"));
				return CommandResult.success();
			}

			Player player = (Player) src;
			Optional<ItemStack> optionalItemInHand = player.getItemInHand();
			ItemStack itemInHand = null;

			if (optionalItemInHand.isPresent())
			{
				itemInHand = optionalItemInHand.get();
				Sponge.getEventManager().post(new AuctionEvent(player, itemInHand, price));

				for (Player p : Sponge.getServer().getOnlinePlayers())
				{
					p.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction] ", TextColors.RED, player.getName(), TextColors.GOLD, " is now auctioning " + itemInHand.getQuantity() + " ", itemInHand.getItem().getName(), " for " + price + " dollars."));
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You aren't holding anything!"));
				return CommandResult.success();
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /auction!"));
		}

		return CommandResult.success();
	}
}
