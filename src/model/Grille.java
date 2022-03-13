package model;

public class Grille {
	
	private Tuyaux [][] schema;
	private String [][] g;
	private int width, height;
	
	public Grille(int h, int w) {
		this.height = h;
		this.width = w;
		
		//creer une grille 
		g = new String[h][w];
		
		schema = new Tuyaux[h][w];
	}
	
	//methodes
	public void displayGrille() {
		
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				
				System.out.printf("%-4s", g[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//afficher le schema qui contient des null initialement
	public void displaySchema() {
		
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				
				System.out.printf("%-5s", schema[i][j]);
				
				//update position
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void refillGrilleVide() {
		g = new String[height][width];		
		schema = new Tuyaux[height][width];
		
		//fill the grille
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				setCell(i, j, ".");
			}
		}
		
		//fill the grille
		for(int i=0; i<height; i++) {
			setCell(i, 0, "X");
			setCell(i, width-1, "X");
		}
		
		for(int j=0; j<width; j++) {
			setCell(0, j, "X");
			setCell(height-1, j, "X");
		}
	}
	public void incrHeight() { 
		height = height+1;
		refillGrilleVide();		
		System.out.println(">>>>> Ajoute une LIGNE "+g.length+" "+ g[0].length);
	}

	public void incrWidth() {  
		width=width+1;
		refillGrilleVide(); 
		System.out.println(">>>>> Ajoute une COLONNE "+g.length+" "+ g[0].length);
	}
	
	public void decrHeight() {
		height=height-1;
		refillGrilleVide();
		System.out.println(">>>>> Supprime une LIGNE "+g.length+" "+ g[0].length);
	}

	public void decrWidth() { 
		width =width-1;
		refillGrilleVide(); 
		System.out.println(">>>>> Supprime une COLONNE "+g.length+" "+ g[0].length);
	}
	
	//getter of grill
	public String[][] getGrille() {
		return g;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	//getter of grill size
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	//getter of case
	public String getCase(int i, int j) {
		return g[i][j];
	}

	public Tuyaux getSchema(int i, int j) {
		return schema[i][j];
	}	

	//setter of case
	public void setCell(int i, int j, String str) {
		this.g[i][j] = str;
	}
	
	public void setSchema(int i, int j, Tuyaux ty) {
		this.schema[i][j] = ty;
	}
}
