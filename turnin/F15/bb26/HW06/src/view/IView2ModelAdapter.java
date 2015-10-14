/**
 * 
 */
package view;

/**
 * @author Boyang Bai
 *
 */
public interface IView2ModelAdapter {
	
	public String loadFile(String fileName);
	
	public String parse();
	
	public void play();
	
	public void stop();
	
	public static final IView2ModelAdapter NULL_OBJECT = new IView2ModelAdapter(){

		@Override
		public String loadFile(String fileName) {
			return null;
		}

		@Override
		public String parse() {
			return null;			
		}

		@Override
		public void play() {
			
		}

		@Override
		public void stop() {
			
		}};
}
