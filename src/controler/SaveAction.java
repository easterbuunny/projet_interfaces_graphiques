package controler;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import View.GrilleView;

public class SaveAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	GrilleView gv;
	AccueilAction accueilAction;

	public SaveAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
		accueilAction = new AccueilAction(text, icon, gv);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		File directory = new File(System.getProperty("user.dir") + "/src/inputfiles/");
		File[] listFiles = directory.listFiles();
		Boolean b = false;

		if (gv.getModel().updateColor(false)==0) {

			gv.ImageOfCell(false);
			gv.getEditorAction().setEnabled(false);	
			int retour = JOptionPane.showConfirmDialog(null, "Confirmer la sauvegarde","Sauvegarde",JOptionPane.OK_CANCEL_OPTION, -1, null);
			System.out.println("int : "+retour);
			if(retour==0) {
				try {
					if(gv.getNewLevel()!=null) {
						FileWriter fw = new FileWriter(gv.getNewLevel());
						fw.write(gv.getH()+" "+gv.getW()+"\n");
						for(int i = 0; i<gv.getH(); i++) {
							for(int j = 0; j<gv.getW(); j++) {
								fw.write(gv.getModel().getMainGrille().getCase(i, j).toString());
								fw.write("  ");
							}
							fw.write("\n");
						}
						fw.flush();
					}

					gv.getModel().setAffichage("Save OK !");	
					accueilAction.actionPerformed(e);

					JOptionPane.showMessageDialog(null, "NEW File : "+ gv.getNewLevel());
					gv.setEditorFalse();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
			else if(retour==2){
				gv.getModel().setAffichage("Save KO !");
				gv.setEditorTrue();
				gv.getEditorAction().setEnabled(true);
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "Il n'y a pas de solution ! " );
			gv.getModel().setAffichage("Save KO !");
			gv.setEditorTrue();
			gv.getEditorAction().setEnabled(true);

		}
		gv.ImageOfCell(false);
	}
}