package me.gsqlin.poketrade.Event;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SendRequest extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Boolean cancelled = false;
    private final Player player;
    private final Player player2;
    private String message;
    private String message2;

    public SendRequest(Player player,Player player2,String message,String message2){
        this.player = player;
        this.player2 = player2;
        this.message = message;
        this.message2 = message2;
    }
    public Player getPlayer(){
        return this.player;
    }
    public Player getPlayer2(){
        return this.player2;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
