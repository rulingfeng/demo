package com.example.demo.design;



//DCL懒汉单例模式
public class DclSingleton {
    private volatile static DclSingleton s = null;

    private DclSingleton(){}

    public static DclSingleton getSingleton(){
        if(null == s){
            synchronized (DclSingleton.class){
                if(null == s){
                    s = new DclSingleton();
                }
            }
        }
        return s;
    }
}
//枚举单例模式
class EnumSingleton{
    private EnumSingleton(){}

    static enum EnumSingletonTest{
        INSTATNS;
        private EnumSingleton enumSingleton;
        private EnumSingletonTest(){
            enumSingleton = new EnumSingleton();
        }
        public EnumSingleton getEnumSingleton(){
            return enumSingleton;
        }
    }
    public static EnumSingleton getInstance(){
        return EnumSingletonTest.INSTATNS.getEnumSingleton();
    }
}
