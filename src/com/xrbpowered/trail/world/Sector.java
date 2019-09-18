package com.xrbpowered.trail.world;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3f;

import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.utils.RandomUtils;

public class Sector {

	public static final float lySize = 16f;
	
	public static final float size = 1f;
	public static final float margin = size/32f;

	private static final int maxd = 6;
	private static final float minc = -1f;
	private static final float maxc = 2f;//2.5f;
	private static final float coffs = 0.5f;
	private static final float cpow = 2f;
	private static final int base = 50;

	public final Region region;
	public final int sx, sy, sz;
	public final ArrayList<Star> stars = new ArrayList<>();
	
	public Sector(Region region, int sx, int sy, int sz) {
		this.region = region;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		
		generate(Region.worldSeed);
	}

	private static Random random = new Random();

	private static long seedXYZ(long seed, long x, long y, long z) {
		seed = RandomUtils.nextSeed(seed, x);
		seed = RandomUtils.nextSeed(seed, y);
		seed = RandomUtils.nextSeed(seed, z);
		seed = RandomUtils.nextSeed(seed, x);
		seed = RandomUtils.nextSeed(seed, y);
		seed = RandomUtils.nextSeed(seed, z);
		return seed;
	}
	
	private void generate(long seed) {
		int bx = region.rx*Region.regionSize+sx;
		int by = region.ry*Region.regionSize+sy;
		int bz = region.rz*Region.regionSize+sz;
		
		float c = minc;
		for(int d=maxd; d>0; d--) {
			int dx = bx>>d;
			int dy = by>>d;
			int dz = bz>>d;
			random.setSeed(seedXYZ(seed+d, dx+0, dy+0, dz+0));
			float c000 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+0, dy+0, dz+1));
			float c001 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+0, dy+1, dz+0));
			float c010 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+0, dy+1, dz+1));
			float c011 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+1, dy+0, dz+0));
			float c100 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+1, dy+0, dz+1));
			float c101 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+1, dy+1, dz+0));
			float c110 = random.nextFloat();
			random.setSeed(seedXYZ(seed+d, dx+1, dy+1, dz+1));
			float c111 = random.nextFloat();
			float sx = -((dx<<d)-bx) / (float)(1<<d);
			float sy = -((dy<<d)-by) / (float)(1<<d);
			float sz = -((dz<<d)-bz) / (float)(1<<d);
			c += (maxc / maxd) * MathUtils.lerp(
					MathUtils.lerp(MathUtils.lerp(c000, c001, sz), MathUtils.lerp(c010, c011, sz), sy),
					MathUtils.lerp(MathUtils.lerp(c100, c101, sz), MathUtils.lerp(c110, c111, sz), sy),
				sx);
		}
		
		double galDist = 1f - (float)Math.sqrt(bx*bx+by*by+bz*bz)/Region.galaxySize;
		if(galDist<0f)
			galDist = 0f;
		c = (float)Math.pow(c*galDist+galDist*coffs, cpow);
		if(c<1f/(float)base)
			return;
		
		int n = (int)(base*c)+1;
		random.setSeed(seedXYZ(Region.worldSeed+maxd+1, bx, by, bz));
		Vector3f pos = new Vector3f();
		for(int i=0; i<n; i++) {
			pos.x = bx*size +(random.nextFloat()-0.5f)*(size-margin); 
			pos.y = by*size +(random.nextFloat()-0.5f)*(size-margin); 
			pos.z = bz*size +(random.nextFloat()-0.5f)*(size-margin); 
			boolean skip = false;
			for(Star s : stars) {
				if(pos.distance(s.location)<margin*2f) {
					skip = true;
					break;
				}
			}
			if(!skip) {
				Star star = new Star(this, pos, random);
				star.name = String.format("%s.%d%d%d.%d", region.name, sx, sy, sz, stars.size());
				stars.add(star);
			}
		}
	}

}
