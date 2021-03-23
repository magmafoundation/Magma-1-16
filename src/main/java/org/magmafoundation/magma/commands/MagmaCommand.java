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

import java.io.*;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
            case "dump":
                createMagmaDump("permissions.mdump");
                createMagmaDump("materials.mdump");
                createMagmaDump("biomes.mdump");
                sender.sendMessage(ChatColor.RED + "Dump saved!");
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
            return ImmutableList.of("mods", "playermods", "dump");
        }
        return Collections.emptyList();
    }

    private void createMagmaDump(String fileName) {
        try {

            File dumpFolder = new File("dump");
            dumpFolder.mkdirs();
            File dumpFile = new File(dumpFolder, fileName);
            OutputStream os = new FileOutputStream(dumpFile);
            Writer writer = new OutputStreamWriter(os);

            switch (fileName.split("\\.")[0]) {
                // TODO: Re-add worlds and entites
                case "permissions":

                    for (Command command : MinecraftServer.getServerInstance().server.getCommandMap().getCommands()) {
                        if (command.getPermission() == null) {
                            continue;
                        }
                        writer.write(command.getName() + ": " + command.getPermission() + "\n");
                    }

                    writer.close();
                case "materials":

                    for (Material material : Material.values()) {
                        writer.write(material.name() + "\n");
                    }

                    writer.close();
                case "biomes":

                    for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                        writer.write(biome.getRegistryName() + "\n");
                    }

                    writer.close();
            }
        } catch (Exception e) {

        }


    }

}
