package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Model {

	private int height, width;
	private int [][] resP;
	private int nbfuite;
	private ArrayList<String> exclure;
	private Grille mainGrille, mainGrilleSolution;
	private File file;
	private Scanner scanner;
	private String affichage,nomLevel;
	private ArrayList<Couleur> listcol;
	private boolean fuite, poly,n,e,s,w;
	private ExtractSubImage esi;
	private boolean step2;
	Tuyaux sauvegarde,current, next=null ;
	Couleur ct ,ct2,cu;

	public Model(String chemin,String nomLevel) throws FileNotFoundException{

		String s;
		this.nomLevel=nomLevel;
		affichage=nomLevel;
		file = new File(chemin);
		scanner = new Scanner(file);
		height = scanner.nextInt();
		width = scanner.nextInt();

		//create a grille with height and width in the file
		mainGrille = new Grille(height, width);
		mainGrilleSolution = new Grille(height, width);

		resP = new int [6][4];
		listcol =new ArrayList<Couleur>();
		exclure = new ArrayList<String>(Arrays.asList(".", "X"));

		//fill the grille
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				s = scanner.next();
				//setting the main grill
				mainGrille.setCell(i, j, s);
				mainGrilleSolution.setCell(i, j, s);
			}
		}
		scanner.close();

	}

	public void reset(Boolean edit) {
		System.out.println("RESET MODEL => " + getMainGrille().getHeight()+ getMainGrille().getWidth() +" " + height + " " + width);
		//reset reserve
		for(int i=0; i<6; i++) {//pour chaque ligne
			for(int j=0; j<4; j++) {//pour chaque col
				resP[i][j]=0;
			}
		}
		height=mainGrilleSolution.getHeight();
		width=mainGrilleSolution.getWidth();
		mainGrille.setHeight(mainGrilleSolution.getHeight());
		mainGrille.setWidth(mainGrilleSolution.getWidth());

		//reset grille et le schema
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				mainGrille.setCell(i, j, mainGrilleSolution.getCase(i, j));	
				mainGrille.setSchema(i, j, null);
			}
		}

		System.out.println("Reset OK!!");
		createTuyaux(mainGrille, true,esi);
		updateColor(edit);
		mainGrille.displayGrille();
		mainGrille.displaySchema();
	}


	//calcule le nombre de tuyaux total de la reserve
	public int totalReserve() {
		int r=0;
		for(int i=0; i<6; i++) {//pour chaque ligne
			for(int j=0; j<4; j++) {//pour chaque col
				r+=resP[i][j];
			}
		}
		return r;
	}


	public int updateColor(Boolean edit) {

		long startTime,endTime,duration;
		boolean checkwin=true;
		fuite=false;
		nbfuite=0;
		poly=false;
		listcol.clear();
		startTime = System.currentTimeMillis();
		resetTuyauxColor();

		//Pour toutes les sources , on les ajoute a l'arraylist Listcol si c'est la premiere source analysee, sinon, checkwin = false
		//on colorie dans les directions de la source , sans condition d'omission sur colorieDansLesDirections()
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {

				//si la case est pas null et son type est une source et elle n'a pas ete visitee
				if ( mainGrille.getSchema(i,j) != null && mainGrille.getSchema(i,j).getModel() == Type.S && mainGrille.getSchema(i,j).getIsVisted() == false  ) {

					//si la couleur de cette case NOT IN la liste de couleur : ajoute cette couleur
					if (!listcol.contains(mainGrille.getSchema(i,j).getColor()) ) {
						listcol.add(mainGrille.getSchema(i,j).getColor());
					}
					//sinon : pas gagner
					else {
						checkwin = false;
					}		
					colorieDansLesDirections(i,j,null);	
				}
			}
		}

		if( checkwin && fuite && !poly && (getNbTuyauxColor() > 0 || !isMonoSource() ) )  {
			return 1; 
		}


		if(getNbTuyauxColor() == 0 && isMonoSource() && !step2 ) {System.out.println("PAS DE TUYAUX ! "+ getNbTuyauxColor() ); return 2;}

		//s'il y a une fuite, pas gagner
		if(fuite || poly) {
			System.out.println("Ca fuit ! isPoly : " + poly + " - TuyauxColor : " + getNbTuyauxColor());			
			checkwin=false;
		}

		// 0 = true  1 = ok mais fuite et non poly  2 == ko
		if(checkwin) {
			if (edit) { affichage = "Complet !";}
			else {affichage = "Bravo !";}
		}
		else {
			if (edit) { affichage = "EDIT";}
			else { affichage=nomLevel;}
		}

		endTime = System.currentTimeMillis();
		duration=endTime - startTime;
		System.out.println("updateColor Duration = " +duration);  //divide by 1000000 to get milliseconds.

		if (checkwin) { return 0;}
		else {return 2;}

	}

	public void colorieDansLesDirections(int i , int j,Orientation MaisPasIci ) {
		Tuyaux current, next=null ;
		Couleur ct ,cu;
		current = mainGrille.getSchema(i, j); 	//Case a colorier

		//Si la case nord existe et qu'elle est liee
		//NORD
		if (MaisPasIci != Orientation.N && current.getOuverture(0)) {
			next = mainGrille.getSchema(i - 1, j);
			if ((next != null) && (next.getOuverture(2))) {
				ct = current.getColor(); // couleur case current
				cu = next.getColor(); // couleur case nord
				if(ct != cu) {
					// si la couleur de la case nord est differente => polychrome
					if (next.getModel() != Type.S && ct != Couleur.W && cu != Couleur.W ) {
						poly=true;
						next.setColor(Couleur.BL); // mis a jour color courant en polychrome
						colorieDansLesDirections(i - 1, j,Orientation.S);

					} else if (cu == Couleur.W ) {
						next.setColor(ct);
						colorieDansLesDirections(i - 1, j,Orientation.S);
					}
				}
				else if(next.getModel() == Type.S) {next.setIsVisted(true);}
			}else {
				fuite=true;}
			next=null;
		}

		//EST		
		if (MaisPasIci != Orientation.E && current.getOuverture(1)) {
			next = mainGrille.getSchema(i, j + 1);
			if ((next != null) && (next.getOuverture(3))) {
				if (current.getModel() != Type.O) {	ct = current.getColor();	} else {ct = current.getColor2();	}
				if (next.getModel() != Type.O) { 	cu = next.getColor();	} else {cu = next.getColor2();	}
				if(ct != cu) {
					if (next.getModel() != Type.S && ct != Couleur.W && cu != Couleur.W) {
						poly=true;
						if (next.getModel() != Type.O) {
							next.setColor(Couleur.BL);
						} else {
							next.setColor2(Couleur.BL);
						}
						colorieDansLesDirections(i, j + 1,Orientation.W);
					} else if (cu == Couleur.W ) {
						if (next.getModel() != Type.O) {
							next.setColor(ct);
						} else {
							next.setColor2(ct);
						}
						colorieDansLesDirections(i, j + 1,Orientation.W);
					}
				}
				else if(next.getModel() == Type.S) {next.setIsVisted(true);}
			}else {
				fuite=true;}
			next=null;
		}

		//SUD
		if (MaisPasIci != Orientation.S && current.getOuverture(2)) {
			next = mainGrille.getSchema(i + 1, j);
			if ((next != null) && (next.getOuverture(0))) {
				ct = current.getColor(); // couleur case current
				cu = next.getColor(); // couleur case nord
				if(ct != cu) {
					if (next.getModel() != Type.S && ct != Couleur.W && cu != Couleur.W ) {
						poly=true;
						next.setColor(Couleur.BL);
						colorieDansLesDirections(i + 1, j,Orientation.N);
					} else if (cu == Couleur.W ) {
						next.setColor(ct);
						colorieDansLesDirections(i + 1, j,Orientation.N);
					}
				}
				else if(next.getModel() == Type.S) {
					next.setIsVisted(true);
				}
			}else {
				fuite=true;
			}
			next=null;
		}

		//OUEST
		if (MaisPasIci != Orientation.W &current.getOuverture(3)) {
			next = mainGrille.getSchema(i, j-1);
			if( (next!=null) && (next.getOuverture(1))  ) {
				if (current.getModel() != Type.O) {ct=current.getColor(); } else {ct=current.getColor2();}
				if (next.getModel() != Type.O) {cu=next.getColor(); } else {cu=next.getColor2();}
				if(ct != cu) {
					if (  next.getModel() != Type.S && ct !=Couleur.W  && cu !=Couleur.W  ) {
						poly=true;
						if (next.getModel() != Type.O) { 
							next.setColor(Couleur.BL); 			
						}
						else {
							next.setColor2(Couleur.BL);
						}
						colorieDansLesDirections(i,j-1,Orientation.E);
					} else if ( cu==Couleur.W  ) {
						if (next.getModel() != Type.O) {
							next.setColor(ct);
						}
						else {
							next.setColor2(ct); 			
						}
						colorieDansLesDirections(i,j-1,Orientation.E);
					}
				}
				else if(next.getModel() == Type.S) {next.setIsVisted(true);}
			}else {
				fuite=true;}
		}	
	}

	public Grille createTuyaux(Grille g, Boolean moveToReserve,ExtractSubImage esi) {
		this.esi=esi;
		String cell; //chaque case qu on va lire sur la grille		
		for(int i=0; i<height; i++) {//pour chaque ligne
			for(int j=0; j<width; j++) {//pour chaque col
				cell = g.getCase(i, j);
				if ( !cell.equals(".") ) convertStringToTuyau(g,cell, i, j, moveToReserve, false);
			}
		}//fin for
		displayP();
		return g;
	}

	//convert une grille en schema
	void convertStringToTuyau(Grille g, String cell, int i, int j, boolean moveToReserve, boolean solution) {
		Type m ; 
		Orientation d; 
		Couleur c;
		int dirFlag; 
		m=null;
		d=null;

		c=Couleur.W; //color par default
		dirFlag=-1;

		//source
		if( (cell.charAt(0)== 'R') ||  (cell.charAt(0)== 'G') ||  (cell.charAt(0)== 'B') ||  (cell.charAt(0)== 'Y') ) {
			m=Type.S;
			c=Couleur.valueOf(cell.charAt(0)+"");
			dirFlag=Integer.parseInt(cell.charAt(1)+"");
		}

		//case non locked
		else if (!exclure.contains(cell) && !cell.contains("*")) {

			//pour les entrees, on les met dans schema car on les douche pas
			m=Type.valueOf(cell.charAt(0)+"");
			dirFlag=Integer.parseInt(cell.charAt(1)+"");
		}

		//case locked
		else if(cell.contains("*") && cell.length()==3) { 
			m=Type.valueOf(cell.charAt(1)+"");
			dirFlag=Integer.parseInt(cell.charAt(2)+"");	
		}

		//d=dir.S -> d=S orienter vers soud
		if (dirFlag>-1) d=Orientation.values()[dirFlag];

		//SET SCHEMA AVEC i j et creer obj Tuyaux avec model, dir, coulor : Tuyaux(model.S, dir.S, couleur.R);
		if ((m != null) && (d != null)) {
			g.setSchema(i, j, new Tuyaux( m, d, c ,esi) );
		}

		if (moveToReserve) {
			//if cell not lock and not source , we move to reserve and reset schema and grille
			if ( !(cell.contains("*") && cell.length()==3)
					&& !( (cell.charAt(0)== 'R') ||  (cell.charAt(0)== 'G') ||  (cell.charAt(0)== 'B') ||  (cell.charAt(0)== 'Y') ) 
					&& ( g.getSchema(i, j) != null )
					) {

				resP[m.ordinal()][d.ordinal()]+=1; //model et direction : L 0

				g.setSchema(i, j, null );
				g.setCell(i, j, "."); //update the cell
			}
		}
		if(solution) {
			resP[m.ordinal()][d.ordinal()] -=1;
		}
	}

	//display int [][] resP
	public void displayP() {
		System.out.println("Reserve : ");
		for(int i=0; i<6; i++) {//pour chaque ligne

			System.out.print(Type.values()[i] + " ");

			for(int j=0; j<4; j++) {//pour chaque col

				System.out.print(resP[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	//give 1 hint per click
	public void solution1() {
		boolean flag=true; 
		int randi=-1, randj=-1;
		if ( totalReserve() >0 ) {//si il y a encore les tuyaux dans la reserve

			while(flag){
				//calcule la position aleartorie pour poser le tuyaux hint
				randi = (int)(Math.random() * (height-2)+1);
				randj = (int)(Math.random() * (width-2)+1);

				if (!mainGrilleSolution.getCase(randi, randj).equals(".") && !mainGrille.getCase(randi, randj).replace("*","").equals(mainGrilleSolution.getCase(randi, randj).replace("*","")) ) {
					
					//verifie que le tuyau sol est dans la reserve
					if( checkReserve(mainGrilleSolution.getCase(randi, randj)) ) {

						//vider le tuyaux
						if( mainGrille.getSchema(randi, randj) != null) {
							System.out.println("si !=null!!");

							addToReserve(mainGrille.getSchema(randi, randj));
							mainGrille.setSchema(randi, randj, null);
							mainGrille.setCell(randi, randj, ".");
						}

						//remplir le hint

						mainGrille.setCell(randi,randj, "*" + mainGrilleSolution.getCase(randi,randj));
						mainGrille.displayGrille();

						convertStringToTuyau(mainGrille , mainGrille.getCase(randi,randj),randi ,randj,false,true);
						flag=false; 
					}
				}
			}
			//
			mainGrille.displayGrille();
			mainGrille.displaySchema();

			System.out.println("------------------------------ Solution : " + randi+ " - " + randj + " : " + mainGrilleSolution.getCase(randi,randj) + " ------------------------------\n");
		}
		else {System.out.println("------------------------------  Reserve vide ------------------------------ ");}
	}

	public void unlockTousLesTuyaux() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {		
				if( mainGrille.getCase(i, j).substring(0,1).equals("*") ) { 
					mainGrille.setCell(i, j, mainGrille.getCase(i, j).substring(1)); }
			}
		}
	}


	public void addToReserve(Tuyaux t) {
		char m = t.toString().charAt(0);
		int d = Character.getNumericValue(t.toString().charAt(1));
		switch(m) {

		case 'L': 
			getResP()[1][d]++; break; 
		case 'O': 
			getResP()[2][d]++; break; 
		case 'T': 
			getResP()[3][d]++; break; 
		case 'F': 
			getResP()[4][d]++; break; 
		case 'C': 
			getResP()[5][d]++; break;
		}
	}

	public void delToReserve(Tuyaux t) {
		char m = t.toString().charAt(0);
		int d = Character.getNumericValue(t.toString().charAt(1));
		switch(m) {

		case 'L': 
			getResP()[1][d]--; break; 
		case 'O': 
			getResP()[2][d]--; break; 
		case 'T': 
			getResP()[3][d]--; break; 
		case 'F': 
			getResP()[4][d]--; break; 
		case 'C': 
			getResP()[5][d]--; break;
		}
	}


	public boolean checkReserve(String s) {

		char m = s.charAt(0);
		int d = Character.getNumericValue(s.charAt(1));
		switch(m) {
		case 'L': 
			if(getResP()[1][d]<=0) { return false; }else {return true; }
		case 'O': 
			if(getResP()[2][d]<=0) { return false; }else {return true; }
		case 'T': 
			if(getResP()[3][d]<=0) { return false; }else {return true; }
		case 'F': 
			if(getResP()[4][d]<=0) { return false; }else {return true; }			
		case 'C': 
			if(getResP()[5][d]<=0) { return false; }else {return true; }
		}
		return true; 		
	}

	public void solution() {
		while (totalReserve() >0) {solution1();}
	}

	public void resetTuyauxColor() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (mainGrille.getSchema(i, j) != null) {
					if (mainGrille.getSchema(i, j).getModel() == Type.S) {
						mainGrille.getSchema(i, j).setIsVisted(false);
					} else if (mainGrille.getSchema(i, j).getModel() != Type.V) {
						mainGrille.getSchema(i, j).setColor(Couleur.W);
						if (mainGrille.getSchema(i, j).getModel() == Type.O) {
							mainGrille.getSchema(i, j).setColor2(Couleur.W);
						}
					}
				}
			}
		}
	}

	public void lockCase(int i, int j) {
		System.out.println("LockCase"+ i + j + mainGrille.getCase(i, j) );
		if(mainGrille.getCase(i,j).charAt(0) != '*') {
			System.out.println("Lock ! "+i+j);  
			mainGrille.setCell(i, j, "*" + mainGrille.getCase(i, j));
		}
		else {
			System.out.println("UnLock ! "+i+j);
			mainGrille.setCell(i, j, mainGrille.getCase(i, j).substring(1));
		}
		System.out.println("=> "+ i + j + mainGrille.getCase(i, j) );

	}

	public boolean checkLink( Tuyaux t, int i, int j, int ui, int uj) {

		Tuyaux u = mainGrille.getSchema(ui,uj);
		System.out.println("------------------------------  CheckLink t= " + t + " " + i +"-" + j + " u=" + u +" " + ui +"-"+ uj + " ------------------------------\n");
		if(u == null) {
			// tuyau au nord
			if (ui == i-1) { if ( ( t.getOuverture(0) == true )  ){System.out.println("CL1"); return false ;}  }
			// Tuyau sud
			if (ui == i+1) { if ( ( t.getOuverture(2) == true )  ){System.out.println("CL2"); return false ;}  }
			//Est
			if (uj == j+1) { if ( ( t.getOuverture(1) == true )  ){System.out.println("CL3"); return false ;}  }
			if (uj == j-1) { if ( ( t.getOuverture(3) == true )  ){System.out.println("CL4"); return false ;}  }
		}
		else {
			// tuyau au nord
			if (ui == i-1) { if ( ( t.getOuverture(0) == false )   ){ System.out.println("CL5");return false ;}  }
			// Tuyau sud-
			if (ui == i+1) { if ( ( t.getOuverture(2) == false )   ){ System.out.println("CL6");return false ;}  }
			//Est
			if (uj == j+1) { if ( ( t.getOuverture(1) == false )  ){ System.out.println("CL7");return false ;}  }
			if (uj == j-1) { if ( ( t.getOuverture(3) == false )   ){ System.out.println("CL8");return false ;}  }
		}
		System.out.println("CL9");
		return true;
	}


	public boolean isMonoSource() {
		int nbSources = 0;

		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {		
				if ((mainGrille.getSchema(i,j) != null) && (mainGrille.getSchema(i,j).getModel() == Type.S))  {
					nbSources=nbSources+1;

				}
			}
		}	
		if (nbSources == 1 ) {return true;}
		return false;
	}

	public Couleur isThereAMonoSource() {
		int occurrences;
		listcol.clear();
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {		
				if ((mainGrille.getSchema(i,j) != null) && (mainGrille.getSchema(i,j).getModel() == Type.S))  {
					listcol.add(mainGrille.getSchema(i,j).getColor());
				}
			}
		}			
		occurrences = Collections.frequency(listcol, Couleur.R);
		if (occurrences == 1 )return Couleur.R;
		occurrences = Collections.frequency(listcol, Couleur.G);
		if (occurrences == 1 )return Couleur.G;
		occurrences = Collections.frequency(listcol, Couleur.B);
		if (occurrences == 1 )return Couleur.B;
		occurrences = Collections.frequency(listcol, Couleur.Y);
		if (occurrences == 1 )return Couleur.Y;
		return Couleur.W;
	}

	public void generateAleaBruteforce() {
		long startTime,endTime,duration;
		startTime = System.currentTimeMillis();
		
		step2 = false;
		System.out.println("------------------------------ ENTER GENERATE ALEA ------------------------------ ");
		int randi,randj,r;
		Tuyaux t;
		Type m ; 
		Orientation d;
		//Vide Tuyaux de la grille
		for(int i=1; i<height-1; i++) {
			for(int j=1; j<width-1; j++) {
				mainGrille.setSchema(i,j,null);
				mainGrille.setCell(i,j,".");
			}
		}
		System.out.println("GENERATE ALEA \n===== Step 1 : Tuyaux aleatoires jusqu'a avoir des chemins entre les sources");

		while (updateColor(false)== 2) {
			t=null;
			randi = (int)(Math.random() * (height-2)+1);
			randj = (int)(Math.random() * (width-2)+1);

			r=(int)(Math.random() * (6)) ;

			m=Type.values()[r] ;

			d=Orientation.values()[(int)(Math.random() * (4))] ;
			if (m!= Type.S) {t=new Tuyaux(m,d,Couleur.W,esi);}
			//t=new Tuyaux(m,d,Couleur.W);
			System.out.println("  "+ t + " en " + randi + " - " + randj);

			// si t est au bord, check liens vers le bord avant de poser le tuyau

			if(m==Type.S) {
				System.out.println("<<< INSERT NULL !!!>>>" + randi + randj);
				if ( 
						(( mainGrille.getSchema(randi-1,randj) == null )  || (( mainGrille.getSchema(randi-1,randj) != null ) && (mainGrille.getSchema(randi-1,randj).getOuverture(2) == false)))
						&& ((mainGrille.getSchema(randi+1,randj) == null ) || ((mainGrille.getSchema(randi+1,randj) != null ) && (mainGrille.getSchema(randi+1,randj).getOuverture(0) == false)))
						&& ((mainGrille.getSchema(randi,randj+1) == null ) || ((mainGrille.getSchema(randi,randj+1) != null ) && (mainGrille.getSchema(randi,randj+1).getOuverture(3) == false)))
						&& ((mainGrille.getSchema(randi,randj-1) == null ) || ((mainGrille.getSchema(randi,randj-1) != null )  &&(mainGrille.getSchema(randi,randj-1).getOuverture(1) == false)))
						&& (mainGrille.getSchema(randi,randj) != null ) 
						) {
					System.out.println("<<< GO NULL !!!>>>" + randi + randj);
					mainGrille.setSchema(randi,randj,null);
					mainGrille.setCell(randi, randj, ".");	
					mainGrille.displayGrille();
					mainGrille.displaySchema();

				}
			}
			else if ( 
					// coin haut gauche
					( (randi == 1 )  && (randj == 1 ) &&   ( checkLink(t,randi,randj,randi-1,randj) ) &&   ( checkLink(t,randi,randj,randi,randj-1)  ) ) ||		
					//coin haut droit
					( (randi == 1 )  && ( randj == width-2 )  &&  ( checkLink(t,randi,randj,randi-1,randj)  )  &&  ( checkLink(t,randi,randj,randi,randj+1)   ) ) || 
					// coin bas gauche
					( (randi == height-2 )  && (randj == 1) && ( checkLink(t,randi,randj,randi+1,randj)  )  &&  ( checkLink(t,randi,randj,randi,randj-1) )  ) || 
					// coin bas droit
					( (randi == height-2 )  &&  ( randj == width-2 )  && ( checkLink(t,randi,randj,randi+1,randj)  )  &&  ( checkLink(t,randi,randj,randi,randj+1) ) ) || 
					//premiere ligne
					( (randi == 1 )  && (randj != 1 ) && ( randj != width-2 )  &&  ( checkLink(t,randi,randj,randi-1,randj)  ) ) || 
					//derniere ligne
					( (randi == height-2) && (randj != 1 ) && ( randj != width-2 )   &&  ( checkLink(t,randi,randj,randi+1,randj)  ) ) || 
					//premiere col
					( (randj == 1)  &&  (randi != 1 ) && (randi != height-2 ) &&  ( checkLink(t,randi,randj,randi,randj-1) )) ||
					//derniere col
					( ( randj == width-2 ) &&  (randi != 1 ) && (randi != height-2 ) &&  ( checkLink(t,randi,randj,randi,randj+1) )) 
					) {
				System.out.println(">>> ON POSE AU BORD "+ t + " en " + randi + " - " + randj);
				mainGrille.setSchema(randi,randj,t);
				mainGrille.setCell(randi, randj, t.toString());	
				mainGrille.displayGrille();
				mainGrille.displaySchema();
			}
			else if ((randi != 1 ) &&  (randi != height-2) && (randj != 1) && ( randj != width-2 )) { 
				System.out.println(">>> ON POSE ! NON BORD "+ t + " en " + randi + " - " + randj);
				mainGrille.setSchema(randi,randj,t);
				mainGrille.setCell(randi, randj, t.toString());
				mainGrille.displayGrille();
				mainGrille.displaySchema();
			}
			else {System.out.println("SKIP pas de lien");}


		}

		System.out.println("===== STEP 2 : Suppression tuyaux totalement blancs" + updateColor(false));
		step2 = true;
		//Remove Tuyaux Blancs
		for (int i = 1; i < height-1; i++) {
			for (int j = 1; j < width-1; j++) {
				if ( mainGrille.getSchema(i, j) != null &&  
						mainGrille.getSchema(i, j).getColor() == Couleur.W && 
						( mainGrille.getSchema(i, j).getColor2() == null || mainGrille.getSchema(i, j).getColor2() == Couleur.W )
						){
					mainGrille.setSchema(i, j, null);
					mainGrille.setCell(i, j, ".");
				}
			}
		}

		mainGrille.displayGrille();
		mainGrille.displaySchema();
		System.out.println("===== STEP 3 : Suppression tuyaux inutiles " + updateColor(false) );
		//Supprime tuyaux inutiles
		for (int i = 1; i < height-1; i++) {
			for (int j = 1; j < width-1; j++) {
				if ( mainGrille.getSchema(i, j) != null ) { 
					sauvegarde=mainGrille.getSchema(i, j);
					System.out.println(" Suppr " +i+ " " +j + " " + sauvegarde);

					//essai de remplacer chaque tuyau par un null, si updateColor(false)  = 2 on rollback
					mainGrille.setSchema(i, j, null);
					mainGrille.setCell(i, j, ".");		
					if(updateColor(false) == 2) {
						//Rollback
						mainGrille.setSchema(i, j, sauvegarde);
						mainGrille.setCell(i, j, sauvegarde.toString());
					}
					//Essay de remplacer chaque over par L0	color 1 				
					if  ( mainGrille.getSchema(i, j) != null  && mainGrille.getSchema(i, j).getModel() == Type.O) {
						System.out.println("Try Replace " + mainGrille.getSchema(i, j) +" par L0 en " + mainGrille.getSchema(i, j).getColor());
						t= new Tuyaux(Type.L,Orientation.N,Couleur.W, esi);
						mainGrille.setSchema(i, j,t);
						mainGrille.setCell(i, j, t.toString());	
					}
					if(updateColor(false) == 2) {
						//Rollbacksdddddddddddddd
						System.out.println("Rollback :("+ updateColor(false) );
						mainGrille.setSchema(i, j, sauvegarde);
						mainGrille.setCell(i, j, sauvegarde.toString());
					}

					//Essai de remplacer chaque over par L0 color 2 
					if ( mainGrille.getSchema(i, j) != null  && mainGrille.getSchema(i, j).getModel() == Type.O) {
						System.out.println("Try Replace " + mainGrille.getSchema(i, j) +" par L0 en " + mainGrille.getSchema(i, j).getColor2());
						t= new Tuyaux(Type.L,Orientation.N,Couleur.W, esi);
						mainGrille.setSchema(i, j,t);
						mainGrille.setCell(i, j, t.toString());	
					}
					if(updateColor(false) == 2) {
						System.out.println("Rollback :(" + updateColor(false) );
						//Rollback
						mainGrille.setSchema(i, j, sauvegarde);
						mainGrille.setCell(i, j, sauvegarde.toString());
					}	

					//Essai de remplacer chaque over par L1 color 
					if ( mainGrille.getSchema(i, j) != null  && mainGrille.getSchema(i, j).getModel() == Type.O) {
						System.out.println("Try Replace " + mainGrille.getSchema(i, j) +" par L1 en " + mainGrille.getSchema(i, j).getColor());
						t= new Tuyaux(Type.L,Orientation.E,Couleur.W, esi);
						mainGrille.setSchema(i, j,t);
						mainGrille.setCell(i, j, t.toString());	
					}
					if(updateColor(false) == 2) {
						System.out.println("Rollback :(" + updateColor(false) );
						//Rollback
						mainGrille.setSchema(i, j, sauvegarde);
						mainGrille.setCell(i, j, sauvegarde.toString());
					}	

					//Essai de remplacer chaque over par L1 color 2 
					if ( mainGrille.getSchema(i, j) != null  && mainGrille.getSchema(i, j).getModel() == Type.O) {
						System.out.println("Try Replace " + mainGrille.getSchema(i, j) +" par L1 en " + mainGrille.getSchema(i, j).getColor2());
						t= new Tuyaux(Type.L,Orientation.E,Couleur.W, esi);
						mainGrille.setSchema(i, j,t);
						mainGrille.setCell(i, j, t.toString());	
					}
					if(updateColor(false) == 2) {
						//Rollback
						System.out.println("Rollback :(" + updateColor(false) );
						mainGrille.setSchema(i, j, sauvegarde);
						mainGrille.setCell(i, j, sauvegarde.toString());
					}	
				}
			}
		}

		mainGrille.displayGrille();
		mainGrille.displaySchema();
		System.out.println("===== STEP 4 : Lissage "+ updateColor(false));
		if (updateColor(false) == 1 ) {
			//Lisse les cases composant le chemin
			for (int i = 1; i < height-1; i++) {
				for (int j = 1; j < width-1; j++) {

					if ( mainGrille.getSchema(i, j) != null ) { 
						System.out.println("Lissage "  + i + "-" + j + " " + mainGrille.getSchema(i, j) + " " + mainGrille.getSchema(i, j).getColor() + " " + mainGrille.getSchema(i, j).getColor2());				
						n=estPresentNord(i,j);
						e=estPresentEst(i,j);
						s=estPresentSud(i,j);
						w=estPresentOuest(i,j);
						ct=mainGrille.getSchema(i, j).getColor();
						ct2=mainGrille.getSchema(i, j).getColor2();
						System.out.println("CURRENT > " + n + " " + e + " " + s + " " +w + " " + ct +" "+ ct2);
						//L0   N E S W 
						if(  n && !e && s  && !w ) {
							t=new Tuyaux(Type.L,Orientation.N,ct, esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//L1
						else if(  !n && e && !s  && w ) {
							t=new Tuyaux(Type.L,Orientation.E,ct , esi);
							System.out.println("New Tuyau L1 : " + t.getColor());
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//T0
						else if (  n  && e  && !s  && !w ) {
							t=new Tuyaux(Type.T,Orientation.N,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//T1
						else if (  !n  && e  && s && !w ) {
							t=new Tuyaux(Type.T,Orientation.E,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//T2
						else if (  !n  && !e  && s  && w) {
							t=new Tuyaux(Type.T,Orientation.S,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//T3
						else if (  n  && !e  && !s  && w ) {
							t=new Tuyaux(Type.T,Orientation.W,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//F0
						else if (  n  && e  && s  && !w ) {
							t=new Tuyaux(Type.F,Orientation.N,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//F1
						else if (  !n  && e  && s  && w ) {
							t=new Tuyaux(Type.F,Orientation.E,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//F2
						else if (  n  && !e  && s  && w ) {
							t=new Tuyaux(Type.F,Orientation.S,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//F3
						else if (  n  && e  && !s  && w ) {
							t=new Tuyaux(Type.F,Orientation.W,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//C
						else if ( mainGrille.getSchema(i, j).getModel() != Type.O && n  && e  && s  && w ) {
							t=new Tuyaux(Type.C,Orientation.N,ct , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//Replace O0 => L1
						else if ( mainGrille.getSchema(i, j).getModel() == Type.O && ct == Couleur.W ) {
							System.out.println("Replace Over Inutile1 "+i+ " " +j);
							t=new Tuyaux(Type.L,Orientation.E, mainGrille.getSchema(i, j).getColor2() , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}
						//Replace O0 => L0
						else if ( mainGrille.getSchema(i, j).getModel() == Type.O && ct2 == Couleur.W ) {
							System.out.println("Replace Over Inutile2 "+i+ " " +j);
							t=new Tuyaux(Type.L,Orientation.N,mainGrille.getSchema(i, j).getColor() , esi);
							mainGrille.setSchema(i, j, t);
							mainGrille.setCell(i, j, t.toString());
						}

						System.out.println("=> " + i + " " + j + " " + mainGrille.getSchema(i, j).toString());
					}
				}
			}	
		}
		endTime = System.currentTimeMillis();
		duration=(endTime - startTime)/1000;
		System.out.println("------------------------------ END GENERATE ALEA - duration : " +  duration + " sec ------------------------------" );
		mainGrille.displayGrille();
		mainGrille.displaySchema();
	}

	public int getNbTuyauxColor() {
		int nb=0;
		for(int i=1; i<height-1; i++) {
			for(int j=1; j<width-1; j++) {
				if( mainGrille.getSchema(i, j) != null ){
					if ( 
							( mainGrille.getSchema(i, j).getColor() != null && mainGrille.getSchema(i, j).getColor() != Couleur.W ) &&
							( mainGrille.getSchema(i, j).getColor2() != null && mainGrille.getSchema(i, j).getColor2() != Couleur.W )
							){ 
						nb =  nb+1; 
					}
				}
			}
		}
		return nb;
	}

	//add une Ligne en plus sur grille en mode editor

	public boolean estPresentNord(int i,int j) {
		boolean ret = true;
		current = mainGrille.getSchema(i, j); 	//Case a colorier
		next = mainGrille.getSchema(i - 1, j);

		if ( next == null ) return false;
		if ( !next.getOuverture(2)) {return false; }	
		ct = current.getColor(); // couleur case current
		cu = next.getColor(); // couleur case nord					
		System.out.println(">N " + i+ " " +j+ " " + current.toString()+ " " +next.toString()+ " " + next.getAllOuvertures()+ " " + ct+" " + cu);
		if( ct != cu) return false;
		return true;
	}
	public boolean estPresentEst(int i,int j) {
		current = mainGrille.getSchema(i, j); 	//Case a colorier
		next = mainGrille.getSchema(i , j+1);

		if ( next == null ) return false;
		if ( !next.getOuverture(3)) {return false; }	
		if (current.getModel() != Type.O) {	ct = current.getColor();	} else {ct = current.getColor2();	}
		if (next.getModel() != Type.O) { 	cu = next.getColor();	} else {cu = next.getColor2();	}			
		System.out.println(">E " + i+ " " +j+ " " + current.toString()+ " " +next.toString() + " " + next.getAllOuvertures()+ " " + ct+" " + cu );
		if( ct != cu) return false;
		return true;
	}
	public boolean estPresentSud(int i,int j) {
		current = mainGrille.getSchema(i, j); 	//Case a colorier
		next = mainGrille.getSchema(i + 1, j);

		if ( next == null ) return false;
		if ( !next.getOuverture(0)) {return false; }	
		ct = current.getColor(); // couleur case current
		cu = next.getColor(); // couleur case nord					
		System.out.println(">S " + i+ " " +j+ " " + current.toString()+ " " +next.toString()+ " " + next.getAllOuvertures()+ " " + ct+" " + cu);
		if( ct != cu) return false;
		return true;
	}
	public boolean estPresentOuest(int i,int j) {
		current = mainGrille.getSchema(i, j); 	//Case a colorier
		next = mainGrille.getSchema(i , j-1);	
		if ( next == null ) return false;

		if ( !next.getOuverture(1)) {return  false; }	
		if (current.getModel() != Type.O) {	ct = current.getColor();	} else {ct = current.getColor2();	}
		if (next.getModel() != Type.O) { 	cu = next.getColor();	} else {cu = next.getColor2();	}		
		System.out.println(">O " + i+ " " +j+ " " + current.toString()+ " " +next.toString()+ " " + next.getAllOuvertures() + " " + ct+" " + cu);
		if( ct != cu) return false;
		return true;
	}


	public void incrHeight() { height=height+1; mainGrille.incrHeight();}

	public void incrWidth() {width =  width+1; mainGrille.incrWidth();}

	public void decrHeight() { height= height-1; mainGrille.decrHeight();}

	public void decrWidth() {width= width-1; mainGrille.decrWidth();}


	//getter
	public int getHeight() { return height; }
	public int getWidth() { return width; }
	public Grille getMainGrille() {return mainGrille; }
	public int[][] getResP(){ return resP; }
	public int getResP(int i,int j){ return resP[i][j] ; }
	public void setResP(int i,int j,int k){ resP[i][j]+=k; }
	public String getAffichage() { return affichage; }
	public void setAffichage(String affichage) { this.affichage = affichage; }
	public String getFile() { return file.toString(); }

}
