/*******************************************************************************
 * 
 * Copyright 2010 Alexandru Craciun, and individual contributors as indicated
 * by the @authors tag. 
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 ******************************************************************************/

package org.netxilia.api.concurrent;

import com.googlecode.concurrentlinkedhashmap.CapacityLimiter;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * Simple, Thread safe LRU objects identity cache.
 * 
 * @author catac
 */

public class LRUCache<K, V> {

	private final static float mapLoadFactor = 0.75f;
	private final ConcurrentLinkedHashMap<K, V> linkedMap;
	private final IValueProvider<K, V> valueProvider;

	private volatile int maxEntries;
	private volatile long hits = 0;
	private volatile long misses = 0;

	public LRUCache(int maxEntries, IValueProvider<K, V> valueProvider) {

		this.maxEntries = maxEntries;
		int initialCapacity = (int) Math.ceil(maxEntries / mapLoadFactor) + 1;
		this.linkedMap = new ConcurrentLinkedHashMap.Builder<K, V>().initialCapacity(initialCapacity)
				.maximumWeightedCapacity(maxEntries).capacityLimiter(new CapacityLimiter() {
					@Override
					public boolean hasExceededCapacity(ConcurrentLinkedHashMap<?, ?> map) {
						return map.size() > LRUCache.this.maxEntries;
					}
				}).build();
		this.valueProvider = valueProvider;
	}

	/** Return the cached (if exists) or given (and cache it) entry for the given value */
	public V getCached(K key) {
		V cachedValue = linkedMap.get(key);
		if (cachedValue != null) {
			hits++;
			return cachedValue;
		}
		cachedValue = valueProvider.getValue(key);
		linkedMap.put(key, cachedValue);
		misses++;
		return cachedValue;
	}

	public long getAccessCount() {
		return hits + misses;
	}

	public void clear() {
		linkedMap.clear();
		resetStats();
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}

	public int getUsedEntries() {
		return linkedMap.size();
	}

	public void resetStats() {
		hits = misses = 0;
	}

	public double getEfficiency() {
		long sum = hits + misses;
		return sum > 0 ? 100 * hits / (double) sum : 0;
	}

	public interface IValueProvider<KK, VV> {
		VV getValue(KK key);
	}
}
