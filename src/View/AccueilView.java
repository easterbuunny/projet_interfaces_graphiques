package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;

import model.Model;

public class AccueilView extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Model gLevel;
	private String url,url_non_edit;
	private JFrame frameglobal;
	private ArrayList<String> listlevelName,listlevelName2;
	private AccueilView acc;

	//ctor
	public AccueilView(JFrame f) throws IOException {
		this.frameglobal = f;
			acc=this;
			frameglobal.setPreferredSize(new Dimension(1200, 1000)); 
		//for unix
		url = System.getProperty("user.dir") + "/src/inputfiles/"; 
		url_non_edit = System.getProperty("user.dir") + "/src/inputfiles_non_edit/"; 
	
		//Compter nb de file present dans le dir inputfiles
		accUpdate();
				
	}
	
	public void accUpdate(){
		this.removeAll();
		listlevelName = fileCount(url);
		listlevelName2 = fileCount(url_non_edit);
		int maxfile=Math.max(listlevelName.size(),listlevelName2.size());
		this.setLayout(new GridLayout(maxfile, 2));
		this.setVisible(true); 
		

		//Create Buttons of each Level
		for(int i=0; i<maxfile; i++) {
			
			if(i<listlevelName.size() ) {  CreateLevelButton(listlevelName.get(i), listlevelName.size(),true); }
			else { this.add(new JLabel());}
			if(i< listlevelName2.size() ) {CreateLevelButton(listlevelName2.get(i), listlevelName2.size(),false); }
			else { this.add(new JLabel());}
		}
		

	}
    
	//count the number of file in a dir
	public ArrayList<String> fileCount(String dir){ 
		
		File[] lf = new File(dir).listFiles();
		String name="";
		ArrayList<String> listName = new ArrayList<String>();
		
		for (int i=0; i<lf.length; i++) {
			
			name = lf[i].getName();
			
			if(name.substring(name.length()-2, name.length()).equals(".p")) {

				listName.add(name.substring(0, name.length()-2));
			}
		}
		
		//sort by number of file
		Collections.sort(listName, new Comparator<String>() {
	        public int compare(String f1, String f2) {
	            return extractLevelNum(f1) - extractLevelNum(f2);
	        }

	        int extractLevelNum(String n) {
	        	
	            //mathcing non-digit \\D, replace them by ""
	        	String levelNum = n.replaceAll("\\D", ""); 
	            
	            if(levelNum.isEmpty()) return 0;
	            else return Integer.parseInt(levelNum);
	            
	        }
	    });
		return listName;
	 }

	
	//methodes
	public void CreateLevelButton(String s, int n,boolean editable) {
		
		JButton bs = new JButton(s);
		bs.setName(s);
		bs.setMaximumSize(new Dimension(200, 120));
		
		ActionListener al =  new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//While click button, display level corresponding
				try{

					if(editable) { new GrilleView(new Model(url+bs.getName()+".p",bs.getName()), frameglobal, acc); }
					else { new GrilleView(new Model(url_non_edit+bs.getName()+".p",bs.getName()), frameglobal, acc); }
					
				}catch(Exception exc) {

					ExceptionMessage(exc);
				}
			}
		};
		
		bs.addActionListener(al);
		this.add(bs);
	}
	

	//display Exception Messages
	public void ExceptionMessage(Exception exc) {
		System.out.println(exc + " - no file found");
		exc.printStackTrace();
		System.exit(1);
	}

	//getter
	public Model getgLevel() {
		return gLevel;
	}
	
}
