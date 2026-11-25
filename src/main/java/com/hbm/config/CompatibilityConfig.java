package com.hbm.config;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class CompatibilityConfig {

		public static HashMap<Integer, Float> dimensionRad;
	public static HashSet<String> bedrockOreBlacklist;

	public static HashMap<Integer, Integer> uraniumSpawn;
	public static HashMap<Integer, Integer> thoriumSpawn;
	public static HashMap<Integer, Integer> titaniumSpawn;
	public static HashMap<Integer, Integer> sulfurSpawn;
	public static HashMap<Integer, Integer> aluminiumSpawn;
	public static HashMap<Integer, Integer> copperSpawn;
	public static HashMap<Integer, Integer> fluoriteSpawn;
	public static HashMap<Integer, Integer> niterSpawn;
	public static HashMap<Integer, Integer> tungstenSpawn;
	public static HashMap<Integer, Integer> leadSpawn;
	public static HashMap<Integer, Integer> berylliumSpawn;
	public static HashMap<Integer, Integer> ligniteSpawn;
	public static HashMap<Integer, Integer> asbestosSpawn;
	public static HashMap<Integer, Integer> rareSpawn;
	public static HashMap<Integer, Integer> lithiumSpawn;
	public static HashMap<Integer, Integer> cinnebarSpawn;
	public static HashMap<Integer, Integer> oilcoalSpawn;
	public static HashMap<Integer, Integer> gassshaleSpawn;
	public static HashMap<Integer, Integer> gasbubbleSpawn;
	public static HashMap<Integer, Integer> explosivebubbleSpawn;
	public static HashMap<Integer, Integer> cobaltSpawn;
	
	public static HashMap<Integer, Integer> ironClusterSpawn;
	public static HashMap<Integer, Integer> titaniumClusterSpawn;
	public static HashMap<Integer, Integer> aluminiumClusterSpawn;
	public static HashMap<Integer, Integer> copperClusterSpawn;

	public static HashMap<Integer, Integer> reiiumSpawn;
	public static HashMap<Integer, Integer> weidaniumSpawn;
	public static HashMap<Integer, Integer> australiumSpawn;
	public static HashMap<Integer, Integer> verticiumSpawn;
	public static HashMap<Integer, Integer> unobtainiumSpawn;
	public static HashMap<Integer, Integer> daffergonSpawn;
	
	public static HashMap<Integer, Integer> netherUraniumSpawn;
	public static HashMap<Integer, Integer> netherTungstenSpawn;
	public static HashMap<Integer, Integer> netherSulfurSpawn;
	public static HashMap<Integer, Integer> netherPhosphorusSpawn;
	public static HashMap<Integer, Integer> netherCoalSpawn;
	public static HashMap<Integer, Integer> netherPlutoniumSpawn;
	public static HashMap<Integer, Integer> netherCobaltSpawn;

	public static HashMap<Integer, Integer> endTixiteSpawn;

	public static HashMap<Integer, Integer> bedrockOilSpawn;
	
	public static HashMap<Integer, Integer> radioStructure;
	public static HashMap<Integer, Integer> antennaStructure;
	public static HashMap<Integer, Integer> atomStructure;
	public static HashMap<Integer, Integer> vertibirdStructure;
	public static HashMap<Integer, Integer> dungeonStructure;
	public static HashMap<Integer, Integer> relayStructure;
	public static HashMap<Integer, Integer> satelliteStructure;
	public static HashMap<Integer, Integer> bunkerStructure;
	public static HashMap<Integer, Integer> siloStructure;
	public static HashMap<Integer, Integer> factoryStructure;
	public static HashMap<Integer, Integer> dudStructure;
	public static HashMap<Integer, Integer> spaceshipStructure;
	public static HashMap<Integer, Integer> barrelStructure;
	public static HashMap<Integer, Integer> geyserWater;
	public static HashMap<Integer, Integer> geyserChlorine;
	public static HashMap<Integer, Integer> geyserVapor;
	public static HashMap<Integer, Integer> geyserNether;
	public static HashMap<Integer, Integer> meteorStructure;
	public static HashMap<Integer, Integer> capsuleStructure;
	public static HashMap<Integer, Integer> broadcaster;
	public static HashMap<Integer, Integer> minefreq;
	public static HashMap<Integer, Integer> radfreq;
	public static HashMap<Integer, Integer> vaultfreq;
	public static HashMap<Integer, Integer> arcticStructure;
	public static HashMap<Integer, Integer> jungleStructure;
	public static HashMap<Integer, Integer> pyramidStructure;
	
	public static HashMap<Integer, Integer> meteorStrikeChance;
	public static HashMap<Integer, Integer> meteorShowerChance;
	public static HashMap<Integer, Integer> meteorShowerDuration;

	public static HashMap<String, Float> mobModRadresistance;
	public static HashSet<String> mobModRadimmune;

	public static HashMap<String, Float> mobRadresistance;
	public static HashSet<String> mobRadimmune;

	public static boolean mobGear = true;

	public static boolean modLoot = true;

	public static boolean doEvaporateWater = true;
	public static HashSet<Integer> evaporateWater;
	public static boolean doFillCraterWithWater = true;
    public static HashMap<Integer, Integer> fillCraterWithWater;

	public static boolean peaceDimensionsIsWhitelist = true;
	public static HashSet<Integer> peaceDimensions;
	
	public static void loadFromConfig(Configuration config) {
		final String CATEGORY_DIMRAD = "01_dimension_radiation";
		final String CATEGORY_DIMORE = "02_dimension_ores";
		final String CATEGORY_DIMSTRUC = "03_dimension_structures";
		final String CATEGORY_DUNGEON = "04_dimension_dungeons";
		final String CATEGORY_METEOR = "05_dimension_meteors";
		final String CATEGORY_MOB = "06_mobs";
		final String CATEGORY_NUKES = "07_nukes";
		final String CATEGORY_BORES = "08_bedrockOres";

		String dimRadComment = "Amount of background radiation in the dimension in Rad/s - <dimID:Rad> (Int:Float)";
				dimensionRad = CommonConfig.createConfigIntFloatMap(config, CATEGORY_DIMRAD, "01.01_dimensionRadiation", dimRadComment, new String[]{
        	"-1:0.666", //Nether
			"1:0.01", //End

			"-13:2.5", //Mercury
			"-61:5", //Mercury - Orbit
			"-60:5", //Mercury - Orbit (Static)

			"-27:0.05", //Earth - Orbit
			"-26:0.05", //Earth - Orbit (Static)

			"-28:0.3", //Moon

			"-29:0.1", //Mars
			"-67:0.15", //Mars - Orbit
			"-66:0.15", //Mars - Orbit (Static)

			"-1502:0.3", //Phobos
			"-1503:0.4", //Deimos

			"-30:1", //Asteroides

			"-31:0.05", //Venus
			"-63:4", //Venus - Orbit
			"-62:4", //Venus - Orbit (Static)
			
			"-20:5", //Ceres
			"-65:5", //Ceres - Orbit
			"-64:5", //Ceres - Orbit (Static)

			"-15:5", //Jupiter
			"-69:20", //Jupiter - Orbit
			"-68:20", //Jupiter - Orbit (Static)

			"-1500:6", //IO
			"-1501:5", //Europa
			"-1506:4.5", //Ganymede
			"-1505:4", //Callisto

			"-16:6", //Saturn
			"-71:16", //Saturn - Orbit
			"-70:16", //Saturn - Orbit (Static)

			"-1507:4", //Rhea
			"-1508:2", //Titan
			"-1511:1", //Iapetus

			"-17:5", //Uranus
			"-73:12", //Uranus - Orbit
			"-72:12", //Uranus - Orbit (Static)

			"-1510:4", //Titania
			"-1509:2", //Oberon

			"-18:4", //Neptune
			"-75:8", //Neptune - Orbit
			"-74:8", //Neptune - Orbit (Static)

			"-1504:1.5", //Triton

			"-19:0.3", //Pluto
			"-77:0.3", //Pluto - Orbit
			"-76:0.3", //Pluto - Orbit (Static)

			"-21:0.1", //Eris
			"-79:0.1", //Eris - Orbit
			"-78:0.1", //Eris - Orbit (Static)
		}, ":");

		//Ores
		uraniumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.01_uraniumSpawnrate", "Amount of uranium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:7", "-6:7" }, ":");
		titaniumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.02_titaniumSpawnrate", "Amount of titanium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:8", "-6:8" }, ":");
		sulfurSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.03_sulfurSpawnrate", "Amount of sulfur ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:5", "-6:5" }, ":");
		aluminiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.04_aluminiumSpawnrate", "Amount of aluminium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:7", "-6:7" }, ":");
		copperSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.05_copperSpawnrate", "Amount of copper ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:12", "-6:12" }, ":");
		fluoriteSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.06_fluoriteSpawnrate", "Amount of fluorite ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		niterSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.07_niterSpawnrate", "Amount of niter ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		tungstenSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.08_tungstenSpawnrate", "Amount of tungsten ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:10", "-6:10" }, ":");
		leadSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.09_leadSpawnrate", "Amount of lead ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		berylliumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.10_berylliumSpawnrate", "Amount of beryllium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		thoriumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.11_thoriumSpawnrate", "Amount of thorium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:7", "-6:7" }, ":");
		ligniteSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.12_ligniteSpawnrate", "Amount of lignite ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:2", "-6:2" }, ":");
		asbestosSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.13_asbestosSpawnRate", "Amount of asbestos ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:2", "-6:2" }, ":");
		lithiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.14_lithiumSpawnRate", "Amount of schist lithium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		rareSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.15_rareEarthSpawnRate", "Amount of rare earth ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:6", "-6:6" }, ":");
		oilcoalSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.16_oilCoalSpawnRate", "Spawns an oily coal vein every nTH chunk - <dimID:amount> (Int:Int)",new String[]{ "0:128", "-6:128" }, ":");
		gassshaleSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.17_gasShaleSpawnRate", "Amount of oil shale veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:5", "-6:5" }, ":");
		explosivebubbleSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.18_explosiveBubbleSpawnRate", "Spawns an explosive gas bubble every nTH chunk - <dimID:amount> (Int:Int)",new String[]{ "0:80", "-6:80" }, ":");
		gasbubbleSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.19_gasBubbleSpawnRate", "Spawns a gas bubble every nTH chunk - <dimID:amount> (Int:Int)",new String[]{ "0:40", "-6:40" }, ":");
		cinnebarSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.20_cinnebarSpawnRate", "Amount of cinnebar ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:1", "-6:1" }, ":");
		cobaltSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.21_cobaltSpawnRate", "Amount of cobalt ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:2", "-6:2" }, ":");
		
		ironClusterSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.22_ironClusterSpawn", "Amount of iron cluster veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:4", "-6:4" }, ":");
		titaniumClusterSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.23_titaniumClusterSpawn", "Amount of titanium cluster veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:2", "-6:2" }, ":");
		aluminiumClusterSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.24_aluminiumClusterSpawn", "Amount of aluminium cluster veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:3", "-6:3" }, ":");
		copperClusterSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.24_copperClusterSpawn", "Amount of copper cluster veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "0:3", "-6:3" }, ":");
		
		reiiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.25_reiiumSpawnRate", "Amount of reiium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-29:1" }, ":");
		weidaniumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.26_weidaniumSpawnRate", "Amount of weidanium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-31:1" }, ":");
		australiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.27_australiumSpawnRate", "Amount of australium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-31:1" }, ":");
		verticiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.28_verticiumSpawnRate", "Amount of verticium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-30:1" }, ":");
		unobtainiumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.29_unobtainiumSpawnRate", "Amount of unobtainium ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-28:1" }, ":");
		daffergonSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.30_daffergonSpawnRate", "Amount of daffergon ore veins per chunk - <dimID:amount> (Int:Int)",new String[]{ "-30:1" }, ":");
		
		bedrockOilSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "01.31_bedrockOilSpawnRate", "Spawn bedrock oil every nTH chunk - <dimID:amount> (Int:Int)",new String[]{ "0:200", "-6:200" }, ":");
		
		netherUraniumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N00_uraniumSpawnrate", "Amount of nether uranium per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:8" }, ":");
		netherTungstenSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N01_tungstenSpawnrate", "Amount of nether tungsten per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:10" }, ":");
		netherSulfurSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N02_sulfurSpawnrate", "Amount of nether sulfur per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:26" }, ":");
		netherPhosphorusSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N03_phosphorusSpawnrate", "Amount of nether phosphorus per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:24" }, ":");
		netherCoalSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N04_coalSpawnrate", "Amount of nether coal per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:24" }, ":");
		netherPlutoniumSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N05_plutoniumSpawnrate", "Amount of nether plutonium per chunk, if enabled - <dimID:amount> (Int:Int)", new String[]{ "-1:8" }, ":");
		netherCobaltSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "02.N06_cobaltSpawnrate", "Amount of nether cobalt per chunk - <dimID:amount> (Int:Int)",new String[]{ "-1:2" }, ":");
		
		endTixiteSpawn = CommonConfig.createConfigIntMap(config, CATEGORY_DIMORE, "03.E01_tixiteSpawnrate", "Amount of end trixite per chunk - <dimID:amount> (Int:Int)",new String[]{ "1:8" }, ":");
		

		//Structures
		radioStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.01_radioSpawn", "Spawn radio station on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:1000" }, ":");
		antennaStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.02_antennaSpawn", "Spawn antenna on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:750" }, ":");
		atomStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.03_atomSpawn", "Spawn power plant on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		vertibirdStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.04_vertibirdSpawn", "Spawn vertibird on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		dungeonStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.05_dungeonSpawn", "Spawn library dungeon on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:64" }, ":");
		relayStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.06_relaySpawn", "Spawn relay on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		satelliteStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.07_satelliteSpawn", "Spawn satellite dish on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		bunkerStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.08_bunkerSpawn", "Spawn bunker on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:1000" }, ":");
		siloStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.09_siloSpawn", "Spawn missile silo on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:1000" }, ":");
		factoryStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.10_factorySpawn", "Spawn factory on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:1000" }, ":");
		dudStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.11_dudSpawn", "Spawn dud on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		spaceshipStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.12_spaceshipSpawn", "Spawn spaceship on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:1000" }, ":");
		barrelStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.13_barrelSpawn", "Spawn waste tank on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:5000" }, ":");
		broadcaster = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.14_broadcasterSpawn", "Spawn corrupt broadcaster on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:5000" }, ":");
		minefreq = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.15_landmineSpawn", "Spawn AP landmine on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:64" }, ":");
		radfreq = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.17_radHotsoptSpawn", "Spawn big radiation hotspot on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:5000" }, ":");
		vaultfreq = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.18_vaultSpawn", "Spawn locked safe on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:2500" }, ":");
		
		geyserWater = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.19_geyserWaterSpawn", "Spawn water geyser on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:3000" }, ":");
		geyserChlorine = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.20_geyserChlorineSpawn", "Spawn poison geyser on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:3000" }, ":");
		geyserVapor = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.21_geyserVaporSpawn", "Spawn vapor geyser on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		geyserNether = CommonConfig.createConfigIntMap(config, CATEGORY_DIMSTRUC, "03.22_geyserNetherSpawn", "Spawn nether geyser on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "-1:2" }, ":");
		
		meteorStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DUNGEON, "03.23_meteorStructureSpawn", "Spawn meteor dungeon on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:15000" }, ":");
		capsuleStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DUNGEON, "03.24_capsuleSpawn", "Spawn landing capsule on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:100" }, ":");
		arcticStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DUNGEON, "03.25_arcticVaultSpawn", "Spawn artic code vault on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:500" }, ":");
		jungleStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DUNGEON, "03.26_jungleDungeonSpawn", "Spawn jungle dungeon on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:2000" }, ":");
		pyramidStructure = CommonConfig.createConfigIntMap(config, CATEGORY_DUNGEON, "03.27_pyramidSpawn", "Spawn pyramid on every nTH chunk - <dimID:n> (Int:Int)",new String[]{ "0:4000" }, ":");
		
		meteorStrikeChance = CommonConfig.createConfigIntMap(config, CATEGORY_METEOR, "05.01_meteorStrikeChance", "The probability of a meteor spawning per tick (an average of once every nTH ticks) - <dimID:n> (Int:Int)",new String[]{ 
			"0:"+20 * 10 * 60 * 5, //Earth
			"-28:"+20 * 10 * 5, //Moon
			"-29:"+20 * 10 * 13, //Mars
			"-31:"+20 * 10 * 60 * 15,  //Venus
			"-13:"+20 * 10 * 30, //Mercury
			"-20:"+20 * 10 * 60 * 10, //Ceres
			"-1500:"+20 * 10 * 3, //IO
			"-1501:"+20 * 10 * 25, //Europa
			"-1506:"+20 * 10 * 60, //Ganymede
			"-1505:"+20 * 10 * 60 * 2, //Callisto
			"-1507:"+20 * 10 * 6, //Rhea
			"-1508:"+20 * 10 * 60 * 8, //Titan
			"-1511:"+20 * 10 * 60 * 4, //Iapetus
			"-1510:"+20 * 10 * 60 * 3, //Titania
			"-1509:"+20 * 10 * 60 * 9, //Oberon
			"-1504:"+20 * 10 * 60 * 20 //Triton
		}, ":");
		meteorShowerChance = CommonConfig.createConfigIntMap(config, CATEGORY_METEOR, "05.02_meteorShowerChance", "The probability of a meteor spawning during meteor shower per tick (an average of once every nTH ticks) - <dimID:n> (Int:Int)",new String[]{ 
			"0:"+20 * 60 * 60 * 5, //Earth
			"-28:"+20 * 60 * 5, //Moon
			"-29:"+20 * 60 * 13, //Mars
			"-31:"+20 * 60 * 60 * 15,  //Venus
			"-13:"+20 * 60 * 30, //Mercury
			"-20:"+20 * 60 * 60 * 10, //Ceres
			"-1500:"+20 * 60 * 3, //IO
			"-1501:"+20 * 60 * 25, //Europa
			"-1506:"+20 * 60 * 60, //Ganymede
			"-1505:"+20 * 60 * 60 * 2, //Callisto
			"-1507:"+20 * 60 * 6, //Rhea
			"-1508:"+20 * 60 * 60 * 8, //Titan
			"-1511:"+20 * 60 * 60 * 4, //Iapetus
			"-1510:"+20 * 60 * 60 * 3, //Titania
			"-1509:"+20 * 60 * 60 * 9, //Oberon
			"-1504:"+20 * 60 * 60 * 20 //Triton
		}, ":");
		meteorShowerDuration = CommonConfig.createConfigIntMap(config, CATEGORY_METEOR, "05.03_meteorShowerDuration", "Max duration of meteor shower in ticks - <dimID:n> (Int:Int)",new String[]{ 
			"0:"+20 * 60 * 5, //Earth
			"-28:"+20 * 5, //Moon
			"-29:"+20 * 13, //Mars
			"-31:"+20 * 60 * 15,  //Venus
			"-13:"+20 * 30, //Mercury
			"-20:"+20 * 60 * 10, //Ceres
			"-1500:"+20 * 3, //IO
			"-1501:"+20 * 25, //Europa
			"-1506:"+20 * 60, //Ganymede
			"-1505:"+20 * 60 * 2, //Callisto
			"-1507:"+20 * 6, //Rhea
			"-1508:"+20 * 60 * 8, //Titan
			"-1511:"+20 * 60 * 4, //Iapetus
			"-1510:"+20 * 60 * 3, //Titania
			"-1509:"+20 * 60 * 9, //Oberon
			"-1504:"+20 * 60 * 20 //Triton
		}, ":");
		

		String mobModRadComment = "Amount of radiation resistance all the mobs of that mod get. Radresistance s is calculated as s=(1-0.1^r). So a resistance value of 3.0 means that 99.9%=(1-0.1^3.0) of the radiation gets blocked. - <mod=radresistance> (String:Float)";
		mobModRadresistance = CommonConfig.createConfigStringFloatMap(config, CATEGORY_MOB, "06.01_mob_Mod_Radresistance", mobModRadComment, new String[]{ 
			"srparasites=0.2",
			"thaumcraft=0.75",
		}, "=");
		

		String mobRadComment = "Amount of radiation resistance the mob gets. Radresistance s is calculated as s=(1-0.1^r). So a resistance value of 3.0 means that 99.9%=(1-0.1^3.0) of the radiation gets blocked. - <mod:mobitentifier=radresistance> (String:Float)";
		mobRadresistance = CommonConfig.createConfigStringFloatMap(config, CATEGORY_MOB, "06.02_mob_Radresistance", mobRadComment, new String[]{ 
			"minecraft:parrot=0.5", 
			"minecraft:rabbit=1.0", 
			"techguns:ghastling=1.2",
			"minecraft:enderman=1.5", 
			"minecraft:blaze=2.0", 
			"techguns:alienbug=2.2",
			"minecraft:bat=2.5", 
			"minecraft:ghast=3.0", 
			"mutantbeasts:mutant_creeper=3.2",
			"minecraft:squid=3.5", 
			"minecraft:spider=4.0", 
			"mutantbeasts:mutant_enderman=4.2",
			"techguns:outcast=4.5",
			"minecraft:cave_spider=5.0", 
			"minecraft:silverfish=6.0", 
			"techguns:stormtrooper=6.4",
			"techguns:cyberdemon=6.5",
			"minecraft:endermite=7.0", 
			"pvj:pvj_beach_starfish=7.5", 
			"pvj:pvj_clam=7.8", 
			"minecraft:shulker=8.0",
			"pvj:pvj_starfish=8.2", 
			"techguns:attackhelicopter=8.5",
			"minecraft:ender_dragon=9.0", 
			"pvj:pvj_snail=9.1", 
			"pvj:pvj_firefly=10.0", 
			"pvj:pvj_fly=11.0",
			"mysticalworld:entity_hell_sprout=2.0",
			"mysticalworld:entity_lava_cat=2.0",
			"thaumcraft:taintseed=1.2",
			"thaumcraft:taintseedprime=4.0"
		}, "=");
		mobModRadimmune = CommonConfig.createConfigStringSet(config, CATEGORY_MOB, "06.03_mob_Mod_Radimmune", "List of mods whose entities should all be immune to radiation. - <mod> (String)", new String[]{
			"biomesoplenty",
			"galacticraftcore", 
			"galacticraftplanets", 
			"extraplanets",
			"thaumicaugmentation",
                        "enderskills",
                        "thaumadditions",
			"cyberware",
			"rewired"
		});
		mobRadimmune = CommonConfig.createConfigStringSet(config, CATEGORY_MOB, "06.04_mob_Radimmune", "List of mobs that are immune to radiation. - <mod:mobitentifier> (String)", new String[]{ 
			"minecraft:magma_cube", 
			"minecraft:slime", 
			"minecraft:vex", 
			"minecraft:villager_golem", 
			"minecraft:snowman", 
			"minecraft:witch",
			"pvj:pvj_icecube",
			"tconstruct:blueslime",
			"mutantbeasts:mutant_snow_golem",
			"mutantbeasts:mutant_zombie",
			"mutantbeasts:mutant_skeleton",
			"techguns:zombieminer",
			"techguns:zombiefarmer",
			"techguns:zombiesoldier",
			"techguns:zombiepigmansoldier",
			"techguns:zombiepoliceman",
			"techguns:skeletonsoldier",
			"techguns:supermutantbasic",
			"techguns:supermutantelite",
			"techguns:supermutantheavy",
			"deepmoblearning:glitch",
			"divinefavor:entity.ping",
			"divinefavor:entity.rope_barrier",
			"divinefavor:entity.rope_explosive",
			"divinefavor:entity.rope_glowing",
			"divinefavor:entity.rope_guide",
			"divinefavor:entity.rope_inert",
			"divinefavor:entity.rope_luminous",
			"divinefavor:entity.rope_teleporting",
			"divinefavor:entity.spell_arrow",
			"divinefavor:entity.spooky",
			"divinefavor:entity.stoneball",
			"embers:ancient_golem",
			"embers:ember_light",
			"embers:ember_packet",
			"embers:ember_projectile",
			"embers:magma_projectile",
			"thaumcraft:eldritchwarden",
			"thaumcraft:eldritchguardian",
			"thaumcraft:cultistportalgreater",
			"thaumcraft:cultistportallesser",
			"thaumcraft:eldritchgolem",
			"thaumcraft:arcanebore",
			"thaumcraft:fluxrift",
			"thaumcraft:golem",
			"thaumcraft:mindspider",
			"thaumcraft:spellbat",
			"thaumcraft:turretadvanced",
			"thaumcraft:turretbasic",
			"tombstone:ghostly_shape"
		});
	
		mobGear = CommonConfig.createConfigBool(config, CATEGORY_MOB, "06.05_mobGear", "If true then mobs will be given gear (armor/weapons/gasmasks) from this mod when spawned", true);
		modLoot = CommonConfig.createConfigBool(config, CATEGORY_MOB, "06.06_modLoot", "If true then this mod will generarte loot for chests", true);
	

		doFillCraterWithWater =  CommonConfig.createConfigBool(config, CATEGORY_NUKES, "07.04_doFillCraterWithWater", "If true then nukes will fill the crater with water if it is in a wet place. It creates a bit of lagg but looks better than without it.", true);
		fillCraterWithWater = CommonConfig.createConfigIntMap(config, CATEGORY_NUKES, "07.04_fillCraterWithWater", "Waterlevel per dimension which the nuke uses to fill the crater. {+n=>waterlevel height, 0=>dimension waterlevel, -n=> n blocks below dimension waterlevel } - <dimID:n> (Int:Int)", new String[]{ "0:0" }, ":");
		
		peaceDimensionsIsWhitelist =  CommonConfig.createConfigBool(config, CATEGORY_NUKES, "07.05_peaceDimensionsIsWhitelist", "If true then the listed dimensions below are all peacefull. If false then the listed dimensions are the only ones where destruction happens.", true);
				peaceDimensions = CommonConfig.createConfigIntSet(config, CATEGORY_BORES, "07.06_peaceDimensions", "List of Dimensions where block destruction and damage is disabled (Used for server lobbies/science servers/pvp arenas) - <dimID> (Int)", new String[]{
		});

		bedrockOreBlacklist = CommonConfig.createConfigStringSet(config, CATEGORY_BORES, "08.01_bedrockOreBlacklist", "List of OreDict entries that should not have bedrock ores - <ore> (String)", new String[]{
			"oreTh232",
			"oreThorium232",
			"oreVolcanic",
			"oreSteel"
		});
	}

	public static boolean isWarDim(World world){
		return isWarDim(world.provider.getDimension());
	}

	public static boolean isWarDim(int dimID){
		if(peaceDimensionsIsWhitelist)
			return !peaceDimensions.contains(dimID);
		else
			return peaceDimensions.contains(dimID);
	}
}
