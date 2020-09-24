package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class PluginsCommand extends BukkitCommand {
    public PluginsCommand(@NotNull String name) {
        super(name);
        this.description = "Gets a list of plugins running on the server";
        this.usageMessage = "/plugins";
        this.setPermission("bukkit.command.plugins");
        this.setAliases(Arrays.asList("pl"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) {
        if (!testPermission(sender)) return true;

        // Spigot start
        if (sender instanceof Player && sender.hasPermission("bukkit.command.version")) {
            sender.spigot().sendMessage(getPluginListSpigot());
        } else {
            sender.sendMessage("Plugins " + getPluginList());
        }
        // Spigot end
        return true;
    }

    // Spigot start
    @NotNull
    private BaseComponent[] getPluginListSpigot() {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        ComponentBuilder pluginList = new ComponentBuilder("Plugins (" + plugins.length + "): ");
        int index = 0;
        for (Plugin plugin : plugins) {
            if (index++ > 0) {
                pluginList.append(", ", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE);
            }
            // Event components
            PluginDescriptionFile description = plugin.getDescription();
            ComponentBuilder hoverEventComponents = new ComponentBuilder();
            hoverEventComponents.append("Version: ").color(net.md_5.bungee.api.ChatColor.WHITE).append(description.getVersion()).color(net.md_5.bungee.api.ChatColor.GREEN);
            if (description.getDescription() != null) {
                hoverEventComponents.append("\nDescription: ").color(net.md_5.bungee.api.ChatColor.WHITE).append(description.getDescription()).color(net.md_5.bungee.api.ChatColor.GREEN);
            }
            if (description.getWebsite() != null) {
                hoverEventComponents.append("\nWebsite: ").color(net.md_5.bungee.api.ChatColor.WHITE).append(description.getWebsite()).color(net.md_5.bungee.api.ChatColor.GREEN);
            }
            if (!description.getAuthors().isEmpty()) {
                if (description.getAuthors().size() == 1) {
                    hoverEventComponents.append("\nAuthor: ");
                } else {
                    hoverEventComponents.append("\nAuthors: ");
                }
                hoverEventComponents.color(net.md_5.bungee.api.ChatColor.WHITE).append(getAuthors(description));
            }
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents.create());
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/version " + description.getName());
            // Plugin list entry
            pluginList.append(plugin.getDescription().getName());
            pluginList.color(plugin.isEnabled() ? net.md_5.bungee.api.ChatColor.GREEN : net.md_5.bungee.api.ChatColor.RED);
            pluginList.event(hoverEvent).event(clickEvent);
            if (plugin.getDescription().getProvides().size() > 0) {
                pluginList.append("( ", FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).append(String.join(", ", plugin.getDescription().getProvides())).append(")");
            }
        }
        return pluginList.create();
    }

    @NotNull
    private BaseComponent[] getAuthors(@NotNull final PluginDescriptionFile description) {
        ComponentBuilder result = new ComponentBuilder();
        List<String> authors = description.getAuthors();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                result.append(i < authors.size() - 1 ? ", " : " and ", FormatRetention.NONE);
                result.color(net.md_5.bungee.api.ChatColor.WHITE);
            }
            result.append(authors.get(i)).color(net.md_5.bungee.api.ChatColor.GREEN);
        }
        return result.create();
    }
    // Spigot end

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    @NotNull
    private String getPluginList() {
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            pluginList.append(plugin.getDescription().getName());

            if (plugin.getDescription().getProvides().size() > 0) {
                pluginList.append(" (").append(String.join(", ", plugin.getDescription().getProvides())).append(")");
            }
        }

        return "(" + plugins.length + "): " + pluginList.toString();
    }
}
