package model;

import java.awt.Dimension;

import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import View.AccueilView;


public class MainApp {	
	public static void main(String[] args) throws IOException {

		//Creer JFrame
		JFrame frame = new JFrame();
		frame.setTitle("Plumber");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.green);
		frame.setResizable( true );

		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setPreferredSize(new Dimension(1200, 1000)); 
				
		//Ajoute View dans JFrame : le menu des levels presents
		frame.getContentPane().add(new AccueilView(frame));
		
		frame.pack();
		frame.setVisible(true);		
	}
}
