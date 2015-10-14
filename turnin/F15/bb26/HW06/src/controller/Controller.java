/**
 * 
 */
package controller;

import java.awt.EventQueue;

import model.*;
import view.*;

/**
 * @author Boyang Bai
 *
 */
public class Controller {
	
	private MusicPlayerView<Object> view;
	private MusicPlayerModel model;
	
	public Controller(){
		
		view = new MusicPlayerView<Object>(new IView2ModelAdapter(){

			@Override
			public String loadFile(String fileName) {
				return model.loadFile(fileName);
			}

			@Override
			public String parse() {
				return model.parse();
			}

			@Override
			public void play() {
				model.play();
			}

			@Override
			public void stop() {
				model.stop();
			}});
		model = new MusicPlayerModel(new IModel2ViewAdapter(){

			@Override
			public void finished() {
				view.finished();
			}
			
		});
	}
	
	void start() {
		model.start();
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
