package com.jlu.harmony.cal;

import ohos.aafwk.ability.AbilityPackage;

import java.util.logging.Logger;

/**
 * 全局类
 * 整个应用的生命周期
 */

public class MyApplication extends AbilityPackage {

    /**
     * 程序开启时执行的方法
     */
    @Override
    public void onInitialize() {
        System.out.println("MyApplication onInitialize 方法");
        super.onInitialize();
    }


    @Override
    public void onEnd(){
        System.out.println("MyApplication onEnd 方法");
        super.onEnd();
    }


}
