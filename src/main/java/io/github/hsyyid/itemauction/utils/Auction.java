package io.github.hsyyid.itemauction.utils;

import java.util.ArrayList;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class Auction
{
	public Player sender;
	public int price;
	public int quantity;
	public ItemStack itemStack;
	public ArrayList<Bid> bids= new ArrayList<Bid>();
	
	public Auction(Player sender, int price, int quantity, ItemStack itemStack)
	{
		this.sender = sender;
		this.price = price;
		this.quantity = quantity;
		this.itemStack = itemStack;
	}
	
	public Player getSender()
	{
		return sender;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public ArrayList<Bid> getBids()
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
