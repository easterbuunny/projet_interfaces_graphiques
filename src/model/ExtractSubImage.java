package model;

import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.awt.*;


public class ExtractSubImage {
	

	private BufferedImage mainImage;
	private ArrayList<ArrayList<BufferedImage>> result; 
	private String url;
	private int SizeOfImage;

	public ExtractSubImage(int SizeOfImage) {
		this.SizeOfImage = SizeOfImage;
		result = new ArrayList<ArrayList<BufferedImage>>();
		
		url = System.getProperty("user.dir") + "/src/images/pipes.gif";
		
		extractSubImage(url);
	}

	//EXTRAIRE SUB IMAGES
	public void extractSubImage(String chemin) {
		try {
			mainImage= ImageIO.read(new File(chemin));
			ArrayList<BufferedImage> eachLineImages;

			for(int i=0; i<7; i++) {//ligne

				eachLineImages = new ArrayList<BufferedImage>();

				for (int j = 0; j <6; j++) {//col
					BufferedImage subImage= mainImage.getSubimage((120*j)+((j)*20), (120*i)+(i*20), 120, 120);
							
					eachLineImages.add(resize(subImage));
					
				}
				result.add(eachLineImages);
			}
		} 
		
		catch (IOException e) {
			e.getStackTrace();
		}
	}
	
	public BufferedImage resize(BufferedImage image) { 
	    Image temp = image.getScaledInstance(SizeOfImage, SizeOfImage, Image.SCALE_SMOOTH);
	    BufferedImage rimg = new BufferedImage(SizeOfImage, SizeOfImage, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = rimg.createGraphics();
	    g.drawImage(temp, 0, 0, null);
	    g.dispose();
	    return rimg;
	}  

	//getter
	public ArrayList<ArrayList<BufferedImage>> getResult() {
		return result;
	}

}



