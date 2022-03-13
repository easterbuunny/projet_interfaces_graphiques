package controler;


import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


import View.GrilleView;


public class EditorAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	GrilleView gv;
	ResetAction reset;

	//compteur de fichier level dans inputfiles
	private int countFile = 1;
	private File inputfiles = new File(System.getProperty("user.dir") + "/src/inputfiles/");
	private File[] f = inputfiles.listFiles();

	public EditorAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
		reset = new ResetAction(text, icon, gv);
	}
	public void actionPerformed(ActionEvent e) {
		gv.setEditorTrue();
		gv.getEditorAction().setEnabled(false);
		//Eviter les doublons, si nous appuyons plusieurs fois sur editor
		if(gv.getEditor()) {
			gv.getModel().solution();
			gv.getModel().unlockTousLesTuyaux();
			gv.getModel().updateColor(true);
			gv.ImageOfCell(false);
		
			//Compter les fichier qui commence par "level" et creer un fichier qui incremente le compteur
			for(int c = 0; c<f.length; c++ ) {
				if(f[c].getName().startsWith("level")){
					countFile++;
				}
			}
			gv.setNewLevel(new File(System.getProperty("user.dir") + "/src/inputfiles/level"+countFile+".p"));
			
		}				
		
		gv.ImageOfCell(false);
		
		//Retourner sur la page d'acceuil
		System.out.println("------------------------------ EDITEUR ------------------------------");
		
	}	
}
	
