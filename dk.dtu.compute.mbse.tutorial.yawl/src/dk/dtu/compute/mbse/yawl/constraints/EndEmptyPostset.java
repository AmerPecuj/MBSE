package dk.dtu.compute.mbse.yawl.constraints;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.pnml.tools.epnk.helpers.FlatAccess;
import org.pnml.tools.epnk.helpers.NetFunctions;

import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.Place;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

public class EndEmptyPostset extends AbstractModelConstraint{

	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject object = ctx.getTarget();
		if(object instanceof Place){
			FlatAccess fA =FlatAccess.getFlatAccess(NetFunctions.getPetriNet(object));
			if(YAWLFunctions.getTypePlace((Place) object).equals(PType.FINISH)){
				if(fA.getOut((Place) object).size()!=0){
					return ctx.createFailureStatus(new Object[] {object});
				}
			}
		}
		return ctx.createSuccessStatus();
		
	}

}
