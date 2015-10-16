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
import provided.util.ABCInstrument;
import provided.util.ABCUtil;
import provided.util.KeySignature;

/**
 * the model part of the ABC file music player application
 * @author Boyang Bai
 */
public class MusicPlayerModel {

	/**
	 * a model to view adapter
	 */
	private IModel2ViewAdapter m2vAdapter;

	/**
	 * a ABC file parser used to parse abc file
	 */
	private ABCParser parser;

	/**
	 * some useful utilities for handle file parsing and playing
	 */
	private ABCUtil abcUtil = ABCUtil.Singleton;

	/**
	 * a player used to play parsed abc file
	 */
	private SequencePlayer sp;

	/**
	 * represent a parsed abc file
	 */
	private IPhrase phrase;

	/**
	 * a visitor containing play algorithm
	 */
	private IPhraseVisitor playAlgo;

	/**
	 * a visitor containing toString algorithm
	 */
	private IPhraseVisitor toStringAlgo;

	/**
	 * the model constructor taking the model to view adapter
	 * @param m2v		an implemented model to view adapter
	 */
	public MusicPlayerModel(IModel2ViewAdapter m2v) {
		this.m2vAdapter = m2v;
	}

	/**
	 * start the model
	 */
	public void start() {
		sp = new SequencePlayer(16, 0);

		//init a default to string algo visitor
		toStringAlgo = new IPhraseVisitor() {
			@Override
			public Object caseAt(String id, IPhrase host, Object... params) {
				String struct = "";
				switch (id) {
				case "MTSeqList":
					struct = params[0] + "}";
					break;
				case "NESeqList":
					NESeqList phrase = (NESeqList) host;
					struct = params[0]
							+ ", "
							+ phrase.getRest().execute(toStringAlgo,
									phrase.getFirst().toString());
					break;
				default:
					struct = host.toString();
				}
				return struct;
			}
		};

		//init a default play algo visitor
		playAlgo = new IPhraseVisitor() {
			private IPhraseVisitor thisAnnoClass = this;
			private IPhraseVisitorCmd defaultCmd;
			private Map<String, IPhraseVisitorCmd> cmds = new Hashtable<String, IPhraseVisitorCmd>();
			{
				this.addCmd("MTSeqList", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						((SequencePlayer) params[0])
								.play(new ISequencePlayerStatus() {
									@Override
									public void finished() {
										m2vAdapter.finished();
									}
								});
						return params;
					}
				});
				this.addCmd("NESeqList", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						params = (Object[]) ((NESeqList) host).getFirst()
								.execute(thisAnnoClass, params);
						return ((NESeqList) host).getRest().execute(
								thisAnnoClass, params);
					}
				});
				this.addCmd("Note", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						params[1] = ((SequencePlayer) params[0]).addNote(
								((KeySignature) params[2]).adjust((Note) host),
								(int) params[1]);
						return params;
					}
				});
				this.addCmd("Chord", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						int next = (int) params[1];
						for (Note n : ((Chord) host).getNotes()) {
							next = ((SequencePlayer) params[0]).addNote(
									((KeySignature) params[2]).adjust(n),
									(int) params[1]);
						}
						params[1] = next;
						return params;
					}
				});
				this.addCmd("Triplet", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						for (Note n : ((Triplet) host).getNotes()) {
							n.setDuration(n.getDuration() * 2 / 3);
							params[1] = ((SequencePlayer) params[0]).addNote(
									((KeySignature) params[2]).adjust(n),
									(int) params[1]);
						}
						return params;
					}
				});
				this.addCmd("L", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						((SequencePlayer) params[0])
								.setTicksPerDefaultNote((int) parseL(((Header) host)
										.getValue()));
						return params;
					}
				});
				this.addCmd("K", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						params[2] = new KeySignature(((Header) host).getValue());
						return params;
					}
				});
				this.addCmd("Q", new IPhraseVisitorCmd() {
					@Override
					public Object apply(String id, IPhrase host,
							Object... params) {
						SequencePlayer sp = ((SequencePlayer) params[0]);
						sp.setTempo((int) abcUtil.parseTempo(((Header) host)
								.getValue(), (sp.getTicksPerQuarterNote() / sp
								.getTicksPerDefaultNote())));
						return params;
					}
				});
				String headerString = "ABCDEFGHIJMNOPRSTUVWXYZ";
				for (int i = 0; i < headerString.length(); i++) {
					this.addCmd("" + headerString.charAt(i),
							new IPhraseVisitorCmd() {
								@Override
								public Object apply(String id, IPhrase host,
										Object... params) {
									System.err
											.println("Header: Ignored header: "
													+ id);
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

		//get all available instruments and send to view
		m2vAdapter.setIstList(abcUtil.getInstruments());
	}

	/**
	 * load an abc file and return the contents of it
	 * @param fileName		file path
	 * @return				file contents
	 */
	public String loadFile(String fileName) {
		this.parser = new ABCParser(fileName);
		return abcUtil.getFileContents(fileName);
	}

	/**
	 * parse the loading file and generate a phrase
	 * @return				the structure of the loaded file
	 */
	public String parse() {
		try {
			phrase = parser.parse();
			NESeqList.setToStringAlgo(toStringAlgo);
		} catch (Exception e) {
			System.err.println("failed to parse file");
		}
		return phrase.toString();
	}

	/**
	 * play the generated phrase in certain instrument
	 * @param ist			the instrument used to play
	 */
	public void play(ABCInstrument ist) {
		sp.init(16, ist.getValue());
		phrase.execute(playAlgo, sp, 10, new KeySignature("C"));
	}

	/**
	 * parse fraction string and return its reciprocal, 
	 * will be used when parsing "L" header and set tempo
	 * @param frac				input fraction string
	 * @return					reciprocal of input fraction
	 */
	private double parseL(String frac) {
		String[] vals = frac.split("/");
		if (vals.length != 2) {
			throw new NumberFormatException(frac + " is not a fraction.");
		}
		double num = Double.parseDouble(vals[0]);
		double denom = Double.parseDouble(vals[1]);
		return denom / num;
	}

	/**
	 * stop playing the phrase
	 */
	public void stop() {
		sp.stop();
	}
}
