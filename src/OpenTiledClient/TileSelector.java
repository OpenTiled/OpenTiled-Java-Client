package OpenTiledClient;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import tiled.core.Tile;
import tiled.core.TileSet;

public class TileSelector extends JFrame {
	
	public static int selectedTile;
	private JLabel selectLabel;
	
	public TileSelector(TileSet tileset) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		boolean first = true;
		Border border = BorderFactory.createLineBorder(Color.GREEN, 3);
		for(Tile tile : tileset) {
			JLabel label = new JLabel();
			label.setIcon(new ImageIcon(tile.getImage()));
			if(first){
				first = false;
				selectLabel = label;
				selectLabel.setBorder(border);
			}
			label.addMouseListener(new MouseAdapter()  
			{  
			    public void mouseClicked(MouseEvent e)  
			    {  
			    	selectLabel.setBorder(null);
			    	selectLabel = label;
			    	selectLabel.setBorder(border);
			    	selectedTile = tile.getId() + 1;
			    }  
			}); 
			panel.add(label);
		}
		add(new JScrollPane(panel));
		pack();
		setVisible(true);
	}

}
