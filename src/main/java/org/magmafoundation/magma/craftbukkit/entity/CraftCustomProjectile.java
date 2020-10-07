package org.magmafoundation.magma.craftbukkit.entity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

/**
 * CraftCustomProjectile
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 07/10/2020 - 07:30 am
 */
public class CraftCustomProjectile extends CraftCustomEntity implements Projectile {

    private ProjectileSource shooter = null;
    private boolean doesBounce;
    public static final GameProfile dropper = new GameProfile(UUID.nameUUIDFromBytes("[Dropper]".getBytes()), "[Dropper]");

    public CraftCustomProjectile(CraftServer server, Entity entity) {
        super(server, entity);
    }

    @Override
    public @Nullable ProjectileSource getShooter() {
        return shooter;
    }

    @Override
    public void setShooter(@Nullable ProjectileSource source) {
        this.shooter = source;
    }

    @Override
    public boolean doesBounce() {
        return doesBounce;
    }

    @Override
    public void setBounce(boolean doesBounce) {
        this.doesBounce = doesBounce;
    }

    @Override
    public String toString() {
        return "CraftCustomProjectile";
    }
}

