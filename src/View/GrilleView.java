package View;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.undo.*;

import controler.HintAction;
import controler.AccueilAction;
import controler.AddListenerAction;
import controler.CancelAction;
import controler.EditorAction;
import controler.GeneratorAction;
import controler.MoveEdit;
import controler.RedoAction;
import controler.ResetAction;
import controler.SaveAction;
import controler.SolutionAction;
import controler.UndoAction;

import model.Couleur;
import model.Orientation;
import model.ExtractSubImage;
import model.Model;
import model.Type;
import model.Move;
import model.Tuyaux;


public class GrilleView extends JPanel{


	private static final long serialVersionUID = 1L;
	private int sizeOfImage = 120;
	private Tuyaux[][] tTuyaux;
	private Tuyaux t;
	private int sourceI,sourceJ;

	private ExtractSubImage subImages;
	private Model gl;
	//private final int size=getSizeOfImage();
	private final int rightAngle=90;

	//ressource images
	private int h, w, k;
	private BufferedImage[][] sprites;
	private JFrame frameglobal;
	private AccueilView acc;

	private BufferedImage[][] listTuyaux;
	private int[][] countTuyaux;

	//tuyaux recuperer a la souris
	private BufferedImage listenerRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(),BufferedImage.TYPE_INT_ARGB);
	private Graphics g = getListenerRes().createGraphics();
	private BufferedImage returnRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(),BufferedImage.TYPE_INT_ARGB);

	//Le tuyaux qui retournera dans le reserve s'il n'est pas bien place
	private int xi,yi, xo, yo;
	private BufferedImage tuile2 = new BufferedImage(getSizeOfImage(),getSizeOfImage(),BufferedImage.TYPE_INT_ARGB);
	private BufferedImage bar2 = new BufferedImage(getSizeOfImage(),getSizeOfImage(),BufferedImage.TYPE_INT_ARGB);
	
	//action
	public UndoManager undoManager;
	ImageIcon undoIcon,redoIcon;
	public UndoAction undoAction;
	public RedoAction redoAction;	
	private ResetAction resetAction;
	public HintAction hintAction;
	private AccueilAction accueilAction;
	private SolutionAction solutionAction;
	private GeneratorAction generatorAction;
	private EditorAction editorAction;
	private SaveAction saveAction;
	private CancelAction cancelAction;
	private AddListenerAction addListener;


	//Attribut pour Editor
	//true si le mode editor est actif
	private Boolean editor=false;
	private int inf = Integer.MAX_VALUE;
	private File newLevel;
	
	//Attribut pour JPopupMenu
	private JPopupMenu popup = new JPopupMenu();
	private ArrayList<JMenuItem> listSrc;
	private ArrayList<BufferedImage> tuyauxSource;
	private JButton source = new JButton("Extra");
	
	JMenuItem srcYellow = new JMenuItem("Source Jaune");
	JMenuItem srcRed = new JMenuItem("Source Rouge");
	JMenuItem srcGreen = new JMenuItem("Source Vert");
	JMenuItem srcBlue = new JMenuItem("Source Bleu");
	JMenuItem visser = new JMenuItem("Visser/Devisser");
	JMenuItem addLigne = new JMenuItem("Ajoute Ligne");
	JMenuItem remLigne = new JMenuItem("Supprime Ligne");
	JMenuItem addCol = new JMenuItem("Ajoute Colonne");
	JMenuItem remCol = new JMenuItem("Supprime Colonne");
	
	//List for Key
	private ArrayList<JButton> listButton = new ArrayList<JButton>();
	private ArrayList<KeyStroke> listKey = new ArrayList<KeyStroke>();
	
	public GrilleView(Model l, JFrame f, AccueilView acc) {
		final String imagedir = "images/";
		this.frameglobal = f;
		this.acc=acc;

		this.gl = l;
		this.h = gl.getMainGrille().getHeight();
		this.w = gl.getMainGrille().getWidth();
		this.setXi(0);
		this.setYi(0);
		addListener = new AddListenerAction(this.getGv());
		listTuyaux = new BufferedImage[6][2];
		countTuyaux = new int[6][2];
		listSrc = new ArrayList<JMenuItem>();
		tuyauxSource = new ArrayList<BufferedImage>();

		//pour avoir un nombre de ligne minimum pour la reserve
		k = getH();
		if (k<6) {k=6;}

		//taille d'ecran 
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


		System.out.println("h = "+getH()+ " w = "+ getW() );
		this.setLayout(new GridLayout(k+1,getW()+2));
		this.setBackground(Color.black);
		//if(screenSize.getWidth() <1000) { sizeOfImage=70; }
		sizeOfImage=(int) (screenSize.getHeight()/10);
		
		System.out.println("La taille d'Image actuelle = " + sizeOfImage);
		subImages = new ExtractSubImage(getSizeOfImage());
		frameglobal.addMouseListener(addListener.addListener());
			
		resizeGrilleView();
		
		setMenu(acc);

		/*undo et redo*/
		undoManager = new UndoManager();
		undoIcon = getIcon(imagedir + "undo.png");
		redoIcon = getIcon(imagedir + "redo.png");
		undoAction.updateEnabled();
		redoAction.updateEnabled();
		
		//vider la grille et generer la grill a cote, et generer les Tuyaux
		gl.createTuyaux(gl.getMainGrille(),true,subImages);
		//update color
		gl.updateColor(false);
		//display the grille after clean
		gl.getMainGrille().displayGrille();
		gl.getMainGrille().displaySchema();
		
		ImageOfCell(false);
	}

	public void resizeGrilleView() {
		frameglobal.setPreferredSize(new Dimension(getSizeOfImage()*(getW()+2), getSizeOfImage()*(k+2)));
		frameglobal.getContentPane().removeAll();
		frameglobal.revalidate();
		frameglobal.repaint();
		frameglobal.getContentPane().add(this);
		frameglobal.pack();
		frameglobal.setVisible(true);
	}
	
	//remplir sprites
	public void ImageOfCell(boolean test) {
		long startTime,endTime,duration;
		startTime = System.currentTimeMillis();
		System.out.println("------------------------------ Begin ImageOfCell ------------------------------");

		String s;

		//border images
		BufferedImage corner, bar, tuile, tuilelock, tuile2use, tuileEmpty;

		//border images
		corner = subImages.getResult().get(6).get(3);
		bar = subImages.getResult().get(6).get(4);
		tuile = subImages.getResult().get(6).get(0);
		tuilelock = mixImage(tuile, subImages.getResult().get(6).get(5), 0); //tuile + lock
		tuileEmpty = subImages.getResult().get(6).get(2); //tuile dark gary
		System.out.println("INIT SPRITE " + k +" " +  (getW()+2));
		sprites= new BufferedImage[k][getW()+2];

		if(!editor) {
			tTuyaux = new Tuyaux[6][2];
			saveAction.setEnabled(false);
			cancelAction.setEnabled(false);
			source.setEnabled(false);
			hintAction.setEnabled(true);
			solutionAction.setEnabled(true);
			generatorAction.setEnabled(false);
			//Counter de tuyaux dans la reserve
			getCountTuyaux()[0][0] = gl.getResP()[5][0];
			getCountTuyaux()[1][0] = gl.getResP()[1][0];
			getCountTuyaux()[2][0] = gl.getResP()[3][1];
			getCountTuyaux()[3][0] = gl.getResP()[3][0];
			getCountTuyaux()[4][0] = gl.getResP()[4][0];
			getCountTuyaux()[5][0] = gl.getResP()[4][3];

			getCountTuyaux()[0][1] = gl.getResP()[2][0];
			getCountTuyaux()[1][1] = gl.getResP()[1][1];
			getCountTuyaux()[2][1] = gl.getResP()[3][2];
			getCountTuyaux()[3][1] = gl.getResP()[3][3];
			getCountTuyaux()[4][1] = gl.getResP()[4][1];
			getCountTuyaux()[5][1] = gl.getResP()[4][2];

			//Display de la reserve
			getSprites()[0][getW()] = displayReserve(5, 0);//C0
			getSprites()[1][getW()] = displayReserve(1, 0);//L0
			getSprites()[2][getW()] = displayReserve(3, 1);//T1
			getSprites()[3][getW()] = displayReserve(3, 0);//T0
			getSprites()[4][getW()] = displayReserve(4, 0);//F0
			getSprites()[5][getW()] = displayReserve(4, 3);//F3

			getSprites()[0][getW()+1] = displayReserve(2, 0);//O0
			getSprites()[1][getW()+1] = displayReserve(1, 1);//L1
			getSprites()[2][getW()+1] = displayReserve(3, 2);//T2
			getSprites()[3][getW()+1] = displayReserve(3, 3);//T3
			getSprites()[4][getW()+1] = displayReserve(4, 1);//F1
			getSprites()[5][getW()+1] = displayReserve(4, 2);//F2
		}
		else {
			saveAction.setEnabled(true);
			cancelAction.setEnabled(true);
			source.setEnabled(true);
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);
			hintAction.setEnabled(false);
			solutionAction.setEnabled(false);
			generatorAction.setEnabled(true);
			//vider les changements dans la stack
			undoManager.discardAllEdits();
			
			//en mode editeur le nombre de tuyaux est infini
			int z = getW();
			for(int k = 0; k<2;k++) {
				for(int l = 0; l<6; l++) {
					setSprites(mixImage(getTuile2(),listTuyaux[l][k],0),l,z);
					setCountTuyaux(inf,l,k);
				}
				z++;
			}
		}

		//parsing schema
		for (int i=0; i<getH(); i++) {//pour chaque ligne
			for (int j=0; j <getW(); j++) {//pour chaque col
				s = gl.getMainGrille().getCase(i, j);

				//definie 2 types de tuile : normal ou avec lock
				if(s.charAt(0)=='*') { tuile2use = tuilelock;}
				else {tuile2use = tuile; }

				if (s.equals("X")) {
					//CORNER
					if((i==0 && j==0))		 { getSprites()[i][j] = corner;/*sprites[i][j].setImage(corner);*/}
					else if(i==0 && j==getW()-1)  { getSprites()[i][j] = rotateImage(corner, getRightAngle());}
					else if(i==getH()-1 && j==0)  {getSprites()[i][j] = rotateImage(corner, getRightAngle()*3);}
					else if(i==getH()-1 && j==getW()-1){ getSprites()[i][j] = rotateImage(corner, getRightAngle()*2);}

					//BAR
					else if (i==0 && j==1) {getSprites()[i][j] = mixScore(rotateImage(bar, 0),gl.getAffichage());}
					else if(i==0) 	{ getSprites()[i][j] = rotateImage(bar, 0);}
					else if(i==getH()-1) { getSprites()[i][j] = rotateImage(bar, getRightAngle()*2);}
					else if(j==0) 	{ getSprites()[i][j] = rotateImage(bar, getRightAngle()*3);}
					else if(j==getW()-1) { getSprites()[i][j] = rotateImage(bar, getRightAngle());}

					//CONTROL EXCEPTION
					else {
						System.out.println("fichier incorrect");
						System.exit(1);
					}

				}
				else 
					if(s.equals(".") || s.equals("-")){  getSprites()[i][j] = tuile2use;	}

				//a cell locked, can't put tuyaux : level diff
					else if(s.equals("*.")) {	getSprites()[i][j] = tuileEmpty;	}

					//for source
					else if((s.charAt(0) == 'R') || (s.charAt(0) == 'B') || (s.charAt(0) == 'G') || (s.charAt(0) == 'Y')) {

						//display border
						if((i==0 && j==0))		 { tuile2use = rotateImage(corner,0);}
						else if(i==0 && j==getW()-1)  { tuile2use= rotateImage(corner, getRightAngle());}
						else if(i==getH()-1 && j==0)  { tuile2use = rotateImage(corner, getRightAngle()*3);}
						else if(i==getH()-1 && j==getW()-1){ tuile2use = rotateImage(corner, getRightAngle()*2);}	
						else if (i==0 && j==1) {tuile2use = mixScore(rotateImage(bar, 0),gl.getAffichage());}
						else if(i==0) 	{ tuile2use = rotateImage(bar, 0);}
						else if(i==getH()-1) { tuile2use = rotateImage(bar, getRightAngle()*2);}
						else if(j==0) 	{ tuile2use = rotateImage(bar, getRightAngle()*3);}
						else if(j==getW()-1) { tuile2use = rotateImage(bar, getRightAngle());}

						//R2 : bar + imageTuyaux(R)
						getSprites()[i][j] =mixImage(tuile2use, gl.getMainGrille().getSchema(i, j).getBufferedImage(), 0);
					}
					//for tuyaux
					else if (gl.getMainGrille().getSchema(i, j) != null) {

						//si c'est pas source, on a pas besoin les bar
						getSprites()[i][j] = mixImage(tuile2use, gl.getMainGrille().getSchema(i, j).getBufferedImage(), 0);
					}
					else {
						System.out.println("Erreur GrilleView i="+i + " j=" +j);
						System.exit(1);

					}
			}//fin for j

		}//fin for i

		//Tuyaux de la reserve ( sans count)
		listTuyaux[0][0] = rotateImage(subImages.getResult().get(0).get(5),90*0);
		listTuyaux[1][0] = rotateImage(subImages.getResult().get(0).get(1),90*0);
		listTuyaux[2][0] = rotateImage(subImages.getResult().get(0).get(3),90*1);
		listTuyaux[3][0] = rotateImage(subImages.getResult().get(0).get(3),90*0);
		listTuyaux[4][0] = rotateImage(subImages.getResult().get(0).get(4),90*0);
		listTuyaux[5][0] = rotateImage(subImages.getResult().get(0).get(4),90*3);

		listTuyaux[0][1] = mixImage(subImages.getResult().get(0).get(1),subImages.getResult().get(0).get(2),90*0);
		listTuyaux[1][1] = rotateImage(subImages.getResult().get(0).get(1),90*1);
		listTuyaux[2][1] = rotateImage(subImages.getResult().get(0).get(3),90*2);
		listTuyaux[3][1] = rotateImage(subImages.getResult().get(0).get(3),90*3);
		listTuyaux[4][1] = rotateImage(subImages.getResult().get(0).get(4),90*1);
		listTuyaux[5][1] = rotateImage(subImages.getResult().get(0).get(4),90*2);
		
		for(int i = 1; i<=4;i++) {
			tuyauxSource.add(subImages.getResult().get(i).get(0));
		}

		//Model du Tuyaux
		tTuyaux[0][0] = new Tuyaux(Type.C, Orientation.N, Couleur.W,subImages);
		tTuyaux[1][0] = new Tuyaux(Type.L, Orientation.N, Couleur.W,subImages);
		tTuyaux[2][0] = new Tuyaux(Type.T, Orientation.E, Couleur.W,subImages);
		tTuyaux[3][0] = new Tuyaux(Type.T, Orientation.N, Couleur.W,subImages);
		tTuyaux[4][0] = new Tuyaux(Type.F, Orientation.N, Couleur.W,subImages);
		tTuyaux[5][0] = new Tuyaux(Type.F, Orientation.W, Couleur.W,subImages);

		tTuyaux[0][1] = new Tuyaux(Type.O, Orientation.N, Couleur.W,subImages);
		tTuyaux[1][1] = new Tuyaux(Type.L, Orientation.E, Couleur.W,subImages);
		tTuyaux[2][1] = new Tuyaux(Type.T, Orientation.S, Couleur.W,subImages);
		tTuyaux[3][1] = new Tuyaux(Type.T, Orientation.W, Couleur.W,subImages);
		tTuyaux[4][1] = new Tuyaux(Type.F, Orientation.E, Couleur.W,subImages);
		tTuyaux[5][1] = new Tuyaux(Type.F, Orientation.S, Couleur.W,subImages);
		
		endTime = System.currentTimeMillis();
		duration=endTime - startTime;
		System.out.println("ImageOfCell Duration = " +duration);  //divide by 1000000 to get milliseconds.
		System.out.println("------------------------------ End ImageOfCell ------------------------------\n");

	}
	


	//creer le menu qui contient les buttons
	public void setMenu(JPanel acc) {
		//les actions 
		undoAction = new UndoAction("Undo", undoIcon,this.getGv());
		redoAction = new RedoAction("Redo", redoIcon,this.getGv());	
		resetAction = new ResetAction("Reset", undoIcon,this.getGv());
		hintAction = new HintAction("Hint", undoIcon, this.getGv());
		accueilAction = new AccueilAction("Accueil", undoIcon, this.getGv());
		solutionAction = new SolutionAction("Solution", undoIcon,this.getGv());
		generatorAction = new GeneratorAction("Generateur", undoIcon, this.getGv());
		editorAction = new EditorAction("Editor", undoIcon, this.getGv());
		saveAction = new SaveAction("Save", undoIcon, this.getGv());
		cancelAction = new CancelAction("Cancel", undoIcon, this.getGv());
		
		JButton accueil = new JButton(accueilAction);
		JButton generator = new JButton(generatorAction);
		JButton hint = new JButton(hintAction);
		JButton solution = new JButton(solutionAction);
		JButton undo = new JButton(undoAction);
		JButton redo = new JButton(redoAction);
		JButton reset = new JButton(resetAction);
		JButton editor = new JButton(editorAction);
		JButton save = new JButton(saveAction);
		JButton cancel = new JButton(cancelAction);
		
		listButton.add(accueil);

		listButton.add(hint);
		listButton.add(solution);
		listButton.add(undo);
		listButton.add(redo);
		listButton.add(reset);
		listButton.add(editor);
		listButton.add(generator);
		listButton.add(save);
		listButton.add(cancel);
		
		ArrayList<String> listString = new ArrayList<String>();
		listString.add("accueil");
        listString.add("hint");
        listString.add("solution");
        listString.add("undo");
        listString.add("redo");
        listString.add("reset");
        listString.add("editor");
        listString.add("generator");
        listString.add("save");
        listString.add("cancel");
		
		KeyStroke keyQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false);	
		KeyStroke keyG = KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false);	
		KeyStroke keyH = KeyStroke.getKeyStroke(KeyEvent.VK_H, 0, false);	
		KeyStroke keyA = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false);	
		KeyStroke keyZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false);	
		KeyStroke keyY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false);	
		KeyStroke keyR = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false);	
		KeyStroke keyE = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false);	
		KeyStroke keyS = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false);	
		KeyStroke keyW = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false);	
		 
		listKey.add(keyQ);
        listKey.add(keyH);
        listKey.add(keyA);
        listKey.add(keyZ);
        listKey.add(keyY);
        listKey.add(keyR);
        listKey.add(keyE);
        listKey.add(keyG);
        listKey.add(keyS);
        listKey.add(keyW);
		

		JMenuBar menuBar = new JMenuBar();

		menuBar.setLayout(new GridLayout(2,6));
		
		//ajoute des buttons sur MenuBar	
		for(int i = 0; i<listButton.size(); i++) {
			menuBar.add(listButton.get(i));
		}
		menuBar.add(source);
		
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
		source.setEnabled(false);
		generatorAction.setEnabled(false);
		
		 InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		 ActionMap am = getActionMap();
		 for(int i = 0; i<listKey.size();i++) {
			 int z = i;
			 im.put(listKey.get(z), listString.get(z));
		     am.put(listString.get(z), new AbstractAction(){
		    	 /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
		    	 public void actionPerformed(ActionEvent e){
		    		 listButton.get(z).doClick();
		    	 }
		     });
		     
		 }
		if(gl.getAffichage().endsWith("_NE")) {editorAction.setEnabled(false);} 
		
		listSrc = new ArrayList<JMenuItem>();
		listSrc.add(srcRed);
		listSrc.add(srcGreen);
		listSrc.add(srcBlue);
		listSrc.add(srcYellow);
		listSrc.add(visser);
		listSrc.add(addLigne);
		listSrc.add(remLigne);
		listSrc.add(addCol);
		listSrc.add(remCol);
			
		for(int i = 0; i<listSrc.size(); i++) {
			popup.add(listSrc.get(i));
		}

        source.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        srcRed.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {              
               System.out.println("Source Rouge");  
               if(getModel().getMainGrille().getCase(0,getW()-1).equals("X")) {
					getModel().getMainGrille().setCell(0, getW()-1, "R2");
					getModel().getMainGrille().setSchema(0, getW()-1,new Tuyaux(Type.S, Orientation.S, Couleur.R,subImages));							
					getModel().updateColor(getEditor());
					getModel().getMainGrille().displayGrille();
					getModel().getMainGrille().displaySchema();					
					ImageOfCell(false); 
               }    
            }  
         });  
        srcGreen.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {              
               System.out.println("Source Verte");               
               if(getModel().getMainGrille().getCase(0,getW()-1).equals("X")) {
					getModel().getMainGrille().setCell(0, getW()-1, "G2");
					getModel().getMainGrille().setSchema(0, getW()-1,new Tuyaux(Type.S, Orientation.S, Couleur.G,subImages));							
					getModel().updateColor(getEditor());	
					getModel().getMainGrille().displayGrille();
					getModel().getMainGrille().displaySchema();					
					ImageOfCell(false); 
               }             
            }  
         });  
        srcBlue.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {              
               System.out.println("Source Bleu");  
               if(getModel().getMainGrille().getCase(0,getW()-1).equals("X")) {
					getModel().getMainGrille().setCell(0, getW()-1, "B2");
					getModel().getMainGrille().setSchema(0, getW()-1,new Tuyaux(Type.S, Orientation.S, Couleur.B,subImages));							
					getModel().updateColor(getEditor());	
					getModel().getMainGrille().displayGrille();
					getModel().getMainGrille().displaySchema();					
					ImageOfCell(false); 
               }    
            }  
         });  
        srcYellow.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {              
               System.out.println("Source Jaune");  
               if(getModel().getMainGrille().getCase(0,getW()-1).equals("X")) {
					getModel().getMainGrille().setCell(0, getW()-1, "Y2");
					getModel().getMainGrille().setSchema(0, getW()-1,new Tuyaux(Type.S, Orientation.S, Couleur.Y,subImages));							
					getModel().updateColor(getEditor());	
					getModel().getMainGrille().displayGrille();
					getModel().getMainGrille().displaySchema();					
					ImageOfCell(false); 
               }    
            }  
         });  
        visser.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {              
               System.out.println("Visser/Devisser");  
               if(getModel().getMainGrille().getCase(0,getW()-1).equals("X")) {
					getModel().getMainGrille().setCell(0, getW()-1, "*");
					getModel().getMainGrille().setSchema(0, getW()-1,new Tuyaux(Type.V, Orientation.N, Couleur.B,subImages));							
					getModel().updateColor(getEditor());	
					getModel().getMainGrille().displayGrille();
					getModel().getMainGrille().displaySchema();					
					ImageOfCell(false); 
               }    
            }  
         });
        
        addLigne.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {  
	                getModel().incrHeight();
		       		h = gl.getMainGrille().getHeight();
		    		k = getH();
		    		if (k<6) {k=6;}    		
		    		resizeGrilleView();
		    		gl.getMainGrille().displayGrille();
		    		gl.getMainGrille().displaySchema();
					ImageOfCell(true); 
            }  
         });
  
        remLigne.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {  
            	if(h>=4) {
	                getModel().decrHeight();
		       		h = gl.getMainGrille().getHeight();
		    		k = getH();
		    		if (k<6) {k=6;}
		    		resizeGrilleView();
		    		gl.getMainGrille().displayGrille();
		    		gl.getMainGrille().displaySchema();
					ImageOfCell(true); 
            	}
            }  
         });
        
        addCol.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {  
                getModel().incrWidth();
	       		w = gl.getMainGrille().getWidth();
	       		resizeGrilleView();
	    		gl.getMainGrille().displayGrille();
	    		gl.getMainGrille().displaySchema();
				ImageOfCell(true); 
            }  
         });
        
        remCol.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) { 
            	if(w>=4) {
                getModel().decrWidth();
                w = gl.getMainGrille().getWidth();
                resizeGrilleView();
	    		gl.getMainGrille().displayGrille();
	    		gl.getMainGrille().displaySchema();
				ImageOfCell(true);
            	}
            }  
         });
        
		frameglobal.setJMenuBar(menuBar);

	}

	public BufferedImage displayReserve(int model, int dir) {
		int count = gl.getResP()[model][dir]+1;
		count--;
		int used = 5; //black
		BufferedImage tuile = subImages.getResult().get(6).get(0);

		if (count > 0) {
			used=0;  //white
		}
		if (model==2) {
			return mixCount(mixImage(tuile, 
					mixImage(subImages.getResult().get(used).get(1), 
							subImages.getResult().get(used).get(model),0), 0), count);
		}
		return mixCount(rotateImage(mixImage(tuile, 
				subImages.getResult().get(used).get(model), 0), 
				getRightAngle()*dir), count);
	}

	public BufferedImage mixImageBis(BufferedImage b1, BufferedImage b2) {

		BufferedImage mixRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = mixRes.getGraphics();
		g.drawImage(b1,0,0,null);
		g.drawImage(b2, 0, 0, null);
		g.dispose();

		return mixRes;
	}

	public BufferedImage mixImage(BufferedImage b1, BufferedImage b2, int degree) {

		BufferedImage mixRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = mixRes.getGraphics();
		g.drawImage(rotateImage(b1, degree), 0, 0, null);
		g.drawImage(b2, 0, 0, null);
		g.dispose();

		return mixRes;
	}

	public BufferedImage mixScore(BufferedImage b1, String s) {

		BufferedImage mixRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = mixRes.getGraphics();

		g.drawImage(b1, 0, 0, null);
		g.setColor(Color.WHITE);

		g.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 

		g.drawString(s, 10, 20);
		g.dispose();

		return mixRes;
	}

	public BufferedImage mixCount(BufferedImage b1, int count) {

		BufferedImage mixRes = new BufferedImage(getSizeOfImage(),getSizeOfImage(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = mixRes.getGraphics();

		g.drawImage(b1, 0, 0, null);
		g.setColor(Color.WHITE);

		g.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 

		g.drawString(Integer.toString(count) , 10, getSizeOfImage()-10);
		g.dispose();

		return mixRes;
	}

	public BufferedImage rotateImage(BufferedImage bi, double degree) {

		double radian = Math.toRadians(degree);
		double sin = Math.abs(Math.sin(radian)), cos = Math.abs(Math.cos(radian));
		int w = bi.getWidth(), h = bi.getHeight();
		int nw = (int )Math.floor((w*cos)+(h*sin));
		int nh = (int )Math.floor((h*cos)+(w*sin));
		int posx = w/2, posy = h/2;

		BufferedImage res = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = res.createGraphics();
		AffineTransform trans = new AffineTransform();
		trans.translate((nw-w)/2, (nh-h)/2);
		trans.rotate(radian, posx, posy);
		g.setTransform(trans);
		g.drawImage(bi, 0, 0, this);
		g.dispose();

		return res;
	}
	

	//display
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//test sprites
		for (int i=0; i<k; i++) {
			for (int j=0; j<getW()+2; j++) {
				g.drawImage(getListenerRes(), xi-getSizeOfImage()/2, yi-getSizeOfImage()/2,null);
				g.drawImage(getSprites()[i][j], j*getSizeOfImage(), i*getSizeOfImage(), null); //print sprites
				repaint();
			}
		}
	}


	// Getters /Setters
	public JFrame getFrameG() { return frameglobal; }
	public AccueilView getAcc() { return acc; }
	public Model getModel() { return gl; }
	public GrilleView getGv() { return this; }
	public ImageIcon getIcon(String file) { return new ImageIcon(file); }
	public Boolean getEditor() { return editor;}
	public void setEditorTrue() {editor=true;}
	public void setEditorFalse() { editor=false; }
	public int getH() {h=gl.getMainGrille().getHeight() ; return h;}
	public int getW() { w=gl.getMainGrille().getWidth() ; return w; }
	public Tuyaux[][] getTTuyaux() { return tTuyaux; }
	public File getNewLevel() { return newLevel;}
	public void setNewLevel(File f) { newLevel = f;} 
	public BufferedImage getTuile2() { return tuile2;}
	public void setTuile2(BufferedImage bi) { tuile2=bi;}
	public ExtractSubImage getSubImages() {return subImages;}
	public int getXi() {return xi;}
	public void setXi(int xi) {this.xi = xi;}
	public int getYi() {return yi;}
	public void setYi(int yi) {this.yi = yi;}
	public Graphics getG() {return g;}
	public BufferedImage getListenerRes() {return listenerRes;}
	public void setListenerRes(BufferedImage listenerRes) {	this.listenerRes = listenerRes;}
	public BufferedImage[][] getListTuyaux(){return listTuyaux;}
	public BufferedImage getReturnRes() {return returnRes;}
	public void setReturnRes(BufferedImage returnRes) {this.returnRes = returnRes;}
	public int getSizeOfImage() {return sizeOfImage;}
	public int[][] getCountTuyaux() {return countTuyaux;}
	public void setCountTuyaux(int countTuyaux,int i, int j) {this.countTuyaux[i][j] = countTuyaux;}
	public BufferedImage[][] getSprites() {return sprites;	}
	public void setSprites(BufferedImage sprites,int i, int j) {this.sprites[i][j] = sprites;}
	public void setBar2(BufferedImage bufferedImage) { bar2=bufferedImage;}
	public BufferedImage getBar2() {return bar2;}
	public EditorAction getEditorAction() {	return editorAction;}
	public int getRightAngle() { return rightAngle;}
	public Tuyaux getT() {return t;}
	public void setT(Tuyaux t) {this.t = t;}
	public int getXo() {return xo;}
	public int getYo() {return yo;}
	public void setXo(int x) { xo=x;}
	public void setYo(int y) { yo=y;}

}