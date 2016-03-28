package com.eryansky.modules.mail.entity;

/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
public interface IContact {

    /**
     * ID
     * @return
     */
    String getId();


    /**
     * 名称
     * @return
     */
    String getNameView();

    /**
     * 接收类型 {@link com.eryansky.modules.mail._enum.ReceiveObjectType}
     * @return
     */
    Integer getReceiveObjectType();

    /**
     * 接收ID 用户/联系人/联系人组
     * @return
     */
    String getReceiveObjectId();

    /**
     * 接收类型 {@link com.eryansky.modules.mail._enum.ReceiveType}
     * @return
     */
    Integer getReceiveType();
}
