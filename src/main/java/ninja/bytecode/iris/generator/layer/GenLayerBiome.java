package ninja.bytecode.iris.generator.layer;

import java.util.Random;
import java.util.function.Function;

import org.bukkit.World;

import ninja.bytecode.iris.Iris;
import ninja.bytecode.iris.generator.IrisGenerator;
import ninja.bytecode.iris.pack.IrisBiome;
import ninja.bytecode.iris.pack.IrisRegion;
import ninja.bytecode.iris.util.GenLayer;
import ninja.bytecode.iris.util.PolygonGenerator.EnumPolygonGenerator;
import ninja.bytecode.shuriken.collections.GList;
import ninja.bytecode.shuriken.collections.GMap;
import ninja.bytecode.shuriken.math.CNG;
import ninja.bytecode.shuriken.math.M;
import ninja.bytecode.shuriken.math.RNG;

public class GenLayerBiome extends GenLayer
{
	private EnumPolygonGenerator<IrisRegion> regionGenerator;
	private GMap<String, IrisRegion> regions;
	private Function<CNG, CNG> factory;
	private CNG fracture;
	private CNG island;

	public GenLayerBiome(IrisGenerator iris, World world, Random random, RNG rng, GList<IrisBiome> biomes)
	{
		super(iris, world, random, rng);
		island = new CNG(rng.nextParallelRNG(10334), 1D, 3).scale(0.003 * Iris.settings.gen.landScale).fractureWith(new CNG(rng.nextParallelRNG(34), 1D, 12).scale(0.6), 180);
		fracture = new CNG(rng.nextParallelRNG(28), 1D, 24).scale(0.0021).fractureWith(new CNG(rng.nextParallelRNG(34), 1D, 12).scale(0.01), 12250);
		factory = (g) -> g.fractureWith(new CNG(rng.nextParallelRNG(29), 1D, 4).scale(0.02), 56);
		regions = new GMap<>();

		for(IrisBiome i : biomes)
		{
			if(i.getName().equals("Beach"))
			{
				continue;
			}

			if(!regions.containsKey(i.getRegion()))
			{
				regions.put(i.getRegion(), new IrisRegion(i.getRegion()));
			}

			regions.get(i.getRegion()).getBiomes().add(i);
		}

		for(IrisRegion i : regions.values())
		{
			i.load();
		}

		int v = 85034;
		regionGenerator = new EnumPolygonGenerator<IrisRegion>(rng.nextParallelRNG(v), 0.00522 * Iris.settings.gen.biomeScale * 0.189, 1, regions.v().toArray(new IrisRegion[regions.v().size()]), factory);

		for(IrisRegion i : regions.v())
		{
			v += 13 - i.getName().length();
			i.setGen(new EnumPolygonGenerator<IrisBiome>(rng.nextParallelRNG(33 + v), 0.000255 * i.getBiomes().size() * Iris.settings.gen.biomeScale, 1, i.getBiomes().toArray(new IrisBiome[i.getBiomes().size()]), factory));
		}
	}

	public boolean hasBorder(int checks, double distance, double... dims)
	{
		IrisBiome current = getBiome(dims[0], dims[1]);
		double ajump = 360D / (double) checks;

		if(dims.length == 2)
		{
			for(int i = 0; i < checks; i++)
			{
				double dx = M.sin((float) Math.toRadians(ajump * i));
				double dz = M.cos((float) Math.toRadians(ajump * i));
				if(!current.equals(getBiome((dx * distance) + dims[0], (dz * distance) + dims[1])))
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasHeightBorder(int checks, double distance, double... dims)
	{
		IrisBiome current = getBiome(dims[0], dims[1]);
		double ajump = 360D / (double) checks;

		if(dims.length == 2)
		{
			for(int i = 0; i < checks; i++)
			{
				double dx = M.sin((float) Math.toRadians(ajump * i));
				double dz = M.cos((float) Math.toRadians(ajump * i));
				if(current.getHeight() != getBiome((dx * distance) + dims[0], (dz * distance) + dims[1]).getHeight())
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean isBorder(int wx, int wz, double range)
	{
		return hasHeightBorder(6, range, wx, wz);
	}

	public EnumPolygonGenerator<IrisBiome> getRegionGenerator(double xx, double zz)
	{
		return regionGenerator.getChoice(xx, zz).getGen();
	}

	public IrisBiome getBiome(double xx, double zz)
	{
		double x = xx + (Iris.settings.gen.biomeEdgeScramble == 0 ? 0 : (fracture.noise(zz, xx) * Iris.settings.gen.biomeEdgeScramble));
		double z = zz - (Iris.settings.gen.biomeEdgeScramble == 0 ? 0 : (fracture.noise(xx, zz) * Iris.settings.gen.biomeEdgeScramble));
		IrisBiome cbi = iris.biome("Ocean");
		double land = island.noise(x, z);
		double landChance = 1D - M.clip(Iris.settings.gen.landChance, 0D, 1D);

		if(land > landChance + 0.0175)
		{
			cbi = getRegionGenerator(x, z).getChoice(x, z);
		}

		else if(land < 0.3)
		{
			cbi = iris.biome("Deep Ocean");
		}

		return cbi;
	}

	@Override
	public double generateLayer(double noise, double dx, double dz)
	{
		return noise;
	}

	public IrisRegion getRegion(String name)
	{
		return regions.get(name);
	}
}
