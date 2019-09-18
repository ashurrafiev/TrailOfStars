package com.xrbpowered.trail.ui;

import java.awt.Color;
import java.awt.Font;

import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.trail.Trail;
import com.xrbpowered.trail.world.Sector;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class UIHud extends UINode {

	public static final Color fill = new Color(0, true);
	public static final Color textColor = new Color(0xdddddd);

	public static final Font font = new Font("Verdana", Font.PLAIN, 14);
	public static final Font fontLarge = font.deriveFont(Font.BOLD, 17);

	public final UIPane info;
	
	public UIHud(UIContainer parent) {
		super(parent);

		info = new UIPane(this, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.graph.setBackground(fill);
				g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
				if(UIMarker.centerMarker!=null && Trail.markers.isVisible()) {
					g.setFont(font);
					g.setColor(textColor);
					g.drawString("Destination:", 15, 30, GraphAssist.LEFT, GraphAssist.BOTTOM);
					g.setFont(fontLarge);
					g.setColor(Color.WHITE);
					g.drawString(String.format("%s (%.2f Ly)", UIMarker.centerMarker.star.name, UIMarker.centerMarker.dist*Sector.lySize), 140, 30, GraphAssist.LEFT, GraphAssist.BOTTOM);
				}
				g.setFont(font);
				g.setColor(textColor);
				g.drawString("Current system:", 15, 55, GraphAssist.LEFT, GraphAssist.BOTTOM);
				g.setFont(fontLarge);
				g.setColor(Color.WHITE);
				g.drawString(Trail.stars.currentSystem.name, 140, 55, GraphAssist.LEFT, GraphAssist.BOTTOM);
			}
		};
		info.setSize(440, 80);
	}
	
	@Override
	public void layout() {
		info.setLocation(getWidth()/2-info.getWidth()/2, getHeight()-info.getHeight()-32);
		super.layout();
	}

}
