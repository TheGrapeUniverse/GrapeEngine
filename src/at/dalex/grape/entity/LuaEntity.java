package at.dalex.grape.entity;

import java.io.File;

import org.joml.Matrix4f;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.gamestatemanager.PlayState;
import at.dalex.grape.script.LuaInterface;
import at.dalex.grape.toolbox.Dialog;
import at.dalex.grape.toolbox.FileUtil;

public class LuaEntity extends LivingEntity implements LuaInterface {

	private LuaValue updateMethod;
	private LuaValue drawMethod;

	private Globals entityGlobals;

	public LuaEntity(int x, int y, int type, int health, String scriptFile) {
		super(x, y, type, health);
		entityGlobals = JsePlatform.standardGlobals();

		scriptFile = "" + scriptFile;

		if (FileUtil.isFile(new File(scriptFile))) {
			//Setup LUA for this entity
			entityGlobals.get("dofile").call(LuaValue.valueOf(scriptFile));
			GrapeEngine.getEngine().getLuaManager().setupAPI(entityGlobals);

			updateMethod = getMethodFromScript(entityGlobals, "update", scriptFile);
			drawMethod = getMethodFromScript(entityGlobals, "draw", scriptFile);
			//Initialize Entity script
			LuaValue entityInstance = CoerceJavaToLua.coerce(this);
			getMethodFromScript(entityGlobals, "init", scriptFile).call(entityInstance);
		}
		else {
			Dialog.error("Error", "Unable to find entity script '" + scriptFile + "'!\n"
					+ "The entity will not function correctly.");
		}

		//Add this entity to the scene
		PlayState.entities.add(this);
	}

	@Override
	public void update(double delta) {
		super.update(delta);
		if (updateMethod != null) {
			updateMethod.call();
		}
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		if (drawMethod != null) {
			drawMethod.call();
		}
	}

	/*****************************************************************/
	/*							LUA-Stuff							 */
	/*****************************************************************/


	static class LuaEntityClass {
		static class CreateEntity extends VarArgFunction {
			@Override
			public Varargs onInvoke(Varargs args) {
				int x = args.checkint(1);
				int y = args.checkint(2);
				int livingType = args.checkint(3);
				int health = args.checkint(4);
				String scriptFile = args.checkjstring(5);

				LuaEntity entity = new LuaEntity(x, y, livingType, health, scriptFile);

				LuaValue luaObject = CoerceJavaToLua.coerce(entity);
				return LuaValue.varargsOf(luaObject, luaObject);
			}
		}
	}

	/*
	 * Setup Main LUA-File Globals to be able to create an entity
	 */
	public static void setupLuaFunctions() {
		Globals _G = GrapeEngine.getEngine().getLuaManager()._G;
		_G.set("LivingType", CoerceJavaToLua.coerce(new LivingType()));
		LuaValue entity = CoerceJavaToLua.coerce(new LuaEntityClass());
		_G.set("Entity", entity);
		LuaTable table = new LuaTable();
		table.set("__index", table);
		table.set("create", new LuaEntityClass.CreateEntity());
		entity.setmetatable(table);
	}
	
	private LuaValue getMethodFromScript(Globals _G, String name, String scriptFile) {
		LuaValue method = null;
		try {
			method = _G.get(name);
		} catch (LuaError e) {
			e.printStackTrace();
			Dialog.error("Error", "Method entry '" + name + "' could not be found\n"
								+ "in script '" + scriptFile + "'");
		}
		return method;
	}
}
