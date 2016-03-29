package com.eryansky.fastweixin.company.api.handle;

import com.eryansky.fastweixin.company.api.config.QYConfigChangeNotice;
import com.eryansky.fastweixin.handle.ApiConfigChangeHandle;
import com.eryansky.fastweixin.util.BeanUtil;

import java.util.Observable;

/**
 *
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public abstract class AbstractQYApiConfigChangeHandle implements ApiConfigChangeHandle {

    public void update(Observable o, Object arg){
        if(BeanUtil.nonNull(arg) && arg instanceof QYConfigChangeNotice){
            configChange((QYConfigChangeNotice) arg);
        }
    }

    /**
     * 子类实现，当配置变化时触发该方法
     * @param notice 消息
     */
    public abstract void configChange(QYConfigChangeNotice notice);
}
