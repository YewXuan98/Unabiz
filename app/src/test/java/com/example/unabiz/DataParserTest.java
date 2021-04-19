package com.example.unabiz;

import com.example.unabiz.DataParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DataParserTest {
    DataParser dp = new DataParser();
    DataParser filled_dp;
    @Before
    public void beforeEachTest(){
        HashMap<String, HashMap<String,Integer>> coordinates = new HashMap<>();
        for (int i=0;i<4;i++){
            HashMap<String,Integer> placeholder = new HashMap<>();
            placeholder.put("x",i);
            placeholder.put("y",i*i);
            coordinates.put("location_"+i,placeholder);
        }


        HashMap<String,HashMap<String,Integer>> mac_rssi = new HashMap<>();
        for (int i=0;i<4;i++){
            HashMap<String,Integer> placeholder = new HashMap<>();
            placeholder.put("SSID_1",-i*10);
            placeholder.put("SSID_2",-i*20);
            placeholder.put("SSID_3",-i*30);
            placeholder.put("SSID_4",-i*40);
            mac_rssi.put("location_"+i,placeholder);
        }

        ArrayList<String> mac_addresses_list = new ArrayList<>();
        for (int i=0;i<6;i++){
            mac_addresses_list.add("location_"+i);
        }

        filled_dp = new DataParser(coordinates,mac_rssi, mac_addresses_list);

    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void array_find_max_isCorrect(){
        ArrayList<Double> data = intToDoubleArray( new int[]{0,1,2,3,4,5,6,7,8,9,10});
        assertEquals(dp.array_find_max(data),10);
    }

    @Test
    public void array_find_max_isCorrectReverse(){
        ArrayList<Double> data = intToDoubleArray( new int[]{5,4,3,2,1,0});
        assertEquals(dp.array_find_max(data),0);
    }

    @Test
    public void array_find_max_isCorrectAllEqual(){
        ArrayList<Double> data = intToDoubleArray( new int[]{5,5,5,5,5,5,5});
        assertEquals(dp.array_find_max(data),0);
    }

    @Test
    public void array_find_max_isCorrectEmpty(){
        ArrayList<Double> data = intToDoubleArray( new int[]{});
        assertEquals(dp.array_find_max(data),0);
    }

    @Test
    public void array_find_max_isCorrectNegatives(){
        ArrayList<Double> data = intToDoubleArray( new int[]{-5,-4,-3,-2,-1});
        assertEquals(dp.array_find_max(data),4);
    }

    @Test
    public void parse_macAddressList_isCorrect(){
        filled_dp.parse();
        ArrayList<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("location_0");
        expectedOutput.add("location_1");
        expectedOutput.add("location_2");
        expectedOutput.add("location_3");
        expectedOutput.add("location_4");
        expectedOutput.add("location_5");
        assertArrayEquals(filled_dp.mac_addresses_list.toArray(), expectedOutput.toArray());
    }


    @Test
    public void getXTest(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getX(5),5);
    }

    @Test
    public void getXTestZero(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getX(0),0);
    }

    @Test
    public void getXTestBoundary(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getX(10),0);
    }

    @Test
    public void getYTest(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getY(5),0);
    }

    @Test
    public void getYTestZero(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getX(0),0);
    }

    @Test
    public void getYTestBoundary(){
        filled_dp.map_length=10;
        assertEquals(filled_dp.getY(20),2);
    }

    @Test
    public void xy_coordinateToIndexTest(){
        HashMap<String,Integer> xy_coordinate = new HashMap<>();
        xy_coordinate.put("x",5);
        xy_coordinate.put("y",10);
        filled_dp.map_length=10;
        assertEquals(filled_dp.xy_coordinate_to_index(xy_coordinate),105);
    }

    @Test
    public void xy_coordinateToIndexZeroTest(){
        HashMap<String,Integer> xy_coordinate = new HashMap<>();
        xy_coordinate.put("x",0);
        xy_coordinate.put("y",0);
        filled_dp.map_length=10;
        assertEquals(filled_dp.xy_coordinate_to_index(xy_coordinate),0);
    }

    @Test
    public void xy_coordinateToIndexBoundaryTest(){
        HashMap<String,Integer> xy_coordinate = new HashMap<>();
        xy_coordinate.put("x",9);
        xy_coordinate.put("y",9);
        filled_dp.map_length=10;
        assertEquals(filled_dp.xy_coordinate_to_index(xy_coordinate),99);
    }

    public ArrayList<Double>  intToDoubleArray(int[] input){
        ArrayList<Double> data =  new ArrayList<>();
        for(int i=0;i<input.length;i++){
            data.add(Double.valueOf(input[i]));
        }
        return data;
    }
}
