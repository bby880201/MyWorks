package model;

import provided.util.ABCInstrument;

/**
 * a model to view adapter interface including all methods
 * need to be implemented
 * @author Boyang Bai
 *
 */
public interface IModel2ViewAdapter {
	/**
	 * tell view when the music playing is finished
	 */
	public void finished();

	/**
	 * tell view to set instrument list
	 * @param instruments			a list includes all available instruments
	 */
	public void setIstList(ABCInstrument[] instruments);

	/**
	 * a null adapter with all no-op methods
	 */
	public static final IModel2ViewAdapter NULL_OBJECT = new IModel2ViewAdapter() {

		@Override
		public void finished() {
		}

		@Override
		public void setIstList(ABCInstrument[] instruments) {
		}
	};

}
