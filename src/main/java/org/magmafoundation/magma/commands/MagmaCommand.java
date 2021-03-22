/*
 * Magma Server
 * Copyright (C) 2019-2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.commands;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.api.PlayerAPI;
import org.magmafoundation.magma.api.ServerAPI;

/**
 * MagmaCommand
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/10/2020 - 04:58 am
 */
public class MagmaCommand extends Command {

    public MagmaCommand(@NotNull String name) {
        super(name);

        this.description = "Magma commands";
        this.usageMessage = "/magma [mods|playermods]";
        this.setPermission("magma.commands.magma");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("magma.commands.magma")) {
            sender.sendMessage(ChatColor.RED + "You don't got the permission to execute this command!");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "mods":
                sender.sendMessage(ChatColor.GREEN + "" + ServerAPI.getModSize() + " " + ServerAPI.getModList());
                break;
            case "playermods":
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                    return false;
                }

                Player player = Bukkit.getPlayer(args[1].toString());
                if (player != null) {
                    sender.sendMessage(ChatColor.GREEN + "" + PlayerAPI.getModSize(player) + " " + PlayerAPI.getModlist(player));
                } else {
                    sender.sendMessage(ChatColor.RED + "The player [" + args[1] + "] is not online.");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        if (args.length <= 1) {
            return ImmutableList.of("mods", "playermods");
        }
        return Collections.emptyList();
    }

}
