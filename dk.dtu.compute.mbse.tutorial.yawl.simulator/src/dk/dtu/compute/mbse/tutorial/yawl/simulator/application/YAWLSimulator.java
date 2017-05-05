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
import org.pnml.tools.epnk.tutorials.app.simulator.techsimannotations.EnabledTransition;
import org.pnml.tools.epnk.tutorials.app.simulator.techsimannotations.Marking;
import org.pnml.tools.epnk.tutorials.app.simulator.techsimannotations.TechsimannotationsFactory;

import dk.dtu.compute.mbse.tutorial.yawl.simulator.marking.NetMarking;
import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.Transition;
import dk.dtu.compute.mbse.yawl.Place;

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
		
		// TODO make sure that there is added exactly one token for the start place
		//      to the marking

		return marking;
	}
	
	private NetMarking computeMarking() {
		NetMarking marking = new NetMarking();
		for (ObjectAnnotation annotation: this.getNetAnnotations().getCurrent().getObjectAnnotations()) {
			
			// TODO from the current annotations compute the correct marking for each place
			//      (defined by the annotations Marking which are for now reused from the
			//      project org.pnml.tools.epnk.tutorials.app.simulator)

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
					EnabledTransition enabledTransition = TechsimannotationsFactory.eINSTANCE.createEnabledTransition();
					enabledTransition.setObject(action);
					annotation.getObjectAnnotations().add(enabledTransition);
					enabledTransition.setEnabled(true);
					
					for (RefTransition refTransition: getFlatAccess().getRefTransitions(action)) {
						EnabledTransition enabledTransition2 = TechsimannotationsFactory.eINSTANCE.createEnabledTransition();
						enabledTransition2.setObject(refTransition);
						enabledTransition2.setResolve(enabledTransition);
						enabledTransition2.setEnabled(enabledTransition.isEnabled());
						annotation.getObjectAnnotations().add(enabledTransition2);
					}
				}
			}
		}
		
		for (Place condition: marking.getSupport()) {
			int m=marking.getMarking(condition);
			if(m>0){
				Marking mAnnotation = TechsimannotationsFactory.eINSTANCE.createMarking();
				mAnnotation.setObject(condition);
				mAnnotation.setValue(m);
				annotation.getObjectAnnotations().add(mAnnotation);
				
				for(RefPlace refCondition: getFlatAccess().getRefPlaces(condition)) {
					Marking mAnnotation2 = TechsimannotationsFactory.eINSTANCE.createMarking();
					mAnnotation2.setObject(refCondition);
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
				if (source instanceof PlaceNode) {
					source = getFlatAccess().resolve((PlaceNode) source);
					if (source instanceof Place) {
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
				if (target instanceof PlaceNode) {
					target = getFlatAccess().resolve((PlaceNode) target);
					if (target instanceof Place) {
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