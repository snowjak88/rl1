/**
 * 
 */
package org.snowjak.rl1.systems;

import java.util.Random;

import org.snowjak.rl1.Context;
import org.snowjak.rl1.components.CanReportVision;
import org.snowjak.rl1.components.HasPosition;
import org.snowjak.rl1.components.IsDrawable;
import org.snowjak.rl1.events.VisionEvent;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

/**
 * 
 * @author snowjak88
 *
 */
@Wire
public class VisionCheckingSystem extends IteratingSystem {
	
	private static final Random RND = new Random(System.currentTimeMillis());
	
	private M<HasPosition> hasPosition;
	private M<CanReportVision> canReport;
	
	@SkipWire
	private EntitySubscription viewingEntities = null;
	
	/**
	 * @param aspect
	 */
	public VisionCheckingSystem() {
		
		super(Aspect.all(IsDrawable.class, HasPosition.class));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.artemis.systems.IteratingSystem#process(int)
	 */
	@Override
	protected void process(int entityID) {
		
		if (viewingEntities == null)
			viewingEntities = Context.get().odb.getAspectSubscriptionManager()
					.get(Aspect.all(CanReportVision.class, HasPosition.class));
		
		for (int i = 0; i < viewingEntities.getEntities().size(); i++) {
			
			final int viewerID = viewingEntities.getEntities().get(i);
			if (canSee(viewerID, entityID))
				Context.get().es.dispatch(new VisionEvent(viewerID, entityID));
			
		}
	}
	
	public boolean canSee(int seerID, int seenID) {
		
		final double maxDistance = canReport.get(seerID).maxDistance;
		final double distance = getDistanceBetween(seerID, seenID);
		if (distance > maxDistance)
			return false;
		
		final double pSee = (canReport.get(seerID).acuity
				// TODO include visibility modifier on seen entity
				* 1d) / Math.pow(distance, 2d);
		return (RND.nextDouble() < pSee);
		
	}
	
	private double getDistanceBetween(int seerID, int seenID) {
		
		final HasPosition seerPos = hasPosition.get(seerID), seenPos = hasPosition.get(seenID);
		return Math.sqrt(Math.pow(seerPos.x - seenPos.x, 2d) + Math.pow(seerPos.y - seenPos.y, 2d));
	}
	
}
