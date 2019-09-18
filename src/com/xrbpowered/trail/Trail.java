package com.xrbpowered.trail;

import static java.awt.event.KeyEvent.*;

import org.lwjgl.glfw.GLFW;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.trail.ui.UIHud;
import com.xrbpowered.trail.ui.UIMarker;
import com.xrbpowered.trail.ui.UIMarkersPane;

public class Trail extends UIClient {

	public static UIStarsPane stars;
	public static UIMarkersPane markers;
	public static UIHud hud;
	
	public Trail() {
		super("Trailblazer Star Map", 1f);
		fullscreen = true;
		vsync = false;
		noVsyncSleep = 4;
		
		stars = new UIStarsPane(getContainer());
		markers = new UIMarkersPane(getContainer());
		markers.setVisible(false);
		hud = new UIHud(getContainer());
		new UIFpsOverlay(this);
	}

	@Override
	public void keyPressed(char c, int code) {
		switch(code) {
			case VK_ESCAPE:
				requestExit();
				break;
			case VK_TAB:
				markers.setVisible(!markers.isVisible());
				break;
		}
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT && UIMarker.centerMarker!=null && markers.isVisible())
			stars.setCurrentSystem(UIMarker.centerMarker.star);
		super.mouseDown(x, y, button);
	}
	public static void main(String[] args) {
		new Trail().run();
	}

}
