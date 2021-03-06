package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.pnml.tools.epnk.pnmlcoremodel.TransitionNode;

import dk.dtu.compute.mbse.tutorial.yawl.simulator.marking.NetMarking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.EnabledTransition;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.Marking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.SelectArc;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.YawlannotationsFactory;
import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.Transition;
//TODO Action is in probably dk.dtu.compute.mbse.yawl.Transition in your cases
import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.Place;
//TODO Condition is in probably dk.dtu.compute.mbse.yawl.Place in your cases
import dk.dtu.compute.mbse.yawl.TType;
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
		getNetAnnotations().setName("A simple YAWL simulator");
		ApplicationUIManager manager = this.getPresentationManager();
		manager.addActionHandler(new EnabledTransitionHandler(this));
		manager.addActionHandler(new SelectArcHandler(this));
		manager.addPresentationHandler(new YAWLAnnotationsPresentationHandler());

		flatAccess = FlatAccess.getFlatAccess(this.getPetrinet());
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
		initialAnnotation.setNet(this.getPetrinet());

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

	NetMarking computeMarking() {
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
	
	/**
	 * @author Amer Ali & Ahmad Almajedi
	 * @sid s145224 & s153317
	 */
	NetAnnotation computeAnnotation(NetMarking marking) {
		FlatAccess flatAccess=this.getFlatAccess();
		NetAnnotation annotation = NetannotationsFactory.eINSTANCE.createNetAnnotation();
		annotation.setNet(getPetrinet());
		Map<Object,Marking> place2MarkingAnnotation = new HashMap<Object,Marking>();
		for(Place place: marking.getSupport()) {
			int value = marking.getMarking(place);
			if(value>0) {
				Marking markingAnnotation = YawlannotationsFactory.eINSTANCE.createMarking();
				markingAnnotation.setValue(value);
				markingAnnotation.setObject(place);
				annotation.getObjectAnnotations().add(markingAnnotation);
				place2MarkingAnnotation.put(place,markingAnnotation);
			}
		}
		
		Set<Transition> enabled=new HashSet<Transition>();
		
		for(org.pnml.tools.epnk.pnmlcoremodel.Transition transition2: flatAccess.getTransitions()) {
			Transition transition=(Transition) transition2;//TODO: MAASKE AENDRE DENNE HER
			if(transition instanceof Transition) {
				if(isEnabled(flatAccess, marking, transition)) {
					enabled.add(transition);
					
					EnabledTransition transitionAnnotation = YawlannotationsFactory.eINSTANCE.createEnabledTransition();
					transitionAnnotation.setObject(transition);
					annotation.getObjectAnnotations().add(transitionAnnotation);
					
					if(transition.getJoinType().getText().equals(TType.XOR)) {
						boolean first = true;
						for (Object in: flatAccess.getIn(transition)){
							if (in instanceof Arc) {
								if(!YAWLFunctions.isResetArc((Arc) in)){
									Marking sourceMarking = place2MarkingAnnotation.get(((Arc) in).getSource());
									if(sourceMarking != null){
										SelectArc arcAnnotation = YawlannotationsFactory.eINSTANCE.createSelectArc();
										arcAnnotation.setObject(((Arc) in));
										arcAnnotation.setTargetTransition(transitionAnnotation);
										if(first){
											arcAnnotation.setSelected(true);
											first = false;
										}
										else arcAnnotation.setSelected(false);
										annotation.getObjectAnnotations().add(arcAnnotation);
									}
								}
							}
						}
					}
					
					if(transition.getSplitType().getText().equals(TType.XOR)){
						boolean first = true;
						for (Object out: flatAccess.getOut(transition)) {
							if(out instanceof Arc){
								SelectArc arcAnnotation = YawlannotationsFactory.eINSTANCE.createSelectArc();
								arcAnnotation.setObject(((Arc) out));
								arcAnnotation.setSourceTransition(transitionAnnotation);
								if(first){
									arcAnnotation.setSelected(true);
									first = false;
								}
								else arcAnnotation.setSelected(false);
								annotation.getObjectAnnotations().add(arcAnnotation);
							}
						}
					}
					
					if(transition.getSplitType().getText().equals(TType.OR)){
						for (Object out: flatAccess.getOut(transition)) {
							if(out instanceof Arc){
								SelectArc arcAnnotation = YawlannotationsFactory.eINSTANCE.createSelectArc();
								arcAnnotation.setObject(((Arc) out));
								arcAnnotation.setSourceTransition(transitionAnnotation);
								arcAnnotation.setSelected(true);
								annotation.getObjectAnnotations().add(arcAnnotation);
							}
						}
					}
				}
			}
		}
		
		return annotation;
	}
	
	/**
	 * @author Ibrahim Al-Bacha & Samil Batir
	 * @sid s118016 & s153191
	 */
	NetMarking fireTransition(FlatAccess flatNet, NetMarking marking1, Arc selectedInArc, Transition transition, Collection<Arc> selectedOutArcs) {
		//*
		NetMarking marking2=new NetMarking(marking1);
		TType joinType=transition.getJoinType().getText();
		if(joinType.equals(TType.AND) || joinType.equals(TType.SINGLE)) {
			for(Object in: flatNet.getIn(transition)) {
				if(in instanceof Arc) {
					Arc inArc = (Arc) in;
					if(!YAWLFunctions.isResetArc(inArc)) {
						Object source = inArc.getSource();
						if(source instanceof PlaceNode) {
							source = flatNet.resolve((PlaceNode) source);
							if(source instanceof Place) {
								marking2.decrementMarkingBy((Place) source, 1);
							}
						}
					}
				}
			}
		}
		else if(joinType.equals(TType.OR)) {
			for(Object in: flatNet.getIn(transition)) {
				if(in instanceof Arc) {
					Arc inArc = (Arc) in;
					if(!YAWLFunctions.isResetArc(inArc)) {
						Object source = inArc.getSource();
						if(source instanceof PlaceNode) {
							source = flatNet.resolve((PlaceNode) source);
							if(source instanceof Place) {
								if(marking2.getMarking((Place) source)>0) {
									marking2.decrementMarkingBy((Place) source, 1);
								}
							}
						}
					}
				}
			}
		}
		else if(joinType.equals(TType.XOR) && selectedInArc!=null && YAWLFunctions.isResetArc(selectedInArc)) {
			Node target = selectedInArc.getTarget();
			if(target instanceof TransitionNode) {
				Transition transition2=(Transition) flatNet.resolve((TransitionNode) target);
				if(transition2==transition) {
					Object source = selectedInArc.getSource();
					if(source instanceof PlaceNode) {
						source = flatNet.resolve((PlaceNode) source);
						if(source instanceof Place) {
							if(marking2.getMarking((Place) source)>0) marking2.decrementMarkingBy((Place) source,1);
						}
					}
				}
			}
		}
		
		for(Object in: flatNet.getIn(transition)) {
			if(in instanceof Arc) {
				Arc inArc = (Arc) in;
				if(!YAWLFunctions.isResetArc(inArc)) {
					Object source = inArc.getSource();
					if(source instanceof PlaceNode) {
						source = flatNet.resolve((PlaceNode) source);
						if(source instanceof Place) marking2.setMarking((Place) source,0);
					}
				}
			}
		}
		
		TType splitType = transition.getSplitType().getText();
		if(splitType.equals(TType.AND) || splitType.equals(TType.SINGLE)) {
			for(Object out: flatNet.getOut(transition)) {
				if(out instanceof Arc) {
					Arc outArc = (Arc) out;
					Object target = outArc.getTarget();
					if(target instanceof PlaceNode) {
						target = flatNet.resolve((PlaceNode) target);
						if(target instanceof Place) marking2.incrementMarkingBy((Place) target,1);
					}
				}
			}
		}
		else if(splitType.equals(TType.OR) || splitType.equals(TType.XOR) && selectedInArc!=null) {
			for(Arc outArc: selectedOutArcs) {
				Node source=outArc.getSource();
				if(source instanceof TransitionNode) source=flatNet.resolve((TransitionNode) source);
				if(transition==source) {
					Object target=outArc.getTarget();
					if(target instanceof PlaceNode) {
						target=flatNet.resolve((PlaceNode) target);
						if(target instanceof Place) marking2.incrementMarkingBy((Place) target,1);
					}
				}
			}
		}
		return marking2;
	}

	/**
	 * 
	 * @author Ibrahim Al-Bacha
	 * @sid s118016
	 *
	 */
	boolean isEnabled(FlatAccess flatNet, NetMarking marking, Transition transition) {
		TType joinType = transition.getJoinType().getText();
		if(joinType.equals(TType.AND) || joinType.equals(TType.SINGLE)) {
			for(Object in: flatNet.getIn(transition)) {
				if(in instanceof Arc) {
					Arc arc=(Arc) in;
					if(!YAWLFunctions.isResetArc(arc)) {
						Object source = arc.getSource();
						if(source instanceof PlaceNode) {
							source = flatNet.resolve((PlaceNode) source);
							if(source instanceof Place) {
								if(marking.getMarking((Place) source) < 1) return false;
							}
							else return false;
						}
						else return false;
					}
				}
			}
			return true;
		}
		else if(joinType.equals(TType.OR) || joinType.equals(TType.XOR)) {
			for(Object in: flatNet.getIn(transition)) {
				if(in instanceof Arc) {
					Arc arc=(Arc) in;
					if(!YAWLFunctions.isResetArc(arc)) {
						Object source = arc.getSource();
						if(source instanceof PlaceNode) {
							source = flatNet.resolve((PlaceNode) source);
							if(source instanceof Place) {
								if(marking.getMarking((Place) source) > 0) return true;
							}
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	private NetMarking consumes(Transition Transition) {
		NetMarking consumes = new NetMarking();
		for (Arc arc: getFlatAccess().getIn(Transition)) {
			if (arc instanceof Arc && YAWLFunctions.getTypeArc(arc) == AType.NORMAL) {
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