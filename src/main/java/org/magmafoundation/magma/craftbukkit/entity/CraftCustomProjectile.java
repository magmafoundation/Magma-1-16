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

