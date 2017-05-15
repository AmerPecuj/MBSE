package dk.dtu.compute.mbse.yawl.constraints;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.Place;
import dk.dtu.compute.mbse.yawl.Transition;

import org.pnml.tools.epnk.pnmlcoremodel.PetriNet;
import dk.dtu.compute.mbse.yawl.YAWLNet;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

/**
 * check if there is a dead end --> that it dosent finish with a PlaceFinish
 * @author Ahmad Almajedi
 * @sid s153317
 *
 */
public class NoDeadEnds extends AbstractModelConstraint {
	
	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject object = ctx.getTarget();
		if(object instanceof YAWLNet){
			EObject container = object.eContainer();
			if(container instanceof PetriNet){
				Iterator<EObject> iterator = container.eAllContents();
				while(iterator.hasNext()){
					EObject content = iterator.next();
					if(content instanceof Place){
						Place condition = (Place) content;
						PType type = YAWLFunctions.getTypePlace(condition);
						if(!type.equals(PType.START) && !type.equals(PType.FINISH) && (condition.getIn().isEmpty() || condition.getOut().isEmpty())) return ctx.createFailureStatus(new Object[] {condition});
					}
					if(content instanceof Transition){
						Transition condition = (Transition) content;
						if(condition.getIn().isEmpty() || condition.getOut().isEmpty()) return ctx.createFailureStatus(new Object[] {condition});
					}
				}
			}
		}
		return ctx.createSuccessStatus();
	}
}
