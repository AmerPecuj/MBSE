package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import org.pnml.tools.epnk.annotations.netannotations.NetAnnotation;
import org.pnml.tools.epnk.annotations.netannotations.NetannotationsFactory;
import org.pnml.tools.epnk.annotations.netannotations.ObjectAnnotation;
import org.pnml.tools.epnk.applications.ApplicationWithUIManager;
import org.pnml.tools.epnk.applications.ui.ApplicationUIManager;
import org.pnml.tools.epnk.helpers.FlatAccess;
import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.Node;
import org.pnml.tools.epnk.pnmlcoremodel.PetriNet;
import org.pnml.tools.epnk.pnmlcoremodel.PlaceNode;
import org.pnml.tools.epnk.pnmlcoremodel.RefPlace;
import org.pnml.tools.epnk.pnmlcoremodel.RefTransition;

import dk.dtu.compute.mbse.tutorial.yawl.simulator.marking.NetMarking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.EnabledTransition;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.Marking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.YawlannotationsFactory;
import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.Transition;
//TODO Action is in probably dk.dtu.compute.mbse.yawl.Transition in your cases
import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.Place;
//TODO Condition is in probably dk.dtu.compute.mbse.yawl.Place in your cases

import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;


/**
 * A first simulator application for YAWL nets. 
 * 
 * TODO Note that this simulator does not yet take split and join types
 * into account; neither does it take reset arcs into account. Moreover,
 * the user can not select arcs yet. This needs to be extended!
 * 
 * @author ekki@dtu.dk
 *
 */
public class YAWLSimulator extends ApplicationWithUIManager {
	
	private FlatAccess flatAccess;
	private NetChangeListener adapter;
	
	public YAWLSimulator(PetriNet petrinet) {
		super(petrinet);
		flatAccess = FlatAccess.getFlatAccess(this.getPetrinet());
		
		getNetAnnotations().setName("A simple YAWL simulator");
		ApplicationUIManager manager = this.getPresentationManager();
		manager.addActionHandler(new EnabledTransitionHandler(this));
		// manager.addActionHandler(new InvolvedArcHandler(this));
		// manager.addPresentationHandler(new YAWLAnnotationsPresentationHandler());
		
		adapter = new NetChangeListener(this);
		flatAccess.addInvalidationListener(adapter);
	}

	public FlatAccess getFlatAccess() {
		return flatAccess;
	}
	
	@Override
	protected void initializeContents() {
		NetMarking initialMarking = computeInitialMarking();
		NetAnnotation initialAnnotation = computeAnnotation(initialMarking);
		
		this.getNetAnnotations().getNetAnnotations().add(initialAnnotation);
		this.getNetAnnotations().setCurrent(initialAnnotation);
	}

	private NetMarking computeInitialMarking() {
		NetMarking marking = new NetMarking();
		for(org.pnml.tools.epnk.pnmlcoremodel.Place place: getFlatAccess().getPlaces()) {
			if(place instanceof Place && YAWLFunctions.getTypePlace(place)==PType.START) {
				marking.setMarking((Place) place, 1);
			}
		}
		return marking;
	}
	
	private NetMarking computeMarking() {
		NetMarking marking = new NetMarking();
		for (ObjectAnnotation annotation: this.getNetAnnotations().getCurrent().getObjectAnnotations()) {
			if(annotation instanceof Marking) {
				Marking markingAnnotation = (Marking) annotation;
				Object object = markingAnnotation.getObject();
				int value = markingAnnotation.getValue();
				if(object instanceof Place && value>0) {
					Place place = (Place) object;
					marking.setMarking(place, value);
				}
			}
		}
		return marking;
	}
	
	private NetAnnotation computeAnnotation(NetMarking marking) {
		NetAnnotation annotation = NetannotationsFactory.eINSTANCE.createNetAnnotation();
		annotation.setNet(getPetrinet());
		
		for (Object object: getFlatAccess().getTransitions()) {
			if (object instanceof Transition) {
				Transition action = (Transition) object;
				if (isEnabled(marking, action)) {
					EnabledTransition enabledTransition = YawlannotationsFactory.eINSTANCE.createEnabledTransition();
					enabledTransition.setObject(action);
					annotation.getObjectAnnotations().add(enabledTransition);
					enabledTransition.setEnabled(true);
					
					for (RefTransition refTransition: getFlatAccess().getRefTransitions(action)) {
						EnabledTransition enabledTransition2 = YawlannotationsFactory.eINSTANCE.createEnabledTransition();
						enabledTransition2.setObject(refTransition);
						enabledTransition2.setResolved(enabledTransition);
						enabledTransition2.setEnabled(enabledTransition.isEnabled());
						annotation.getObjectAnnotations().add(enabledTransition2);
					}
				}
			}
		}
		
		for (Place place: marking.getSupport()) {
			int m= marking.getMarking(place);
			if(m>0) {
				Marking mAnnotation = YawlannotationsFactory.eINSTANCE.createMarking();
				mAnnotation.setObject(place);
				mAnnotation.setValue(m);
				annotation.getObjectAnnotations().add(mAnnotation);
				
				for(RefPlace refPlace: getFlatAccess().getRefPlaces(place)) {
					Marking mAnnotation2 = YawlannotationsFactory.eINSTANCE.createMarking();
					mAnnotation2.setObject(refPlace);
					mAnnotation2.setValue(m);
					annotation.getObjectAnnotations().add(mAnnotation2);
				}
			}
		}
		return annotation;
	}
	
	boolean fireTransition(Transition action) {
		NetMarking marking1 = this.computeMarking();

		if (this.isEnabled(marking1, action)) {
			NetMarking marking2 = this.fireTransition(marking1, action);
			NetAnnotation netAnnotation = this.computeAnnotation(marking2);
			netAnnotation.setNet(this.getPetrinet());

			this.deleteNetAnnotationAfterCurrent();
			this.addNetAnnotationAsCurrent(netAnnotation);
			return true;
		}
		return false;
	}

	private NetMarking fireTransition(NetMarking marking1, Transition action) {
		NetMarking marking2 = new NetMarking(marking1);
		
		// consume tokens from preset
		NetMarking consumes = consumes(action);
		marking2.subtract(consumes);
				
		// reset places on page connected to reset arc
        // TODO
		
		// produce tokens on postset
		NetMarking produces = produces(action);
		marking2.add(produces);
		
		return marking2;
	}

	private boolean isEnabled(NetMarking marking, Transition action) {
		NetMarking consumes = consumes(action);
		return marking.isGreaterOrEqual(consumes);
	}
	
	private NetMarking consumes(Transition action) {
		NetMarking consumes = new NetMarking();
		for (Arc arc: getFlatAccess().getIn(action)) {
			if (arc instanceof Arc && YAWLFunctions.getTypeArc(arc) == AType.NORMAL ) {
				Node source = arc.getSource();
				if(source instanceof PlaceNode) {
					source=getFlatAccess().resolve((PlaceNode) source);
					if(source instanceof Place) {
						consumes.incrementMarkingBy((Place) source, 1);
					}
				}
			}
		}
		return consumes;
	}
	
	private NetMarking produces(Transition transition) {
		NetMarking produces = new NetMarking();
		for (org.pnml.tools.epnk.pnmlcoremodel.Arc arc: getFlatAccess().getOut(transition)) {
			if (arc instanceof Arc && YAWLFunctions.getTypeArc(arc) == AType.NORMAL ) {
				Node target = arc.getTarget();
				if(target instanceof PlaceNode) {
					target=getFlatAccess().resolve((PlaceNode) target);
					if(target instanceof Place) {
						produces.incrementMarkingBy((Place) target, 1);
					}
				}
			}
		}
		return produces;
	}

	/* (non-Javadoc)
	 * @see org.pnml.tools.epnk.applications.Application#shutDown()
	 */
	@Override
	protected void shutDown() {
		super.shutDown();
		
		if (flatAccess != null && adapter != null) {
			flatAccess.removeInvalidationListener(adapter);
			adapter = null;
		}
	}

}