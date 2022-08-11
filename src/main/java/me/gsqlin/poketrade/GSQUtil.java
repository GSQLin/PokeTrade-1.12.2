package me.gsqlin.poketrade;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import me.clip.placeholderapi.PlaceholderAPI;
import me.gsqlin.poketrade.Event.AgreeToApply;
import me.gsqlin.poketrade.Event.SendRequest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class GSQUtil {
    static Plugin plugin = PokeTrade.getPlugin(PokeTrade.class);

    static File folder = new File(plugin.getDataFolder(),"\\playerData");
    public static Map<String,FileConfiguration> fcHub = new HashMap<>();
    public static Map<String,File> fileHub = new HashMap<>();
    public static Map<Player, Inventory> ItemTInv = new HashMap<>();

    static public FileConfiguration getFc(String pn){
        if (fcHub.containsKey(pn)) return fcHub.get(pn);
        load(pn);
        return fcHub.get(pn);
    }
    static public FileConfiguration getFc(Player p){
        String pn = p.getName();
        if (fcHub.containsKey(pn)) return fcHub.get(pn);
        load(pn);
        return fcHub.get(pn);
    }

    static public void load(String pn){
        if (!folder.exists()) folder.mkdirs();
        File file = new File(folder.getAbsolutePath() + "\\" + pn + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileHub.put(pn,file);
        fcHub.put(pn,YamlConfiguration.loadConfiguration(file));
    }

    static public void save(FileConfiguration fc,Player p){
        try {
            fc.save(fileHub.get(p.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fcHub.replace(p.getName(),fc);
    }
    static public void save(FileConfiguration fc,String pn){
        try {
            fc.save(fileHub.get(pn));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fcHub.replace(pn,fc);
    }




    //↑文件Util
    static public Map<Player,Player> applyList = new HashMap<>();
    static public String[] help = new String[]{
            "§r==>§3Poke§7Trade§r<==",
            "§r/poketrade apply §7[玩家名字] §7向指定玩家发送申请/同意该玩家的申请",
            "§r/poketrade help §7查看PokeTrade帮助",
            "§r/poketrade blacklist §7[玩家名字] §7添加/移除黑名单(不加玩家名字显示列表)",
            "§r/poketrade reload §7[指定玩家配置] §7重载指定玩家配置(不加玩家名字重载默认配置)",
            "§r/poketrade dustbin §7物品交易后留下的物品[临时垃圾箱]",
            "§7poketrade可写为pt,poket,ptrade,ptr",
    };

    static public String getMsg(String m,Player p,Player p2){
        String msg = m.replace("&","§");
        if (p!=null)msg = PlaceholderAPI.setPlaceholders(p,msg);
        if (p!=null)msg = msg.replace("{PLAYER_NAME}",p.getName());
        if (p2!=null)msg = msg.replace("{PLAYER2_NAME}",p2.getName());
        return msg;
    }
    static public List<String> getMsg(Pokemon poke,List<String> list){
        List<String> msg = new ArrayList<>();
        for (String str :list){
            msg.add(setMyPapi(poke,str));
        }
        return msg;
    }
    static public String setMyPapi(Pokemon poke,String str){
        String string = str.replace("&","§");
        string = PlaceholderAPI.setPlaceholders(poke.getOwnerPlayer().getBukkitEntity().getPlayer(),string);
        string = getHasSpec(poke,string);
        return string.replace("{pokemon_name}",poke.getLocalizedName()).
                replace("{pokemon_uuid}",poke.getUUID().toString()).
                replace("{poke_name}",poke.getDisplayName()).
                replace("{poke_level}",String.valueOf(poke.getLevel())).
                replace("{poke_ivs}",String.valueOf((int)poke.getIVs().getPercentage(poke.getIVs().getTotal()))).
                replace("{poke_evs}",String.valueOf((int)poke.getIVs().getPercentage(poke.getIVs().getTotal()))).
                replace("{poke_ivs_hp}",String.valueOf(poke.getIVs().hp)).
                replace("{poke_evs_hp}",String.valueOf(poke.getEVs().hp)).
                replace("{poke_ivs_attack}",String.valueOf(poke.getIVs().attack)).
                replace("{poke_evs_attack}",String.valueOf(poke.getEVs().attack)).
                replace("{poke_ivs_defence}",String.valueOf(poke.getIVs().defence)).
                replace("{poke_evs_defence}",String.valueOf(poke.getEVs().defence)).
                replace("{poke_ivs_speed}",String.valueOf(poke.getIVs().speed)).
                replace("{poke_evs_speed}",String.valueOf(poke.getEVs().speed)).
                replace("{poke_ivs_specialDefence}",String.valueOf(poke.getIVs().specialDefence)).
                replace("{poke_evs_specialDefence}",String.valueOf(poke.getEVs().specialDefence)).
                replace("{poke_ivs_specialAttack}",String.valueOf(poke.getIVs().specialAttack)).
                replace("{poke_evs_specialAttack}",String.valueOf(poke.getEVs().specialAttack)).
                replace("{poke_ability}",poke.getAbilityName()).
                replace("{poke_shiny}",getShiny(poke));
    }
    public static String getHasSpec(Pokemon poke , String str){
        List<String> tags = plugin.getConfig().getStringList("GUI.PokePhotoItem.tags.list");
        String string = str;
        for (String x : tags){
            String tag = plugin.getConfig().getString("GUI.PokePhotoItem.tags." + x);
            string = str.replace("{"+x+"}",getHasSpecLang(poke,tag));
        }
        return string;
    }
    static public String getShiny(Pokemon poke){
        if (poke.isShiny()){
            return "§a是";
        }else return "§c否";
    }
    static public String getHasSpecLang(Pokemon poke,String key){
        if (poke.hasSpecFlag(key)){
            return "§a是";
        }else return "§c否";
    }
    static public List<String> getMsg(List<String> m,Player p,Player p2){
        List<String> list = new ArrayList<>();
        for (String x:m){
            x = x.replace("&","§");
            if (p!=null)x = PlaceholderAPI.setPlaceholders(p,x);
            if (p!=null)x = x.replace("{PLAYER_NAME}",p.getName());
            if (p2!=null)x = x.replace("{PLAYER2_NAME}",p2.getName());
            list.add(x);
        }
        return list;
    }

    static public Integer getPokeV(Pokemon poke){
        int i = 0;
        if (poke.getIVs().hp==31)i++;
        if (poke.getIVs().speed==31)i++;
        if (poke.getIVs().attack==31)i++;
        if (poke.getIVs().specialAttack==31)i++;
        if (poke.getIVs().defence==31)i++;
        if (poke.getIVs().specialDefence==31)i++;
        return i;
    }

    static public int[] getFxc(String fx){
        if (fx.equals("z")){
            int [] i = IntStream.concat(Arrays.stream(
                    IntStream.concat(Arrays.stream(InvHub.zjl),
                            Arrays.stream(InvHub.zjl2)).toArray()),
                    Arrays.stream(InvHub.zty)).toArray();
            return IntStream.concat(Arrays.stream(i), Arrays.stream(InvHub.zgj)).toArray();
        }
        if (fx.equals("y")){
            int [] i = IntStream.concat(Arrays.stream(
                    IntStream.concat(Arrays.stream(InvHub.yjl),
                            Arrays.stream(InvHub.yjl2)).toArray()),
                    Arrays.stream(InvHub.yty)).toArray();
            return IntStream.concat(Arrays.stream(i), Arrays.stream(InvHub.ygj)).toArray();
        }
        return null;
    }

    static public ItemStack getItemStack(String str){
        ItemStack itemStack = null;
        if (str.contains(":")){
            String front = str.substring(0,str.indexOf(":"));
            int after = Integer.parseInt(str.substring(front.length() + 1, str.length()));
            try {
                Integer.valueOf(front);
                itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(front)),1,(short)after);
            } catch (Exception e){
                itemStack = new ItemStack(Material.getMaterial(front),1,(short)after);
            }
        }else{
            try {
                Integer.valueOf(str);
                itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(str)));
            } catch (Exception e){
                itemStack = new ItemStack(Material.getMaterial(str));
            }
        }
        return itemStack;
    }

    static public ItemStack getStartXj(){
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item"),null,null));
        itemMeta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore"),null,null));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static public ItemStack getPlayerHead(Player p){
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM,1,(short)3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(p);
        skullMeta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item1"),p,null));
        skullMeta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore1"),null,null));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
    static public ItemStack getKc(){
        ItemStack kc = new ItemStack(Material.BARRIER);
        ItemMeta kc_meta = kc.getItemMeta();
        kc_meta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item2"),null,null));
        kc_meta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore2"),null,null));
        kc.setItemMeta(kc_meta);
        return kc;
    }
    static public ItemStack getHongBl(){
        ItemStack hongBl = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)14);
        ItemMeta hongBl_meta = hongBl.getItemMeta();
        hongBl_meta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item3"),null,null));
        hongBl_meta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore3"),null,null));
        hongBl.setItemMeta(hongBl_meta);
        return hongBl;
    }
    static public ItemStack getLvBl(){
        ItemStack lvBl = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)13);
        ItemMeta lvBl_meta = lvBl.getItemMeta();
        lvBl_meta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item4"),null,null));
        lvBl_meta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore4"),null,null));
        lvBl.setItemMeta(lvBl_meta);
        return lvBl;
    }
    static public ItemStack getHuiBl(){
        ItemStack huiBl = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)7);
        ItemMeta huiBl_meta = huiBl.getItemMeta();
        huiBl_meta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("GUI.Item5"),null,null));
        huiBl_meta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("GUI.Lore5"),null,null));
        huiBl.setItemMeta(huiBl_meta);
        return huiBl;
    }
    static public ItemStack getItemTrade(){
        ItemStack itemStack = getItemStack(plugin.getConfig().getString("ItemTrade.Button.Material"));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(GSQUtil.getMsg(plugin.getConfig().getString("ItemTrade.Button.Name"),null,null));
        itemMeta.setLore(GSQUtil.getMsg(plugin.getConfig().getStringList("ItemTrade.Button.Lore"),null,null));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static public ItemStack getPokeItemData(Player p,int i){
        ItemStack itemStack = null;
        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(p.getUniqueId());
        Pokemon pokemon = pps.get(i-1);
        String itemName = plugin.getConfig().getString("GUI.PokePhotoItem.name");
        List<String> itemLore = plugin.getConfig().getStringList("GUI.PokePhotoItem.lore");
        if (pokemon!=null){
            itemStack = CraftItemStack.asBukkitCopy(ItemPixelmonSprite.getPhoto(pokemon));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(setMyPapi(pokemon,itemName));
            itemMeta.setLore(getMsg(pokemon,itemLore));
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    static public void applySystem (Player p, Player p2){
        if (!p.hasPermission("poketrade.apply")){
            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPermissions"),p,p2));
            return;
        }
        if (!p2.hasPermission("poketrade.apply")){
            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.OtherNoPermissions"),p,p2));
            return;
        }
        if (p==p2) {
            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.SZXW"),p,p2));
            return;
        }
        FileConfiguration fc = GSQUtil.getFc(p2);
        List<String> blacklist = fc.getStringList("blacklist");
        if (blacklist.contains(p.getName())){
            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.InBlackList"),p,p2));
            return;
        }

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        if (applyList.get(p2)!=null){
            if (applyList.get(p2).equals(p)){
                AgreeToApply event = new AgreeToApply(p,p2,GSQUtil.getMsg(plugin.getConfig().getString("Message.Agree"),p2,p),GSQUtil.getMsg(plugin.getConfig().getString("Message.Refuse"),p,p2));
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) return;
                p.sendMessage(event.getMessage());
                p2.sendMessage(event.getMessage2());
                applyList.remove(p2);
                applyList.remove(p);
                InvHub.mainInv(p2,p,plugin);
                return;
            }
        }
        if (applyList.get(p) != null)
            if (applyList.get(p).equals(p2)){
            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Already"),p2,p));
            return;
        }
        SendRequest event = new SendRequest(p,p2,GSQUtil.getMsg(plugin.getConfig().getString("Message.Overtime1"),p2,p),GSQUtil.getMsg(plugin.getConfig().getString("Message.Overtime2"),p,p2));
        Bukkit.getServer().getPluginManager().callEvent(event);
        applyList.put(p,p2);
        if (event.isCancelled()) return;
        p.sendMessage(event.getMessage());
        p2.sendMessage(event.getMessage2());
        Runnable runnable = () -> {
            if (applyList.get(p)!=null)if (applyList.get(p).equals(p2)){
                applyList.remove(p);
                p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Invalid1"),p2,p));
                p2.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Invalid2"),p,p2));
            }
        };
        scheduler.runTaskLater(plugin, runnable , 20 * 15);
    }
    static public void onStartPokeTrade(Player p, Player p2, List<Integer> pPokes, List<Integer> p2Pokes){
        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(p.getUniqueId());
        PlayerPartyStorage pps2 = Pixelmon.storageManager.getParty(p2.getUniqueId());
        for (int i:pPokes) {
            Pokemon pokemon = pps.get(i-1);
            pps.set(i-1,null);
            pps2.add(pokemon);
        }
        for (int i:p2Pokes) {
            Pokemon pokemon = pps2.get(i-1);
            pps2.set(i-1,null);
            pps.add(pokemon);
        }
    }
    static public void onItemTrade(Player p, Player p2){
        ItemStack[] pItems = getItemTradeInv(p,null).getContents();
        ItemStack[] p2Items = getItemTradeInv(p2,null).getContents();

        addDustbinAndPlayer(p, p2Items);
        addDustbinAndPlayer(p2, pItems);
    }

    private static void addDustbinAndPlayer(Player p, ItemStack[] pItems) {
        boolean b = false;
        for (ItemStack item:pItems){
            if (item != null) {
                Inventory inv = p.getInventory();
                if (inv.firstEmpty() != -1){
                    inv.addItem(item);
                }else {
                    b = true;
                    InvHub invHub = InvHub.dustbin(p);
                    invHub.getInventory().addItem(item);
                }
            }
        }
        if (b)p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.InsufficientSpace"),p,null));
    }

    static public void returnItem(Player p){
        ItemStack[] pItems = getItemTradeInv(p,null).getContents();
        for (ItemStack item:pItems){
            if (item != null)p.getInventory().addItem(item);
        }
    }
    static public Inventory getItemTradeInv(Player p,InvHub upInvHub){
        if (ItemTInv.get(p)==null)putItemTradeInv(p,upInvHub);
        return ItemTInv.get(p);
    }
    static public void putItemTradeInv(Player p,InvHub upInvHub){
        ItemTInv.put(p,InvHub.itemTrade(plugin,p,upInvHub).getInventory());
    }
    static public void updateItemTradeInv(Player p,InvHub upInvHub){
        InvHub invHub = (InvHub) ItemTInv.get(p).getHolder();
        invHub.setUpInvHub(upInvHub);
        ItemTInv.replace(p,getItemTradeInv(p,invHub));
    }
    static public void removeItemTradeInv(Player p){
        ItemTInv.remove(p);
    }
}