package dk.dtu.compute.mbse.tutorial.yawl.simulator.application;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.emf.common.util.EList;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotation;
import org.pnml.tools.epnk.annotations.netannotations.NetAnnotations;
import org.pnml.tools.epnk.annotations.netannotations.ObjectAnnotation;
import org.pnml.tools.epnk.applications.ui.IActionHandler;
import dk.dtu.compute.mbse.tutorial.yawl.simulator.yawlannotations.EnabledTransition;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0, ObjectAnnotation annotation) {
		if (annotation instanceof SelectArc) {
			SelectArc selectArc = (SelectArc) annotation;
			EnabledTransition parentTransition = selectArc.getSourceTransition();
			NetAnnotations netAnnotations = application.getNetAnnotations();
			NetAnnotation current = netAnnotations.getCurrent();	

			if (current.getObjectAnnotations().contains(parentTransition)) {
				Object object = parentTransition.getObject();

				if (object instanceof Transition) {
					Transition transition = (Transition) object;
					if (transition.getSplitType().getText() == TType.OR || transition.getSplitType().getText() == TType.XOR) {
						getArcsList(parentTransition.getOutArcs(), selectArc, transition);
					} else {
						selectArc.setSelected(!selectArc.isSelected());
					}
				}
			}
			parentTransition = selectArc.getTargetTransition();
			if (current.getObjectAnnotations().contains(parentTransition)) {
				Object object = parentTransition.getObject();

				if (object instanceof Transition) {
					Transition transition = (Transition) object;

					if (transition.getJoinType().getText() == TType.OR || transition.getJoinType().getText() == TType.XOR) {
						getArcsList(parentTransition.getInArcs(), selectArc, transition);
					} else {
						selectArc.setSelected(!selectArc.isSelected());
					}
				}
			}
			application.update();

			return true;
		}
		return false;
	}
	
	public EList<SelectArc> getArcsList(EList<SelectArc> arcs, SelectArc arcAnnotation, Transition transition) {
		if (arcAnnotation.isSelected()) {
			boolean AtleasOneSelected = false;
			if (arcs.size() > 1) {
				arcAnnotation.setSelected(false);
				for (SelectArc arc : arcs) {
					if (arc.isSelected()) {
						AtleasOneSelected = true;					
						break;
					}
				}
				if (!AtleasOneSelected) {
					for (SelectArc arc : arcs) {
						if (arc != arcAnnotation) {
							arc.setSelected(true);
							break;
						}
					}
				}
			}
		} else if (transition.getSplitType().getText() == TType.OR || transition.getJoinType().getText() == TType.OR) {
			arcAnnotation.setSelected(true);
		} else if (transition.getSplitType().getText() == TType.XOR || transition.getJoinType().getText() == TType.XOR) {
			for (SelectArc outArc : arcs) {
				outArc.setSelected(false);
			}
			arcAnnotation.setSelected(true);
		}
		return arcs;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0, ObjectAnnotation annotation) {
		// TODO Auto-generated method stub
		return false;
	}
}
