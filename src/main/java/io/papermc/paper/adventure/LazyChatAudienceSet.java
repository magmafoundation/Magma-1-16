package io.papermc.paper.adventure;

import java.util.HashSet;
import java.util.Set;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.util.LazyHashSet;
import org.bukkit.craftbukkit.v1_16_R3.util.LazyPlayerSet;
import org.bukkit.entity.Player;

import net.minecraft.server.MinecraftServer;

final class LazyChatAudienceSet extends LazyHashSet<Audience> {
    @Override
    protected Set<Audience> makeReference() {
        final Set<Player> playerSet = LazyPlayerSet.makePlayerSet(MinecraftServer.getServer());
        final HashSet<Audience> audiences = new HashSet<>(playerSet);
        audiences.add(Bukkit.getConsoleSender());
        return audiences;
    }
}
