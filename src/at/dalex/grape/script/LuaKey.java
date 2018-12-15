package at.dalex.grape.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import at.dalex.grape.input.Input;

import static java.awt.event.KeyEvent.*;

class LuaKey {

	public static class IsKeyDown extends OneArgFunction {

		@Override
		public LuaValue call(LuaValue arg0) {
			String characterText = arg0.checkjstring();
			char character = characterText.toLowerCase().toCharArray()[0];
			
			int virtualKeyCode = -1;
			
			switch (character) {
	        case 'a': virtualKeyCode = VK_A; break;
	        case 'b': virtualKeyCode = VK_B; break;
	        case 'c': virtualKeyCode = VK_C; break;
	        case 'd': virtualKeyCode = VK_D; break;
	        case 'e': virtualKeyCode = VK_E; break;
	        case 'f': virtualKeyCode = VK_F; break;
	        case 'g': virtualKeyCode = VK_G; break;
	        case 'h': virtualKeyCode = VK_H; break;
	        case 'i': virtualKeyCode = VK_I; break;
	        case 'j': virtualKeyCode = VK_J; break;
	        case 'k': virtualKeyCode = VK_K; break;
	        case 'l': virtualKeyCode = VK_L; break;
	        case 'm': virtualKeyCode = VK_M; break;
	        case 'n': virtualKeyCode = VK_N; break;
	        case 'o': virtualKeyCode = VK_O; break;
	        case 'p': virtualKeyCode = VK_P; break;
	        case 'q': virtualKeyCode = VK_Q; break;
	        case 'r': virtualKeyCode = VK_R; break;
	        case 's': virtualKeyCode = VK_S; break;
	        case 't': virtualKeyCode = VK_T; break;
	        case 'u': virtualKeyCode = VK_U; break;
	        case 'v': virtualKeyCode = VK_V; break;
	        case 'w': virtualKeyCode = VK_W; break;
	        case 'x': virtualKeyCode = VK_X; break;
	        case 'y': virtualKeyCode = VK_Y; break;
	        case 'z': virtualKeyCode = VK_Z; break;
//	        case 'A': virtualKeyCode = VK_SHIFT, VK_A; break;
//	        case 'B': virtualKeyCode = VK_SHIFT, VK_B; break;
//	        case 'C': virtualKeyCode = VK_SHIFT, VK_C; break;
//	        case 'D': virtualKeyCode = VK_SHIFT, VK_D; break;
//	        case 'E': virtualKeyCode = VK_SHIFT, VK_E; break;
//	        case 'F': virtualKeyCode = VK_SHIFT, VK_F; break;
//	        case 'G': virtualKeyCode = VK_SHIFT, VK_G; break;
//	        case 'H': virtualKeyCode = VK_SHIFT, VK_H; break;
//	        case 'I': virtualKeyCode = VK_SHIFT, VK_I; break;
//	        case 'J': virtualKeyCode = VK_SHIFT, VK_J; break;
//	        case 'K': virtualKeyCode = VK_SHIFT, VK_K; break;
//	        case 'L': virtualKeyCode = VK_SHIFT, VK_L; break;
//	        case 'M': virtualKeyCode = VK_SHIFT, VK_M; break;
//	        case 'N': virtualKeyCode = VK_SHIFT, VK_N; break;
//	        case 'O': virtualKeyCode = VK_SHIFT, VK_O; break;
//	        case 'P': virtualKeyCode = VK_SHIFT, VK_P; break;
//	        case 'Q': virtualKeyCode = VK_SHIFT, VK_Q; break;
//	        case 'R': virtualKeyCode = VK_SHIFT, VK_R; break;
//	        case 'S': virtualKeyCode = VK_SHIFT, VK_S; break;
//	        case 'T': virtualKeyCode = VK_SHIFT, VK_T; break;
//	        case 'U': virtualKeyCode = VK_SHIFT, VK_U; break;
//	        case 'V': virtualKeyCode = VK_SHIFT, VK_V; break;
//	        case 'W': virtualKeyCode = VK_SHIFT, VK_W; break;
//	        case 'X': virtualKeyCode = VK_SHIFT, VK_X; break;
//	        case 'Y': virtualKeyCode = VK_SHIFT, VK_Y; break;
//	        case 'Z': virtualKeyCode = VK_SHIFT, VK_Z; break;
	        case '`': virtualKeyCode = VK_BACK_QUOTE; break;
	        case '0': virtualKeyCode = VK_0; break;
	        case '1': virtualKeyCode = VK_1; break;
	        case '2': virtualKeyCode = VK_2; break;
	        case '3': virtualKeyCode = VK_3; break;
	        case '4': virtualKeyCode = VK_4; break;
	        case '5': virtualKeyCode = VK_5; break;
	        case '6': virtualKeyCode = VK_6; break;
	        case '7': virtualKeyCode = VK_7; break;
	        case '8': virtualKeyCode = VK_8; break;
	        case '9': virtualKeyCode = VK_9; break;
	        case '-': virtualKeyCode = VK_MINUS; break;
	        case '=': virtualKeyCode = VK_EQUALS; break;
//	        case '~': virtualKeyCode = VK_SHIFT, VK_BACK_QUOTE; break;
	        case '!': virtualKeyCode = VK_EXCLAMATION_MARK; break;
	        case '@': virtualKeyCode = VK_AT; break;
	        case '#': virtualKeyCode = VK_NUMBER_SIGN; break;
	        case '$': virtualKeyCode = VK_DOLLAR; break;
//	        case '%': virtualKeyCode = VK_SHIFT, VK_5; break;
	        case '^': virtualKeyCode = VK_CIRCUMFLEX; break;
	        case '&': virtualKeyCode = VK_AMPERSAND; break;
	        case '*': virtualKeyCode = VK_ASTERISK; break;
	        case '(': virtualKeyCode = VK_LEFT_PARENTHESIS; break;
	        case ')': virtualKeyCode = VK_RIGHT_PARENTHESIS; break;
	        case '_': virtualKeyCode = VK_UNDERSCORE; break;
	        case '+': virtualKeyCode = VK_PLUS; break;
	        case '\t': virtualKeyCode = VK_TAB; break;
	        case '\n': virtualKeyCode = VK_ENTER; break;
	        case '[': virtualKeyCode = VK_OPEN_BRACKET; break;
	        case ']': virtualKeyCode = VK_CLOSE_BRACKET; break;
	        case '\\': virtualKeyCode = VK_BACK_SLASH; break;
//	        case '{': virtualKeyCode = VK_SHIFT, VK_OPEN_BRACKET; break;
//	        case '}': virtualKeyCode = VK_SHIFT, VK_CLOSE_BRACKET; break;
//	        case '|': virtualKeyCode = VK_SHIFT, VK_BACK_SLASH; break;
	        case ';': virtualKeyCode = VK_SEMICOLON; break;
	        case ':': virtualKeyCode = VK_COLON; break;
	        case '\'': virtualKeyCode = VK_QUOTE; break;
	        case '"': virtualKeyCode = VK_QUOTEDBL; break;
	        case ',': virtualKeyCode = VK_COMMA; break;
//	        case '<': virtualKeyCode = VK_SHIFT, VK_COMMA; break;
	        case '.': virtualKeyCode = VK_PERIOD; break;
//	        case '>': virtualKeyCode = VK_SHIFT, VK_PERIOD; break;
	        case '/': virtualKeyCode = VK_SLASH; break;
//	        case '?': virtualKeyCode = VK_SHIFT, VK_SLASH; break;
	        case ' ': virtualKeyCode = VK_SPACE; break;
	        default:
	            throw new IllegalArgumentException("Cannot convert character " + character);
	        }
			
			return LuaValue.valueOf(Input.keys[virtualKeyCode]);
		}
	}
}
