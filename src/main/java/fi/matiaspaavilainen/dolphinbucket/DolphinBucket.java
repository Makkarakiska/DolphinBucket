package fi.matiaspaavilainen.dolphinbucket;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DolphinBucket extends JavaPlugin implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        // If the item is bucket and the target is Dolphin
        if (p.getInventory().getItemInMainHand().equals(new ItemStack(Material.WATER_BUCKET)) && e.getRightClicked() instanceof Dolphin) {

            // Remove the dolphin
            Dolphin dolphin = (Dolphin) e.getRightClicked();
            dolphin.remove();

            // Create hidden meta tag, so we know the bucket is used.
            createBucket(p.getInventory().getItemInMainHand());

            // Sends message
            sendMessage(p, getConfig().getString("caught"));

            //Prevent for respawning
            cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 200);
        }
    }

    @EventHandler
    public void emptyBucket(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            List<Block> lineOfSight = p.getLineOfSight(null, 1);
            for (Block b : lineOfSight) {
                // If clicked block is water
                if (b.getType() == Material.WATER) {
                    // If player doesn't have that little cooldown
                    if (!cooldown.containsKey(p.getUniqueId()) || (cooldown.containsKey(p.getUniqueId()) && cooldown.get(p.getUniqueId()) < System.currentTimeMillis())) {

                        // Remove player from cooldown list, maybe not necessary(?)
                        cooldown.remove(p.getUniqueId());

                        // Get used item
                        ItemStack bucket = p.getInventory().getItemInMainHand();

                        // If item is similar than bucket with hidden item meta
                        if (bucket.equals(createBucket(new ItemStack(Material.WATER_BUCKET)))) {
                            ItemMeta meta = bucket.getItemMeta();
                            List<String> dolphinLore = meta.getLore();

                            // Check if the bucket has hidden item meta
                            if (dolphinLore != null && dolphinLore.size() > 0 && HiddenStringUtils.hasHiddenString(dolphinLore.get(0))) {

                                // Read it
                                String dolphinMeta = HiddenStringUtils.extractHiddenString(dolphinLore.get(0));
                                if (dolphinMeta.equals("dolphin")) {

                                    // Spawn the dolphin
                                    Block block = e.getClickedBlock();
                                    Location loc = new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ());
                                    p.getWorld().spawnEntity(loc, EntityType.DOLPHIN);

                                    // Reset bucket data to default
                                    List<String> lore = new ArrayList<>();
                                    meta.setDisplayName(null);
                                    meta.setLore(lore);
                                    bucket.setItemMeta(meta);

                                    // Sends message
                                    sendMessage(p, getConfig().getString("released"));
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    // Cleaner code
    private void sendMessage(Player p, String msg) {
        if (getConfig().getBoolean("messages")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }


    // Cleaner code
    private ItemStack createBucket(ItemStack bucket) {
        List<String> lore = new ArrayList<>();
        lore.add(HiddenStringUtils.encodeString("dolphin"));

        // And set the meta back to bucket
        ItemMeta meta = bucket.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("bucket-name")));
        bucket.setItemMeta(meta);
        return bucket;
    }
}
