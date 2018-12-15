
package at.dalex.grape.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import at.dalex.grape.GrapeEngine;

public interface LuaInterface {

	default LuaValue getLuaMethod(String methodName) {
		return getGlobals().get(methodName);
	}
	
	default Globals getGlobals() {
		LuaManager luaManager = GrapeEngine.getEngine().getLuaManager();
		return luaManager._G;
	}
}
