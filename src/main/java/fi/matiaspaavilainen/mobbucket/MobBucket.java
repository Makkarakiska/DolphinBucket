package fi.matiaspaavilainen.mobbucket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MobBucket extends JavaPlugin implements Listener {

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
        checkEntity(p.getInventory().getItemInMainHand(), e.getRightClicked());
        if (p.getInventory().getItemInMainHand().equals(new ItemStack(Material.WATER_BUCKET)) && e.getRightClicked() instanceof Dolphin) {
            // Remove the dolphin
            Dolphin dolphin = (Dolphin) e.getRightClicked();
            dolphin.remove();

            // Create hidden meta tag, so we know the bucket is used.

            createBucket(p.getInventory().getItemInMainHand(), e.getRightClicked().getName());

            // Sends message
            sendMessage(p, getConfig().getString("caught").replace("%entity%", e.getRightClicked().getName()));

            //Prevent for respawning
            cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 200);
        }
        if (p.getInventory().getItemInMainHand().equals(createBucket(new ItemStack(Material.WATER_BUCKET), e.getRightClicked().getName()))) {
            if (!cooldown.containsKey(p.getUniqueId()) || (cooldown.containsKey(p.getUniqueId()) && cooldown.get(p.getUniqueId()) < System.currentTimeMillis())) {

            }
        }
    }
    /*@EventHandler
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
                        if (bucket.equals(createBucket(new ItemStack(Material.WATER_BUCKET), e.tar))) {
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
    }*/

        // Cleaner code
        private void sendMessage (Player p, String msg){
            if (getConfig().getBoolean("messages")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }


        private boolean checkBucket (List < String > lore, Entity entity){


            // Check if the bucket has hidden item meta
            if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                // If hidden meta equals mob's name
                return lore.get(0).equalsIgnoreCase(entity.getName());
            }
            return false;
        }

        private void checkEntity (ItemStack bucket, Entity entity){
            switch (entity.getName()) {
                case ("Dolphin"):
                    controlBucket(bucket, entity);
                    System.out.println(entity.getName());
                    break;
                case ("Squid"):
                    System.out.println(entity.getName());
                    break;
                case ("Turtle"):
                    System.out.println(entity.getName());
                    break;
                case ("Elder Guardian"):
                    System.out.println(entity.getName());
                    break;
                case ("Guardian"):
                    System.out.println(entity.getName());
                    break;
                case ("Magma Cube"):
                    System.out.println(entity.getName());
                    break;
            }
        }


        private ItemStack createBucket (ItemStack bucket, String mob){
            List<String> lore = new ArrayList<>();
            lore.add(HiddenStringUtils.encodeString(mob));

            // And set the meta back to bucket
            ItemMeta meta = bucket.getItemMeta();
            meta.setLore(lore);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("bucket-name." + mob)));
            bucket.setItemMeta(meta);
            return bucket;
        }

        // Cleaner code
        private ItemStack controlBucket (ItemStack bucket, Entity entity){
            ItemMeta meta = bucket.getItemMeta();
            List<String> lore = meta.getLore();

            if (checkBucket(lore, entity)) {
                lore = new ArrayList<>();
                meta.setDisplayName(null);
            } else {
                entity.remove();
                lore = new ArrayList<>();
                lore.add(HiddenStringUtils.encodeString(entity.getName().toLowerCase()));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("bucket-name." + entity.getName().toLowerCase())));
            }

            // And set the meta back to bucket
            meta.setLore(lore);

            bucket.setItemMeta(meta);
            return bucket;
        }

    }
