package controler;


import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
//import javax.swing.KeyStroke;

import View.GrilleView;

public class AccueilAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;

	GrilleView gv;
	
	public AccueilAction(String text, ImageIcon icon, GrilleView gv) {
		super(text, icon);
		this.gv=gv;
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("------------------------------ Retouner Accueil ------------------------------");

		gv.getFrameG().getContentPane().removeAll();
		gv.getFrameG().setJMenuBar(null);
		gv.getFrameG().revalidate();
		gv.getFrameG().repaint();
		gv.getFrameG().setSize(new Dimension(1200, 1000));
		gv.getAcc().accUpdate();
		gv.getFrameG().getContentPane().add(gv.getAcc());

	}
	
}