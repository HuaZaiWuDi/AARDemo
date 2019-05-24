package com.vondear.rxtools.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;


/**
 * JavaBean类
 *
 * @author 李玉江[QQ:1032694760]
 * @since 2014-04-23 16:14
 */
public class JavaBean implements Serializable {

    /**
     * 反射出所有字段值
     */
    @Override
    public String toString() {
        ArrayList<Field> list = new ArrayList<>();
        Class<?> clazz = getClass();
        //得到自身的所有字段
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            list.add(field);
        }

        StringBuilder sb = new StringBuilder();
        while (clazz != Object.class) {
            clazz = clazz.getSuperclass();//得到继承自父类的字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                int modifier = field.getModifiers();
                if (Modifier.isPublic(modifier) || Modifier.isProtected(modifier)) {
                    list.add(field);
                }
            }
        }
        for (Field field : list) {
            String fieldName = field.getName();
            Class<?> type = field.getType();

            try {
                Object obj = field.get(this);
                sb.append(fieldName);
                sb.append("=");

                if (type.isArray()) {
                    growArray(obj, sb);
                } else {
                    sb.append(obj);
                }
                sb.append("\n");
            } catch (IllegalAccessException e) {
                RxLogUtils.e(e);
            }
        }
        return sb.toString();
    }

    public void growArray(Object array, StringBuilder sb) {
        sb.append("{");
        for (int i = 0; i < Array.getLength(array); i++) {
            Object o = Array.get(array, i);
            if (o.getClass().isArray()) {
                growArray(o, sb);
                if (i != Array.getLength(array) - 1)
                    sb.append(",");
            } else {
                sb.append(o);
                if (i != Array.getLength(array) - 1)
                    sb.append(",");
            }
        }
        sb.append("}");
    }

}
