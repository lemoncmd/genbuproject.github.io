package net.zhuoweizhang.mcpelauncher;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class BlockContextFactory extends ContextFactory {
    protected Context makeContext() {
        Context cx = super.makeContext();
        cx.setWrapFactory(ScriptManager.getWrapFactory());
        return cx;
    }
}
