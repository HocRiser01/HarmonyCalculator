package com.jlu.harmony.cal;

import ohos.app.Context;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpRequestUtil {
    /**
     *                          有前三个参数即可访问成功,设置更多参数以便更通用
     * @param context           上下文
     * @param urlString         请求的接口路径
     * @param requestMethod     请求方式：GET POST DELETE PUT
     * @param token             访问受限资源携带token，非受限资源传递null即可
     * @param data              当请求方式为PUT/POST时，通过请求体传递数据（JSON格式的字符串）
     * @return                  返回请求结果（字符串类型）
     */

    private static String sendRequest(Context context, String urlString, String requestMethod, String token, String data){
        String result = null;

        //创建链接
        NetManager netManager = NetManager.getInstance(context);

        if (!netManager.hasDefaultNet()) {
            return null;
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

            //发送链接

            URL url = new URL(urlString);

            connection = (HttpURLConnection) netHandle.openConnection(url, Proxy.NO_PROXY);
            //设置请求方式
            connection.setRequestMethod(requestMethod);

            if (data != null){
                //允许通过此网络连接向服务器端写数据
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json;charset=utf-8");
            }

            //如果参数token!=null，则需要将token设置到请求头
            if (token != null){
                connection.setRequestProperty("token",token);
            }


            //发送请求建立连接
            connection.connect();

            //向服务器传递data中的数据
            if (data != null){
                byte[] bytes = data.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(bytes);
                os.flush();
            }


            //获取链接结果
            //从连接中获取输入流，读取api接口返回的数据
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = connection.getInputStream();
                StringBuilder builder = new StringBuilder();
                byte[] bs = new byte[1024];
                int len = -1;
                while ((len = is.read(bs)) != -1){
                    builder.append(new String(bs,8,len));
                }
                result = builder.toString();
            }


        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return result;
    }



    //get请求
    public static String sendGetRequest(Context context,String urlString){
        return sendRequest(context,urlString,"GET",null,null);
    }
    public static String sendGetRequestWithToken(Context context,String urlString,String token){
        return sendRequest(context,urlString,"GET",token,null);
    }

    //post请求
    public static String sendPostRequest(Context context,String urlString){
        return sendRequest(context,urlString,"POST",null,null);
    }
    public static String sendPostRequestWithToken(Context context,String urlString,String token){
        return sendRequest(context,urlString,"POST",token,null);
    }
    public static String sendPostRequestWithData(Context context,String urlString,String data){
        return sendRequest(context,urlString,"POST",null,data);
    }
    public static String sendPostRequestWithTakenWithData(Context context,String urlString,String token,String data){
        return sendRequest(context,urlString,"POST", token,data);
    }
}

