package io.github.hsyyid.itemauction.utils;

import org.spongepowered.api.entity.living.player.Player;

public class Bid
{
	private Player bidder;
	private int price;
	private Auction auction;
	
	public Bid(Player bidder, int price, Auction auction)
	{
		this.bidder = bidder;
		this.price = price;
		this.auction = auction;
	}
	
	public Player getBidder()
	{
		return bidder;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public Auction getAuction()
	{
		return auction;
	}
}
