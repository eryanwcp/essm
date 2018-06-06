/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.mail.support;


import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.exception.NotSupportedException;
import com.eryansky.common.mail.receiver.Receiver;

import javax.mail.Message;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;

/**
 * 实现新邮件提醒的核心类 (示例程序)
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public abstract class AbstractMonitor implements Runnable, MessageCountListener {
    public static int DEFAUL_INTERVAL = 3000;

    private boolean enabled;

    protected Receiver receiver;
    protected Account account;

    protected Thread thread;

    public AbstractMonitor(Account account) throws NotSupportedException {
        this.account = account;
    }

    private void init() throws NotSupportedException {
        this.receiver = Receiver.make(this.account);
        this.receiver.open();
        this.receiver.getFolder().addMessageCountListener(this);
        thread = new Thread(this);
    }

    /**
     * 重新从服务器上获取所有未读的邮件
     */
    public abstract void reNewMessages();

    /**
     * 新邮件
     *
     * @param messages 新邮件
     */
    public abstract void newMessages(Message[] messages);

    /**
     * 当前监视器是否启用
     *
     * @return <tt>true</tt>启用状态；<tt>false</tt>禁用状态
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * 开启或关闭监视器
     *
     * @param enabled <tt>true</tt>表示开启；<tt>false</tt>表示关闭
     * @throws NotSupportedException 启动监视器失败
     * @throws InterruptedException  关闭监视器失败
     */
    public void enable(boolean enabled) throws NotSupportedException,
            InterruptedException {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                // 第一次开启服务时，初始化所有服务
                if (thread == null) {
                    init();
                }
                reNewMessages();
                thread.start();
            } else {
                if (thread != null && thread.isAlive()) { // 等待监视进程自然结束
                    thread.join();
                }
            }
        }
    }

    /**
     * 销毁该监视器对象，调用后该监视器将不可用
     */
    public void dispose() {
        // 关闭监视器线程
        if (this.isEnabled()) {
            try {
                this.enable(false);
            } catch (NotSupportedException ignored) {

            } catch (InterruptedException ignored) {

            }
        }

        // 关闭接受服务器对象
        if (this.receiver != null) {
            this.receiver.close();
        }
    }

    public void join() throws InterruptedException {
        this.thread.join();
    }

    @Override
    public void run() {
        while (this.enabled) {
            try {
                Thread.sleep(AbstractMonitor.DEFAUL_INTERVAL);
                this.receiver.getFolder().getMessageCount();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void messagesAdded(MessageCountEvent event) {
        Message[] messages = event.getMessages();
        this.newMessages(messages);
    }

    @Override
    public void messagesRemoved(MessageCountEvent event) {

    }
}
