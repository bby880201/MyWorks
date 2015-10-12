/**
 * 
 */
package model;

import java.io.BufferedReader;
import java.io.FileReader;

import provided.abcParser.ABCParser;
import provided.music.IPhrase;

/**
 * @author Boyang Bai
 *
 */
public class MusicPlayerModel {
	
	private IModel2ViewAdapter m2vAdapter;
	private ABCParser parser;
	
	public MusicPlayerModel(IModel2ViewAdapter m2v){
		this.m2vAdapter = m2v;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	public String loadFile(String fileName){
		String fileContent = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
		    while ((line = reader.readLine()) != null){
		    	fileContent += line;
		    }
		    reader.close();
		} catch (Exception e) {
			System.out.println("failed to load file!");
			e.printStackTrace();
		}
		this.parser = new ABCParser(fileName);
		return fileContent;
	}
	
	public IPhrase parse(){
		return parser.parse();
	}

}
