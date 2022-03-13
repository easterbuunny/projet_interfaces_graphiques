package model;


import View.GrilleView;

public class Move {
	
	int sourceI, sourceJ, destI, destJ;
	// Construction
	GrilleView gv;
	Tuyaux t; //tuyaux courrent pendant movement
	
	public Move(GrilleView gv, Tuyaux tt, int sourceI, int sourceJ, int destI, int destJ) {
		this.gv=gv;
	    this.sourceI = sourceI;
	    this.sourceJ = sourceJ;
	    this.destI = destI;
	    this.destJ = destJ;
	    this.t=tt;
	}
	

	// Faire le deplacement
	public void doit() {
		//source = grille => -1 reserve
		//gv.getGl().delToReserve(t);
		
		System.out.println("DOIT ! " +  t+sourceI+"-"+sourceJ+ " " +destI+"-"+destJ);
		
		//source = border
		if(sourceI == 0 || sourceJ == 0 || sourceJ==gv.getW()-1 || sourceI==gv.getH()-1) {
			gv.getModel().getMainGrille().setSchema(sourceI, sourceJ, null);
			gv.getModel().getMainGrille().setCell(sourceI, sourceJ, "X");
			
		}
		//source = grille
		else if (sourceI != -1 && sourceJ != -1 ) {
			gv.getModel().getMainGrille().setSchema(sourceI, sourceJ, null);
			gv.getModel().getMainGrille().setCell(sourceI, sourceJ, ".");
		}
		//source = reserve
		else{
			gv.getModel().delToReserve(t);
			System.out.println(" DEL FROM RESERVE");
			gv.getModel().displayP();
		}
		
		// DEST = GRILLE
		if(destI!= -1 && destJ != -1) {
			gv.getModel().getMainGrille().setSchema(destI, destJ, t);
			gv.getModel().getMainGrille().setCell(destI, destJ, t.toString());
			
			//Update Color on MouseRelease
			gv.getModel().getMainGrille().getSchema(destI, destJ).setColor(Couleur.W);
			
			if(gv.getModel().getMainGrille().getSchema(destI, destJ).getModel() == Type.O){
				gv.getModel().getMainGrille().getSchema(destI, destJ).setColor2(Couleur.W);
			}
		}
		//DEST = RESERVE
		else {
			gv.getModel().addToReserve(t);
			System.out.println("ADD TO RESERVE");
			gv.getModel().displayP();
		}
		gv.getModel().updateColor( gv.getEditor());
		gv.ImageOfCell(false);
		gv.repaint();
		gv.undoAction.updateEnabled();
		gv.redoAction.updateEnabled();
	}
	
	// Faire le deplacement inverse
	public void undo() {
		System.out.println("UNDO ! " +  t+sourceI+"-"+sourceJ+ " " +destI+"-"+destJ);
		//G (source) => R (dest) =>  DOIT ! L0-1--1 2-5 => UNDO ! L0-1--1 2-5
		//G1 => g2 => DOIT ! L01-4 1-3 => UNDO ! L01-4 1-3
		
		//DOIT ! L0-1--1 2-5 
		
		//Dest = grille
		if (destI != -1 && destJ != -1 ) {

			//la dest devient null
			gv.getModel().getMainGrille().setSchema(destI, destJ, null);
			gv.getModel().getMainGrille().setCell(destI, destJ, ".");	
			//gv.getModel().delToReserve(t);
		}
		// DEST = RESERVE
		else {
			//dest =  reserve
			gv.getModel().delToReserve(t);
		}
		
		//si la source existe, on update la grille et le schema , et on recolorie le tuyau source en blanc , suivi d'un update de couleur et de repaint sprite
		// SOURCE = GRILLE
		if(sourceI!= -1 && sourceJ != -1) {
			
			gv.getModel().getMainGrille().setSchema(sourceI, sourceJ, t);
			gv.getModel().getMainGrille().setCell(sourceI, sourceJ, t.toString());
			gv.getModel().getMainGrille().getSchema(sourceI, sourceJ).setColor(Couleur.W);
			
			//type Over, update color2
			if(gv.getModel().getMainGrille().getSchema(sourceI, sourceJ).getModel() == Type.O){
				gv.getModel().getMainGrille().getSchema(sourceI, sourceJ).setColor2(Couleur.W);
			}
		}
		// SOURCE = RESERVE
		else {

			gv.getModel().addToReserve(t);
			
		}

		gv.getModel().updateColor(false);
		gv.ImageOfCell(false);
		gv.repaint();
		gv.undoAction.updateEnabled();
		gv.redoAction.updateEnabled();
	}
 }