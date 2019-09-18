package com.xrbpowered.trail.ui;

import java.awt.Color;
import java.awt.Font;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.ui.common.UIPointerActor;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.trail.world.Sector;
import com.xrbpowered.trail.world.Star;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class UIMarker extends UIPane {

	public static final Color fill = new Color(0, true);
	public static final Color lineColor = new Color(0x55999999, true);
	public static final Color textColor = new Color(0xdddddddd, true);

	public static final Font font = UIHud.font;
	public static final float maxCenter = 200;
	
	public static UIMarker centerMarker = null;
	
	public final Star star;
	
	private UIPointerActor pointer;
	
	public float dist = 0f;
	public float center;
	
	public UIMarker(UIContainer parent, Star star) {
		super(parent, false);
		this.star = star;
		setSize(192, 64);
	}
	
	public void setPointer(UIPointerActor pointer) {
		this.pointer = pointer;
		pointer.setPivot(32, 32);
	}
	
	public void updateView(RenderTarget target) {
		pointer.updateView(target);
		if(isVisible() && dist>0f) {
			float dx = getX() + pointer.pivotx - target.getWidth()/2;
			float dy = getY() + pointer.pivoty - target.getHeight()/2;
			center = (float)Math.sqrt(dx*dx+dy*dy);
			if(center<=maxCenter) {
				if(centerMarker==null || center<centerMarker.center)
					centerMarker = this;
			}
		}
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.graph.setBackground(fill);
		g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
		g.setColor(this==centerMarker ? Color.CYAN : dist==0f ? Color.WHITE : lineColor);
		g.setStroke(2f);
		g.graph.drawOval(2, 2, 60, 60);
		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(star.name, 70, 30, GraphAssist.LEFT, GraphAssist.BOTTOM);
		if(dist>0f) {
			g.setColor(textColor);
			g.drawString(String.format("%.1f LY", dist * Sector.lySize), 70, 34, GraphAssist.LEFT, GraphAssist.TOP);
		}
	}
	
}
