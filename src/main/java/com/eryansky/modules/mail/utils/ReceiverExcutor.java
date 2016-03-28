/**
 * Copyright (c) 2013-2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.utils;

import com.eryansky.common.exception.ServiceException;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 接收邮件执行器
 */
public class ReceiverExcutor {


    private ExecutorService executorService = Executors.newScheduledThreadPool(5);
    private CompletionService<Boolean> completionExtractService = new ExecutorCompletionService<Boolean>(executorService);


    public Future<Boolean> submit(ReceiverCallable receiverCallable) {
        if (!executorService.isShutdown()) {
            return completionExtractService.submit(receiverCallable);
        }
        return null;
    }


    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }

    }

    public void shutdowNow() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    public Boolean take() throws ServiceException {
        Boolean flag = null;
        try {
            Future<Boolean> future = completionExtractService.take();
            flag = future.get();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return flag;
    }

}
