/**
 * 
 */
package org.snowjak.rl1.archetypes;

import org.snowjak.rl1.components.CanReportVision;
import org.snowjak.rl1.components.HasPosition;
import org.snowjak.rl1.components.IsDrawable;
import org.snowjak.rl1.components.IsOccupyable;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.World;

/**
 * @author snowjak88
 *
 */
public class Archetypes {
	
	public static Archetype terrain(World world) {
		
		return new ArchetypeBuilder().add(HasPosition.class).add(IsDrawable.class).add(IsOccupyable.class).build(world);
	}
	
	public static Archetype wall(World world) {
		
		return new ArchetypeBuilder(terrain(world)).remove(IsOccupyable.class).build(world);
	}
	
	public static Archetype mob(World world) {
		
		return new ArchetypeBuilder().add(HasPosition.class).add(IsDrawable.class).build(world);
	}
	
	public static Archetype seeingMob(World world) {
		
		return new ArchetypeBuilder(mob(world)).add(CanReportVision.class).build(world);
	}
}
