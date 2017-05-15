package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.draw2d.MouseEvent;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotation;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotations;
import org.pnml.tools.epnk.annotations.netannotations.ObjectAnnotation;
import org.pnml.tools.epnk.applications.ui.IActionHandler;
import org.pnml.tools.epnk.helpers.FlatAccess;
import org.pnml.tools.epnk.helpers.NetFunctions;
import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.TransitionNode;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.marking.NetMarking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.EnabledTransition;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.SelectArc;
import dk.dtu.compute.mbse.yawl.Transition;

/**
 * Action handler dealing with mouse clicks on EnableTransition annotations.
 * It will fire the transition, if it is enabled.
 * 
 * @author ekki@dtu.dk
 *
 */
public class EnabledTransitionHandler implements IActionHandler {

	private YAWLSimulator application;

	public EnabledTransitionHandler(YAWLSimulator application) {
		super();
		this.application = application;	
	}

	@Override
	public boolean mouseDoubleClicked(MouseEvent arg0, ObjectAnnotation annotation) {
		NetAnnotations netAnnotations = application.getNetAnnotations();
		NetAnnotation current = netAnnotations.getCurrent();
		//*
		FlatAccess flatNet = application.getFlatAccess();
		if(current.getObjectAnnotations().contains(annotation)) {
			Object object = annotation.getObject();
			if(object instanceof TransitionNode) object=flatNet.resolve((TransitionNode) object);
			if(object instanceof Transition && annotation instanceof EnabledTransition) {
				Transition transition=(Transition) object;
				NetMarking marking1=application.computeMarking();
				if(application.isEnabled(flatNet, marking1, transition)) {
					NetMarking marking2=application.fireTransition(flatNet,marking1,selectedInArc((EnabledTransition) annotation),transition,selectedOutArcs((EnabledTransition) annotation));
					NetAnnotation netAnnotation = application.computeAnnotation(marking2);
					netAnnotation.setNet(application.getPetrinet());
					
					application.deleteNetAnnotationAfterCurrent();
					application.addNetAnnotationAsCurrent(netAnnotation);
					return true;
				}
			}
		}
		/*/
		if (current.getObjectAnnotations().contains(annotation)) {
			Object object = annotation.getObject();
			if (object instanceof TransitionNode) {
				object = NetFunctions.resolve((TransitionNode) object);
			}
			if (object instanceof Transition && annotation instanceof EnabledTransition) {
				Transition transition = (Transition) object;
				EnabledTransition enabledTransition = (EnabledTransition) annotation;
				
				if (enabledTransition.isEnabled()) {
					
					// TODO eventually, you need to compute the selected arcs for
					//      XOR-joins and XOR-splits and OR-splits so that the
					//      tokens are produced and consumed on the respective arcs
					//      (see project org.pnml.tools.epnk.tutorials.app.simulator)

					return application.fireTransition(transition);
				}
			}
		}
		//*/
		return false;
	}

	private Arc selectedInArc(EnabledTransition enabledTransition) {
		EnabledTransition resolved = enabledTransition.getResolved();
		if(resolved==null) resolved=enabledTransition;
		for(SelectArc selectArc: resolved.getInArcs()) {
			if(selectArc.isSelected()) {
				Object result=selectArc.getObject();
				if(result instanceof Arc) return (Arc) result;
			}
		}
		return null;
	}
	
	private Collection<Arc> selectedOutArcs(EnabledTransition enabledTransition) {
		EnabledTransition resolved = enabledTransition.getResolved();
		if(resolved==null) resolved=enabledTransition;
		Collection<Arc> result=new ArrayList<Arc>();
		for(SelectArc selectArc: resolved.getOutArcs()) {
			if(selectArc.isSelected()) {
				Object arc=selectArc.getObject();
				if(result instanceof Arc) result.add((Arc) arc);
			}
		}
		return result;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0, ObjectAnnotation annotation) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0, ObjectAnnotation annotation) {
		return false;
	}

}
