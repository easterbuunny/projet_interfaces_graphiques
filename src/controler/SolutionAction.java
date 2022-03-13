package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import View.GrilleView;

public class SolutionAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	// Construction
	GrilleView gv;
	
	public SolutionAction(String text, ImageIcon icon,GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("------------------------------ SOLUTION ------------------------------");

		gv.getModel().solution();
		gv.getModel().updateColor(false);
		gv.ImageOfCell(false);
		
		gv.undoManager.discardAllEdits();
		
		gv.undoAction.updateEnabled();
		gv.redoAction.updateEnabled();
		gv.hintAction.setEnabled(false);
		
		gv.getFrameG().revalidate();
		gv.getFrameG().repaint();
	}
	
}