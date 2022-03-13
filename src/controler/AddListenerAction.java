package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import View.GrilleView;
import model.Orientation;
import model.Move;
import model.Type;

public class AddListenerAction {
	// Construction
	private GrilleView gv;
	private int sourceI,sourceJ, sourceW, sourceH;
	private float x, y;

	public AddListenerAction(GrilleView gv) {
		this.gv=gv;
	}

	public MouseAdapter addListener() {

		gv.setTuile2(gv.getSubImages().getResult().get(6).get(0));
		gv.setBar2(gv.getSubImages().getResult().get(6).get(4));
		MouseAdapter actionListener = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				gv.setT(null);
				gv.setXi(e.getX());
				gv.setYi(e.getY());
				System.out.println("--------------------------------------------------------------------------------\n > PRESS : "+gv.getXi() + " " +gv.getYi());
				gv.getModel().displayP();
				gv.getModel().getMainGrille().displayGrille();
				gv.getModel().getMainGrille().displaySchema();

				//Sortir un tuyaux de la reserve
				if(gv.getXi()>=gv.getW()*gv.getSizeOfImage() && gv.getXi()<=(gv.getW()+2)*gv.getSizeOfImage() && gv.getYi()>=0 && gv.getYi()<=6*gv.getSizeOfImage()) {
					int z = gv.getW();
					for(int i = 0; i<2;i++) {
						for(int j = 0; j<6; j++) {
							if(gv.getXi()>=gv.getSizeOfImage()*z && gv.getXi()<gv.getSizeOfImage()*(z+1) && gv.getYi()>=gv.getSizeOfImage()*j && gv.getYi()<gv.getSizeOfImage()*(j+1) && gv.getCountTuyaux()[j][i]>0) {
								gv.setT(gv.getTTuyaux()[j][i]);
								if(!gv.getEditor()) {
									gv.getModel().delToReserve(gv.getT());
								}
								sourceW=z;
								sourceH=j;
								gv.setListenerRes(gv.getListTuyaux()[j][i]);
								gv.setReturnRes(gv.getSprites()[j][z]);	
								gv.ImageOfCell(false);
								sourceI=-1;
								sourceJ=-1;
							}
						}
						z++;
					}
				}
				//Deplacer les tuyaux qui sont deja dans la grille 
				else if (gv.getXi()>=0*gv.getSizeOfImage() && gv.getXi()<gv.getW()*gv.getSizeOfImage() && gv.getYi()>=0*gv.getSizeOfImage() && gv.getYi()<gv.getH()*gv.getSizeOfImage()) {
					for(int i = 0; i<gv.getH();i++) {
						for(int j = 0; j<gv.getW();j++) {

							if(i!=0 && j!= 0 && i!=gv.getH()-1 && j!=gv.getW()-1) {
								if((gv.getXi()>=j*gv.getSizeOfImage() && gv.getXi()<(j+1)*gv.getSizeOfImage()) && (gv.getYi()>=i*gv.getSizeOfImage() && gv.getYi()<(i+1)*gv.getSizeOfImage())) {
									if((!gv.getModel().getMainGrille().getCase(i, j).contains("*")) && (gv.getModel().getMainGrille().getSchema(i, j) != null ) ) {
										if(gv.getModel().getMainGrille().getSchema(i, j)!=null) {
											System.out.println("Tuyaux : click " + i +" - " +  j+ gv.getModel().getMainGrille().getSchema(i, j));

											//mis a jour tuyaux
											gv.setT(gv.getModel().getMainGrille().getSchema(i, j));
											sourceI=i;
											sourceJ=j;
											gv.getModel().getMainGrille().getSchema(i, j).resetColor();
											gv.setListenerRes(gv.getModel().getMainGrille().getSchema(i, j).getBufferedImage());
											gv.setSprites(gv.getTuile2(),i,j);
											//quand on click, update model
											gv.getModel().getMainGrille().setCell(i, j, ".");
											gv.getModel().getMainGrille().setSchema(i, j, null);
											gv.getModel().resetTuyauxColor();
											gv.getModel().updateColor(gv.getEditor());
											gv.ImageOfCell(false);
											gv.setReturnRes(gv.getSprites()[i][j]);
										}
									}
								}
							}
							//Deplacer source
							else if(i==0 || j==0 || j==gv.getW()-1 || i==gv.getH()-1){
								if((i!=0 && j!=0) || (j!=gv.getW()-1 && i!=gv.getH()-1) || (i!=0 && j!=gv.getW()-1) || (j!=0 && i!=gv.getH()-1)) {
									if(gv.getEditor()) {
										if(gv.getXi()>=j*gv.getSizeOfImage() && gv.getXi()<(j+1)*gv.getSizeOfImage() && gv.getYi()>=i*gv.getSizeOfImage() && gv.getYi()<(i+1)*gv.getSizeOfImage()) {
											if(gv.getModel().getMainGrille().getSchema(i, j)!=null){
												System.out.println("Source : click " + i +" - " +  j+ gv.getModel().getMainGrille().getSchema(i, j));

												gv.setT(gv.getModel().getMainGrille().getSchema(i, j));
												sourceI=i;
												sourceJ=j;

												gv.setListenerRes(gv.getModel().getMainGrille().getSchema(i, j).getBufferedImage());

												gv.getModel().getMainGrille().setCell(i, j, "X");
												//BAR
												if (i==0 && j==1) {gv.setSprites(gv.mixScore(gv.rotateImage(gv.getBar2(), 0),gv.getModel().getAffichage()),i,j);}
												else if(i==0) 	{ gv.setSprites( gv.rotateImage(gv.getBar2(), 0),i,j);}
												else if(i==gv.getH()-1) { gv.setSprites( gv.rotateImage(gv.getBar2(), gv.getRightAngle()*2),i,j);}
												else if(j==0) 	{ gv.setSprites(gv.rotateImage(gv.getBar2(), gv.getRightAngle()*3),i,j);}
												else if(j==gv.getW()-1) { gv.setSprites(gv.rotateImage(gv.getBar2(),gv.getRightAngle()),i,j);}

												gv.getModel().getMainGrille().setSchema(i, j, null);
												gv.getModel().resetTuyauxColor();
												gv.getModel().updateColor(gv.getEditor());
												gv.ImageOfCell(false);
												gv.setReturnRes(gv.getSprites()[i][j]);
												//System.out.println("t : "+gv.getT().toString());
											}
										}

									}
								}
								else { j=gv.getW(); }
							}
						}
					}
				}
				gv.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Move mo; 
				gv.setXi(e.getX());
				gv.setYi(e.getY());

				System.out.println("--------------------------------------------------------------------------------\n > RELEASE : "+gv.getXi() + " " +gv.getYi());
				if(gv.getXi()>=0*gv.getSizeOfImage() && 
						gv.getXi()<gv.getW()*gv.getSizeOfImage() && 
						gv.getYi()>=0*gv.getSizeOfImage() && 
						gv.getYi()<gv.getH()*gv.getSizeOfImage() 

						) {
					for(int i = 0; i<gv.getH();i++) {
						for(int j = 0; j<gv.getW();j++) {
							if(i!=0 && j!= 0 && i!=gv.getH()-1 && j!=gv.getW()-1) {
								//verifie dans quelle case si la souris est lache 
								//dans la grille
								if((gv.getXi()>=j*gv.getSizeOfImage() && gv.getXi()<(j+1)*gv.getSizeOfImage()) && (gv.getYi()>=i*gv.getSizeOfImage() && gv.getYi()<(i+1)*gv.getSizeOfImage()) && gv.getT()!=null && gv.getT().toString().charAt(0) != 'S') {
									if(gv.getListenerRes()!=null && gv.getReturnRes()!=null) {
										//si lache sur une case vide
										if(gv.getT().toString().equals("V0")) { 
											//System.out.println("Visse/Devisse");
											gv.getModel().lockCase(i,j);
											gv.getModel().updateColor( gv.getEditor());
											gv.ImageOfCell(false);
										}
										else if(gv.getModel().getMainGrille().getSchema(i, j)==null && (gv.getT() != null)) {
											System.out.println("RELEASED dans la Grille  : "+ gv.getXi() + " " +gv.getYi());
											//si la source est la reserve, on la re ajoute car l'update count se fait dans le move
											if(sourceI == -1 && sourceJ == -1) {
												gv.getModel().addToReserve(gv.getT());
											}
											mo =  new Move(gv.getGv(),gv.getT(),sourceI,sourceJ,i,j);
											//remplir stack de undoManager
											gv.undoManager.addEdit(new MoveEdit(mo));
											mo.doit();
											i=gv.getH();
											j=gv.getW();
										}
										//Depose dans une case qui n'est pas vide 
										else  if(gv.getModel().getMainGrille().getSchema(i, j)!=null && gv.getT()!=null){
											//En mode editor nous pouvons modifier la case ou nous deposons	
											if(gv.getEditor()) {
												gv.getModel().addToReserve(gv.getT());
												mo =  new Move(gv.getGv(),gv.getT(),sourceI,sourceJ,i,j);

												//remplir stack de undoManager
												gv.undoManager.addEdit(new MoveEdit(mo));
												mo.doit();
												i=gv.getH();
												j=gv.getW();
											}
											//Retourne dans la reserve si le tuyaux vient de la reserve
											else if(sourceW>=gv.getW() && sourceW<gv.getW()+2 && sourceH>=0 && sourceH<6) {
												System.out.println("RETOUR RESERVE");

												/**
												 * Deplacement rectiligne automatique d'un tuyaux qui est placé
												 */
												//												//n'est pas fonctionnel mais pas d'erreur
												//												Timer timer = new Timer(0,new ActionListener() {
												//													@Override
												//													public void actionPerformed(ActionEvent e) {
												//														//Distance en le point de depart et le point d'arrive
												//														//Recherche equuation des la droite : y = ax+b
												//														float a = (float)(gv.getYo()-gv.getYi())/(float)(gv.getXo()-gv.getXi());
												//														float b = (-a*gv.getXo())+gv.getYi();
												//														//Equation de la droite
												//														x=(float)gv.getXi();
												//														y = a*x+b;			
												//														boolean dec;
												//														if(x>gv.getXi()) {
												//															dec=true;
												//														}
												//														else {
												//															dec=false;
												//														}
												//
												//													    new Thread(new Runnable() {
												//
												//															@Override
												//															public void run() {
												//																while(gv.getXi()!=x && gv.getYi()!=y) {
												//																	gv.getG().translate((int)x,(int)y);
												//																	if(dec) {
												//																		x--;
												//																	}
												//																	else {
												//																		x++;
												//																	}
												//																	y=(a*x)+b;
												//																}
												//																
												//															}
												//													    	
												//													    }).start();												        
												//													}
												//											    });
												//												timer.start();
												//												gv.getG().dispose();

												gv.getModel().addToReserve(gv.getT());
												gv.ImageOfCell(false); //mis a jour image
												gv.repaint();
												i=gv.getH();
												j=gv.getW();
											}
											//Retourne la position initial dans la grille
											else if(sourceW>=1 && sourceW<gv.getW()-1 && sourceH>=1 && sourceH<gv.getH()-1){
												gv.getModel().addToReserve(gv.getT());
												mo =  new Move(gv.getGv(),gv.getT(),sourceI,sourceJ,sourceH,sourceW);

												//remplir stack de undoManagers
												gv.undoManager.addEdit(new MoveEdit(mo));
												mo.doit();
												gv.setSprites(gv.getListenerRes(),sourceH,sourceW);
												gv.ImageOfCell(false); //mis a jour image
												gv.repaint();				
												i=gv.getH();
												j=gv.getW();
												System.out.println("RELEASED dans la Grille non null i : " + sourceW + "- j : " + sourceH);
											}							
											//si lache sur une case locked et undo
											//reserve -> grille : return reserve
											//grille -> grille : reutrn reserve ??
											else if (gv.getModel().getMainGrille().getCase(i, j).contains("*")){
												System.out.println("RELEASED sur une case LOCKED ");
												//remplir stack de undoManagers
												gv.getModel().addToReserve(gv.getT());
												gv.ImageOfCell(false); //mis a jour image
												gv.repaint();
												i=gv.getH();
												j=gv.getW();
											}
										}
										//Retourn dans la reserve tout les autres cas
										else { 
											gv.getModel().addToReserve(gv.getT());
											gv.ImageOfCell(false); //mis a jour image
											gv.repaint();		
											i=gv.getH();
											j=gv.getW();
										}
									}
								}
							}
							//RELEASE BORD
							else if ( i==0 || j==0 || j==gv.getW()-1 || i==gv.getH()-1)  {
								if((i!=0 && j!=0) || (j!=gv.getW()-1 && i!=gv.getH()-1) || (i!=0 && j!=gv.getW()-1) || (j!=0 && i!=gv.getH()-1)) {								
									if(gv.getEditor() && gv.getT()!=null && gv.getT().getModel() == Type.S ) {
										if(gv.getXi()>=j*gv.getSizeOfImage() && gv.getXi()<(j+1)*gv.getSizeOfImage() && gv.getYi()>=i*gv.getSizeOfImage() && gv.getYi()<(i+1)*gv.getSizeOfImage()) {										
											if (i==0 && j==1) {				
												//Return le tuyaux de sa position original à la position final
												gv.getT().setOrientation(Orientation.S);
											}
											else if(i==0) 	{	gv.getT().setOrientation(Orientation.S);	}
											else if(i==gv.getH()-1) {	gv.getT().setOrientation(Orientation.N);	}
											else if(j==0) 	{ 	gv.getT().setOrientation(Orientation.E);	}
											else if(j==gv.getW()-1) {	gv.getT().setOrientation(Orientation.W);	}

											gv.getModel().getMainGrille().setCell(i, j, gv.getT().toStringSrc());
											gv.getModel().getMainGrille().setSchema(i, j, gv.getT());							
											gv.getModel().updateColor(true);						
											gv.ImageOfCell(false); //mis a jour image
											//System.out.println("Tuyaux mis : "+gv.getModel().getMainGrille().getCase(i, j));
											i=gv.getH();
											j=gv.getW();
										}
									}
									else if ( gv.getT()!=null && (gv.getXi()>=j*gv.getSizeOfImage() && gv.getXi()<(j+1)*gv.getSizeOfImage() && gv.getYi()>=i*gv.getSizeOfImage() && gv.getYi()<(i+1)*gv.getSizeOfImage())   )   {
										System.out.println("RELEASED sur le BORDER : " + gv.getT());
										if ( sourceI == -1 && sourceJ == -1 ) gv.getModel().addToReserve(gv.getT());

										mo =  new Move(gv.getGv(),gv.getT(),sourceI,sourceJ,-1,-1);
										//remplir stack de undoManager
										gv.undoManager.addEdit(new MoveEdit(mo));
										mo.doit();

										gv.ImageOfCell(false); //mis a jour image
										gv.repaint();
										i=gv.getH();
										j=gv.getW();
									}
								}
							}
						}
					}
				}
				//Retourne dans la reserve si le tuyaux est depose hors de la grille 
				else  if(gv.getT()!=null && gv.getListenerRes()!=null && gv.getReturnRes()!=null) {
					System.out.println("RELEASED sur la RESERVE : " + gv.getT());
					if ( sourceI == -1 && sourceJ == -1 ) gv.getModel().addToReserve(gv.getT());

					mo =  new Move(gv.getGv(),gv.getT(),sourceI,sourceJ,-1,-1);
					//remplir stack de undoManager
					gv.undoManager.addEdit(new MoveEdit(mo));
					mo.doit();

					gv.ImageOfCell(false); //mis a jour image
					gv.repaint();
				}
				//Lorsque la depose a bien ete effectuer
				//nous devons vider le click de la souris et le retour a la position initial
				//pour effectuer l'action suivante
				gv.getModel().displayP();
				gv.getModel().getMainGrille().displayGrille();
				gv.getModel().getMainGrille().displaySchema();

				gv.setT(null);
				gv.setListenerRes(null);
				gv.setReturnRes(null);
				gv.repaint();
			}

			@Override
			//A recupere l'image et le deplacer a chaque movement
			public void mouseDragged(MouseEvent e) {
				gv.setXi(e.getX());
				gv.setYi(e.getY());
				gv.getG().drawImage(gv.getListenerRes(),0,0,null);
				gv.repaint();
			}
		};
		gv.getGv().addMouseListener(actionListener);
		gv.getGv().addMouseMotionListener(actionListener);

		return actionListener;
	}
}
