package com.lexi.cqu.json;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class MainActivityFragment extends Fragment{
    private List<String> listNews=new ArrayList<String>();
    private ListView listView;
    private ArrayAdapter myForecastArrayAdapter;
    private android.os.Handler handler=new android.os.Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    //  listNews=(List<String>) msg.obj;
                    myForecastArrayAdapter = new ArrayAdapter<String>(
                            //The current context
                            getActivity(),
                            //ID of the list item layout
                            R.layout.layout_each_item,
                            // ID of the textView to populate
                            R.id.tv_title,
                            //Forecast data
                            (List<String>) msg.obj
                    );
                    // myForecastArrayAdapter.notifyDataSetChanged(); //发送消息通知ListView更新
                    Log.e("get",msg.what+"");
                    listView.setAdapter(myForecastArrayAdapter); // 重新设置ListView的数据适配

                default:
                    break;
            }
        }
    };
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Create a root view for the fragment
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        Context context=getActivity();
        //获得一个handler对象，为后面的各个线程提供处理UI的依据


        //Reference to the listView
        listView = (ListView) rootView.findViewById(R.id.listview);
        //Set array adapter on the listView
        listView.setAdapter(myForecastArrayAdapter);
        new JsonThread(handler).start();
        String[] forecastArray = {
                "Today - Sunny - 55/ 63",
                "Tomorrow - Foggy - 70/46",
                "Saturday - Cloudy - 72 / 63",
                "Sunday - Rainy - 64 / 51",
                "Monday - Foggy - 70 / 46",
                "Tuesday - Sunny - 76 / 68"};

        return rootView;


    }
}
class JsonThread extends Thread {


    private List<String> listNews=new ArrayList<String>();
    private android.os.Handler handler;
    public JsonThread(android.os.Handler handler) {


        this.handler=handler;

    }

    @Override
    public void run() {

        //从网络中获取数据，转换为String类型
        try {
            URL url = new URL(
                    "http://mpianatra.com/Courses/files/data.json");
            // 通过url对象获取一个网络连接对象
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            // 设置请求方式
            conn.setRequestMethod("GET");
            // 设置读取数据的时间
            conn.setReadTimeout(4000);
            // 设置联网时间
            conn.setConnectTimeout(5000);

            conn.connect();
            StringBuffer sBuffer = new StringBuffer();
            String str = null;

            // 从网络上获取到的数据是一个json字符串,
            BufferedReader bReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            // 读取bRead指向的数据
            while ((str = bReader.readLine()) != null) {
                sBuffer.append(str);
            }
            // textView.setText(sBuffer.toString());
            // Json解析
            JSONObject object = new JSONObject(sBuffer.toString());
            JSONArray jsonArray=object.getJSONArray("allNews");
            for (int i = 0; i < jsonArray.length(); i++) {
                object = (JSONObject) jsonArray.get(i);
                Log.e("title",object.getString("title"));
                listNews.add(object.getString("title"));
            }
            Message message=new Message();
            message.what=1;
            message.obj=listNews;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.run();
    }
}
class MyAdapter extends BaseAdapter {

    List<String> data;
    Context context;
    LayoutInflater inflater;


    public MyAdapter(Context context,List<String> data) {
        this.context=context;
        this.data=data;
        inflater=LayoutInflater.from(context);//从MainActivity中上下文对象中获取LayoutInflater；所以说这个context,和handler对象很重要，贯穿整项目
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //重写getView方法，即设置ListView每一项的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if(convertView==null){
            convertView=inflater.inflate(R.layout.layout_each_item,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);//设置tag
        }else {
            holder= (ViewHolder) convertView.getTag(); //获取tag
        }
        holder.title.setText(data.get(position));
        return convertView;
    }

    //用于暂时保存视图对象
    class ViewHolder{
        public TextView title;

        public ViewHolder(View view){
            title= (TextView) view.findViewById(R.id.tv_title);
        }
    }
}