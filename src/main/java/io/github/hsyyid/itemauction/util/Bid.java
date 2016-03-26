package io.github.hsyyid.itemauction.util;

import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;

public class Bid
{
	private Player bidder;
	private BigDecimal price;
	private Auction auction;

	public Bid(Player bidder, BigDecimal price, Auction auction)
	{
		this.bidder = bidder;
		this.price = price;
		this.auction = auction;
	}

	public Player getBidder()
	{
		return bidder;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	public Auction getAuction()
	{
		return auction;
	}
}
