package com.jlu.harmony.cal;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.OnClickListener;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

import java.util.ArrayList;

/**
 * Controller 控制器，是程序的入口，窗口的生命周期
 *
 * @author XiaoMingliang
 * @Create date 2023-02-20
 */

public class MainAbility extends Ability {

    // 全局变量，用来临时保存屏幕中的数值
    String tempValue = "";
    // 运算符号标志,用来保存用户点击了什么运算符号，以便点击等于号时，判断运算方法
    String cal = "";


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        //super.setMainRoute(MainAbilitySlice.class.getName());
        super.setUIContent(ResourceTable.Layout_ability_main);

        /**
         * 获取文本框对象
         */
        Text myTxt = findComponentById(ResourceTable.Id_txt_screen);

        /**
         * 定义一个列表组件，用来保存所有数字按钮
         */
        ArrayList<Button> btnList = new ArrayList<>();
        /**
         * 获取到组件对象
         * 定义按钮对象， 获取布局文件中的组件id是 按钮7。
         */
        Button myBtn7 = findComponentById(ResourceTable.Id_btn_7);
        Button myBtn8 = findComponentById(ResourceTable.Id_btn_8);
        Button myBtn9 = findComponentById(ResourceTable.Id_btn_9);

        Button myBtn4 = findComponentById(ResourceTable.Id_btn_4);
        Button myBtn5 = findComponentById(ResourceTable.Id_btn_5);
        Button myBtn6 = findComponentById(ResourceTable.Id_btn_6);

        Button myBtn1 = findComponentById(ResourceTable.Id_btn_1);
        Button myBtn2 = findComponentById(ResourceTable.Id_btn_2);
        Button myBtn3 = findComponentById(ResourceTable.Id_btn_3);

        /**
         * 添加所有数字按钮到列表中。
         */
        btnList.add(myBtn7);
        btnList.add(myBtn8);
        btnList.add(myBtn9);
        btnList.add(myBtn4);
        btnList.add(myBtn5);
        btnList.add(myBtn6);
        btnList.add(myBtn1);
        btnList.add(myBtn2);
        btnList.add(myBtn3);

        // 循环，每次自动获取btnList中的内容，并赋给变量：t
        for (Button t : btnList) {
            t.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    String org_value = myTxt.getText();// 获取当前屏幕中的值
                    System.out.println("->" + org_value + "<-");

                    if (org_value.equals("0")) { // 如果当前屏幕中的数字是0， 那么清空0
                        myTxt.setText("");
                        org_value = "";
                    }
                    myTxt.setText(org_value + t.getText());  // 把当前的值后面加按钮上显示的数值
                }
            });
        }
        /**
         * 加法运算
         * 1） 将当前屏幕中的值保存起来
         * 2） 清空当前屏幕
         */
        Button myBtnAdd = findComponentById(ResourceTable.Id_btn_add);
        myBtnAdd.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 加法按钮点击事件,临时保存屏幕中的值
                tempValue = myTxt.getText();
                // 清空屏幕的内容，等待下次输入
                myTxt.setText("");
                // 设置标志位
                cal = "+";

            }
        });

        /**
         * 减法
         */
        Button myBtnTake = findComponentById(ResourceTable.Id_btn_take);
        myBtnTake.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 减法按钮点击事件,临时保存屏幕中的值
                tempValue = myTxt.getText();
                // 清空屏幕的内容，等待下次输入
                myTxt.setText("");
                // 设置标志位
                cal = "-";

            }
        });

        /**
         * 等于号，点击等于号后，计算结果
         */
        Button myBtnEqual = findComponentById(ResourceTable.Id_btn_equals);
        myBtnEqual.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                int valueA = Integer.valueOf(tempValue).intValue();
                int valueB = Integer.valueOf(myTxt.getText()).intValue();
                /**
                 *  两种写法-第一种写法*******************************************************
                 */
                // 进行运算
                // 判断符号，再进行运算
                /**
                if (cal.equals("+")) {
                    myTxt.setText(valueA + valueB);
                }
                if (cal.equals("-")) {
                    myTxt.setText(valueA - valueB);

                }
                 **/
                /**
                 *  两种写法 -第二种写法*******************************************************
                 */
                System.out.println("equals:"+(valueA+valueB));

                switch (cal) {
                    case "+": {
                        myTxt.setText(String.valueOf(valueA + valueB));
                    }
                    break;
                    case "-": {
                        myTxt.setText(String.valueOf(valueA - valueB));
                    }
                    break;
                }
                /**
                 *  两种写法*******************************************************
                 */

            }
        });


        /**
         * 给按钮对象，添加click监听。如果按钮被按下，则调用34行中的方法。
         */
        /*
        myBtn7.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String org_value = myTxt.getText();// 获取当前屏幕中的值
                System.out.println("->" + org_value + "<-");

                if (org_value.equals("0")) { // 如果当前屏幕中的数字是0， 那么清空0
                    myTxt.setText("");
                    org_value = "";
                }
                myTxt.setText(org_value + "7");  // 把当前的值后面加7

            }
        });

    */


    }
}
