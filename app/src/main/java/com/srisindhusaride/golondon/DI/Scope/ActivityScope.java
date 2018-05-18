package com.srisindhusaride.golondon.DI.Scope;

/*
 * @date 08/02/17.
 * @author Sindhu
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {

}

