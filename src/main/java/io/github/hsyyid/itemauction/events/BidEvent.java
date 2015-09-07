package io.github.hsyyid.itemauction.events;

import io.github.hsyyid.itemauction.utils.Auction;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;

public class BidEvent extends AbstractEvent implements Cancellable
{
   private boolean cancelled = false;

   private Player bidder;
   private int price;
   private Auction auction;
   
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

   public boolean isCancelled()
   {
      return cancelled;
   }

   public void setCancelled(boolean cancel)
   {
      cancelled = cancel;
   }

   public BidEvent(Player bidder, int price, Auction auction)
   {
      this.bidder = bidder;
      this.price = price;
      this.auction = auction;
   }
}
