package com.sainttx.blocksyntax;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockSyntax extends JavaPlugin implements Listener {

    private String invalidCommandMessage;
    private Set<String> whitelistedPlugins = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        whitelistedPlugins.addAll(getConfig().getStringList("whitelistedPlugins"));
        this.invalidCommandMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("invalidCommandMessage"));
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Pattern p = Pattern.compile("^/([a-zA-Z0-9_]+):");
        Matcher m = p.matcher(e.getMessage());
        String pluginRef;

        if (!m.find()) {
            return;
        }

        pluginRef = m.group(1);

        // Plugin might be white-listed
        if (whitelistedPlugins.contains(pluginRef)) {
            return;
        }

        // Quick check for Bukkit or Minecraft commands
        if (pluginRef.equalsIgnoreCase("bukkit") || pluginRef.equalsIgnoreCase("minecraft")) {
            e.getPlayer().sendMessage(this.invalidCommandMessage);
            e.setCancelled(true);
        } else {
            // Check all plugins
            for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
                if (plugin.getName().equalsIgnoreCase(pluginRef)) {
                    e.getPlayer().sendMessage(this.invalidCommandMessage);
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}