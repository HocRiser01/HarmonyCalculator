package com.jlu.harmony.cal;

import com.bumptech.glide.Glide;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.TextField;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;

import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class LoginAbility extends Ability {

    Context context = null;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_login);
        //super.setMainRoute(LoginAbilitySlice.class.getName());
        context =this;

        Image image = findComponentById(ResourceTable.Id_captcha);
        /**
         * uri为指定的图片地址
         */
        Uri uri = Uri.parse("http://101.133.225.23:8080/images/captcha");
        /**
         * 将加载的图片绑定到图片控件中
         */
        Glide.with(getContext()).load(uri).into(image);

        image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Uri uri = Uri.parse("http://101.133.225.23:8080/images/captcha?d="+ UUID.randomUUID());
                Glide.with(getContext()).load(uri).into((Image)component);
            }
        });

        Button login = findComponentById(ResourceTable.Id_btn_login);
        Button logon = findComponentById(ResourceTable.Id_btn_logon);
        login.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                /**
                 * 点击登录按钮时，获取三个输入框的值
                 */
                String username = ((TextField)findComponentById(ResourceTable.Id_i_username)).getText();
                String password = ((TextField)findComponentById(ResourceTable.Id_i_userpasswd)).getText();
                String verifycode = ((TextField)findComponentById(ResourceTable.Id_i_verify)).getText();
                System.out.println("----"+username+";"+password+";"+verifycode);

                //原因：移动应用中不允许在主线程中运行耗时的程序
                //解决：将耗时的操作放在一个独立的线程中完成，主线程就不会被阻塞
                //方法：创建TaskDispatcher对象
                //参数TaskPriority:线程优先级，default表示默认
                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
                globalTaskDispatcher.asyncDispatch(() -> {
                    //网络访问
                    login(context,username,password,verifycode);
                });
                //login(context,username,password,verifycode);
            }
        });
        logon.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                /**
                 * 点击注册按钮时，跳转界面
                 */

                //原因：移动应用中不允许在主线程中运行耗时的程序
                //解决：将耗时的操作放在一个独立的线程中完成，主线程就不会被阻塞
                //方法：创建TaskDispatcher对象
                //参数TaskPriority:线程优先级，default表示默认
                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
                globalTaskDispatcher.asyncDispatch(() -> {
                    //网络访问
                    logon(context);
                });
                //login(context,username,password,verifycode);
            }
        });
    }


    /**
     * 登录系统
     * 接收4个参数，分别是 上下文，用户名、密码、验证码
     * @param context
     * @return
     */
    public boolean login(Context context,String username,String password,String verifyCode){
        /**
         * 获取网络，如果无法获取，则返回false
         */
        NetManager netManager = NetManager.getInstance(context);
        if (!netManager.hasDefaultNet()) {
            return false;
        }
        NetHandle netHandle = netManager.getDefaultNet();

// 可以获取网络状态的变化
        NetStatusCallback callback = new NetStatusCallback() {
            // 重写需要获取的网络状态变化的override函数
        };
        netManager.addDefaultNetStatusCallback(callback);

// 通过openConnection来获取URLConnection
        HttpURLConnection connection = null;
        try {
            String urlString = "http://101.133.225.23:8080/login2?username="+username+"&password="+password+"&verifyCode="+verifyCode; // 开发者根据实际情况自定义EXAMPLE_URL
            URL url = new URL(urlString);

            URLConnection urlConnection = netHandle.openConnection(url,
                    java.net.Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
                connection.setRequestMethod("POST");
                connection.connect();
                // 之后可进行url的其他操作
            }
            /**
             * connection 获取返回值
             */
            System.out.print("----code"+connection.getResponseCode());
            System.out.print("----msg"+connection.getContent().toString());

            StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                /**
                 * 读取返回的内容
                 */
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String temp;
                while((temp = reader.readLine())!= null){
                    sb.append(temp);
                }
                System.out.println("result:----"+sb.toString());
                String ret = sb.toString();
                // 将JSON字符串转换成 ZSON对象

                ZSONObject zo = ZSONObject.stringToZSON(ret);
                int code = zo.getIntValue("code");
                System.out.println("---->"+code);

                if(code==200){
                    System.out.println("----登录成功");
                    /**
                     * 打开新的窗口
                     */
                    // Intent： 意图 主要用来启动新窗口、启动后台服务
                    Intent intent = new Intent();
                    // 用来指定 启动的窗口。需要设置设备ID、程序的名称、窗口名称
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")
                            .withBundleName("com.jlu.harmony.cal")
                            .withAbilityName("com.jlu.harmony.cal.FindAbility")
                            .build();

                    intent.setOperation(operation);
                    startAbility(intent);

                }else{
                    System.out.println("----登录失败");
                }

            }
            connection.disconnect();

        } catch(IOException e) {
            System.out.println( "exception happened.");
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        return false;
    }

    public boolean logon(Context context){
        /**
         * 打开新的窗口
         */
        // Intent： 意图 主要用来启动新窗口、启动后台服务
        Intent intent = new Intent();
        // 用来指定 启动的窗口。需要设置设备ID、程序的名称、窗口名称
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.jlu.harmony.cal")
                .withAbilityName("com.jlu.harmony.cal.LogonAbility")
                .build();
        intent.setOperation(operation);
        startAbility(intent);
        return false;
    }
}
