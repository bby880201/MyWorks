package view;

/**
 * a view to model adapter interface including all methods
 * need to be implemented
 * @author Boyang Bai
 *
 */
public interface IView2ModelAdapter<TInstrument> {

	/**
	 * tell model to load an abc file
	 * @param fileName			the path of the abc file
	 * @return					file contents
	 */
	public String loadFile(String fileName);

	/**
	 * tell model to parse the loaded file
	 * @return					the structure of the loaded file
	 */
	public String parse();

	/**
	 * tell model to play the parsed file
	 * @param ist				the instrument a music will be played in
	 */
	public void play(TInstrument ist);

	/**
	 * tell model to stop playing music
	 */
	public void stop();

	/**
	 * a null adapter with all no-op methods
	 */
	public static final IView2ModelAdapter<?> NULL_OBJECT = new IView2ModelAdapter<Object>() {

		@Override
		public String loadFile(String fileName) {
			return null;
		}

		@Override
		public String parse() {
			return null;
		}

		@Override
		public void stop() {
		}

		@Override
		public void play(Object ist) {
		}
	};
}
