package io.nfls.forum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Rickliu on 4/2/17.
 */

public class DiscussionListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public DiscussionListAdapter (Context context, List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);

    }

    public final class components{
        public ImageView image;
        public TextView title;
        public Button view;
        public TextView info;
    }
    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        components components=null;
        if(convertView==null){
            components=new components();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.discussion_list, null);
            components.image=(ImageView)convertView.findViewById(R.id.image);
            components.title=(TextView)convertView.findViewById(R.id.title);
            components.info=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(components);
        }else{
            components=(components)convertView.getTag();
        }
        String imageStr=(String)data.get(position).get("image");
        Bitmap bitmap = null;
        try {
            byte [] encodeByte=Base64.decode(imageStr, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        } catch(Exception e) {
            e.getMessage();
            return null;
        }
        //绑定数据
        components.image.setImageBitmap(bitmap);
        components.title.setText((String)data.get(position).get("title"));
        components.info.setText((String)data.get(position).get("info"));
        return convertView;
    }

}