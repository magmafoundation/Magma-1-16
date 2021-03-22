package org.bukkit.craftbukkit.util;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import org.jetbrains.annotations.Nullable;

// TODO: Come aback and finish this implementation
public class DummyGeneratorAccess implements IWorld {

    public static final IWorld INSTANCE = new DummyGeneratorAccess();

    @Override
    public DynamicRegistries registryAccess() {
        return null;
    }

    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        return 0;
    }

    @Override
    public WorldLightManager getLightEngine() {
        return null;
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPos p_175625_1_) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos p_180495_1_) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos p_204610_1_) {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
        return null;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
        return null;
    }

    @Override
    public List<? extends PlayerEntity> players() {
        return null;
    }

    @Override
    public ITickList<Block> getBlockTicks() {
        return null;
    }

    @Override
    public ITickList<Fluid> getLiquidTicks() {
        return null;
    }

    @Override
    public IWorldInfo getLevelData() {
        return null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_) {
        return null;
    }

    @Override
    public AbstractChunkProvider getChunkSource() {
        return null;
    }

    @Override
    public Random getRandom() {
        return null;
    }

    @Override
    public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {

    }

    @Override
    public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {

    }

    @Override
    public void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {

    }

    @Override
    public ServerWorld getMinecraftWorld() {
        return null;
    }

    @Nullable
    @Override
    public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
        return null;
    }

    @Override
    public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return null;
    }

    @Override
    public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public DimensionType dimensionType() {
        return null;
    }

    @Override
    public boolean setBlock(BlockPos p_241211_1_, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos p_241212_1_, boolean p_241212_2_, @Nullable Entity p_241212_3_, int p_241212_4_) {
        return false;
    }

    @Override
    public boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
        return false;
    }

//    @Override
//    public ITickList<Block> getPendingBlockTicks() {
//        return EmptyTickList.get();
//    }
//
//    @Override
//    public ITickList<Fluid> getPendingFluidTicks() {
//        return EmptyTickList.get();
//    }
//
//    @Override
//    public IWorldInfo getWorldInfo() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public AbstractChunkProvider getChunkProvider() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public Random getRandom() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public ServerWorld getMinecraftWorld() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public DynamicRegistries func_241828_r() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public List<? extends PlayerEntity> getPlayers() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Nullable
//    @Override
//    public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public int getHeight(Heightmap.Type heightmapType, int x, int z) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public int getSkylightSubtracted() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public BiomeManager getBiomeManager() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public Biome getNoiseBiomeRaw(int x, int y, int z) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public boolean isRemote() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public int getSeaLevel() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public DimensionType getDimensionType() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public TileEntity getTileEntity(BlockPos blockposition) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public BlockState getBlockState(BlockPos pos) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public FluidState getFluidState(BlockPos pos) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public WorldBorder getWorldBorder() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public boolean hasBlockState(BlockPos bp, Predicate<BlockState> prdct) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft) {
//        return false;
//    }
//
//    @Override
//    public boolean removeBlock(BlockPos blockposition, boolean flag) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public WorldLightManager getLightManager() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
//        return false;
//    }
}
