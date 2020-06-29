package com.jantvrdik.intellij.latte.codeStyle;

import com.intellij.psi.codeStyle.*;

public class LatteCodeStyleSettings extends CustomCodeStyleSettings {
	public static boolean SPACE_AROUND_CONCATENATION = true;

	public LatteCodeStyleSettings(CodeStyleSettings settings) {
		super("LatteCodeStyleSettings", settings);
	}
}
