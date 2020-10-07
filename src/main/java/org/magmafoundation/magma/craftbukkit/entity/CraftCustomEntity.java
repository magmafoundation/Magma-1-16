package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * CraftCustomEntity
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 07/10/2020 - 07:21 am
 */
public class CraftCustomEntity extends CraftEntity {

    private String entityName;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
        if (entityName == null) {
            entityName = entity.getName().getString();
        }
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    @Override
    public @NotNull EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        return type != null ? type : EntityType.UNKNOWN;
    }

    @Override
    public String toString() {
        return entityName;
    }

    @Override
    public String getCustomName() {
        String name = this.getHandle().getCustomName().getString();
        return name == null || name.length() == 0 ? this.entity.getName().getString() : name;
    }
}
