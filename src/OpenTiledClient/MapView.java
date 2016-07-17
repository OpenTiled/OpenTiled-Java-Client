package OpenTiledClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.view.OrthogonalRenderer;

public class MapView extends JPanel{
	private Map map;
	private OrthogonalRenderer renderer;
	private MapOverlayRenderer mapOverlay;
	private double x, y;
	private double cameraSpeed;
	
	private String mapChange;
	
	public MapView(Map map) {
		this.map = map;
		renderer =  new OrthogonalRenderer(map);
		mapOverlay = new MapOverlayRenderer(map);
		x = 0;
		y = 0;
		cameraSpeed = 10;
		mapChange = "";
	}
	
	public double getOffsetX() {
		return x;
	}
	
	public double getOffsetY() {
		return y;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		final Graphics2D g2d = (Graphics2D) g.create();
        final Rectangle clip = g2d.getClipBounds();
        // Draw a gray background
        g2d.setPaint(new Color(100, 100, 100));
        g2d.fill(clip);
        g2d.translate(x, y);
        // Draw each tile map layer
        for (MapLayer layer : map) {
            if (layer instanceof TileLayer) {
                renderer.paintTileLayer(g2d, (TileLayer) layer);
                //mapOverlay.paintTileLayer(g2d, (TileLayer) layer);
            }
        }
	}
	
	public void update(InputHandler input) {
		if(input.pressingKey('W')) 
			y += cameraSpeed;
		if(input.pressingKey('A'))
			x += cameraSpeed;
		if(input.pressingKey('S'))
			y -= cameraSpeed;
		if(input.pressingKey('D'))
			x -= cameraSpeed;
		if(input.clicked()) {
			System.out.println("clicked");
			int x = (int)(input.getClickX() - getOffsetX())/32;
			int y = ((int)(input.getClickY() - getOffsetY())/32)-1;
			mapChange= "{"
					 	+ "x: " + x + ", " 
					 	+ "y: " + y + ", " 
					 	+ "tileID: " + TileSelector.selectedTile
					 + "}";
		}
		repaint();
	}
	
	public String getMapChange() {
		String save = mapChange;
		mapChange = "";
		return save;
	}


}
