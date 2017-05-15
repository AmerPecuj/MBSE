package dk.dtu.compute.mbse.yawl.functions;

import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.Place;
import org.pnml.tools.epnk.pnmlcoremodel.Transition;

import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.ArcType;
import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.PlaceType;
import dk.dtu.compute.mbse.yawl.TType;
import dk.dtu.compute.mbse.yawl.TransitionType;
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
	/**
	 * 
	 * @author Ibrahim Al-Bacha & Samil Batir
	 * @sid s118016 & s153191
	 */
	public static AType getTypeArc(Arc arc) {
		if (arc instanceof Arc) {
			Arc YAWLArc = (Arc) arc;
			ArcType type = ((dk.dtu.compute.mbse.yawl.Arc) YAWLArc).getArctype();
			if (type != null) {
				if (((ArcType) type).getText().equals(AType.RESET)) {
					return AType.RESET;
				}
			} else {
				return AType.NORMAL;
			}
		}
		return AType.NORMAL;
	}
	
	/**
	 * 
	 * @author Ahmad Almajedi og Amer ali
	 * @sid s153317 & 
	 */
	public static TType[] getTypeTransition(Transition transition) {
		TType[] types=new TType[2];
		types[0]=TType.SINGLE;
		types[1]=TType.SINGLE;
		if (transition instanceof Transition) {
			Transition YAWLTransition = (Transition) transition;
			if(((dk.dtu.compute.mbse.yawl.Transition) YAWLTransition).getJoinType() != null) {
				TransitionType type = ((dk.dtu.compute.mbse.yawl.Transition) YAWLTransition).getJoinType();
				if(type.getText().equals(TType.OR)) types[0]=TType.OR;
				else if(type.getText().equals(TType.XOR)) types[0]=TType.XOR;
				else if(type.getText().equals(TType.AND)) types[0]=TType.AND;
			}
			if(((dk.dtu.compute.mbse.yawl.Transition) YAWLTransition).getSplitType() != null) {
				TransitionType type = ((dk.dtu.compute.mbse.yawl.Transition) YAWLTransition).getSplitType();
				if(type.getText().equals(TType.OR)) types[1]=TType.OR;
				else if(type.getText().equals(TType.XOR)) types[1]=TType.XOR;
				else if(type.getText().equals(TType.AND)) types[1]=TType.AND;
			}
			return types;
		}
		else return types;
	}
	
	public static boolean isResetArc(Arc arc) {
		return getTypeArc(arc).equals(AType.RESET);
	
	}
}
