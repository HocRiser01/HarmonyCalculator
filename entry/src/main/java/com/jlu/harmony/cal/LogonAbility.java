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

public class LogonAbility extends Ability {
    Context context = null;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_logon);
        //super.setMainRoute(LoginAbilitySlice.class.getName());
        context = this;

        Button logon = findComponentById(ResourceTable.Id_jbtn_logon);

        logon.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                /**
                 * 点击注册按钮时，获取三个输入框的值
                 */
                String username = ((TextField)findComponentById(ResourceTable.Id_o_username)).getText();
                String password = ((TextField)findComponentById(ResourceTable.Id_o_userpasswd)).getText();
                String verifycode = ((TextField)findComponentById(ResourceTable.Id_o_verify)).getText();

                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
                globalTaskDispatcher.asyncDispatch(() -> {
                    //网络访问
                    logon(context,username,password,verifycode);
                });
                //login(context,username,password,verifycode);
            }
        });
    }

    public boolean logon(Context context,String username,String password,String verifyCode){
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
        if (!password.equals(verifyCode)){
            return false;
        }
        try {
            String urlString = "http://101.133.225.23:8080/register?username="+username+"&password="+password;
            URL url = new URL(urlString);

            URLConnection urlConnection = netHandle.openConnection(url,
                    java.net.Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
                connection.setRequestMethod("POST");
                connection.connect();
                // 之后可进行url的其他操作
            }

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
                    System.out.println("----注册成功");
                    /**
                     * 打开新的窗口
                     */
                    // Intent： 意图 主要用来启动新窗口、启动后台服务
                    Intent intent = new Intent();
                    // 用来指定 启动的窗口。需要设置设备ID、程序的名称、窗口名称
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")
                            .withBundleName("com.jlu.harmony.cal")
                            .withAbilityName("com.jlu.harmony.cal.LoginAbility")
                            .build();

                    intent.setOperation(operation);
                    startAbility(intent);

                }else{
                    System.out.println("----注册失败");
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
}
