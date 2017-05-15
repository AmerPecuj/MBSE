package dk.dtu.compute.mbse.tutorial.yawl.graphics.figures;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.swt.SWT;

import org.pnml.tools.epnk.gmf.extensions.graphics.decorations.ArrowHeadDecoration;
import org.pnml.tools.epnk.gmf.extensions.graphics.decorations.DoubleArrowHeadDecoration;
import org.pnml.tools.epnk.gmf.extensions.graphics.figures.ArcFigure;
import org.pnml.tools.epnk.pnmlcoremodel.Arc;
import org.pnml.tools.epnk.pnmlcoremodel.Node;

import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

/**
 * The figure defining implementing the graphical appearance of arcs in the
 * Technical Net Type.
 * 
 * @author Sigbatullah Samil Batir & Amer Ali
 * @Sid s153191 & s145224
 * 
 */
public class YAWLArcFigure extends ArcFigure {

	private AType arcType;
	
	public YAWLArcFigure(Arc arc) {
		super(arc);
		arcType = YAWLFunctions.getTypeArc(arc);
		setGraphics();
	}

	@Override
	public void update() {
		AType oldArcType = arcType;
		arcType = YAWLFunctions.getTypeArc(arc);
		if (arcType != oldArcType) {
			setGraphics();
			// Notify attached target node of the change (which will initiate the update of the
			// corresponding graphical representation if needed).
			Node target = arc.getTarget();
			if (target instanceof InternalEObject) {
				target.eNotify(new ENotificationImpl((InternalEObject) target, Notification.NO_FEATURE_ID, null, null, null));
			}
		}
	}

	private void setGraphics() {
		if (arcType == AType.NORMAL){
			this.setTargetDecoration(new ArrowHeadDecoration());
			this.setSourceDecoration(null);
			this.setLineStyle(SWT.LINE_SOLID);
		} else if (arcType == AType.RESET){
			this.setTargetDecoration(new DoubleArrowHeadDecoration());
			this.setSourceDecoration(null);
			this.setLineStyle(SWT.LINE_DASH);
		}
	}

}
