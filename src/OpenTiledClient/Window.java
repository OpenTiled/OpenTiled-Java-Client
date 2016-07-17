package OpenTiledClient;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;

import tiled.core.Map;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

public class Window extends JFrame {
	
	public static TileLayer layer;
	
	public static void main(String[] args0) {
		Map map;
		/*URL url = null;
		try {
			//url = new URL("http://10.108.70.139:90");
			url = new URL("http://google.com");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
		    for (String line; (line = reader.readLine()) != null;) {
		        //System.out.println(line);
		    }
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
            TMXMapReader mapReader = new TMXMapReader();
            map = mapReader.readMap("/Users/kerkinmichel/Desktop/OpenTiled/OpenTiled/resources/sewers.tmx");
            //map = mapReader.readMapFromServer(url);
            
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Error while reading the map:\n" + e.getMessage());
            return;
        }
		
		System.out.println(map.toString() + " loaded");*/
		String ip = JOptionPane.showInputDialog(null, "Input ip and host of OpenTiled Server"
				+ "\n Example: 127.0.0.1:3000");
		String[] ipDetails = ip.split(":"); 
		System.out.println(ipDetails[0]);
		IO io = new IO();
		io.connect(ipDetails[0], Integer.parseInt(ipDetails[1]));
		System.out.println("waiting");
		System.out.println(io.doneReadFiles());
		try {
			while(!io.doneReadFiles()) {
				System.out.println(io.doneReadFiles());
			}
			//System.out.println("now done can get start");
			TMXMapReader mapReader = new TMXMapReader();
            //System.out.println("files" + io.getTMXString());
            map = mapReader.readMapFromString(io.getTMXString(), io.getTileset());    
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Error while reading the map:\n" + e.getMessage());
            return;
        }
		
		InputHandler inputHandler = new InputHandler();
		TileLayer layer = (TileLayer) map.getLayer(0);
		Window.layer = layer;
		//OrthogonalRenderer renderer =  new OrthogonalRenderer(map);
		Window window = new Window();
		MapView mapView = new MapView(map);
		window.add(mapView);
		window.setSize(640, 480);
		window.addKeyListener(inputHandler);
		window.addMouseListener(inputHandler);
		window.addMouseMotionListener(inputHandler);
		window.setVisible(true);
		TileSelector tileSelector = new TileSelector(map.getTileSets().get(0));
		while(true) {
			mapView.update(inputHandler);
			int x = 0, y = 0;
			String mapChange = mapView.getMapChange();
			if(mapChange.length() > 0) {
				//mapChange = "mapchange: " + mapChange;
				io.sendMapChanges(mapChange);
			}
			String payload = "{"
								+ "protocol: 'update',"
								+ "posX: " + x + ", "
								+ "posY: " + y + ", "
								//+ mapChange
							+"}";
			
			io.streamData(new JSONObject(payload));
			//updateFromServer(io.getServerUpdate(), layer);
			try {
				Thread.sleep(1000/30);//Max 30FPS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void updateFromServer(String update, TileLayer layer) {
		if(update.isEmpty())
			return;
		
		JSONObject data = new JSONObject(update);
		if(!data.isNull("mapchange")) {
			try {
				JSONObject mapchange = data.getJSONObject("mapchange");
				int x = mapchange.getInt("x");
				int y = mapchange.getInt("y");
				int tileID = mapchange.getInt("tileID");
				//There seems to be a bug with the id of the first tile
				tileID = tileID > 0 ? tileID-1 : tileID;   
				layer.setTileAt(x, y, layer.getMap().getTileSets().get(0).getTile(tileID));
			} catch(JSONException e) {
				System.out.println("Malformed JSON " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
