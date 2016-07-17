package OpenTiledClient;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;

public class MapOverlayRenderer {
	
	private Map map;
	
	private ArrayList<Rectangle> tileBoundingBox;
	
	public MapOverlayRenderer(Map map) {
		this.map = map;
	}
	
	public void paintTileLayer(Graphics2D g, TileLayer layer) {
        final Rectangle clip = g.getClipBounds();
        final int tileWidth = map.getTileWidth();
        final int tileHeight = map.getTileHeight();
        final Rectangle bounds = layer.getBounds();

        g.translate(bounds.x * tileWidth, bounds.y * tileHeight);
        clip.translate(-bounds.x * tileWidth, -bounds.y * tileHeight);

        clip.height += map.getTileHeightMax();

        final int startX = Math.max(0, clip.x / tileWidth);
        final int startY = Math.max(0, clip.y / tileHeight);
        final int endX = Math.min(layer.getWidth(),
                (int) Math.ceil(clip.getMaxX() / tileWidth));
        final int endY = Math.min(layer.getHeight(),
                (int) Math.ceil(clip.getMaxY() / tileHeight));
        
        Color prevColor = null;
        for (int x = startX; x < endX; ++x) {
            for (int y = startY; y < endY; ++y) {
                final Tile tile = layer.getTileAt(x, y);
                if (tile == null)
                    continue;
                final Image image = tile.getImage();
                if (image == null)
                    continue;

                /*g.drawImage(
                        image,
                        x * tileWidth,
                        (y + 1) * tileHeight - image.getHeight(null),
                        null);*/
                prevColor = g.getColor();
                g.setColor(Color.green);
                g.drawRect(x * tileWidth, (y + 1) * tileHeight - image.getHeight(null),
                		image.getWidth(null), image.getWidth(null));
            }
        }
        g.setColor(prevColor);
        
        g.translate(-bounds.x * tileWidth, -bounds.y * tileHeight);
    }
}
