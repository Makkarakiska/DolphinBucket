package fi.matiaspaavilainen.dolphinbucket;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DolphinBucket extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
    }
}
