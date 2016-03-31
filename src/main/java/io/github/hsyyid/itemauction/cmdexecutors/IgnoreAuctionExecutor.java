package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.ItemAuction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class IgnoreAuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;

			if (ItemAuction.ignorePlayers.contains(player.getUniqueId()))
			{
				ItemAuction.ignorePlayers.remove(player.getUniqueId());
			}
			else
			{
				ItemAuction.ignorePlayers.add(player.getUniqueId());
			}

			player.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.YELLOW, "Toggled ignoring auctions ", TextColors.GOLD, ItemAuction.ignorePlayers.contains(player.getUniqueId()) ? "on" : "off"));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /ignoreauction!"));
		}

		return CommandResult.success();
	}
}
