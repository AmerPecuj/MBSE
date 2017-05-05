package dk.dtu.compute.mbse.tutorial.yawl.graphics.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.pnml.tools.epnk.gmf.extensions.graphics.figures.PlaceFigure;
import org.pnml.tools.epnk.gmf.extensions.graphics.figures.TransitionFigure;
import org.pnml.tools.epnk.pnmlcoremodel.Place;
import org.pnml.tools.epnk.pnmlcoremodel.Transition;
import dk.dtu.compute.mbse.yawl.PType;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

/**
 * The figure defining implementing the graphical appearance of transitions in the
 * Technical Net Type. The transitions will be shown with a cross in on the
 * left or ride side of the transition if the transition does not have NORMAL
 * incoming or outgoing arcs.
 * 
 * @author ekki@dtu.dk
 * 
 */
public class YAWLPlaceFigure extends PlaceFigure {
	
	private PType type;

	public YAWLPlaceFigure(Place place) {
		super(place);
		type=YAWLFunctions.getTypePlace(place);
	}

	/**
	 * This method is called whenever some attribute of the transition which might
	 * influence the graphical appearance of the transition changes.
	 */
	@Override
	public void update() {
		PType oldType=type;
		type=YAWLFunctions.getTypePlace(place);
		if (oldType!=type) {
			// only call the repaint() method, when there was a change that has
			// an effect to the graphical appearance of the transition
			this.repaint();
		}
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
		super.fillShape(graphics);
		Rectangle rectangle = this.getClientArea();
		int d1 = rectangle.width/3;
		int d2 = rectangle.height/3;
		
		if(type.equals(PType.START)){
			graphics.pushState();
			Display display = Display.getCurrent();
			Color green = display.getSystemColor(SWT.COLOR_GREEN);
			graphics.setBackgroundColor(green);
			graphics.setLineWidth(1);
			int x1=rectangle.x+d1+1;
			int y1=rectangle.y+d2;
			int x2=rectangle.x+2*d1+2;
			int y2=rectangle.y+rectangle.height/2;
			int x3=x1;
			int y3=rectangle.y+2*d2;
			graphics.fillPolygon(new int[]{x1,y1, x2,y2, x3,y3});
			graphics.drawPolygon(new int[]{x1,y1, x2,y2, x3,y3});
			graphics.popState();
		}
		else if(type.equals(PType.FINISH)){
			graphics.pushState();
			Display display = Display.getCurrent();
			Color red = display.getSystemColor(SWT.COLOR_RED);
			graphics.setBackgroundColor(red);
			graphics.setLineWidth(1);
			int x1=rectangle.x+d1;
			int y1=rectangle.y+d2;
			int x2=x1+d1;
			int y2=y1;
			int x3=x1+d1;
			int y3=y1+d2;
			int x4=x1;
			int y4=y1+d2;
			graphics.fillPolygon(new int[]{x1,y1, x2,y2, x3,y3, x4,y4});
			graphics.drawPolygon(new int[]{x1,y1, x2,y2, x3,y3, x4,y4});
			graphics.popState();
		}
	}
}
