package dk.dtu.compute.mbse.tutorial.yawl.simulator.marking;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import dk.dtu.compute.mbse.yawl.Place;
// TODO Condition is in probably dk.dtu.compute.mbse.yawl.Place in your cases


/**
 * This class represents a marking of a YAWL net. It guarantees a uniform
 * way of dealing with markings and avoids a lot of redundant code scattered
 * in the simulator.
 * 
 * @author ekki@dtu.dk
 *
 */
public class NetMarking {
	
	private Map<Place,Integer> marking;

	/**
	 * Creates a new empty marking.
	 */
	public NetMarking() {
		marking = new HashMap<Place,Integer>();
	}
	
	/**
	 * Creates a new marking, which is a copy of a given marking.
	 */
	public NetMarking(NetMarking marking) {
		this();
		for (Place condition: marking.getSupport()) {
			this.setMarking(condition, marking.getMarking(condition));
		}
	}

	/**
	 * Sets the marking of a given condition to the given value. 
	 */
	public void setMarking(Place condition, int value) {
		if (value > 0) {
			marking.put(condition, value);
		} else {
			marking.remove(condition);
		}
	}

	/**
	 * Returns the marking of a given condition. 
	 */
	public int getMarking(Place condition) {
		return marking.getOrDefault(condition,0);
	}

	/**
	 * Increments the marking of a given condition by a given increment. 
	 */
	public void incrementMarkingBy(Place condition, int increment) {
		Assert.isLegal(increment >= 0, "The incrememt must be greater or equal than 0");
		setMarking((Place) condition, getMarking((Place) condition) + increment);
	}
	
	/**
	 * Decrements the marking of a given condition by a given decrement. 
	 */
	public void decrementMarkingBy(Place condition, int decrement) {
		Assert.isLegal(decrement >= 0, "The decrememt must be greater or equal than 0");
		Assert.isLegal(getMarking((Place) condition) >= decrement,
				"Marking of condition must be greater than the decrement");
		setMarking((Place) condition, getMarking((Place) condition) - decrement);
	}
	
	/**
	 * Adds marking2 to this marking
	 */
	public void add(NetMarking marking2) {
		for (Place condition: marking2.marking.keySet()) {
			int m1 = marking.getOrDefault(condition,0);
			int m2 = marking2.marking.getOrDefault(condition, 0);
			if (m1 + m2 > 0) {
				marking.put(condition, m1 + m2);
			} else {
				marking.remove(condition);
			}
		}
	}
	
	/**
	 * Subtracts marking2 from this marking
	 */
	public void subtract(NetMarking marking2) {
		Assert.isLegal(this.isGreaterOrEqual(marking2), 
				"Subtraction the marking does not result in a marking again");
		
		for (Place condition: marking2.marking.keySet()) {
			int m1 = marking.getOrDefault(condition,0);
			int m2 = marking2.marking.getOrDefault(condition, 0);
			if (m1 - m2 > 0) {
				marking.put(condition, m1 - m2);
			} else {
				marking.remove(condition);
			}
		}
	}
	
	/**
	 * Checks whether this marking is greater or equal than marking2.
	 */
	public boolean isGreaterOrEqual(NetMarking marking2) {
		for (Place condition: marking2.marking.keySet()) {
			int m1 = marking.getOrDefault(condition,0);
			int m2 = marking2.marking.getOrDefault(condition, 0);
			if (m1 < m2) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the set of all conditions that have at least one token, which is
	 * often called the support of the marking.
	 */
	public Collection<Place> getSupport() {
		return java.util.Collections.unmodifiableCollection(marking.keySet());
	}

}