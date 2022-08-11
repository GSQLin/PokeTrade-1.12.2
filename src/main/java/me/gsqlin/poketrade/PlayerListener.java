package me.gsqlin.poketrade;

import com.mc9y.nyeconomy.Main;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import me.gsqlin.poketrade.Event.GuiSelectPokeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    Plugin plugin = me.gsqlin.poketrade.PokeTrade.getPlugin(me.gsqlin.poketrade.PokeTrade.class);
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        if (!plugin.getConfig().getBoolean("Convenient")) return;
        Player p = e.getPlayer();
        if (p.isSneaking())if (e.getHand().equals(EquipmentSlot.HAND))if (e.getRightClicked().getType().equals(EntityType.PLAYER)){
            Player p2 = (Player) e.getRightClicked();
            GSQUtil.applySystem(p,p2);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof InvHub){
            InvHub invHub = (InvHub) e.getInventory().getHolder();
            Player p = (Player) e.getPlayer();
            Player p2 = invHub.getDf(p);
            if (e.getInventory().getTitle().equals("§6§lDustbin")) return;
            if (invHub.getRawTitle().equals(plugin.getConfig().getString("GUI.title"))) {
                if (invHub.close){
                    invHub.setClose(false);
                    if (invHub.closeMsg) {
                        p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Cancel1"), p2, p));
                        p2.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Cancel2"), p, p2));
                        GSQUtil.returnItem(p);
                        GSQUtil.returnItem(p2);
                    }
                    p2.closeInventory();
                    GSQUtil.removeItemTradeInv(p);
                    GSQUtil.removeItemTradeInv(p2);
                }
            }else if (invHub.getRawTitle().equals(plugin.getConfig().getString("ItemTrade.GuiTitle"))){
                Timer timer = new Timer();
                if (invHub.getUpInvHub().close) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            p.openInventory(invHub.getUpInvHub().getInventory());
                        }
                    },0);
                }
                plugin.getLogger().info("§e下方提示可以无视");
                return;
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof InvHub){
            ItemStack itemStack = e.getCurrentItem();
            Inventory inv = e.getInventory();
            if (inv.getTitle().equals("§6§lDustbin"))return;
            if (itemStack!=null){
                e.setCancelled(true);
                InvHub invHub = (InvHub) e.getInventory().getHolder();
                Player p = (Player) e.getWhoClicked();
                Player p2 = invHub.getDf(p);
                int slot = e.getRawSlot();
                String fx = invHub.getFx(p);
                ItemStack kc = GSQUtil.getKc();

                if (invHub.getRawTitle().equals(plugin.getConfig().getString("ItemTrade.GuiTitle"))){
                    Player owner = invHub.getOwner();
                    if (p==owner) e.setCancelled(false);
                    return;
                }

                if (itemStack.getType().equals(Material.NETHER_STAR)){
                    if (!invHub.getZb2(p)) invHub.setZb2(p,true);

                    if (invHub.getZb2(p) && invHub.getZb2(p2)){
                        //开始执行交易程序  开始交易
                        invHub.setCloseMsg(false);
                        GSQUtil.onItemTrade(p,p2);

                        List<Integer> pPokes = new ArrayList<>();
                        List<Integer> p2Pokes = new ArrayList<>();
                        for (int i:invHub.js.get(p)) if (!inv.getItem(i).getType().equals(Material.BARRIER)) pPokes.add(PokeTrade.pokes.get(i));
                        for (int i:invHub.js.get(p2)) if (!inv.getItem(i).getType().equals(Material.BARRIER))p2Pokes.add(PokeTrade.pokes.get(i));

                        GSQUtil.onStartPokeTrade(p,p2,pPokes,p2Pokes);

                        p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.End"),p2,p));
                        p2.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.End"),p,p2));
                        p.closeInventory();
                    }
                }
//判断玩家gui内方向
                List<Integer> zc = Arrays.stream(GSQUtil.getFxc(fx)).boxed().collect(Collectors.toList());
                if (zc.contains(slot)){
                    //点精灵
                    if (itemStack.getType().equals(Material.getMaterial("PIXELMON_PIXELMON_SPRITE"))){
                        List<String> NoPokes = plugin.getConfig().getStringList("NoPokemons");
                        List<String> NoAbility = plugin.getConfig().getStringList("NoAbility");
                        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(p.getUniqueId());
                        Pokemon poke = pps.get(PokeTrade.pokes.get(slot)-1);
                        assert poke != null;
                        GuiSelectPokeEvent event = new GuiSelectPokeEvent(p,poke);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        if (poke.hasSpecFlag("untradeable")){
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Pokemon.Untradeable"),p,p2));
                            return;
                        }
                        if (!p.hasPermission("poketrade.nov"))if (GSQUtil.getPokeV(poke)>=plugin.getConfig().getInt("NoV")){
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Pokemon.NoV"),p,p2).replace("{NoV}",String.valueOf(plugin.getConfig().getInt("NoV"))));
                            return;
                        }

                        if (!p.hasPermission("poketrade.noability"))if (NoAbility.contains(poke.getAbilityName())){
                            p.sendMessage("§6该精灵特性被限制精灵交易");
                            return;
                        }

                        if (!p.hasPermission("poketrade.nopokemons"))if (NoPokes.contains(poke.getUnlocalizedName())){
                            p.sendMessage("§6该精灵被禁止了交易");
                            return;
                        }
                        if (event.isCancelled()) return;


                        if (invHub.getZb(p)){
                            p.sendMessage("§6你已准备,请取消准备后在进行修整");
                            return;
                        }
                        if (Arrays.stream(InvHub.zjl).boxed().collect(Collectors.toList()).contains(slot)){
                            inv.setItem(slot+2,itemStack);
                            inv.setItem(slot,kc);
                        }
                        if (Arrays.stream(InvHub.zjl2).boxed().collect(Collectors.toList()).contains(slot)){
                            inv.setItem(slot-2,itemStack);
                            inv.setItem(slot,kc);
                        }
                        if (Arrays.stream(InvHub.yjl).boxed().collect(Collectors.toList()).contains(slot)){
                            inv.setItem(slot-2,itemStack);
                            inv.setItem(slot,kc);
                        }
                        if (Arrays.stream(InvHub.yjl2).boxed().collect(Collectors.toList()).contains(slot)){
                            inv.setItem(slot+2,itemStack);
                            inv.setItem(slot,kc);
                        }
                    }
                    //点击准备取消
                    if (Arrays.stream(InvHub.zty).boxed().collect(Collectors.toList()).contains(slot) || Arrays.stream(InvHub.yty).boxed().collect(Collectors.toList()).contains(slot)){
                        if (Arrays.stream(InvHub.zty).boxed().collect(Collectors.toList()).contains(slot)){
                            if (invHub.getZb(p)){
                                p.sendMessage("§6你取消了准备");
                                p2.sendMessage("§6由于对方取消了准备,你需要重新检查一遍");
                                invHub.setZb(p,false);
                                invHub.setZb(p2,false);
                                invHub.setZb2(p2,false);
                                invHub.setZb2(p,false);
                                for (int i:InvHub.zty) inv.setItem(i,GSQUtil.getHongBl());
                                for (int i:InvHub.yty) inv.setItem(i,GSQUtil.getHongBl());
                            }else{
                                p.sendMessage("§6你已准备");
                                p2.sendMessage("§6对方准备就绪");
                                invHub.setZb(p,true);
                                for (int i:InvHub.zty) inv.setItem(i,GSQUtil.getLvBl());
                            }
                        }
                        if (Arrays.stream(InvHub.yty).boxed().collect(Collectors.toList()).contains(slot)){
                            if (invHub.getZb(p)){
                                p.sendMessage("§6你取消了准备");
                                p2.sendMessage("§6由于对方取消了准备,你需要重新检查一遍");
                                invHub.setZb(p,false);
                                invHub.setZb(p2,false);
                                invHub.setZb2(p2,false);
                                invHub.setZb2(p,false);
                                for (int i:InvHub.yty) inv.setItem(i,GSQUtil.getHongBl());
                                for (int i:InvHub.zty) inv.setItem(i,GSQUtil.getHongBl());
                            }else{
                                p.sendMessage("§6你已准备");
                                p2.sendMessage("§6对方准备就绪");
                                invHub.setZb(p,true);
                                for (int i:InvHub.yty) inv.setItem(i,GSQUtil.getLvBl());
                            }
                        }
                        if (invHub.getZb(p) && invHub.getZb(p2)){
                            //31
                            inv.setItem(31,GSQUtil.getStartXj());
                        }else{
                            inv.setItem(31,GSQUtil.getHuiBl());
                        }
                    }
                    //准备后点击其他地方
                    if (invHub.getZb(p)){
                        p.sendMessage("§6你已准备,请取消准备后在进行修整");
                        return;
                    }
                    clickItemTrade(invHub, p, p, slot);
                }else{
                    clickItemTrade(invHub, p, p2, slot);
                    p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.ClickDF"),p,p2));
                }
            }
        }
    }
    private void clickItemTrade(InvHub invHub, Player p, Player p2, int slot) {
        if (slot == 48||slot == 50){
            if (GSQUtil.getItemTradeInv(p2,invHub)!=null){
                GSQUtil.updateItemTradeInv(p2,invHub);
            }else GSQUtil.putItemTradeInv(p2,invHub);
            invHub.setClose(false);
            p.closeInventory();
            invHub.setClose(true);
            p.openInventory(GSQUtil.getItemTradeInv(p2,invHub));
        }
    }
}
