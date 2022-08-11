package me.gsqlin.poketrade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class InvHub implements InventoryHolder {
    static int[] zty = {1,10,19,28,37,46};
    static int[] zjl2 = {2,11,20,29,38,47};
    static int[] zjl = {0,9,18,27,36,45};
    static int[] yty = {7,16,25,34,43,52};
    static int[] yjl2 = {6,15,24,33,42,51};
    static int[] yjl = {8,17,26,35,44,53};
    static int [] zgj = {3,12,21,30,39,48};
    static int [] ygj = {5,14,23,32,41,50};
    static int[] fjx = {12,21,30,39,48,4,13,22,31,40,49,14,23,32,41,50};
    //左头 3 右头 5

    private final Inventory inv;
    private Player owner;
    private InvHub upInvHub;
    private String rawTitle;
    public InvHub(String title,int slot,Player p){
        inv = Bukkit.createInventory(this,9*slot,title);
    }
    private Player p;
    private Player p2;
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public boolean pzb2 = false;
    public boolean p2zb2 = false;

    public boolean pzb = false;
    public boolean p2zb = false;

    public Boolean getZb(Player p){
        boolean bl = false;
        if (p.equals(this.p)) bl = this.pzb;
        if (p.equals(this.p2)) bl = this.p2zb;
        return bl;
    }
    public Boolean getZb2(Player p){
        boolean bl = false;
        if (p.equals(this.p)) bl = this.pzb2;
        if (p.equals(this.p2)) bl = this.p2zb2;
        return bl;
    }

    public void setZb(Player p, boolean bl){
        if (p.equals(this.p)) this.pzb = bl;
        if (p.equals(this.p2)) this.p2zb = bl;
    }
    public void setZb2(Player p, boolean bl){
        if (p.equals(this.p)) this.pzb2 = bl;
        if (p.equals(this.p2)) this.p2zb2 = bl;
    }
    public String getFx(Player p) {
        String fx = null;
        if (p.equals(this.p)) fx = "z";
        if (p.equals(this.p2)) fx = "y";
        return fx;
    }

    public Player getDf(Player p) {
        Player df = null;
        if (p.equals(this.p)) df = this.p2;
        if (p.equals(this.p2)) df = this.p;
        return df;
    }

    public Player getP() {
        return p;
    }

    public Player getP2() {
        return p2;
    }

    public boolean close = true;
    public boolean closeMsg = true;
    public void setClose(boolean close){
        this.close = close;
    }
    public void setCloseMsg(boolean closeMsg){
        this.closeMsg = closeMsg;
    }
    public Player getOwner(){return this.owner;}
    public InvHub getUpInvHub(){return this.upInvHub;}
    public String getRawTitle(){return this.rawTitle;}
    public void setUpInvHub(InvHub invHub){this.upInvHub = invHub;}

    Map<Player,int[]> js = new HashMap<>();
    static public InvHub itemTrade(Plugin plugin, Player p, InvHub upInvHub){
        String rawTitle = plugin.getConfig().getString("ItemTrade.GuiTitle");
        InvHub invHub = new InvHub(GSQUtil.getMsg(rawTitle,p,null),4,p);
        invHub.owner = p;
        invHub.upInvHub = upInvHub;
        invHub.rawTitle = rawTitle;
        GSQUtil.ItemTInv.remove(p);
        GSQUtil.ItemTInv.put(p,invHub.getInventory());
        invHub.setClose(false);
        return invHub;
    }
    static public Map<Player,InvHub> dustbinMap = new HashMap<>();
    static public InvHub getDustbin(Player p){
        return dustbinMap.get(p);
    }
    static public InvHub dustbin(Player p){
        if (dustbinMap.get(p)==null)dustbinMap.put(p,new InvHub("§6§lDustbin",6,null));
        return dustbinMap.get(p);
    }
    static public void mainInv(Player p,Player p2,Plugin plugin){
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                String rawTitle = plugin.getConfig().getString("GUI.title");
                InvHub invHub = new InvHub(GSQUtil.getMsg(rawTitle,p,p2),6,null);
                invHub.p = p;
                invHub.p2 = p2;
                invHub.js.put(p,zjl2);
                invHub.js.put(p2,yjl2);
                invHub.rawTitle = rawTitle;
                Inventory inv = invHub.getInventory();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack pHead = GSQUtil.getPlayerHead(p);
                        ItemStack p2Head = GSQUtil.getPlayerHead(p2);
                        inv.setItem(3,pHead);
                        inv.setItem(5,p2Head);
                    }
                }.runTask(plugin);

                ItemStack kc = GSQUtil.getKc();
                ItemStack hongBl = GSQUtil.getHongBl();
                ItemStack huiBl = GSQUtil.getHuiBl();
                ItemStack itemTrade = GSQUtil.getItemTrade();

                setPokeItem(p, inv, kc, zjl);
                setPokeItem(p2, inv, kc, yjl);

                for (int i:zjl2){
                    inv.setItem(i,kc);
                }

                for (int i:yjl2){
                    inv.setItem(i,kc);
                }

                for (int i:zty){
                    inv.setItem(i,hongBl);
                }
                for (int i:yty){
                    inv.setItem(i,hongBl);
                }
                for (int i:fjx){
                    inv.setItem(i,huiBl);
                }
                inv.setItem(48,itemTrade);
                inv.setItem(50,itemTrade);

                p.closeInventory();
                p2.closeInventory();
                p.openInventory(inv);
                p2.openInventory(inv);
            }
        };
        runnable.run();
    }

    private static void setPokeItem(Player p, Inventory inv, ItemStack kc, int[] jl) {
        int yx = 1;
        for (int i: jl){
            ItemStack itemStack = kc;
            if (GSQUtil.getPokeItemData(p,yx)!=null) itemStack = GSQUtil.getPokeItemData(p,yx);
            inv.setItem(i,itemStack);
            yx++;
        }
    }
}
