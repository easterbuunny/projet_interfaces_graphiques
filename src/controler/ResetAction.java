package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import View.GrilleView;

public class ResetAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	GrilleView gv;
	
	public ResetAction(String text, ImageIcon icon,GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("------------------------------ RESET ------------------------------");

		gv.getModel().reset(gv.getEditor());
		
		//vider les changements dans la stack
		gv.undoManager.discardAllEdits();
		if(gv.getEditor()) gv.getModel().unlockTousLesTuyaux();
		gv.undoAction.updateEnabled();
		gv.redoAction.updateEnabled();
		gv.resizeGrilleView() ;
		gv.ImageOfCell(false); 
		gv.getFrameG().revalidate();
		gv.getFrameG().repaint();
	}
	
}