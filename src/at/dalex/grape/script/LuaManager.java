package at.dalex.grape.script;

import java.io.File;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import at.dalex.grape.developer.GameInfo;
import at.dalex.grape.entity.LuaEntity;
import at.dalex.grape.toolbox.Dialog;
import at.dalex.grape.toolbox.FileUtil;

public class LuaManager {

	private String main_file = GameInfo.engine_location + "/scripts/main.lua";
	public Globals _G;
	private LuaValue initFunction;
	private LuaValue updateFunction;
	private LuaValue drawFunction;

	public LuaManager() {
		_G = JsePlatform.standardGlobals();
	}

	public void executeMain() {

		if (FileUtil.isFile(new File(main_file))) {
			_G.get("dofile").call(LuaValue.valueOf(main_file));
			initFunction = _G.get("init");
			drawFunction = _G.get("draw");
			updateFunction = _G.get("update");
		}
		else Dialog.error("Error", "Unable to find Lua script entry file 'main.lua'\nin directory 'scripts/'!");
	}

	public void callInit() {
		initFunction.call();
	}

	public void callDraw() {
		drawFunction.call();
	}

	public void callUpdate() {
		updateFunction.call();
	}

	public void setupAPI(Globals _G) {
		/* Graphics */
		LuaValue graphics = CoerceJavaToLua.coerce(new LuaGraphics());
		_G.set("Graphics", graphics);
		LuaTable table = new LuaTable();
		table.set("fillRectangle", new LuaGraphics.LuaFillRectangle());
		table.set("loadImage", new LuaGraphics.LuaLoadImage());
		table.set("drawImage", new LuaGraphics.LuaDrawImage());
		table.set("__index", table);
		graphics.setmetatable(table);

		/* Display */
		LuaValue display = CoerceJavaToLua.coerce(new LuaDisplay());
		_G.set("Display", display);
		LuaTable displayTable = new LuaTable();
		displayTable.set("getWidth", new LuaDisplay.LuaGetWidth());
		displayTable.set("getHeight", new LuaDisplay.LuaGetHeight());
		displayTable.set("setVSync", new LuaDisplay.LuaSetVSync());
		displayTable.set("__index", displayTable);
		display.setmetatable(displayTable);

		/* Camera */
		LuaValue camera = CoerceJavaToLua.coerce(new LuaCamera());
		_G.set("Camera", camera);
		LuaTable cameraTable = new LuaTable();
		cameraTable.set("getPosition", new LuaCamera.LuaGetPosition());
		cameraTable.set("setPosition", new LuaCamera.LuaSetPosition());
		cameraTable.set("translate", new LuaCamera.LuaTranslate());
		cameraTable.set("__index", cameraTable);
		camera.setmetatable(cameraTable);

		/* Map */
		LuaValue map = CoerceJavaToLua.coerce(new LuaMap());
		_G.set("Map", map);
		LuaTable mapTable = new LuaTable();
		mapTable.set("changeMap", new LuaMap.ChangeMap());
		mapTable.set("setScale", new LuaMap.SetScale());
		mapTable.set("getScale", new LuaMap.GetScale());
		mapTable.set("setPosition", new LuaMap.SetPosition());
		mapTable.set("getX", new LuaMap.GetX());
		mapTable.set("getY", new LuaMap.GetY());
		mapTable.set("__index", mapTable);
		map.setmetatable(mapTable);

		/* Key */
		LuaValue key = CoerceJavaToLua.coerce(new LuaKey());
		_G.set("Keyboard", key);
		LuaTable keyTable = new LuaTable();
		keyTable.set("isKeyDown", new LuaKey.IsKeyDown());
		keyTable.set("__index", keyTable);
		key.setmetatable(keyTable);

		/* LuaEntity */
		LuaEntity.setupLuaFunctions();

		/* Font */
		LuaValue font = CoerceJavaToLua.coerce(new LuaFont());
		_G.set("Font", font);
		LuaTable fontTable = new LuaTable();
		fontTable.set("createFont", new LuaFont.LuaCreateFont());
		fontTable.set("__index", fontTable);
		font.setmetatable(fontTable);

		/* Timer */
		LuaValue timer = CoerceJavaToLua.coerce(new LuaTimer());
		_G.set("Timer", timer);
		LuaTable timerTable = new LuaTable();
		timerTable.set("getTime", new LuaTimer.LuaGetTime());
		timerTable.set("getDelta", new LuaTimer.LuaGetDelta());
		timerTable.set("getFPS", new LuaTimer.LuaGetFPS());
		timerTable.set("getUPS", new LuaTimer.LuaGetUPS());
		timerTable.set("__index", timerTable);
		timer.setmetatable(timerTable);

		/* Logger */
		LuaValue logger = CoerceJavaToLua.coerce(new LuaLogger());
		_G.set("Logger", logger);
		LuaTable loggerTable = new LuaTable();
		loggerTable.set("info", new LuaLogger.LuaInfo());
		loggerTable.set("error", new LuaLogger.LuaError());
		loggerTable.set("__index", loggerTable);
		logger.setmetatable(loggerTable);
	}

	public static boolean methodExists(Globals globals, String name) {
		boolean exists = true;
		try {
			globals.get(name);
		} catch (LuaError e) {
			exists = false;
		}
		return exists;
	}
}
