package com.zengshi.ecp.frame.bean;

import java.util.HashSet;
import java.util.Set;

/**
 */
class BeanSet<E> extends HashSet<E> {

    public BeanSet(Set<E> c) {
        super(c);
    }

}
