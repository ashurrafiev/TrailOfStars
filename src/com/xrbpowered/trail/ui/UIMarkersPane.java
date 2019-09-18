package com.xrbpowered.trail.ui;

import java.util.ArrayList;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.common.UIPointerActor;
import com.xrbpowered.trail.Trail;
import com.xrbpowered.trail.world.Sector;
import com.xrbpowered.trail.world.Star;
import com.xrbpowered.trail.world.StarFunction;
import com.xrbpowered.zoomui.UIContainer;

public class UIMarkersPane extends UINode {

	public static final float maxDist = Sector.size;
	
	public StarFunction placerFunc = new StarFunction() {
		@Override
		public boolean apply(Star star) {
			float dist = Trail.stars.currentSystem.location.distance(star.location);
			if(dist<=maxDist)
				addMarker(star, dist, Trail.stars.camera);
			return false;
		}
	};
	
	public ArrayList<UIMarker> markers = new ArrayList<>();
	
	public UIMarkersPane(UIContainer parent) {
		super(parent);
	}
	
	public void clear() {
		removeAllChildren();
		markers.clear();
	}
	
	public void addMarker(Star star, float dist, CameraActor camera) {
		UIMarker m = new UIMarker(this, star);
		m.dist = dist;
		UIPointerActor ptr = new UIPointerActor(m, camera);
		ptr.position = star.location;
		ptr.updateTransform();
		m.setPointer(ptr);
		markers.add(m);
	}
	
	public void updateView(RenderTarget target) {
		UIMarker prevMarker = UIMarker.centerMarker; 
		UIMarker.centerMarker = null;
		for(UIMarker m : markers)
			m.updateView(target);
		if(UIMarker.centerMarker!=null && UIMarker.centerMarker!=prevMarker) {
			UIMarker.centerMarker.repaint();
			Trail.hud.repaint();
		}
	}

}
