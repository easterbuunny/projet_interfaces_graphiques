package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import View.GrilleView;

public class HintAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	GrilleView gv;
	
	public HintAction(String text, ImageIcon icon,GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("------------------------------ HINT ------------------------------");

		gv.getModel().solution1();
		gv.getModel().updateColor(false);

		gv.ImageOfCell(false);
		
		gv.undoManager.discardAllEdits();

		gv.undoAction.updateEnabled();
		gv.redoAction.updateEnabled();

		gv.getFrameG().revalidate();
		gv.getFrameG().repaint();
		

	}
	
}