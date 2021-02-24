package com.example.unabiz;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.transition.Hold;

import java.util.List;


public class ListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<ScanResult> wifiList;



    public ListAdapter(Context context, List<ScanResult> wifiList ) {
        this.context = context;
        this.wifiList = wifiList;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        View view = convertView;

        if(view == null) {

            view = inflater.inflate(R.layout.list_item,null);
            holder = new Holder();

            holder.tvdetails = (TextView)view.findViewById(R.id.txtWifiName);
            holder.tvRSSI = (TextView) view.findViewById(R.id.txtWifiRSSI);
            holder.tvMAC = (TextView) view.findViewById(R.id.txtWifiMAC);
            view.setTag(holder);

        } else {
            holder = (Holder)view.getTag();
        }

        holder.tvdetails.setText(wifiList.get(position).SSID);
        int rssi = wifiList.get(position).level;
        holder.tvRSSI.setText(Integer.toString(rssi) + "dBm");
        holder.tvMAC.setText(wifiList.get(position).BSSID);

        return view;
    }

    class Holder{
        TextView tvdetails;
        TextView tvRSSI;
        TextView tvMAC;
    }
}
