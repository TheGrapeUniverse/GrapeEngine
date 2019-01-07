package at.dalex.grape.script;

import at.dalex.grape.graphics.Camera;
import org.joml.Vector3f;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import at.dalex.grape.GrapeEngine;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

class LuaCamera {

	public static class LuaSetPosition extends ThreeArgFunction {
		@Override
		public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
			double xPos = arg0.checkdouble();
			double yPos = arg1.checkdouble();
			double zPos = arg2.checkdouble();
			Vector3f newPosition = new Vector3f((float) xPos, (float) yPos, (float) zPos);
			GrapeEngine.getEngine().getCamera().setPosition(newPosition);
			return NIL;
		}
	}
	
	public static class LuaGetPosition extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			Vector3f cameraPos = GrapeEngine.getEngine().getCamera().getPosition();
			LuaTable positionVector = new LuaTable();
			positionVector.add(cameraPos.x);
			positionVector.add(cameraPos.y);
			positionVector.add(cameraPos.z);
			return positionVector;
		}
	}
 	
	public static class LuaTranslate extends ThreeArgFunction {
		@Override
		public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
			double x = arg0.checkdouble();
			double y = arg1.checkdouble();
			double z = arg2.checkdouble();
			Vector3f translation = new Vector3f((float) x, (float) y, (float) z);
			GrapeEngine.getEngine().getCamera().translate(translation);
			return NIL;
		}
	}

	public static class LuaGetProjectionMatrix extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return CoerceJavaToLua.coerce(GrapeEngine.getEngine().getCamera().getProjectionMatrix());
		}
	}

	public static class LuaGetProjectionAndViewMatrix extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return CoerceJavaToLua.coerce(GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());
		}
	}
}
