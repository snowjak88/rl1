/**
 * 
 */
package org.snowjak.rl1.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A Provider is a repository for named resources.
 * 
 * @author snowjak88
 *
 */
public abstract class Provider<T> {
	
	private final Map<String, T> instances = new HashMap<>();
	
	/**
	 * Get the resource identified by {@code name}.
	 * 
	 * @param name
	 * @return
	 */
	public Optional<T> get(String name) {
		
		return Optional.ofNullable(instances.computeIfAbsent(name, (n) -> load(n).orElse(null)));
	}
	
	/**
	 * Load the resource identified by {@code name}, e.g. from a data-file.
	 * 
	 * @param name
	 * @return
	 */
	protected abstract Optional<T> load(String name);
	
}
