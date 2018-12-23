package at.dalex.grape.entity;

import at.dalex.grape.script.LuaScript;
import org.joml.Matrix4f;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.gamestatemanager.PlayState;

public class LuaEntity extends LivingEntity {

	private LuaScript entityScript;
	private LuaValue updateMethod;
	private LuaValue drawMethod;

	public LuaEntity(double x, double y, int type, int health, String scriptFile) {
		super(x, y, type, health);

		//Parse lua-entityScript
		this.entityScript = new LuaScript(scriptFile);
		this.updateMethod 	= entityScript.getMethod("update");
		this.drawMethod 	= entityScript.getMethod("draw");

		//Initialize Entity-Script
		LuaValue entityInstance = CoerceJavaToLua.coerce(this);
		entityScript.getMethod("init").call(entityInstance);

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
}
