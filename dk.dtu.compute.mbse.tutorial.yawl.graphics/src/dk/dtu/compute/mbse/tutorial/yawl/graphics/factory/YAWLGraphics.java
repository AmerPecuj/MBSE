package dk.dtu.compute.mbse.tutorial.yawl.graphics.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.pnml.tools.epnk.gmf.extensions.graphics.GraphicalExtension;
import org.pnml.tools.epnk.gmf.extensions.graphics.IArcFigure;
import org.pnml.tools.epnk.gmf.extensions.graphics.IUpdateableFigure;
import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.Place;
import org.pnml.tools.epnk.pnmlcoremodel.Transition;

import dk.dtu.compute.mbse.tutorial.yawl.graphics.figures.YAWLArcFigure;
import dk.dtu.compute.mbse.tutorial.yawl.graphics.figures.YAWLPlaceFigure;
import dk.dtu.compute.mbse.tutorial.yawl.graphics.figures.YAWLTransitionFigure;
import dk.dtu.compute.mbse.yawl.YawlPackage;

/**
 * The graphical extension the the Technical Net extension. This is
 * the class plugged in to the ePNK  (see plugin.xml).
 * 
 * @author ekki@dtu.dk
 *
 */
public class YAWLGraphics extends GraphicalExtension {

	
	public YAWLGraphics() {
		super();
	}

	@Override
	public List<EClass> getExtendedNetTypes() {
		ArrayList<EClass> results = new ArrayList<EClass>();
		results.add(YawlPackage.eINSTANCE.getYAWLNet());
		return results;
	}
	/**
	 * edited the generated code
	 * @Author Sigbatullah Samil Batir
	 * @sid s153191
	 */
	@Override
	public List<EClass> getExtendedNetObjects(EClass netType) {
		ArrayList<EClass> results = new ArrayList<EClass>();
		if (netType.equals(YawlPackage.eINSTANCE.getYAWLNet())) {
			results.add(YawlPackage.eINSTANCE.getArc());
			results.add(YawlPackage.eINSTANCE.getPlace());
			results.add(YawlPackage.eINSTANCE.getTransition());
		}
		return results;
	}

	@Override
	public IArcFigure createArcFigure(Arc arc) {
		if (arc instanceof dk.dtu.compute.mbse.yawl.Arc) {
			return new YAWLArcFigure(arc);
		}
		return null;
	}

	@Override
	public IUpdateableFigure createPlaceFigure(Place place) {
		if (place instanceof dk.dtu.compute.mbse.yawl.Place) {
			return new YAWLPlaceFigure(place);
		}
		return null;
	}
	/**
	 * @Author Ibrahim Al-Bacha
	 * @sid s118016
	 */
	@Override
	public IUpdateableFigure createTransitionFigure(Transition transition) {
		if (transition instanceof dk.dtu.compute.mbse.yawl.Transition) {
			return new YAWLTransitionFigure(transition);
		}
		return null;
	}
}
