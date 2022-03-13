package model;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;


public  class Tuyaux {

	protected final int size=120;
	private final int rightAngle=90;

	protected BufferedImage bufferedImage;
	protected Boolean[] ouvertures; //un tableau de boolean ou est-ce que les tuyaux sont ouverts

	protected Boolean isVisted; //for parcours du graphe

	protected String type;
	protected Couleur c, c2,source; //c2 pour over
	protected Type m;
	protected Orientation d;
	protected ExtractSubImage image;

	public Tuyaux(Type m2, Orientation d2, Couleur cc,ExtractSubImage esi) {
		this.m=m2;
		this.d=d2;
		source=cc;
		image = esi;
		c = cc; //color par default est white
		c2 = null;
		
		if(m==Type.O) {
			c2 = Couleur.W;
		}
		
		//created and init tab direction
		ouvertures = new Boolean[4];


		//init bufferedImage
		bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);

		//pour parcours du graphe
		isVisted=false;

		if(m==Type.V) {bufferedImage = image.getResult().get(6).get(5);}		
		else if (m==Type.O)  {
			Graphics2D g = bufferedImage.createGraphics();

			//Associe deux image l'une sur l'autre 
			g.drawImage(image.getResult().get(0).get(1),null,0,0);
			g.drawImage(image.getResult().get(0).get(2),null,0,0);
			g.dispose();
		}
		else {
			bufferedImage = rotateLF(image.getResult().get(c.ordinal()).get(m.ordinal()),d.ordinal());
		}
		
		updateDirection();
	}

	//update direction
	public void updateDirection() {
		for(int i = 0 ; i<ouvertures.length;i++) {	ouvertures[i]=false; }
		switch(m) {
		case L : 
			if (d==Orientation.S){d=Orientation.N;}
			if (d==Orientation.W){d=Orientation.E;}
			
			if(d==Orientation.N) {
				setOuvertures(0);
				setOuvertures(2);
			}else if (d==Orientation.E){
				setOuvertures(1);
				setOuvertures(3);
			}
			break;
		case O : 
			d=Orientation.N;
			//isOver = true;
			for(int i = 0; i<4; i++) {
				setOuvertures(i);
			}
			break;
		case T : 
			if (d==Orientation.W) {//T3
				setOuvertures(d.ordinal());
				setOuvertures(0);
			}else {//T1, T2, T0
				setOuvertures(d.ordinal()); //0=ouvert vers n
				setOuvertures(d.ordinal()+1); //1=ouvert vers e
			}
			break;
		case F :
			if(d.ordinal()<2) {
				setOuvertures(d.ordinal());
				setOuvertures(d.ordinal()+1);
				setOuvertures(d.ordinal()+2);
			}
			else if(d.ordinal()==2) {
				setOuvertures(d.ordinal());
				setOuvertures(d.ordinal()+1);
				setOuvertures(0);
			}
			else {
				setOuvertures(d.ordinal());
				setOuvertures(0);
				setOuvertures(d.ordinal()-2);
			}
			break;
		case C : 
			d=Orientation.N;
			for(int i = 0; i<4; i++) {
				setOuvertures(i);
			}
			break;

		case S : 
			if(d==Orientation.N) {setOuvertures(0);}
			if(d==Orientation.E) {setOuvertures(1);}
			if(d==Orientation.S) {setOuvertures(2);}
			if(d==Orientation.W) {setOuvertures(3);}					
			break;
		default :
			break;
		}
	}

    public void resetColor() {
    	//Reset Color
    	c=Couleur.W;
		if(m==Type.O) {
			c2 = Couleur.W;
		}
		
		//Reset BufferImage
		if(m!=Type.O) {
			bufferedImage = rotateLF(image.getResult().get(c.ordinal()).get(m.ordinal()),d.ordinal());
		}else {
			Graphics2D g = bufferedImage.createGraphics();

			//Associe deux image l'une sur l'autre 
			g.drawImage(image.getResult().get(c.ordinal()).get(1),null,0,0);
			g.drawImage(image.getResult().get(c2.ordinal()).get(2),null,0,0); //2e image
			g.dispose();
		}
    }
    
	//Getter et Setter
	public BufferedImage getBufferedImage() { return bufferedImage; }
	public String getAllOuvertures() { return "Ouvertures : "+ouvertures[0]+" "+ouvertures[1]+" "+ouvertures[2]+" "+ouvertures[3]; }
	public Boolean getOuverture(int x) { return ouvertures[x]; }
	public void setOuvertures(int x) {ouvertures[x]=true; }
	public void setOrientation(Orientation ddd) {
		
		d=ddd;
		updateDirection();
		bufferedImage = rotateLF(image.getResult().get(c.ordinal()).get(m.ordinal()),d.ordinal());
	}
		
	public void setColor(Couleur c_new) {
		c = c_new;
		if(m!=Type.O) {
			bufferedImage = rotateLF(
					image.getResult().get(c.ordinal()).get(m.ordinal()),d.ordinal());
		}else {
			Graphics2D g = bufferedImage.createGraphics();

			//Associe deux image l'une sur l'autre 
			g.drawImage(image.getResult().get(c.ordinal()).get(1),null,0,0);
			g.drawImage(image.getResult().get(c2.ordinal()).get(2),null,0,0); //2e image
			g.dispose();
		}
	}
	
	public void setColor2(Couleur c_new) {
		c2 = c_new;
		if(m!=Type.O) {
			bufferedImage = rotateLF(image.getResult().get(c.ordinal()).get(m.ordinal()),d.ordinal());
		}else {
			Graphics2D g = bufferedImage.createGraphics();

			//Associe deux image l'une sur l'autre 
			g.drawImage(image.getResult().get(c.ordinal()).get(1),null,0,0);
			g.drawImage(image.getResult().get(c2.ordinal()).get(2),null,0,0); //2e image
			g.dispose();
		}
	}
	
	
	public BufferedImage rotateLF(BufferedImage bi, int r) {

		double radian = Math.toRadians(rightAngle*r);
		double sin = Math.abs(Math.sin(radian)), cos = Math.abs(Math.cos(radian));
		int w = bi.getWidth(), h = bi.getHeight();
		int nw = (int )Math.floor((w*cos)+(h*sin));
		int nh = (int )Math.floor((h*cos)+(w*sin));
		int posx = w/2, posy = h/2;

		BufferedImage res = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		AffineTransform trans = new AffineTransform();
		trans.translate((nw-w)/2, (nh-h)/2);
		trans.rotate(radian, posx, posy);
		AffineTransformOp rotateOp = new AffineTransformOp(trans, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(bi,res);
		return res;
	}

	public Orientation getOrientation() { return d; }
	public int getRotate() { return d.ordinal(); }
	public Type getModel() {return m; }	
	public Couleur getSource() { return source; }
	public Couleur getColor() { return c; }
	public Couleur getColor2() { return c2; }
	public Boolean getIsVisted() { return isVisted; }
	public void setIsVisted(Boolean isVisted) { this.isVisted = isVisted; }
	public String toString() { return m+ "" + d.ordinal(); }
	public String toStringSrc() { return c+""+d.ordinal(); }
}
