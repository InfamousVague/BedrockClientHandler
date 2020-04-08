package com.github.retropronghorn.bedrockhandler;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedrockHandler extends JavaPlugin implements Listener {
    String pluginPrefix = this.getConfig().getString("plugin-prefix");

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent event) {
        String bedrockGroupName = this.getConfig().getString("luckperms-groupname");
        String bedrockPrefix = this.getConfig().getString("bedrock-prefix");

        Player player = event.getPlayer();
        String displayName = player.getDisplayName();

        assert bedrockPrefix != null;
        if (displayName.contains(bedrockPrefix)) {
            getServer().getConsoleSender().sendMessage(
                    ChatColor.GREEN + "["+ pluginPrefix +"]: " + ChatColor.GRAY + "Bedrock client connected: " + player.getUniqueId());

            // LuckPerms Handler
            notifyUser(player);
            setLuckPermsGroup(player, bedrockGroupName);
            cleanUser(player, bedrockPrefix);
        }
        // TODO Add Tebex.io hook to remove username prefix and apply packages correctly

    }

    // Remove prefixes, and other cleanup tasks
    private void cleanUser(Player player, String prefix) {
        // Remove prefix now that we have a LuckPerms group
        player.setDisplayName(player.getDisplayName().substring(prefix.length()));
    }

    // Let the player know they are connecting with a partially supported version.
    private void notifyUser(Player player) {
        player.sendTitle(
                "Alpha Warning!",
                "Heads up, " + player.getDisplayName() + " bedrock is not complete.",
                10,
                70,
                10);
    }

    // Set users LuckPerms group
    private void setLuckPermsGroup(Player player, String groupName) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            LuckPerms api = provider.getProvider();
            User user = api.getUserManager().getUser(player.getUniqueId());
            assert user != null;

            String currentLuckPermsGroup = user.getPrimaryGroup();

            if (!currentLuckPermsGroup.equals(groupName)) {
                DataMutateResult result = user.data().add(Node.builder("group." + groupName).build());

                System.out.println(result);
                api.getUserManager().saveUser(user);
                System.out.println(user.getPrimaryGroup());
            }
        }
    }
}
