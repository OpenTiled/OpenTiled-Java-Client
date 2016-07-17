package OpenTiledClient;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	
	private ArrayList<Character> keysDown;
	private int mouseX, mouseY;
	private int clickX, clickY;
	private boolean clicked;
	
	public InputHandler() {
		keysDown = new ArrayList<Character>();
	}
	
	public boolean pressingKey(char keyCode) {
		keyCode = Character.toLowerCase(keyCode);
		for(Character key : keysDown) {
			if(key == keyCode)
				return true;
		}
		return false;
	}
	
	public int getMousePosX() {
		return mouseX;
	}
	
	public int getMousePosY() {
		return mouseY;
	}
	
	public boolean clicked() {
		boolean save = clicked;
		clicked = false;
		return save;
	}
	
	public int getClickX() {
		return clickX;
	}
	
	public int getClickY() {
		return clickY;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add((Character)e.getKeyChar());
		//System.out.println(e.getKeyChar());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(int i = 0; i < keysDown.size(); i++) {
			if(keysDown.get(i) == e.getKeyChar())
				keysDown.remove(i);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
		clicked = true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
	}

}
