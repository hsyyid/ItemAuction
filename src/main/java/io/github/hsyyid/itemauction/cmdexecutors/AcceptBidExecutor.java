package io.github.hsyyid.itemauction.cmdexecutors;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.utils.Auction;
import io.github.hsyyid.itemauction.utils.Bid;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

public class AcceptBidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Server server = ItemAuction.game.getServer();
		Player bidder = ctx.<Player> getOne("player").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;

			Auction endedAuction = null;
			Bid endedBid = null;
			
			for (Auction auction : ItemAuction.auctions)
			{
				if (auction.getSender() == player)
				{
					for (Bid bid : auction.getBids())
					{
						
						if (bid.getBidder().getUniqueId() == bidder.getUniqueId())
						{
							endedBid = bid;
							endedAuction = auction;
							break;
						}
					}
				}
			}

			if (endedAuction != null && endedBid != null && player.getItemInHand().isPresent() && player.getItemInHand().get() == endedAuction.getItemStack())
			{
				player.setItemInHand(null);
				ItemAuction.auctions.remove(endedAuction);
				for (Player p : server.getOnlinePlayers())
				{
					p.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction] ", TextColors.WHITE, player.getName() + " auction for " + endedAuction.getQuantity() + " " + endedAuction.getItemStack().getItem().getName() + " has ended."));
				}
				bidder.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction] ", TextColors.WHITE, "Your bid was accepted by " + player.getName() + "."));
				bidder.setItemInHand(endedAuction.getItemStack());

				TotalEconomy totalEconomy = (TotalEconomy) ItemAuction.game.getPluginManager().getPlugin("TotalEconomy").get().getInstance().get();
				AccountManager accountManager = totalEconomy.getAccountManager();

				BigDecimal price = new BigDecimal(endedBid.getPrice());
				accountManager.removeFromBalance(bidder.getUniqueId(), price);
				accountManager.addToBalance(player.getUniqueId(), price, true);	

				src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.WHITE, "Bid accepted."));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Bid not found!"));
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}
		return CommandResult.success();
	}
}
