package com.example.esc;

import java.util.List;

public class Driver {
    //size of available WiFi APs
    static double [][] X= {
            {40,0,40,80},
            {40,0,20,70},
            {40,0,10,60},
            {20,0,10,60},
    };

    //coordinates relating to map size, scaled down to 0 to 1
    static double [][] Y= {
            {0.0,0.0},
            {0.0,0.2},
            {0.0,0.4},
            {0.2,0.4}
    };

    public static void main(String[] args) {

        int hiddenLayerNum = 1;
        int hiddenLayerSize = 20;
        int epoch = 500;

        NeuralNetwork nn = new NeuralNetwork(X[0].length,hiddenLayerSize,Y[0].length);


        List<Double>output;

        nn.fit(X, Y, epoch);
        double [][] input = {
                {60,20,60,60},{60,0,20,40}
        };
        for(double d[]:input)
        {
            output = nn.predict(d);
            System.out.println(output.toString());
        }

    }

}
