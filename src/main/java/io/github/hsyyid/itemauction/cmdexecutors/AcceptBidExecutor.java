package io.github.hsyyid.itemauction.cmdexecutors;

import io.github.hsyyid.itemauction.Main;
import io.github.hsyyid.itemauction.events.BidEvent;
import io.github.hsyyid.itemauction.utils.Auction;
import io.github.hsyyid.itemauction.utils.Bid;
import io.github.hsyyid.itemauction.utils.Utils;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
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

public class AcceptBidExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Server server = Main.game.getServer();
		Player bidder = ctx.<Player>getOne("player").get();
		
		if(src instanceof Player)
		{
			Player player = (Player) src;

			Auction endedAuction = null;
			Bid endedBid = null;
			for(Auction auction : Main.auctions)
			{	
				if(auction.getSender() == player)
				{
					for(Bid bid : auction.getBids())
					{
						if(bid.getBidder().getUniqueId() == bidder.getUniqueId())
						{
							endedBid = bid;
							endedAuction = auction;
							break;
						}
					}
				}
			}
			
			if(endedAuction != null && endedBid != null && player.getItemInHand().isPresent() && player.getItemInHand().get() == endedAuction.getItemStack())
			{
				player.setItemInHand(null);
				Main.auctions.remove(endedAuction);
				for(Player p : server.getOnlinePlayers())
				{
					p.sendMessage(Texts.of(TextColors.GREEN,"[ItemAuction] ", TextColors.WHITE, player.getName() + " auction for " + endedAuction.getQuantity() + " " + endedAuction.getItemStack().getItem().getName() + " has ended."));
				}
				bidder.sendMessage(Texts.of(TextColors.GREEN,"[ItemAuction] ", TextColors.WHITE, "Your bid was accepted by " + player.getName() + "."));
				bidder.setItemInHand(endedAuction.getItemStack());
				
				if(Utils.isPlayerInConfig(bidder.getUniqueId().toString()))
				{
					double balance = Utils.getBalance(bidder.getUniqueId().toString());
					double newBalance = balance - endedBid.getPrice();
					Utils.setBalance(bidder.getUniqueId().toString(), newBalance);
				}
				
				if(Utils.isPlayerInConfig(player.getUniqueId().toString()))
				{
					double balance = Utils.getBalance(player.getUniqueId().toString());
					double newBalance = balance + endedBid.getPrice();
					Utils.setBalance(player.getUniqueId().toString(), newBalance);
				}
				else
				{
					double newBalance = endedBid.getPrice();
					Utils.setBalance(player.getUniqueId().toString(), newBalance);
				}
				
				src.sendMessage(Texts.of(TextColors.GREEN,"Success! ", TextColors.WHITE, "Bid accepted."));
			}
			else
			{
				src.sendMessage(Texts.of(TextColors.DARK_RED,"Error! ", TextColors.RED, "Bid not found!"));
			}
		}
		else if(src instanceof ConsoleSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED,"Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}
		else if(src instanceof CommandBlockSource)
		{
			src.sendMessage(Texts.of(TextColors.DARK_RED,"Error! ", TextColors.RED, "Must be an in-game player to use /acceptbid!"));
		}
		return CommandResult.success();
	}
}