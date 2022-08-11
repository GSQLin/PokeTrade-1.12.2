package me.gsqlin.poketrade;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Cmd implements CommandExecutor , TabCompleter {
    Plugin plugin = me.gsqlin.poketrade.PokeTrade.getPlugin(me.gsqlin.poketrade.PokeTrade.class);
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("poketrade.help")) return false;
        if (args.length >= 1){
            if (args[0].equalsIgnoreCase("help")){
                sender.sendMessage(GSQUtil.help);
                return false;
            }else if (args[0].equalsIgnoreCase("reload")){
                if (!sender.hasPermission("poketrade.reload")){
                    sender.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPermissions"),null,null));
                    return false;
                }
                if (args.length >= 2){
                    String pn = args[1];
                    GSQUtil.load(pn);
                }else{
                    plugin.reloadConfig();
                    plugin.saveDefaultConfig();
                }
                sender.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.Reload"),null,null));
                return false;
            }
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("apply")){
                    if (args.length >= 2){
                        Player p2 = Bukkit.getPlayer(args[1]);
                        if (p2==null){
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPlayer"),p, null));
                            return false;
                        }
                        GSQUtil.applySystem(p,p2);
                    }else sender.sendMessage("§7用法 /poketrade apply [玩家名字]");

                }else if (args[0].equalsIgnoreCase("blacklist")){
                    if (!p.hasPermission("poketrade.blacklist")) {
                        p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPermissions"),p,null));
                        return false;
                    }
                    List<String> blacklist = new ArrayList<>();
                    FileConfiguration fc = GSQUtil.getFc(p.getName());
                    if (fc.getStringList("blacklist")!=null) blacklist = fc.getStringList("blacklist");
                    if (args.length >= 2){
                        Player p2 = Bukkit.getPlayer(args[1]);
                        if (p2.equals(p)){
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPermissions"),p,p2));
                            return false;
                        }
                        if (blacklist.contains(p2.getName())){
                            blacklist.remove(p2.getName());
                            fc.set("blacklist",blacklist);
                            GSQUtil.save(fc,p);
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.RemovePlayer"),p2,p));
                        }else{
                            blacklist.add(p2.getName());
                            fc.set("blacklist",blacklist);
                            GSQUtil.save(fc,p);
                            p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.AddPlayer"),p2,p));
                        }
                    }else sender.sendMessage(new String[]{
                                    "§7用法 /poketrade blacklist [玩家名字]\n" +
                                    "§3以下是你的黑名单列表\n§a" +
                                    blacklist
                    });
                }else if (args[0].equalsIgnoreCase("dustbin")){
                    if (!p.hasPermission("poketrade.use")){
                        p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoPermissions"),p,null));
                        return false;
                    }
                    if (p.hasPermission(""))
                    if (InvHub.getDustbin(p)!=null){
                        p.openInventory(InvHub.getDustbin(p).getInventory());
                    }else{
                        p.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NotDustbin"),p,null));
                        return false;
                    }
                }else sender.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.NoCommand"),p,null));

            }else{
                sender.sendMessage(GSQUtil.getMsg(plugin.getConfig().getString("Message.IsConsole"),null,null));
            }
        }else{
            sender.sendMessage(GSQUtil.help);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] subCmd = {"help","apply","blacklist","reload","dustbin"};
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<String> playerList = new ArrayList<>();
        for (Player p:players)playerList.add(p.getName());
        String[] playerListName = playerList.toArray(new String[playerList.size()]);
        if (args.length > 1) return Arrays.stream(playerListName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        if (args.length == 0) return Arrays.asList(subCmd);
        return Arrays.stream(subCmd).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
