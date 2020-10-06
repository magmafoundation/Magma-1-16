package org.bukkit.command.defaults;

import java.util.Collections;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.magmafoundation.magma.Magma;

/**
 * VersionCommand
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 04:57 am
 */
public class VersionCommand extends Command {

    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of the server";
        this.usageMessage = "/version";
        this.setAliases(Collections.singletonList("ver"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(TextFormatting.RED + "You don't have permission to do that!");
            return false;
        }

        sender.sendMessage("This server is running " + Bukkit.getName() + " version " + Magma.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge Version " + ForgeVersion.getVersion() + ")");
        return true;
    }
}
