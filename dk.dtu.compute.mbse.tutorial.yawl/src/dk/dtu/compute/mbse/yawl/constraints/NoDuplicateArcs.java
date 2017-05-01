package dk.dtu.compute.mbse.yawl.constraints;

import javax.xml.soap.Node;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.pnml.tools.epnk.helpers.FlatAccess;
import org.pnml.tools.epnk.helpers.NetFunctions;
import org.pnml.tools.epnk.tutorials.app.technical.helpers.ArcType;
import org.pnml.tools.epnk.tutorials.app.technical.helpers.TechnicalNetTypeFunctions;

import dk.dtu.compute.mbse.yawl.Arc;

public class NoDuplicateArcs extends AbstractModelConstraint{

	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject object = ctx.getTarget();
		if(object instanceof Arc){
			Arc arc = (Arc) object;
			ArcType arcType = TechnicalNetTypeFunctions.getArcType(arc);
			Node source = (Node) NetFunctions.resolve(arc.getSource());
			Node target = (Node) NetFunctions.resolve(arc.getTarget());
			FlatAccess flatAccess =
					FlatAccess.getFlatAccess(NetFunctions.getPetriNet(arc));
			if ((arcType == ArcType.NORMAL ||arcType == ArcType.READ) && source != null && flatAccess != null) {
				for (org.pnml.tools.epnk.pnmlcoremodel.Arc other:
					flatAccess.getOut((org.pnml.tools.epnk.pnmlcoremodel.Node) source)) {
					if (other != arc) {
						if (other instanceof Arc) {
							Arc arc2 = (Arc) other;
							Node target2 =
									(Node) NetFunctions.resolve(arc2.getTarget());
							if (target == target2) {
								if (TechnicalNetTypeFunctions.getArcType(arc) ==
										TechnicalNetTypeFunctions.getArcType(arc2)) {
									return ctx.createFailureStatus(new Object[]{arc});
								}
							}
						}
					}
				}
			}
		}
		return ctx.createSuccessStatus();


	}
}