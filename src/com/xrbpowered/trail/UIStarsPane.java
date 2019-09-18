package com.xrbpowered.trail;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.trail.world.BlackBodySpectrum;
import com.xrbpowered.trail.world.RegionManager;
import com.xrbpowered.trail.world.Star;
import com.xrbpowered.trail.world.StarFunction;
import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.utils.TweenUtils;
import com.xrbpowered.zoomui.UIContainer;

public class UIStarsPane extends UIOffscreen {

	private VertexInfo starInfo;
	private Shader starShader;
	
	private Texture spectrum;

	public CameraActor camera;
	public Controller controller;

	public Star origin;
	public Star currentSystem;
	public RegionManager regions;

	public boolean transition = false;
	public float transitionTime = 0f;
	public Vector3f initialPos;
	public Vector3f targetPos;
	public boolean showMarkersAfterTransition;
	
	public UIStarsPane(UIContainer parent) {
		super(parent);
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		camera.setAspectRatio(getWidth(), getHeight());
	}
	
	@Override
	public void setupResources() {
		clearColor = new Color(0x000911);

		camera = new CameraActor.Perspective().setFov(75f).setRange(0.01f, 20f).setAspectRatio(getWidth(), getHeight());
		
		controller = new Controller(getClient().input).setActor(camera);
		controller.moveSpeed = 1f;
		controller.setMouseLook(true);
		
		spectrum = new Texture(BlackBodySpectrum.generateImage(512, 1));
		
		starInfo = new VertexInfo().addAttrib("in_Position", 3).addAttrib("in_Brightness", 1).addAttrib("in_Temperature", 1);
		
		starShader = new Shader(starInfo, "points_v.glsl", "points_f.glsl") {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			private int screenHeightLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				screenHeightLocation  = GL20.glGetUniformLocation(pId, "screenHeight");
			}
			@Override
			public void updateUniforms() {
				glDisable(GL_DEPTH_TEST);
				glEnable(GL20.GL_POINT_SPRITE);
				glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				glEnable(GL11.GL_BLEND);
				glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				uniform(projectionMatrixLocation, camera.getProjection());
				uniform(viewMatrixLocation, camera.getView());
				GL20.glUniform1f(screenHeightLocation, getHeight());
			}
			@Override
			public void unuse() {
				glDisable(GL11.GL_BLEND);
				glEnable(GL_DEPTH_TEST);
				super.unuse();
			}
		};

		regions = new RegionManager(starInfo, camera.position);

		regions.regions[RegionManager.range][RegionManager.range][RegionManager.range].iterateStars(new StarFunction() {
			@Override
			public boolean apply(Star star) {
				origin = star;
				return true;
			}
		});
		setCurrentSystem(origin);
		transitionTime = 1f;

		super.setupResources();
	}
	
	public void setCurrentSystem(Star star) {
		currentSystem = star;
		initialPos = new Vector3f(camera.position);
		targetPos = currentSystem.location;
		transitionTime = 0f;
		transition = true;
		showMarkersAfterTransition = Trail.markers.isVisible();
		
		Trail.markers.clear();
		regions.iterateStars(Trail.markers.placerFunc);
		Trail.markers.setVisible(false);
	}
	
	@Override
	public void updateTime(float dt) {
		super.updateTime(dt);
		if(transition) {
			transitionTime += dt;
			if(transitionTime>=1f) {
				transition = false;
				camera.position.set(targetPos);
				camera.updateTransform();
				if(showMarkersAfterTransition)
					Trail.markers.setVisible(true);
			}
			else {
				float s = (float) TweenUtils.easeInOut(transitionTime);
				camera.position.x = MathUtils.lerp(initialPos.x, targetPos.x, s);
				camera.position.y = MathUtils.lerp(initialPos.y, targetPos.y, s);
				camera.position.z = MathUtils.lerp(initialPos.z, targetPos.z, s);
				camera.updateTransform();
			}
		}
		if(!transition){
			controller.update(dt);
			regions.updateOrigin(camera.position);
		}
	}
	
	@Override
	public void render(RenderTarget target) {
		super.render(target);
		starShader.use();
		spectrum.bind(0);
		regions.draw();
		starShader.unuse();
		if(Trail.markers.isVisible())
			Trail.markers.updateView(target);
	}
}
