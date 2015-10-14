/**
 * 
 */
package model;

import java.util.Hashtable;
import java.util.Map;

import provided.abcParser.ABCParser;
import provided.music.Chord;
import provided.music.Header;
import provided.music.IPhrase;
import provided.music.IPhraseVisitor;
import provided.music.IPhraseVisitorCmd;
import provided.music.NESeqList;
import provided.music.Note;
import provided.music.Triplet;
import provided.player.ISequencePlayerStatus;
import provided.player.SequencePlayer;
import provided.util.ABCUtil;
import provided.util.KeySignature;

/**
 * @author Boyang Bai
 *
 */
public class MusicPlayerModel {
	
	private IModel2ViewAdapter m2vAdapter;
	private ABCParser parser;
	private ABCUtil abcUtil = ABCUtil.Singleton;
	private SequencePlayer sp;
	private IPhrase phrase;
	private IPhraseVisitor playAlgo;
	private IPhraseVisitor toStringAlgo;
	
	public MusicPlayerModel(IModel2ViewAdapter m2v){
		this.m2vAdapter = m2v;
	}

	public void start() {
		phrase = new IPhrase(){
			@Override
			public Object execute(IPhraseVisitor algo, Object... params) {
				return null;
			}};
		
		toStringAlgo = new IPhraseVisitor() {
			@Override
			public Object caseAt(String id, IPhrase host, Object... params) {
				String struct = "";
				switch (id){
				case "MTSeqList":
					struct = (String) params[0] + "}";
					break;
				case "NESeqList":
					NESeqList phrase = (NESeqList) host;
					struct = (String) params[0] + ", " + (String) (phrase.getRest().execute(toStringAlgo, phrase.getFirst().toString()));
					break;
				default:
					struct = host.toString();
				}
				return struct;
			}
		};
	}
	
	public String loadFile(String fileName){
		this.parser = new ABCParser(fileName);
		return abcUtil.getFileContents(fileName);
	}
	
	public String parse(){
		try {
			phrase = parser.parse();
			NESeqList.setToStringAlgo(toStringAlgo);
		} catch (Exception e){
			System.err.println("failed to parse file");
		}
		return phrase.toString();
	}
	
	
	public void play() {
		playAlgo = new IPhraseVisitor(){	
			private IPhraseVisitor thisAnnoClass = this;
			private IPhraseVisitorCmd defaultCmd;
			private Map<String, IPhraseVisitorCmd> cmds = new Hashtable<String, IPhraseVisitorCmd>();
			private Map<String, Boolean> flgs = new Hashtable<String, Boolean>();
			{
				flgs.put("L", false);
				flgs.put("Q", false);
				defaultCmd = new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						System.err.println("IPhraseVisitor: Unknown Phrase encountered: " + id);
						return params;
					}
				};
				this.addCmd("MTSeqList", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						((SequencePlayer) params[0]).play(new ISequencePlayerStatus(){
							@Override
							public void finished() {
								m2vAdapter.finished();
							}});
						return params;
					}
				});
				this.addCmd("NESeqList", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						params = (Object[]) ((NESeqList) host).getFirst().execute(thisAnnoClass, params);
						return ((NESeqList) host).getRest().execute(thisAnnoClass, params);
					}
				});
				this.addCmd("Note", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						params[1] = ((SequencePlayer) params[0])
								.addNote(((KeySignature) params[2]).adjust((Note) host), (int) params[1]); 
						return params;
					}	
				});
				this.addCmd("Chord", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						int next = (int) params[1];
						for (Note n : ((Chord) host).getNotes()){
							next = ((SequencePlayer) params[0])
							.addNote(((KeySignature) params[2]).adjust(n), (int) params[1]);
						}
						params[1] = next;
						return params;
					}	
				});
				this.addCmd("Triplet", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						for (Note n : ((Triplet) host).getNotes()){
							n.setDuration(n.getDuration()/3);
							params[1] = ((SequencePlayer) params[0])
									.addNote(((KeySignature) params[2]).adjust(n), (int) params[1]);
						}
						return params;
					}	
				});
				this.addCmd("L", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						if 	(!flgs.containsKey("L") || !flgs.get("L")){
							SequencePlayer sp = ((SequencePlayer) params[0]);
							int defNotes = (int) parseL(((Header) host).getValue());
							sp.init(defNotes, 0);
							sp.setTicksPerDefaultNote(defNotes);
							flgs.put("L",true);
						}
						return params;
					}
				});
				this.addCmd("K", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						params[2] = new KeySignature(((Header) host).getValue());
						return params;
					}
				});
				this.addCmd("Q", new IPhraseVisitorCmd(){
					@Override
					public Object apply(String id, IPhrase host, Object... params) {
						if 	(!flgs.containsKey("Q") || !flgs.get("Q")){
							SequencePlayer sp = ((SequencePlayer) params[0]);
							sp.setTempo((int) abcUtil.parseTempo(((Header) host).getValue(), (sp.getTicksPerQuarterNote() / sp.getTicksPerDefaultNote())));
							flgs.put("Q", true);
						}
						return params;
					}
				});
				String headerString = "ABCDEFGHIJMNOPRSTUVWXYZ";
				for (int i = 0; i < headerString.length(); i++) {
				     this.addCmd("" + headerString.charAt(i), new IPhraseVisitorCmd(){
						@Override
						public Object apply(String id, IPhrase host,
								Object... params) {
							System.err.println("Header: Ignored header: " + id);
							return params;
						} 
				     });
				}
			}
			public Object caseAt(String id, IPhrase host, Object... params) {
				if (cmds.containsKey(id)) {
					return cmds.get(id).apply(id, host, params);
				} else {
					return defaultCmd.apply(id, host, params);
				}
			}
			private void addCmd(String id, IPhraseVisitorCmd cmd) {
				cmds.put(id, cmd);
			}	
		};
		sp = new SequencePlayer(16, 0);
		phrase.execute(playAlgo, sp, 10, new KeySignature("C"));
	}
	

	public double parseL(String frac) {
		String[] vals = frac.split("/");
		if (vals.length != 2) {
			throw new NumberFormatException(frac + " is not a fraction.");
		}
		double num = Double.parseDouble(vals[0]);
		double denom = Double.parseDouble(vals[1]);
		return denom / num;
	}

	public void stop() {
		sp.stop();
	}
}
