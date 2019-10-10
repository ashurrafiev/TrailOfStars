package com.xrbpowered.trail.world;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class Region {

	public static final float globalScale = 1f;//0.05f;
	public static final int galaxySize = 128;//128;
	public static final int regionSize = 8;
	
	public static long worldSeed = System.currentTimeMillis();
	
	public final int rx, ry, rz;
	public Sector[][][] sectors;
	public int numStars;
	
	public String name;

	public Region(int rx, int ry, int rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.name = String.format("%s%s%s", regChar(rx), regChar(ry), regChar(rz));
		this.sectors = new Sector[regionSize][regionSize][regionSize];
		numStars = 0;
		for(int x=0; x<regionSize; x++)
			for(int y=0; y<regionSize; y++)
				for(int z=0; z<regionSize; z++) {
					sectors[x][y][z] = new Sector(this, x, y, z);
					numStars += sectors[x][y][z].stars.size(); 
				}
	}
	
	public StaticMesh createPointMesh(VertexInfo info) {
		float[] data = new float [numStars*5];
		int offs = 0;
		for(int x=0; x<regionSize; x++)
			for(int y=0; y<regionSize; y++)
				for(int z=0; z<regionSize; z++) {
					for(Star s : sectors[x][y][z].stars) {
						data[offs++] = s.location.x * globalScale;
						data[offs++] = s.location.y * globalScale;
						data[offs++] = s.location.z * globalScale;
						data[offs++] = s.brightness;
						data[offs++] = s.temperature;
					}
				}
		return new StaticMesh(info, data, 1, numStars, false);
	}
	
	public boolean iterateStars(StarFunction func) {
		for(int x=0; x<regionSize; x++)
			for(int y=0; y<regionSize; y++)
				for(int z=0; z<regionSize; z++) {
					for(Star star : sectors[x][y][z].stars) {
						if(func.apply(star))
							return true;
					}
				}
		return false;
	}
	
	private static String regChar(int r) {
		return Integer.toString((r + galaxySize/regionSize), 36).toUpperCase();
	}

}
