package com.jlu.harmony.cal;

import ohos.agp.components.ListContainer;
import ohos.app.Context;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HttpRequest {

    /**
     * Http 请求
     * @param context  上下文
     * @param u 请求的地址
     * @param method POST 或者 GET
     * @param cb 网络请求结果的接口
     * @param id 请求标识
     */
    public void request(Context context,String u,String method, Callback cb, int id){
        /**
         * 获取网络，如果无法获取，则返回false
         */
        NetManager netManager = NetManager.getInstance(context);
        if (!netManager.hasDefaultNet()) {
            return;
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
            String urlString = u; // 开发者根据实际情况自定义EXAMPLE_URL
            URL url = new URL(urlString);

            URLConnection urlConnection = netHandle.openConnection(url,
                    java.net.Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
                connection.setRequestMethod(method);
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
                String ret = sb.toString();
                // 将JSON字符串转换成 ZSON对象
                cb.getResult(ret,id);
            }
            connection.disconnect();

        } catch(IOException e) {
            System.out.println( "exception happened.");
            cb.getError("",id);
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        return;
    }


    /**
     * 定义接口
     * 用来做回调
     */
    interface  Callback{
        // 回调方法
        void getResult(String ret,int id);
        // 回调方法
        void getError(String ret,int id);
    }


}
