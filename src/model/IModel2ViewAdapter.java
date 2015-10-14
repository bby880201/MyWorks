/**
 * 
 */
package model;

/**
 * @author Boyang Bai
 *
 */
public interface IModel2ViewAdapter {
	
	public void finished();
	
	public static final IModel2ViewAdapter NULL_OBJECT = new IModel2ViewAdapter(){

		@Override
		public void finished() {
			
		}};


}
