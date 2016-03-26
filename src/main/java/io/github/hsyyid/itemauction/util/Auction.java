package io.github.hsyyid.itemauction.util;

import com.google.common.collect.Lists;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public class Auction
{
	public Player sender;
	public BigDecimal price;
	public int quantity;
	public ItemStack itemStack;
	public List<Bid> bids;

	public Auction(Player sender, BigDecimal price, int quantity, ItemStack itemStack)
	{
		this.sender = sender;
		this.price = price;
		this.quantity = quantity;
		this.itemStack = itemStack;
		this.bids = Lists.newArrayList();
	}

	public Player getSender()
	{
		return sender;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	public List<Bid> getBids()
	{
		return bids;
	}

	public void addBid(Bid bid)
	{
		bids.add(bid);
	}

	public int getQuantity()
	{
		return quantity;
	}

	public ItemStack getItemStack()
	{
		return itemStack;
	}
}
