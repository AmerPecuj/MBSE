package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.emf.common.util.EList;
import org.pnml.tools.epnk.annotations.netannotations.ObjectAnnotation;
import org.pnml.tools.epnk.applications.ui.IActionHandler;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.EnabledTransition;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.Marking;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.SelectArc;
import dk.dtu.compute.mbse.yawl.TType;
import dk.dtu.compute.mbse.yawl.Transition;

public class SelectArcHandler implements IActionHandler {
	private YAWLSimulator application;
	
	public SelectArcHandler(YAWLSimulator application) {
		super();		
		this.application = application;
	}

	@Override
	public boolean mouseDoubleClicked(MouseEvent arg0, ObjectAnnotation annotation) {
		return false;
	}

	/**
	 * @author Ahmad Almajedi
	 * @sid s153317
	 */
	@Override
	public boolean mousePressed(MouseEvent arg0, ObjectAnnotation annotation) {
		if (annotation instanceof SelectArc) {
			SelectArc selectArc = (SelectArc) annotation;
			EnabledTransition targetTransition = selectArc.getTargetTransition();
			EnabledTransition sourceTransition = selectArc.getSourceTransition();
			Marking sourceMarking = selectArc.getSourceMarking();
			
			if(targetTransition != null) {
				if(!selectArc.isSelected() && sourceMarking!=null && sourceMarking.getValue()>0) {
					Transition transition = (Transition) targetTransition.getAction();
					if(transition.getJoinType().getText().equals(TType.XOR)) {
						for(SelectArc s_arc: targetTransition.getInArcs()) s_arc.setSelected(false);
						selectArc.setSelected(true);
						application.update();
						return true;
					}
				}
			}
			else if(sourceTransition!=null) {
				Transition transition=(Transition) sourceTransition.getAction();
				TType tType=transition.getSplitType().getText();
				if(tType.equals(TType.XOR)) {
					for(SelectArc s_arc: sourceTransition.getOutArcs()) s_arc.setSelected(false);
					selectArc.setSelected(true);
					application.update();
					return true;
				}
				else if(tType.equals(TType.OR)) {
					selectArc.setSelected(!selectArc.isSelected());
					boolean notEmpty=false;
					for(SelectArc s_arc: sourceTransition.getOutArcs()) {
						if(s_arc.isSelected()) {
							notEmpty=true;
							break;
						}
					}
					if(!notEmpty) {
						for(SelectArc s_arc: sourceTransition.getOutArcs()) {
							if(s_arc!=selectArc) {
								s_arc.setSelected(true);
								break;
							}
						}
					}
					application.update();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0, ObjectAnnotation annotation) {
		return false;
	}
}
