/**
 * 
 */
package controller;

import java.awt.EventQueue;

import view.MusicPlayerView;

/**
 * @author Boyang Bai
 *
 */
public class Controller {
	
	private MusicPlayerView<Object> view;
	
	public Controller(){
		
		view = new MusicPlayerView<Object>();
	}
	
	void start() {
//		model.start();
		view.start();
	}
	
	/**
	 * Launch the application.
	 * @param args Arguments given by the system or command line.
	 */
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controller ctl = new Controller();
					ctl.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
