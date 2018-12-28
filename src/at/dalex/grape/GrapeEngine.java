package at.dalex.grape;

import at.dalex.grape.map.manager.MapManager;
import org.lwjgl.opengl.GL11;

import at.dalex.grape.developer.GameInfo;
import at.dalex.grape.developer.ResourceMonitor;
import at.dalex.grape.gamestatemanager.GameStateManager;
import at.dalex.grape.graphics.Camera;
import at.dalex.grape.graphics.DisplayCallback;
import at.dalex.grape.graphics.DisplayManager;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.resource.DefaultResources;
import at.dalex.grape.script.LuaManager;
import at.dalex.grape.tiled.TilesetManager;
import at.dalex.grape.toolbox.OSManager;

public abstract class GrapeEngine implements DisplayCallback {

	private String gameLocation;
	private DisplayManager displayManager;
	private GameStateManager gameStateManager;
	private TilesetManager tilesetManager;
	private MapManager mapManager;
	private LuaManager luaManager;
	private ResourceMonitor resourceMonitor;
	
	private GameInfo gameInfo;
	private static GrapeEngine instance;
	
	public abstract void onEnable();
	public abstract void onDisable();
	
	private Camera camera;
	
	public GrapeEngine(String gameLocation) {
		instance = this;

		OSManager.setLook();
		gameInfo = new GameInfo(gameLocation);
		this.gameLocation = gameLocation;
	}
	
	public void startEngine() {
		//Parse Window-Data and create DisplayManager
		String windowTitle = gameInfo.getValue("title");
		displayManager = new DisplayManager(windowTitle, this);
		displayManager.createDisplay();

		//Create Managers
		new Graphics();
		new DefaultResources();
		//resourceMonitor = new ResourceMonitor();
		gameStateManager = new GameStateManager();
		tilesetManager = new TilesetManager();
		mapManager = new MapManager();
		luaManager = new LuaManager();

		luaManager.setupAPI(luaManager._G);
		camera = new Camera(DisplayManager.windowWidth, DisplayManager.windowHeight);
		mapManager.upateMapInformations();

		onEnable();
		
		displayManager.loop();
		System.out.println("[INFO] Shutting down ...");
		onDisable();
		displayManager.destroy();
		System.out.println("Goodbye.");
	}
	
	@Override
	public void updateEngine(double delta) {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		//resourceMonitor.update();
		//MemoryManager.drawCallsAmount = 0;
		//MemoryManager.verticesAmount = 0;
		tilesetManager.updateTileset();
		gameStateManager.update(displayManager.getTimer().getDelta());
		gameStateManager.draw(camera.getProjectionAndViewMatrix());
	}
	
	public DisplayManager getDisplayManager() {
		return this.displayManager;
	}
	
	public GameStateManager getGameStateManager() {
		return this.gameStateManager;
	}
	
	public TilesetManager getTilesetManager() {
		return this.tilesetManager;
	}

	public MapManager getMapManager() {
		return this.mapManager;
	}

	public LuaManager getLuaManager() {
		return this.luaManager;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public GameInfo getGameInfo() {
		return this.gameInfo;
	}

	public String getGameLocation() {
		return this.gameLocation;
	}

	public static GrapeEngine getEngine() {
		return instance;
	}
}
