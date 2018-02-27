package com.stur.lib.app;

/**
 * AppContext 接口
 * <p/>
 * Created by tony on 9/28/14.
 */
public interface IContext {

    /**
     * 获取上下文环境
     *
     * @return
     */
    public <T extends ContextBase> T getAppContext();
}
