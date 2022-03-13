package controler;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import View.GrilleView;
import model.Couleur;

public class GeneratorAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	GrilleView gv;
	
	public GeneratorAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("------------------------------ Aleatoire Bruteforce ------------------------------");
		//isThereAMonoSource renvoi la couleur des sources solitaires
		//isMonoSource() renvoi true si il y a une unique source sur la carte
		if (
				(gv.getModel().isMonoSource() && gv.getModel().getHeight()>=5 && gv.getModel().getWidth()>=5 ) 
				|| ( !(gv.getModel().isMonoSource()) && ( gv.getModel().isThereAMonoSource() == Couleur.W ) )
				)  {
			gv.getModel().generateAleaBruteforce();
			gv.ImageOfCell(false);
			gv.getFrameG().revalidate();
			gv.getFrameG().repaint();	
		}
		
		else if (gv.getModel().isMonoSource() ) {	
			JOptionPane.showMessageDialog(null, "Veuillez entrer une grille au minimum 3x3 pour generer un niveau aleatoire avec une unique source ");
		}
		else if  ( gv.getModel().isThereAMonoSource() != Couleur.W ) {
			JOptionPane.showMessageDialog(null, "Veuillez ajouter une source " +  gv.getModel().isThereAMonoSource() );
		} 

	}
	
}