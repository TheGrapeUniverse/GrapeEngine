package at.dalex.grape.script;

import at.dalex.grape.info.Logger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

class LuaLogger {

    public static class LuaInfo extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg0) {
            String message = arg0.checkjstring();
            Logger.info("[LUA] " + message);
            return NIL;
        }
    }

    public static class LuaError extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg0) {
            String message = arg0.checkjstring();
            Logger.error("[LUA] " + message);
            return NIL;
        }
    }
}
