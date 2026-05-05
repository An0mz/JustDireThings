package com.direwolf20.justdirethings.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Forge 1.20.1 equivalent of NeoForge's {@code BlockCapabilityCache}.
 *
 * <p>Caches {@link LazyOptional}{@code <IEnergyStorage>} lookups from neighboring block entities.
 * When the cached optional is invalidated by the neighbor (e.g. the block entity is removed or
 * replaced), the entry is automatically evicted so the next call transparently re-queries — saving
 * redundant {@link BlockEntity#getCapability} calls every tick.
 *
 * <p>Usage:
 * <pre>{@code
 *   private final BlockEnergyCache energyCache = new BlockEnergyCache();
 *
 *   // In your tick method:
 *   IEnergyStorage storage = energyCache.get(level, neighborPos, queryDirection);
 *
 *   // In invalidateCaps():
 *   energyCache.invalidateAll();
 * }</pre>
 */
public class BlockEnergyCache {

    /**
     * Key composed of an immutable {@link BlockPos} and the query {@link Direction} (nullable for isotropic).
     * Records provide structural equals/hashCode automatically.
     */
    private record CacheKey(BlockPos pos, @Nullable Direction side) {}

    private final Map<CacheKey, LazyOptional<IEnergyStorage>> cache = new HashMap<>();

    /**
     * Returns the {@link IEnergyStorage} at {@code pos} queried from {@code side}, using a
     * cached {@link LazyOptional} when still valid.
     *
     * @param level the level to query
     * @param pos   the target block position
     * @param side  the side to query from ({@code null} = isotropic / side-agnostic query)
     * @return the energy storage, or {@code null} if unavailable
     */
    @Nullable
    public IEnergyStorage get(Level level, BlockPos pos, @Nullable Direction side) {
        CacheKey key = new CacheKey(pos.immutable(), side);
        LazyOptional<IEnergyStorage> cached = cache.get(key);

        // Cache hit — optional is still valid
        if (cached != null && cached.isPresent()) {
            return cached.orElse(null);
        }

        // Cache miss or invalidated — re-query the block entity
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            cache.remove(key);
            return null;
        }

        LazyOptional<IEnergyStorage> cap = be.getCapability(ForgeCapabilities.ENERGY, side);
        cache.remove(key); // evict any stale entry
        if (cap.isPresent()) {
            cache.put(key, cap);
            // When the neighbor calls invalidateCaps() the LazyOptional is invalidated,
            // which fires this listener and auto-evicts our cache entry.
            cap.addListener(opt -> cache.remove(key));
        }
        return cap.orElse(null);
    }

    /**
     * Eagerly invalidate all cached entries for a specific position (across all sides).
     * Use when you know a particular block has changed.
     */
    public void invalidatePos(BlockPos pos) {
        BlockPos immPos = pos.immutable();
        cache.entrySet().removeIf(e -> Objects.equals(e.getKey().pos(), immPos));
    }

    /**
     * Clear the entire cache. Call from {@code invalidateCaps()} or when the tracked area is rescanned.
     */
    public void invalidateAll() {
        cache.clear();
    }
}

