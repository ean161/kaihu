package vn.ean.kaihu;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin implements Listener {
    Kaihu kaihu = new Kaihu();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(kaihu, this);
        getLogger().info("Kaihu enabled, developed by ean (nghoaian161@gmail.com)");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kaihu disabled");
    }
}
