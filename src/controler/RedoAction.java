package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.undo.CannotRedoException;

import View.GrilleView;

public class RedoAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	GrilleView gv;
	public RedoAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	
	// Realisation de l'action (partie controleur de l'action)
	public void actionPerformed(ActionEvent e) {
		try {
			System.out.println("------------------------------ REDO ------------------------------");
			gv.undoManager.redo();
		}catch(CannotRedoException exp) {
			System.out.println("CannotRedoException!");
		}

	}
	// Mise a jour de l'activation
	public void updateEnabled() {
		// L'activation est determinee par le gestionnaire de modifications
		setEnabled(gv.undoManager.canRedo());
	}
}