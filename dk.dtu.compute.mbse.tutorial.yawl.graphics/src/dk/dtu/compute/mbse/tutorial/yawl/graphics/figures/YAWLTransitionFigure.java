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
import dk.dtu.compute.mbse.yawl.TType;
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
public class YAWLTransitionFigure extends TransitionFigure {
	private TType[] type;

	public YAWLTransitionFigure(Transition transition) {
		super(transition);
		type=YAWLFunctions.getTypeTransition(transition);
	}

	/**
	 * This method is called whenever some attribute of the transition which might
	 * influence the graphical appearance of the transition changes.
	 */
	@Override
	public void update() {
		TType[] oldType=type;
		type=YAWLFunctions.getTypeTransition(transition);
		if (oldType[0]!=type[0] || oldType[1]!=type[1]) {
			// only call the repaint() method, when there was a change that has
			// an effect to the graphical appearance of the transition
			this.repaint();
		}
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
		super.fillShape(graphics);
		Rectangle rectangle = this.getClientArea();
		int w = rectangle.width/3;
		int h = rectangle.height/3;
		/*
		graphics.pushState();
		Display display = Display.getCurrent();
		Color green = display.getSystemColor(SWT.COLOR_GREEN);
		graphics.setBackgroundColor(green);
		graphics.setLineWidth(1);
		int x1=rectangle.x+w+1;
		int y1=rectangle.y+h;
		int x2=rectangle.x+2*w+2;
		int y2=rectangle.y+rectangle.height/2;
		int x3=x1;
		int y3=rectangle.y+2*h;
		graphics.fillPolygon(new int[]{x1,y1, x2,y2, x3,y3});
		graphics.drawPolygon(new int[]{x1,y1, x2,y2, x3,y3});
		graphics.popState();
		/*/
		graphics.pushState();
		graphics.setLineWidth(2);
		if(type[0]==TType.XOR) {
			graphics.drawPolygon(new int[]{
				rectangle.x+w,rectangle.y,
				rectangle.x,rectangle.y+rectangle.height/2,
				rectangle.x+w,rectangle.y+rectangle.height
			});
		}
		else if(type[0]==TType.OR) {
			graphics.drawPolygon(new int[]{
				rectangle.x+w/2,rectangle.y,
				rectangle.x,rectangle.y+rectangle.height/2,
				rectangle.x+w/2,rectangle.y+rectangle.height,
				rectangle.x+w,rectangle.y+rectangle.height/2
			});
			graphics.drawLine(rectangle.x+w, rectangle.y, rectangle.x+w, rectangle.y+rectangle.height);
		}
		else {
			graphics.drawLine(rectangle.x+w, rectangle.y, rectangle.x+w, rectangle.y+rectangle.height);
			graphics.drawLine(rectangle.x, rectangle.y, rectangle.x+w, rectangle.y+rectangle.height/2);
			graphics.drawLine(rectangle.x+w, rectangle.y+rectangle.height/2, rectangle.x, rectangle.y+rectangle.height);
		}
		
		if(type[1]==TType.XOR) {
			graphics.drawPolygon(new int[]{
				rectangle.x+w*2,rectangle.y,
				rectangle.x+rectangle.width,rectangle.y+rectangle.height/2,
				rectangle.x+w*2,rectangle.y+rectangle.height
			});
		}
		else if(type[1]==TType.OR) {
			graphics.drawPolygon(new int[]{
				rectangle.x+w*2+w/2,rectangle.y,
				rectangle.x+rectangle.width,rectangle.y+rectangle.height/2,
				rectangle.x+w*2+w/2,rectangle.y+rectangle.height,
				rectangle.x+w*2,rectangle.y+rectangle.height/2
			});
			graphics.drawLine(rectangle.x+w*2, rectangle.y, rectangle.x+w*2, rectangle.y+rectangle.height);
		}
		else {
			graphics.drawLine(rectangle.x+w*2, rectangle.y, rectangle.x+w*2, rectangle.y+rectangle.height);
			graphics.drawLine(rectangle.x+rectangle.width, rectangle.y, rectangle.x+w*2, rectangle.y+rectangle.height/2);
			graphics.drawLine(rectangle.x+w*2, rectangle.y+rectangle.height/2, rectangle.x+rectangle.width, rectangle.y+rectangle.height);
		}
		graphics.popState();
		//*/
	}
}
