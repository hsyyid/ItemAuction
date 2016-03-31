package io.github.hsyyid.itemauction.cmdexecutors;

import com.google.common.collect.Lists;
import io.github.hsyyid.itemauction.ItemAuction;
import io.github.hsyyid.itemauction.util.Auction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.List;

public class ListAuctionExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (ItemAuction.auctions.size() == 0)
		{
			src.sendMessage(Text.of(TextColors.GREEN, "[ItemAuction]: ", TextColors.RED, "There are no current ItemAuctions!"));
			return CommandResult.success();
		}

		PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
		List<Text> text = Lists.newArrayList();

		for (Auction auction : ItemAuction.auctions)
		{
			String auctioneer = auction.getSender().getName();
			String itemName = auction.getItemStack().getTranslation().get();
			BigDecimal askingPrice = auction.getPrice();

			Text auctionText = Text.builder()
				.append(Text.of(TextColors.GREEN, "Auctioneer: ", TextColors.GOLD, auctioneer + "\n"))
				.append(Text.of(TextColors.GREEN, "Item: ", TextColors.GOLD, itemName + "\n"))
				.append(Text.of(TextColors.GREEN, "Amount: ", TextColors.GOLD, auction.getItemStack().getQuantity() + "\n"))
				.append(Text.of(TextColors.GREEN, "Asking Price: ", TextColors.GOLD, askingPrice + "\n"))
				.build();

			text.add(auctionText);
		}

		PaginationList.Builder paginationBuilder = paginationService.builder()
			.contents(text)
			.title(Text.of(TextColors.GREEN, "Auctions"))
			.padding(Text.of("-"));

		paginationBuilder.sendTo(src);
		return CommandResult.success();
	}
}
