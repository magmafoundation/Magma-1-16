package org.bukkit.craftbukkit.tag;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class CraftFluidTag extends CraftTag<Fluid, org.bukkit.Fluid> {

    public CraftFluidTag(ITagCollection<Fluid> registry, ResourceLocation tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(org.bukkit.@NotNull Fluid fluid) {
        return getHandle().contains(CraftMagicNumbers.getFluid(fluid));

    }

    @Override
    public Set<org.bukkit.Fluid> getValues() {
        return Collections.unmodifiableSet(getHandle().getAllElements().stream().map(CraftMagicNumbers::getFluid).collect(Collectors.toSet()));
    }
}
