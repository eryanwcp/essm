package test.ngtest;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
public class AddTest {

    @Test
    public void testAdd() throws Exception {
        Add add = new Add();
        System.out.println(add.add(1,1));
    }
}