package OpenTiledClient;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.JSONObject;

public class IO {
	
	private Socket socket;
	
	private Thread inputThread;
	private InputStream input;
	private BufferedReader reader;
	
	private Thread outputThread;
	private OutputStream output;
	private DataOutputStream writer;
	
	private boolean loadedFiles = false;
	private String tmxFile = "";
	private BufferedImage tileset;
	
	private JSONObject payload;
	private Queue<String> mapChanges;
	private boolean sentPayload;
	
	private String serverUpdate;

	public void connect(String ip, int port) {
		payload = new JSONObject("{protocol: 'update', posX: 0, posY: 0}");
		serverUpdate = "";
		mapChanges = new LinkedList<String>();
		try {
			socket = new Socket(ip, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = new BufferedReader(new InputStreamReader(input));
		inputThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						//System.out.println("reading from server");
						System.out.println(reader.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		writer = new DataOutputStream(output);
		outputThread = new Thread() {
			
			boolean tmxRead = false;
			@Override
			public void run() {
				while(true) {
					try {
						if(!loadedFiles) {
							if(!tmxRead) {
								writer.writeBytes("req:map \n");
								writer.flush();
								String line;
								while(!(line = reader.readLine()).contains("EOF")) {
									tmxFile += line;
									//System.out.println(tmxFile);
									//System.out.println(line);
								}
								//System.out.println("end of file");
								tmxRead = true;					
							} else {
								//System.out.println("reading image");
								writer.writeBytes("req:tileset \n");
								writer.flush();
								byte[] sizeAr = new byte[4];
						        input.read(sizeAr);
						        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
						        //System.out.println("size: " + size);
						        byte[] imageAr = new byte[size];
						        //input.read(imageAr);
						        new DataInputStream(input).readFully(imageAr, 0, imageAr.length);
						        //System.out.println(Arrays.toString(imageAr));
						        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
						        tileset = image;
						        loadedFiles = true;
							} 
						} else  {
							//System.out.println("payload send" + payload);
							//String payload = payloads.poll();
							if(mapChanges.size() > 0) {
								payload.accumulate("mapchange", new JSONObject(mapChanges.poll()));
							}
							String output = payload.toString();
							output = (output.isEmpty()) ? "{protocol: 'update', posX: 0, posY: 0}" : output;
							writer.writeBytes(payload+"\n");
							writer.flush();
							payload = new JSONObject("{protocol: 'update', posX: 0, posY: 0}");
							sentPayload = true;
							serverUpdate = reader.readLine();
							if(serverUpdate.length() > 0) {
								System.out.println(serverUpdate);
								Window.updateFromServer(serverUpdate, Window.layer);
							}
						}
						//Thread.sleep(10);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}				
			}
			
		}; 
		outputThread.start();
	}
	
	public String getServerUpdate() {
		String save = serverUpdate;
		serverUpdate = "";
		return save;
	}
	
	public void streamData(JSONObject payload) {
		this.payload = payload;
	}
	
	public void sendMapChanges(String value) {
		mapChanges.add(value);
	}
	
	public String getTMXString() {
		return tmxFile;
	}
	
	public BufferedImage getTileset() {
		return tileset;
	}
	
	public boolean doneReadFiles() {
		return loadedFiles;
	}

}
