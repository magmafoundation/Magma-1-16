package org.spigotmc;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.magmafoundation.magma.util.TPSTracker;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.server.ServerWorld;

public class TicksPerSecondCommand extends Command {

    private boolean hasShownMemoryWarning; // Paper

    public TicksPerSecondCommand(String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        World currentWorld = null;
        if (sender instanceof CraftPlayer) {
            currentWorld = ((CraftPlayer) sender).getWorld();
        }
        sender.sendMessage(String
                .format("%s%s%s-----------%s%s%s<%s%s Worlds %s%s%s>%s%s%s-----------", ChatColor.GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.DARK_GRAY, ChatColor.BOLD,
                        ChatColor.STRIKETHROUGH, ChatColor.GREEN, ChatColor.ITALIC, ChatColor.DARK_GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH));
        final MinecraftServer server = MinecraftServer.getServerInstance();
        ChatColor colourTPS;
        for (World world : server.server.getWorlds()) {
            if (world instanceof CraftWorld) {
                boolean current = currentWorld != null && currentWorld == world;
                ServerWorld mcWorld = ((CraftWorld) world).getHandle();
                RegistryKey<net.minecraft.world.World> dimension = mcWorld.dimension();
                String dimensionName = dimension.location().toString();
                String bukkitName = world.getName();
                String name = dimension.location().toString();
                String displayName = name.equals(bukkitName) ? name : String.format("%s | %s", name, bukkitName);

                long[] times = server.getTickTime(mcWorld.dimension());
                double worldTickTime = mean(times) * 1.0E-6D;
                double worldTPS = Math.min(1000.0 / worldTickTime, 20);

                if (worldTPS >= 18.0) {
                    colourTPS = ChatColor.GREEN;
                } else if (worldTPS >= 15.0) {
                    colourTPS = ChatColor.YELLOW;
                } else {
                    colourTPS = ChatColor.RED;
                }

                sender.sendMessage(String.format("%s[%s] %s%s %s- %s%.2fms / %s%.2ftps", ChatColor.GOLD, dimensionName,
                        current ? ChatColor.GREEN : ChatColor.YELLOW, displayName, ChatColor.RESET,
                        ChatColor.YELLOW, worldTickTime, colourTPS, worldTPS));
            }
        }

        double meanTickTime = mean(server.tickTimes) * 1.0E-6D;
        double meanTPS = Math.min(1000.0 / meanTickTime, 20);
        if (meanTPS >= 18.0) {
            colourTPS = ChatColor.GREEN;
        } else if (meanTPS >= 15.0) {
            colourTPS = ChatColor.YELLOW;
        } else {
            colourTPS = ChatColor.RED;
        }

        // Paper start - Further improve tick handling
        double[] tps = org.bukkit.Bukkit.getTPS();
        String[] tpsAvg = new String[tps.length];
        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i]);
        }
        sender.sendMessage(String.format("%s%sTPS from last 1m, 5m, 15m: %s%s", ChatColor.WHITE, ChatColor.BOLD, ChatColor.YELLOW, org.apache.commons.lang.StringUtils.join(tpsAvg, ", ")));
        if (args.length > 0 && args[0].equals("mem") && sender.hasPermission("bukkit.command.tpsmemory")) {
            sender.sendMessage(ChatColor.GOLD + "Current Memory Usage: " + ChatColor.GREEN + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "/" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " mb (Max: " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " mb)");
            if (!hasShownMemoryWarning) {
                sender.sendMessage(ChatColor.RED + "Warning: " + ChatColor.GOLD + " Memory usage on modern garbage collectors is not a stable value and it is perfectly normal to see it reach max. Please do not pay it much attention.");
                hasShownMemoryWarning = true;
            }
        }
        sender.sendMessage(String.format("%s%sOverall: %s%s%.2fms / %s%.2ftps", ChatColor.WHITE, ChatColor.BOLD, ChatColor.RESET, ChatColor.YELLOW, meanTickTime, colourTPS, meanTPS));
        sender.sendMessage(String.format("%s%s%s-----------%s%s%s<%s%s TPS Graph (48 Seconds) %s%s%s>%s%s%s-----------", ChatColor.GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.DARK_GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.GREEN, ChatColor.ITALIC, ChatColor.DARK_GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.GRAY, ChatColor.BOLD, ChatColor.STRIKETHROUGH));
        if (!TPSTracker.lines.isEmpty()) {
            TPSTracker.lines.forEach(sender::sendMessage);
        }
        String status = ChatColor.GRAY + "Unknown";
        try {
            final double currentTPS = (double) Double.valueOf(DedicatedServer.TPS);
            if (currentTPS >= 17.0) {
                status = ChatColor.GREEN + "STABLE";
            } else if (currentTPS >= 15.0) {
                status = ChatColor.YELLOW + "SOME STABILITY ISSUES";
            } else if (currentTPS >= 10.0) {
                status = ChatColor.RED + "LAGGING. CHECK TIMINGS.";
            } else if (currentTPS < 10.0) {
                status = ChatColor.DARK_RED + "UNSTABLE";
            } else if (currentTPS < 3.0) {
                status = ChatColor.RED + "SEND HELP!!!!!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sender.sendMessage(String.format("%s%sServer Status: %s", ChatColor.WHITE, ChatColor.BOLD, status));
        return true;
    }

    private static String format(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString()
                + ((tps > 21.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0); // Paper - only print * at 21, we commonly peak to 20.02 as the tick sleep is not accurate enough, stop the noise
    }

    private static final long mean(long[] array) {
        if (array == null) return -1;
        long r = 0;
        for (long i : array) {
            r += i;
        }
        return r / array.length;
    }

}
