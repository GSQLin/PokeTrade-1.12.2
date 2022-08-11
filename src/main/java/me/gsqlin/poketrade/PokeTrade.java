package me.gsqlin.poketrade;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PokeTrade extends JavaPlugin{
    static public Map<Integer,Integer> pokes = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        pokes.put(0, 1);
        pokes.put(9, 2);
        pokes.put(18, 3);
        pokes.put(27, 4);
        pokes.put(36, 5);
        pokes.put(45, 6);

        pokes.put(8, 1);
        pokes.put(17, 2);
        pokes.put(26, 3);
        pokes.put(35, 4);
        pokes.put(44, 5);
        pokes.put(53, 6);

        pokes.put(2, 1);
        pokes.put(11, 2);
        pokes.put(20, 3);
        pokes.put(29, 4);
        pokes.put(38, 5);
        pokes.put(47, 6);

        pokes.put(6, 1);
        pokes.put(15, 2);
        pokes.put(24, 3);
        pokes.put(33, 4);
        pokes.put(42, 5);
        pokes.put(51, 6);

        getServer().getPluginCommand("poketrade").setExecutor(new Cmd());
        getCommand("poketrade").setTabCompleter(new Cmd());
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        getServer().getConsoleSender().sendMessage(new String[]{
                "",
                "§3 ___         §7___           ",
                "§3| o \\ _ ||  §7|_ _|_  _  ||_ ",
                "§3|  _//o\\|/7/o\\ §7|/_|/o\\/o/o\\",
                "§3|_|  \\_/L|\\\\(§7|_|L| \\_,]_\\( ",
                "§3Poke§7Trade:§3Plug in loaded!(插件已载入!)"
        });
        new Metrics(this, 15763);
    }
}
