package com.xrbpowered.trail.world;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class RegionManager {

	public static int range = 1;
	public static int span = range*2+1;
	
	public final VertexInfo info;
	public int ox, oy, oz;
	public int dx, dy, dz;
	
	public Region[][][] regions = new Region[span][span][span];
	public StaticMesh[][][] meshes = new StaticMesh[span][span][span];
	
	public RegionManager(VertexInfo info, Vector3f origin) {
		this.info = info;
		setOrigin(origin);
		createAll();
	}
	
	public void release(StaticMesh[][][] meshes) {
		for(int x=0; x<span; x++)
			for(int y=0; y<span; y++)
				for(int z=0; z<span; z++) {
					StaticMesh m = meshes[x][y][z];
					if(m!=null)
						m.release();
					meshes[x][y][z] = null;
				}
	}

	public void release() {
		release(meshes);
	}
	
	public void createAll() {
		release();
		for(int x=-range; x<=range; x++)
			for(int y=-range; y<=range; y++)
				for(int z=-range; z<=range; z++) {
					Region reg = new Region(ox+x, oy+y, oz+z);
					StaticMesh m = reg.createPointMesh(info);
					regions[x+range][y+range][z+range] = reg;
					meshes[x+range][y+range][z+range] = m;
				}
	}
	
	public boolean setOrigin(Vector3f origin) {
		int px = ox;
		int py = oy;
		int pz = oz;
		float s = Sector.size * Region.regionSize * Region.globalScale;
		ox = (int)Math.floor(origin.x / s);
		oy = (int)Math.floor(origin.y / s);
		oz = (int)Math.floor(origin.z / s);
		dx = ox - px;
		dy = oy - py;
		dz = oz - pz;
		return dx!=0 || dy!=0 || dz!=0;
	}
	
	private boolean inRange(int x) {
		return x>=-range && x<=range;
	}
	
	public void updateOrigin(Vector3f origin) {
		if(!setOrigin(origin))
			return;
		
		Region[][][] prevRegions = regions;
		StaticMesh[][][] prevMeshes = meshes;
		regions = new Region[span][span][span];
		meshes = new StaticMesh[span][span][span];
		
		for(int x=-range; x<=range; x++)
			for(int y=-range; y<=range; y++)
				for(int z=-range; z<=range; z++) {
					int px = x+dx;
					int py = y+dy;
					int pz = z+dz;
					if(inRange(px) && inRange(py) && inRange(pz)) {
						regions[x+range][y+range][z+range] = prevRegions[px+range][py+range][pz+range];
						meshes[x+range][y+range][z+range] = prevMeshes[px+range][py+range][pz+range];
						prevMeshes[px+range][py+range][pz+range] = null;
					}
					else {
						Region reg = new Region(ox+x, oy+y, oz+z);
						StaticMesh m = reg.createPointMesh(info);
						regions[x+range][y+range][z+range] = reg;
						meshes[x+range][y+range][z+range] = m;
					}
				}
		release(prevMeshes);
	}
	
	public void draw() {
		for(int x=0; x<span; x++)
			for(int y=0; y<span; y++)
				for(int z=0; z<span; z++) {
					StaticMesh m = meshes[x][y][z];
					if(m!=null)
						m.draw();
				}
	}
	
	public boolean iterateStars(StarFunction func) {
		for(int x=0; x<span; x++)
			for(int y=0; y<span; y++)
				for(int z=0; z<span; z++) {
					if(regions[x][y][z].iterateStars(func))
						return true;
				}
		return false;
	}


}
