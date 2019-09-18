package com.xrbpowered.trail.world;

import java.util.Random;

import org.joml.Vector3f;

public class Star {

	public static final float brightnessScale = 10f;
	
	public final Sector sector;
	public final Vector3f location;
	public final float brightness;
	public final float temperature;
	
	public String name;
	
	public Star(Sector sector, Vector3f location, Random random) {
		this.sector = sector;
		this.location = new Vector3f(location);
		this.brightness = ((float)Math.pow(random.nextFloat(), 5)+0.1f)*brightnessScale;
		this.temperature = (float) BlackBodySpectrum.randomTemp(random);
	}

}
