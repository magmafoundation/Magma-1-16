package org.magmafoundation.magma.custom;

import net.minecraft.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * CraftCustomEntity
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 03/10/2020 - 09:13 pm
 */
public class CraftCustomEntity extends CraftEntity {

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
    }

    @Override
    public @NotNull EntityType getType() {
        return null;
    }
}
