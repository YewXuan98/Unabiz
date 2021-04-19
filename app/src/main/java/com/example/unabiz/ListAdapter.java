package com.example.unabiz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.math.RoundingMode;

import com.google.android.material.transition.Hold;

import java.util.List;

import static android.net.wifi.WifiManager.*;


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

    @RequiresApi(api = Build.VERSION_CODES.R)
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
            holder.tvdistance = view.findViewById(R.id.textWifiDistance);
            view.setTag(holder);

        } else {
            holder = (Holder)view.getTag();
        }



        int rssi = wifiList.get(position).level;

        double frequencyInMhz = wifiList.get(position).frequency;
        double distance = Math.round(getdistance(rssi,frequencyInMhz));
        //Log.i("TEst frequency", Double.toString(frequencyInMhz));
        //Log.i("TEst Distance", Double.toString(distance));

        holder.tvdetails.setText(wifiList.get(position).SSID);
        holder.tvRSSI.setText(Double.toString(rssi) + " dBm");
        holder.tvMAC.setText("(" + wifiList.get(position).BSSID + ")");
        holder.tvdistance.setText("~" + Double.toString(distance) + " metres" );


        return view;
    }

    private double getdistance(double signalLevelindb, double frequencyInMHz) {

        double exp = (27.55 - (20 * Math.log10(frequencyInMHz)) + Math.abs(signalLevelindb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    class Holder{
        TextView tvdetails;
        TextView tvRSSI;
        TextView tvMAC;
        TextView tvdistance;

    }
}
