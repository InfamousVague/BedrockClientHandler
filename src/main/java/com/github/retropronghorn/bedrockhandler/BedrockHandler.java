package com.github.retropronghorn.bedrockhandler;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedrockHandler extends JavaPlugin implements Listener {
    // TODO add this to config
    String bedrockGroupName = "bedrock";
    String bedrockPrefix = "Retro";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String displayName = player.getDisplayName();

        if (displayName.contains(bedrockPrefix)) {
            // LuckPerms Handler
            notifyUser(player);
            setLuckPermsGroup(player);
            cleanUser(player);
        }
        // TODO Add Tebex.io hook to remove username prefix and apply packages correctly

    }

    // Remove prefixes, and other cleanup tasks
    private void cleanUser(Player player) {
        // Remove prefix now that we have a LuckPerms group
        player.setDisplayName(player.getDisplayName().substring(bedrockPrefix.length()));
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
    private void setLuckPermsGroup(Player player) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            LuckPerms api = provider.getProvider();
            User user = api.getUserManager().getUser(player.getUniqueId());

            assert user != null;

            String currentLuckPermsGroup = user.getPrimaryGroup();
            System.out.println(!currentLuckPermsGroup.equals(bedrockGroupName));

            if (!currentLuckPermsGroup.equals(bedrockGroupName)) {
                System.out.println(user.setPrimaryGroup("group." + bedrockGroupName));
                api.getUserManager().saveUser(user);
                System.out.println(user.getPrimaryGroup());
            }
        }
    }
}
