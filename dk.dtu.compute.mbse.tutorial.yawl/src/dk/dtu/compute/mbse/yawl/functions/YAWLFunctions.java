package dk.dtu.compute.mbse.yawl.functions;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.Node;
import org.pnml.tools.epnk.pnmlcoremodel.Place;
import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.ArcType;
import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.PlaceType;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

public class YAWLFunctions {
	public static PType getTypePlace(Place place) {
		if (place instanceof Place) {
			Place YAWLplace = place;
			PlaceType type = ((dk.dtu.compute.mbse.yawl.Place) YAWLplace).getPlacetype();
			if (type != null) {
				if (type.getText().equals(PType.START)) {
					return PType.START;
				} else if (type.getText().equals(PType.FINISH)) {
					return PType.FINISH;
				}
			} else {
				return PType.NORMAL;
			}
		}
		return PType.NORMAL;
	}

	public static AType getTypeArc(Arc arc) {
		if (arc instanceof Arc) {
			Arc YAWLArc = (Arc) arc;
			ArcType type = ((dk.dtu.compute.mbse.yawl.Arc) YAWLArc).getArctype();
			if (type != null) {
				if (((PlaceType) type).getText().equals(AType.RESET)) {
					return AType.RESET;
				}
			} else {
				return AType.NORMAL;
			}
		}
		return AType.NORMAL;
	}
	
	public static boolean isResetArc(Arc arc) {
		return getTypeArc(arc).equals(AType.RESET);
	
	}

	
}

