package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.Main;
import io.github.hsyyid.itemauction.events.AuctionEvent;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.source.CommandBlockSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.google.common.base.Optional;

public class AuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Game game = Main.game;
		Server server = game.getServer();
		int price = ctx.<Integer> getOne("price").get();
		if (src instanceof Player)
		{
			if (price < 0)
			{
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot create an auction with a negative number!"));
				return CommandResult.success();
			}
			Player player = (Player) src;
			Optional<ItemStack> optionalItemInHand = player.getItemInHand();
			ItemStack itemInHand = null;
			if (optionalItemInHand != Optional.<ItemStack> absent())
			{
				itemInHand = optionalItemInHand.get();
				game.getEventManager().post(new AuctionEvent(player, itemInHand, price));
				for (Player p : server.getOnlinePlayers())
				{
					p.sendMessage(Texts.of(TextColors.GREEN, "[ItemAuction] ", TextColors.RED, player.getName(), TextColors.GOLD, " is now auctioning " + itemInHand.getQuantity() + " ", itemInHand.getItem().getName(), " for " + price + " dollars."));
				}
			}
			else
			{
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You aren't holding anything!"));
				return CommandResult.success();
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /auction!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /auction!"));
		}

		return CommandResult.success();
	}
}
