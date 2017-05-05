package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import org.eclipse.draw2d.MouseEvent;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotation;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotations;
import org.pnml.tools.epnk.annotations.netannotations.ObjectAnnotation;
import org.pnml.tools.epnk.applications.ui.IActionHandler;
import org.pnml.tools.epnk.helpers.NetFunctions;
import org.pnml.tools.epnk.pnmlcoremodel.TransitionNode;
import org.pnml.tools.epnk.tutorials.app.simulator.techsimannotations.EnabledTransition;
// TODO EnabledTransition should eventually be replaced by your own annotations (Tutorial 8)
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
		return false;
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
