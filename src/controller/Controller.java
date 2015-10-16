/**
 * 
 */
package controller;

import java.awt.EventQueue;

import provided.util.ABCInstrument;
import model.*;
import view.*;

/**
 * @author Boyang Bai
 *
 */
public class Controller {

	/**
	 * GUI of this application
	 */
	private MusicPlayerView<ABCInstrument> view;

	/**
	 * model of this application
	 */
	private MusicPlayerModel model;

	/**
	 * construct whole application
	 */
	public Controller() {
		//view takes a view to model adapter which is implemented below
		view = new MusicPlayerView<ABCInstrument>(
				new IView2ModelAdapter<ABCInstrument>() {

					@Override
					public String loadFile(String fileName) {
						return model.loadFile(fileName);
					}

					@Override
					public String parse() {
						return model.parse();
					}

					@Override
					public void stop() {
						model.stop();
					}

					@Override
					public void play(ABCInstrument ist) {
						model.play(ist);
					}
				});

		//model takes a model to view adapter which is implemented below
		model = new MusicPlayerModel(new IModel2ViewAdapter() {

			@Override
			public void finished() {
				view.finished();
			}

			@Override
			public void setIstList(ABCInstrument[] istList) {
				view.setIstList(istList);
			}

		});
	}

	/**
	 * start the application
	 */
	void start() {
		model.start();
		view.start();
	}

	/**
	 * Launch the application.
	 * @param args Arguments given by the system or command line.
	 */
	public static void main(String[] args) {
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
