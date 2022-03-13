package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.undo.CannotUndoException;

import View.GrilleView;

public class UndoAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	GrilleView gv;
	
	public UndoAction(String text, ImageIcon icon,GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		try {
			System.out.println("------------------------------ UNDO ------------------------------");
			gv.undoManager.undo();
		}catch(CannotUndoException exp) {
			System.out.println("CannotUndoException!");
		}
		
	}
	
	// Mise a jour de l'activation
	public void updateEnabled() {
		setEnabled(gv.undoManager.canUndo());
	}
}