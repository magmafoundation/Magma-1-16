package org.bukkit.craftbukkit.block.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.registry.Registry;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class CraftBlockData implements BlockData {

    private BlockState state;
    private Map<Property<?>, Comparable<?>> parsedStates;

    protected CraftBlockData() {
        throw new AssertionError("Template Constructor");
    }

    protected CraftBlockData(BlockState state) {
        this.state = state;
    }

    @Override
    public Material getMaterial() {
        return CraftMagicNumbers.getMaterial(state.getBlock());
    }

    public BlockState getState() {
        return state;
    }

    /**
     * Get a given EnumProperty's value as its Bukkit counterpart.
     *
     * @param nms the NMS state to convert
     * @param bukkit the Bukkit class
     * @param <B> the type
     * @return the matching Bukkit type
     */
    protected <B extends Enum<B>> B get(EnumProperty<?> nms, Class<B> bukkit) {
        return toBukkit(state.get(nms), bukkit);
    }

    /**
     * Convert all values from the given EnumProperty to their appropriate
     * Bukkit counterpart.
     *
     * @param nms the NMS state to get values from
     * @param bukkit the bukkit class to convert the values to
     * @param <B> the bukkit class type
     * @return an immutable Set of values in their appropriate Bukkit type
     */
    @SuppressWarnings("unchecked")
    protected <B extends Enum<B>> Set<B> getValues(EnumProperty<?> nms, Class<B> bukkit) {
        ImmutableSet.Builder<B> values = ImmutableSet.builder();

        for (Enum<?> e : nms.getAllowedValues()) {
            values.add(toBukkit(e, bukkit));
        }

        return values.build();
    }

    /**
     * Set a given {@link EnumProperty} with the matching enum from Bukkit.
     *
     * @param nms the NMS EnumProperty to set
     * @param bukkit the matching Bukkit Enum
     * @param <B> the Bukkit type
     * @param <N> the NMS type
     */
    protected <B extends Enum<B>, N extends Enum<N> & IStringSerializable> void set(EnumProperty<N> nms, Enum<B> bukkit) {
        this.parsedStates = null;
        this.state = this.state.with(nms, toNMS(bukkit, nms.getValueClass()));
    }

    @Override
    public BlockData merge(BlockData data) {
        CraftBlockData craft = (CraftBlockData) data;
        Preconditions.checkArgument(craft.parsedStates != null, "Data not created via string parsing");
        Preconditions.checkArgument(this.state.getBlock() == craft.state.getBlock(), "States have different types (got %s, expected %s)", data, this);

        CraftBlockData clone = (CraftBlockData) this.clone();
        clone.parsedStates = null;

        for (Property parsed : craft.parsedStates.keySet()) {
            clone.state = clone.state.with(parsed, craft.state.get(parsed));
        }

        return clone;
    }

    @Override
    public boolean matches(BlockData data) {
        if (data == null) {
            return false;
        }
        if (!(data instanceof CraftBlockData)) {
            return false;
        }

        CraftBlockData craft = (CraftBlockData) data;
        if (this.state.getBlock() != craft.state.getBlock()) {
            return false;
        }

        // Fastpath an exact match
        boolean exactMatch = this.equals(data);

        // If that failed, do a merge and check
        if (!exactMatch && craft.parsedStates != null) {
            return this.merge(data).equals(this);
        }

        return exactMatch;
    }

    private static final Map<Class, BiMap<Enum<?>, Enum<?>>> classMappings = new HashMap<>();

    /**
     * Convert an NMS Enum (usually a EnumProperty) to its appropriate Bukkit
     * enum from the given class.
     *
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    private static <B extends Enum<B>> B toBukkit(Enum<?> nms, Class<B> bukkit) {
        Enum<?> converted;
        BiMap<Enum<?>, Enum<?>> nmsToBukkit = classMappings.get(nms.getClass());

        if (nmsToBukkit != null) {
            converted = nmsToBukkit.get(nms);
            if (converted != null) {
                return (B) converted;
            }
        }

        if (nms instanceof Direction) {
            converted = CraftBlock.notchToBlockFace((Direction) nms);
        } else {
            converted = bukkit.getEnumConstants()[nms.ordinal()];
        }

        Preconditions.checkState(converted != null, "Could not convert enum %s->%s", nms, bukkit);

        if (nmsToBukkit == null) {
            nmsToBukkit = HashBiMap.create();
            classMappings.put(nms.getClass(), nmsToBukkit);
        }

        nmsToBukkit.put(nms, converted);

        return (B) converted;
    }

    /**
     * Convert a given Bukkit enum to its matching NMS enum type.
     *
     * @param bukkit the Bukkit enum to convert
     * @param nms the NMS class
     * @return the matching NMS type
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    private static <N extends Enum<N> & IStringSerializable> N toNMS(Enum<?> bukkit, Class<N> nms) {
        Enum<?> converted;
        BiMap<Enum<?>, Enum<?>> nmsToBukkit = classMappings.get(nms);

        if (nmsToBukkit != null) {
            converted = nmsToBukkit.inverse().get(bukkit);
            if (converted != null) {
                return (N) converted;
            }
        }

        if (bukkit instanceof BlockFace) {
            converted = CraftBlock.blockFaceToNotch((BlockFace) bukkit);
        } else {
            converted = nms.getEnumConstants()[bukkit.ordinal()];
        }

        Preconditions.checkState(converted != null, "Could not convert enum %s->%s", nms, bukkit);

        if (nmsToBukkit == null) {
            nmsToBukkit = HashBiMap.create();
            classMappings.put(nms, nmsToBukkit);
        }

        nmsToBukkit.put(converted, bukkit);

        return (N) converted;
    }

    /**
     * Get the current value of a given state.
     *
     * @param ibs the state to check
     * @param <T> the type
     * @return the current value of the given state
     */
    protected <T extends Comparable<T>> T get(Property<T> ibs) {
        // Straight integer or boolean getter
        return this.state.get(ibs);
    }

    /**
     * Set the specified state's value.
     *
     * @param ibs the state to set
     * @param v the new value
     * @param <T> the state's type
     * @param <V> the value's type. Must match the state's type.
     */
    public <T extends Comparable<T>, V extends T> void set(Property<T> ibs, V v) {
        // Straight integer or boolean setter
        this.parsedStates = null;
        this.state = this.state.with(ibs, v);
    }

    @Override
    public String getAsString() {
        return toString(((StateHolder) state).getValues());
    }

    @Override
    public String getAsString(boolean hideUnspecified) {
        return (hideUnspecified && parsedStates != null) ? toString(parsedStates) : getAsString();
    }

    @Override
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Clone not supported", ex);
        }
    }

    @Override
    public String toString() {
        return "CraftBlockData{" + getAsString() + "}";
    }

    // Mimicked from StateHolder#toString()
    public String toString(Map<Property<?>, Comparable<?>> states) {
        StringBuilder stateString = new StringBuilder(Registry.BLOCK.getKey(state.getBlock()).toString());

        if (!states.isEmpty()) {
            stateString.append('[');
            stateString.append(states.entrySet().stream().map(StateHolder.field_235890_a_).collect(Collectors.joining(",")));
            stateString.append(']');
        }

        return stateString.toString();
    }

    public CompoundNBT toStates() {
        CompoundNBT compound = new CompoundNBT();

        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            Property iblockstate = (Property) entry.getKey();

            compound.putString(iblockstate.getName(), iblockstate.getName(entry.getValue()));
        }

        return compound;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CraftBlockData && state.equals(((CraftBlockData) obj).state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    protected static BooleanProperty getBoolean(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(String name, boolean optional) {
        throw new AssertionError("Template Method");
    }

    protected static EnumProperty<?> getEnum(String name) {
        throw new AssertionError("Template Method");
    }

    protected static IntegerProperty getInteger(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name) {
        return (BooleanProperty) getState(block, name, false);
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name, boolean optional) {
        return (BooleanProperty) getState(block, name, optional);
    }

    protected static EnumProperty<?> getEnum(Class<? extends Block> block, String name) {
        return (EnumProperty<?>) getState(block, name, false);
    }

    protected static IntegerProperty getInteger(Class<? extends Block> block, String name) {
        return (IntegerProperty) getState(block, name, false);
    }

    /**
     * Get a specified {@link Property} from a given block's class with a
     * given name
     *
     * @param block the class to retrieve the state from
     * @param name the name of the state to retrieve
     * @param optional if the state can be null
     * @return the specified state or null
     * @throws IllegalStateException if the state is null and {@code optional}
     * is false.
     */
    private static Property<?> getState(Class<? extends Block> block, String name, boolean optional) {
        Property<?> state = null;

        for (Block instance : Registry.BLOCK) {
            if (instance.getClass() == block) {
                if (state == null) {
                    state = instance.getStateContainer().getProperty(name);
                } else {
                    Property<?> newState = instance.getStateContainer().getProperty(name);

                    Preconditions.checkState(state == newState, "State mistmatch %s,%s", state, newState);
                }
            }
        }

        Preconditions.checkState(optional || state != null, "Null state for %s,%s", block, name);

        return state;
    }

    /**
     * Get the minimum value allowed by the IntegerProperty.
     *
     * @param state the state to check
     * @return the minimum value allowed
     */
    protected static int getMin(IntegerProperty state) {
        return state.min;
    }

    /**
     * Get the maximum value allowed by the IntegerProperty.
     *
     * @param state the state to check
     * @return the maximum value allowed
     */
    protected static int getMax(IntegerProperty state) {
        return state.max;
    }

    //
    private static final Map<Class<? extends Block>, Function<BlockState, CraftBlockData>> MAP = new HashMap<>();

    static {
        //<editor-fold desc="CraftBlockData Registration" defaultstate="collapsed">
        /* // TODO: 26/06/2020 Come back and add this
        register(net.minecraft.server.BlockAnvil.class, org.bukkit.craftbukkit.block.impl.CraftAnvil::new);
        register(net.minecraft.server.BlockBamboo.class, org.bukkit.craftbukkit.block.impl.CraftBamboo::new);
        register(net.minecraft.server.BlockBanner.class, org.bukkit.craftbukkit.block.impl.CraftBanner::new);
        register(net.minecraft.server.BlockBannerWall.class, org.bukkit.craftbukkit.block.impl.CraftBannerWall::new);
        register(net.minecraft.server.BlockBarrel.class, org.bukkit.craftbukkit.block.impl.CraftBarrel::new);
        register(net.minecraft.server.BlockBed.class, org.bukkit.craftbukkit.block.impl.CraftBed::new);
        register(net.minecraft.server.BlockBeehive.class, org.bukkit.craftbukkit.block.impl.CraftBeehive::new);
        register(net.minecraft.server.BlockBeetroot.class, org.bukkit.craftbukkit.block.impl.CraftBeetroot::new);
        register(net.minecraft.server.BlockBell.class, org.bukkit.craftbukkit.block.impl.CraftBell::new);
        register(net.minecraft.server.BlockBlastFurnace.class, org.bukkit.craftbukkit.block.impl.CraftBlastFurnace::new);
        register(net.minecraft.server.BlockBrewingStand.class, org.bukkit.craftbukkit.block.impl.CraftBrewingStand::new);
        register(net.minecraft.server.BlockBubbleColumn.class, org.bukkit.craftbukkit.block.impl.CraftBubbleColumn::new);
        register(net.minecraft.server.BlockCactus.class, org.bukkit.craftbukkit.block.impl.CraftCactus::new);
        register(net.minecraft.server.BlockCake.class, org.bukkit.craftbukkit.block.impl.CraftCake::new);
        register(net.minecraft.server.BlockCampfire.class, org.bukkit.craftbukkit.block.impl.CraftCampfire::new);
        register(net.minecraft.server.BlockCarrots.class, org.bukkit.craftbukkit.block.impl.CraftCarrots::new);
        register(net.minecraft.server.BlockCauldron.class, org.bukkit.craftbukkit.block.impl.CraftCauldron::new);
        register(net.minecraft.server.BlockChain.class, org.bukkit.craftbukkit.block.impl.CraftChain::new);
        register(net.minecraft.server.BlockChest.class, org.bukkit.craftbukkit.block.impl.CraftChest::new);
        register(net.minecraft.server.BlockChestTrapped.class, org.bukkit.craftbukkit.block.impl.CraftChestTrapped::new);
        register(net.minecraft.server.BlockChorusFlower.class, org.bukkit.craftbukkit.block.impl.CraftChorusFlower::new);
        register(net.minecraft.server.BlockChorusFruit.class, org.bukkit.craftbukkit.block.impl.CraftChorusFruit::new);
        register(net.minecraft.server.BlockCobbleWall.class, org.bukkit.craftbukkit.block.impl.CraftCobbleWall::new);
        register(net.minecraft.server.BlockCocoa.class, org.bukkit.craftbukkit.block.impl.CraftCocoa::new);
        register(net.minecraft.server.BlockCommand.class, org.bukkit.craftbukkit.block.impl.CraftCommand::new);
        register(net.minecraft.server.BlockComposter.class, org.bukkit.craftbukkit.block.impl.CraftComposter::new);
        register(net.minecraft.server.BlockConduit.class, org.bukkit.craftbukkit.block.impl.CraftConduit::new);
        register(net.minecraft.server.BlockCoralDead.class, org.bukkit.craftbukkit.block.impl.CraftCoralDead::new);
        register(net.minecraft.server.BlockCoralFan.class, org.bukkit.craftbukkit.block.impl.CraftCoralFan::new);
        register(net.minecraft.server.BlockCoralFanAbstract.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanAbstract::new);
        register(net.minecraft.server.BlockCoralFanWall.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanWall::new);
        register(net.minecraft.server.BlockCoralFanWallAbstract.class, org.bukkit.craftbukkit.block.impl.CraftCoralFanWallAbstract::new);
        register(net.minecraft.server.BlockCoralPlant.class, org.bukkit.craftbukkit.block.impl.CraftCoralPlant::new);
        register(net.minecraft.server.BlockCrops.class, org.bukkit.craftbukkit.block.impl.CraftCrops::new);
        register(net.minecraft.server.BlockDaylightDetector.class, org.bukkit.craftbukkit.block.impl.CraftDaylightDetector::new);
        register(net.minecraft.server.BlockDirtSnow.class, org.bukkit.craftbukkit.block.impl.CraftDirtSnow::new);
        register(net.minecraft.server.BlockDispenser.class, org.bukkit.craftbukkit.block.impl.CraftDispenser::new);
        register(net.minecraft.server.BlockDoor.class, org.bukkit.craftbukkit.block.impl.CraftDoor::new);
        register(net.minecraft.server.BlockDropper.class, org.bukkit.craftbukkit.block.impl.CraftDropper::new);
        register(net.minecraft.server.BlockEndRod.class, org.bukkit.craftbukkit.block.impl.CraftEndRod::new);
        register(net.minecraft.server.BlockEnderChest.class, org.bukkit.craftbukkit.block.impl.CraftEnderChest::new);
        register(net.minecraft.server.BlockEnderPortalFrame.class, org.bukkit.craftbukkit.block.impl.CraftEnderPortalFrame::new);
        register(net.minecraft.server.BlockFence.class, org.bukkit.craftbukkit.block.impl.CraftFence::new);
        register(net.minecraft.server.BlockFenceGate.class, org.bukkit.craftbukkit.block.impl.CraftFenceGate::new);
        register(net.minecraft.server.BlockFire.class, org.bukkit.craftbukkit.block.impl.CraftFire::new);
        register(net.minecraft.server.BlockFloorSign.class, org.bukkit.craftbukkit.block.impl.CraftFloorSign::new);
        register(net.minecraft.server.BlockFluids.class, org.bukkit.craftbukkit.block.impl.CraftFluids::new);
        register(net.minecraft.server.BlockFurnaceFurace.class, org.bukkit.craftbukkit.block.impl.CraftFurnaceFurace::new);
        register(net.minecraft.server.BlockGlazedTerracotta.class, org.bukkit.craftbukkit.block.impl.CraftGlazedTerracotta::new);
        register(net.minecraft.server.BlockGrass.class, org.bukkit.craftbukkit.block.impl.CraftGrass::new);
        register(net.minecraft.server.BlockGrindstone.class, org.bukkit.craftbukkit.block.impl.CraftGrindstone::new);
        register(net.minecraft.server.BlockHay.class, org.bukkit.craftbukkit.block.impl.CraftHay::new);
        register(net.minecraft.server.BlockHopper.class, org.bukkit.craftbukkit.block.impl.CraftHopper::new);
        register(net.minecraft.server.BlockHugeMushroom.class, org.bukkit.craftbukkit.block.impl.CraftHugeMushroom::new);
        register(net.minecraft.server.BlockIceFrost.class, org.bukkit.craftbukkit.block.impl.CraftIceFrost::new);
        register(net.minecraft.server.BlockIronBars.class, org.bukkit.craftbukkit.block.impl.CraftIronBars::new);
        register(net.minecraft.server.BlockJigsaw.class, org.bukkit.craftbukkit.block.impl.CraftJigsaw::new);
        register(net.minecraft.server.BlockJukeBox.class, org.bukkit.craftbukkit.block.impl.CraftJukeBox::new);
        register(net.minecraft.server.BlockKelp.class, org.bukkit.craftbukkit.block.impl.CraftKelp::new);
        register(net.minecraft.server.BlockLadder.class, org.bukkit.craftbukkit.block.impl.CraftLadder::new);
        register(net.minecraft.server.BlockLantern.class, org.bukkit.craftbukkit.block.impl.CraftLantern::new);
        register(net.minecraft.server.BlockLeaves.class, org.bukkit.craftbukkit.block.impl.CraftLeaves::new);
        register(net.minecraft.server.BlockLectern.class, org.bukkit.craftbukkit.block.impl.CraftLectern::new);
        register(net.minecraft.server.BlockLever.class, org.bukkit.craftbukkit.block.impl.CraftLever::new);
        register(net.minecraft.server.BlockLoom.class, org.bukkit.craftbukkit.block.impl.CraftLoom::new);
        register(net.minecraft.server.BlockMinecartDetector.class, org.bukkit.craftbukkit.block.impl.CraftMinecartDetector::new);
        register(net.minecraft.server.BlockMinecartTrack.class, org.bukkit.craftbukkit.block.impl.CraftMinecartTrack::new);
        register(net.minecraft.server.BlockMycel.class, org.bukkit.craftbukkit.block.impl.CraftMycel::new);
        register(net.minecraft.server.BlockNetherWart.class, org.bukkit.craftbukkit.block.impl.CraftNetherWart::new);
        register(net.minecraft.server.BlockNote.class, org.bukkit.craftbukkit.block.impl.CraftNote::new);
        register(net.minecraft.server.BlockObserver.class, org.bukkit.craftbukkit.block.impl.CraftObserver::new);
        register(net.minecraft.server.BlockPiston.class, org.bukkit.craftbukkit.block.impl.CraftPiston::new);
        register(net.minecraft.server.BlockPistonExtension.class, org.bukkit.craftbukkit.block.impl.CraftPistonExtension::new);
        register(net.minecraft.server.BlockPistonMoving.class, org.bukkit.craftbukkit.block.impl.CraftPistonMoving::new);
        register(net.minecraft.server.BlockPortal.class, org.bukkit.craftbukkit.block.impl.CraftPortal::new);
        register(net.minecraft.server.BlockPotatoes.class, org.bukkit.craftbukkit.block.impl.CraftPotatoes::new);
        register(net.minecraft.server.BlockPoweredRail.class, org.bukkit.craftbukkit.block.impl.CraftPoweredRail::new);
        register(net.minecraft.server.BlockPressurePlateBinary.class, org.bukkit.craftbukkit.block.impl.CraftPressurePlateBinary::new);
        register(net.minecraft.server.BlockPressurePlateWeighted.class, org.bukkit.craftbukkit.block.impl.CraftPressurePlateWeighted::new);
        register(net.minecraft.server.BlockPumpkinCarved.class, org.bukkit.craftbukkit.block.impl.CraftPumpkinCarved::new);
        register(net.minecraft.server.BlockRedstoneComparator.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneComparator::new);
        register(net.minecraft.server.BlockRedstoneLamp.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneLamp::new);
        register(net.minecraft.server.BlockRedstoneOre.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneOre::new);
        register(net.minecraft.server.BlockRedstoneTorch.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneTorch::new);
        register(net.minecraft.server.BlockRedstoneTorchWall.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneTorchWall::new);
        register(net.minecraft.server.BlockRedstoneWire.class, org.bukkit.craftbukkit.block.impl.CraftRedstoneWire::new);
        register(net.minecraft.server.BlockReed.class, org.bukkit.craftbukkit.block.impl.CraftReed::new);
        register(net.minecraft.server.BlockRepeater.class, org.bukkit.craftbukkit.block.impl.CraftRepeater::new);
        register(net.minecraft.server.BlockRespawnAnchor.class, org.bukkit.craftbukkit.block.impl.CraftRespawnAnchor::new);
        register(net.minecraft.server.BlockRotatable.class, org.bukkit.craftbukkit.block.impl.CraftRotatable::new);
        register(net.minecraft.server.BlockSapling.class, org.bukkit.craftbukkit.block.impl.CraftSapling::new);
        register(net.minecraft.server.BlockScaffolding.class, org.bukkit.craftbukkit.block.impl.CraftScaffolding::new);
        register(net.minecraft.server.BlockSeaPickle.class, org.bukkit.craftbukkit.block.impl.CraftSeaPickle::new);
        register(net.minecraft.server.BlockShulkerBox.class, org.bukkit.craftbukkit.block.impl.CraftShulkerBox::new);
        register(net.minecraft.server.BlockSkull.class, org.bukkit.craftbukkit.block.impl.CraftSkull::new);
        register(net.minecraft.server.BlockSkullPlayer.class, org.bukkit.craftbukkit.block.impl.CraftSkullPlayer::new);
        register(net.minecraft.server.BlockSkullPlayerWall.class, org.bukkit.craftbukkit.block.impl.CraftSkullPlayerWall::new);
        register(net.minecraft.server.BlockSkullWall.class, org.bukkit.craftbukkit.block.impl.CraftSkullWall::new);
        register(net.minecraft.server.BlockSmoker.class, org.bukkit.craftbukkit.block.impl.CraftSmoker::new);
        register(net.minecraft.server.BlockSnow.class, org.bukkit.craftbukkit.block.impl.CraftSnow::new);
        register(net.minecraft.server.BlockSoil.class, org.bukkit.craftbukkit.block.impl.CraftSoil::new);
        register(net.minecraft.server.BlockStainedGlassPane.class, org.bukkit.craftbukkit.block.impl.CraftStainedGlassPane::new);
        register(net.minecraft.server.BlockStairs.class, org.bukkit.craftbukkit.block.impl.CraftStairs::new);
        register(net.minecraft.server.BlockStem.class, org.bukkit.craftbukkit.block.impl.CraftStem::new);
        register(net.minecraft.server.BlockStemAttached.class, org.bukkit.craftbukkit.block.impl.CraftStemAttached::new);
        register(net.minecraft.server.BlockStepAbstract.class, org.bukkit.craftbukkit.block.impl.CraftStepAbstract::new);
        register(net.minecraft.server.BlockStoneButton.class, org.bukkit.craftbukkit.block.impl.CraftStoneButton::new);
        register(net.minecraft.server.BlockStonecutter.class, org.bukkit.craftbukkit.block.impl.CraftStonecutter::new);
        register(net.minecraft.server.BlockStructure.class, org.bukkit.craftbukkit.block.impl.CraftStructure::new);
        register(net.minecraft.server.BlockSweetBerryBush.class, org.bukkit.craftbukkit.block.impl.CraftSweetBerryBush::new);
        register(net.minecraft.server.BlockTNT.class, org.bukkit.craftbukkit.block.impl.CraftTNT::new);
        register(net.minecraft.server.BlockTallPlant.class, org.bukkit.craftbukkit.block.impl.CraftTallPlant::new);
        register(net.minecraft.server.BlockTallPlantFlower.class, org.bukkit.craftbukkit.block.impl.CraftTallPlantFlower::new);
        register(net.minecraft.server.BlockTallSeaGrass.class, org.bukkit.craftbukkit.block.impl.CraftTallSeaGrass::new);
        register(net.minecraft.server.BlockTarget.class, org.bukkit.craftbukkit.block.impl.CraftTarget::new);
        register(net.minecraft.server.BlockTorchWall.class, org.bukkit.craftbukkit.block.impl.CraftTorchWall::new);
        register(net.minecraft.server.BlockTrapdoor.class, org.bukkit.craftbukkit.block.impl.CraftTrapdoor::new);
        register(net.minecraft.server.BlockTripwire.class, org.bukkit.craftbukkit.block.impl.CraftTripwire::new);
        register(net.minecraft.server.BlockTripwireHook.class, org.bukkit.craftbukkit.block.impl.CraftTripwireHook::new);
        register(net.minecraft.server.BlockTurtleEgg.class, org.bukkit.craftbukkit.block.impl.CraftTurtleEgg::new);
        register(net.minecraft.server.BlockTwistingVines.class, org.bukkit.craftbukkit.block.impl.CraftTwistingVines::new);
        register(net.minecraft.server.BlockVine.class, org.bukkit.craftbukkit.block.impl.CraftVine::new);
        register(net.minecraft.server.BlockWallSign.class, org.bukkit.craftbukkit.block.impl.CraftWallSign::new);
        register(net.minecraft.server.BlockWeepingVines.class, org.bukkit.craftbukkit.block.impl.CraftWeepingVines::new);
        register(net.minecraft.server.BlockWitherSkull.class, org.bukkit.craftbukkit.block.impl.CraftWitherSkull::new);
        register(net.minecraft.server.BlockWitherSkullWall.class, org.bukkit.craftbukkit.block.impl.CraftWitherSkullWall::new);
        register(net.minecraft.server.BlockWoodButton.class, org.bukkit.craftbukkit.block.impl.CraftWoodButton::new);
         */
        //</editor-fold>
    }

    private static void register(Class<? extends Block> nms, Function<BlockState, CraftBlockData> bukkit) {
        Preconditions.checkState(MAP.put(nms, bukkit) == null, "Duplicate mapping %s->%s", nms, bukkit);
    }

    public static CraftBlockData newData(Material material, String data) {
        Preconditions.checkArgument(material == null || material.isBlock(), "Cannot get data for not block %s", material);

        BlockState blockData;
        Block block = CraftMagicNumbers.getBlock(material);
        Map<Property<?>, Comparable<?>> parsed = null;

        // Data provided, use it
        if (data != null) {
            try {
                // Material provided, force that material in
                if (block != null) {
                    data = Registry.BLOCK.getKey(block) + data;
                }

                StringReader reader = new StringReader(data);
                BlockStateParser arg = new BlockStateParser(reader, false).parse(false);
                Preconditions.checkArgument(!reader.canRead(), "Spurious trailing data: " + data);

                blockData = arg.getState();
                parsed = arg.getProperties();
            } catch (CommandSyntaxException ex) {
                throw new IllegalArgumentException("Could not parse data: " + data, ex);
            }
        } else {
            blockData = block.getDefaultState();
        }

        CraftBlockData craft = fromData(blockData);
        craft.parsedStates = parsed;
        return craft;
    }

    public static CraftBlockData fromData(BlockState data) {
        return MAP.getOrDefault(data.getBlock().getClass(), CraftBlockData::new).apply(data);
    }
}