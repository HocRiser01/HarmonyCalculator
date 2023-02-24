package com.jlu.harmony.cal;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.components.TextField;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindAbility extends Ability implements HttpRequest.Callback {
    ListContainer listContainer;
    Context context;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_find);
        context = this;

        // 获取 布局文件中的ListContainer
        listContainer = findComponentById(ResourceTable.Id_list_container);

        // 构造数据
        List<SampleItem> data = getData();

        // 将构造的数据加入到适配器中
        SampleItemProvider sampleItemProvider = new SampleItemProvider(data, this);

        // 将适配器绑定布局文件中的ListContainer
        listContainer.setItemProvider(sampleItemProvider);


        // 开启新线程，调用查询方法
        TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        globalTaskDispatcher.asyncDispatch(() -> {
            //网络访问
            // findResult(this, listContainer);
            findResult2(this, "");

        });

        Button search = findComponentById(ResourceTable.Id_search);
        TextField q = findComponentById(ResourceTable.Id_q_box);
        search.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                /**
                 * 更新的内容
                 * 使用串行任务
                 */
                TaskDispatcher ptd = createSerialTaskDispatcher("name", TaskPriority.DEFAULT);
                ptd.asyncDispatch(() -> {
                    //网络访问
                    // findResult(this, listContainer);
                    findResult2(context, q.getText());

                });
            }
        });
    }

    private ArrayList<SampleItem> getData() {
        ArrayList<SampleItem> list = new ArrayList<>();
        for (int i = 0; i <= 80; i++) {
            list.add(new SampleItem("标题" + i, "内容....." + i));
        }
        return list;
    }

    public void findResult2(Context context, String q) {
        HttpRequest hr = new HttpRequest();
        hr.request(context, "http://101.133.225.23:8080/mInfo?iTitle=" + q, "GET", this, 0);
    }


    /**
     * 粘贴的方法--------------------------------------------------------------------------------------------------------
     */

//    public boolean findResult(Context context,ListContainer listContainer){
//        /**
//         * 获取网络，如果无法获取，则返回false
//         */
//        NetManager netManager = NetManager.getInstance(context);
//        if (!netManager.hasDefaultNet()) {
//            return false;
//        }
//        NetHandle netHandle = netManager.getDefaultNet();
//
//// 可以获取网络状态的变化
//        NetStatusCallback callback = new NetStatusCallback() {
//            // 重写需要获取的网络状态变化的override函数
//        };
//        netManager.addDefaultNetStatusCallback(callback);
//
//// 通过openConnection来获取URLConnection
//        HttpURLConnection connection = null;
//        try {
//            String urlString = "http://101.133.225.23:8080/mInfo?iTitle="; // 开发者根据实际情况自定义EXAMPLE_URL
//            URL url = new URL(urlString);
//
//            URLConnection urlConnection = netHandle.openConnection(url,
//                    java.net.Proxy.NO_PROXY);
//            if (urlConnection instanceof HttpURLConnection) {
//                connection = (HttpURLConnection) urlConnection;
//                connection.setRequestMethod("GET");
//                connection.connect();
//                // 之后可进行url的其他操作
//            }
//            /**
//             * connection 获取返回值
//             */
//            System.out.print("----code"+connection.getResponseCode());
//            System.out.print("----msg"+connection.getContent().toString());
//
//            StringBuilder sb = new StringBuilder();
//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                /**
//                 * 读取返回的内容
//                 */
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String temp;
//                while((temp = reader.readLine())!= null){
//                    sb.append(temp);
//                }
//                System.out.println("result:----"+sb.toString());
//                String ret = sb.toString();
//                // 将JSON字符串转换成 ZSON对象
//
//                ZSONObject zo = ZSONObject.stringToZSON(ret);
//                int code = zo.getIntValue("code");
//                System.out.println("---->"+code);
//
//                ArrayList<SampleItem> al = new ArrayList<>();
//                ZSONArray list = zo.getZSONArray("data");
//
//                for(int i=0 ; i<list.size();i++){
//                    ZSONObject z = (ZSONObject) list.get(i);
//                    String title = z.getString("ititle");
//                    String content = z.getString("icontent");
//                    System.out.println("----:第"+i+"项 标题：:"+title);
//                    al.add(new SampleItem(title,content));
//                }
//
//                //将列表 al 的值，赋给适配器
//                // 将构造的数据加入到适配器中
//                SampleItemProvider sampleItemProvider = new SampleItemProvider(al, this);
//
//                // 将适配器绑定布局文件中的ListContainer
//                listContainer.setItemProvider(sampleItemProvider);
//
//            }
//            connection.disconnect();
//
//        } catch(IOException e) {
//            System.out.println( "exception happened.");
//        } finally {
//            if (connection != null){
//                connection.disconnect();
//            }
//        }
//
//        return false;
//    }
    @Override
    public void getResult(String ret, int id) {
        System.out.println("---->>>>>>>>>>>>>>>>>>>>>>>>>>>>:" + ret);

        ZSONObject zo = ZSONObject.stringToZSON(ret);
        int code = zo.getIntValue("code");
        System.out.println("---->" + code);

        ArrayList<SampleItem> al = new ArrayList<>();
        ZSONArray list = zo.getZSONArray("data");

        for (int i = 0; i < list.size(); i++) {
            ZSONObject z = (ZSONObject) list.get(i);
            String title = z.getString("ititle");
            String content = z.getString("icontent");
            System.out.println("----:第" + i + "项 标题：:" + title);
            al.add(new SampleItem(title, content));
        }

        /**
         * 更新的内容
         * 在UI线程中更新适配器
         */
        getUITaskDispatcher().asyncDispatch(() -> {
            //将列表 al 的值，赋给适配器
            // 将构造的数据加入到适配器中
            SampleItemProvider sampleItemProvider = new SampleItemProvider(al, this);
            // 将适配器绑定布局文件中的ListContainer
            listContainer.setItemProvider(sampleItemProvider);
        });
    }

    @Override
    public void getError(String ret, int id) {

    }


}
