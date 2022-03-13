package controler;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import View.GrilleView;

public class CancelAction extends AbstractAction {
	// Construction
	
	private static final long serialVersionUID = 1L;
	GrilleView gv;
	ResetAction reset;
	
	public CancelAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
		reset = new ResetAction(text, icon, gv);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int retour = JOptionPane.showConfirmDialog(null, "Confirmer l'annulation","Annulation",JOptionPane.OK_CANCEL_OPTION, -1, null);
		if(retour==0) {
			gv.getEditorAction().setEnabled(true);
			
			if(gv.getEditor() && gv.getNewLevel()!=null) {
				gv.getNewLevel().delete();
			}
			gv.setEditorFalse();
			gv.getModel().reset(gv.getEditor()); //mis a jour la grille, schema
			gv.getFrameG().revalidate();
			gv.getFrameG().repaint();
			System.out.println("RESET");
			JOptionPane.showMessageDialog(null, "Vous avez quitter le mode editeur");
			
			this.setEnabled(false);
			gv.ImageOfCell(false);
			
		}
	}

}
