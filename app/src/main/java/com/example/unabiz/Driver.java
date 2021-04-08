package com.example.unabiz;

import java.util.List;

public class Driver {
    //the RSSI value of each WiFi APs
    static double [][] X= {
            {-40,0,-40,-80},
            {-40,0,0,-70},
            {-40,0,-10,-60},
            {0,-20,-40,-60},
    };

    //coordinates relating to map size, one-hot encoding.
    //a one-to-one mapping of (x,y) pair to an index
    //value is calculated by x_coordinate*columnTotal+y_coordinate
    static double [][] Y= {
            {0,0,0,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,1,0}
    }; //3,4,8,10

    public static void main(String[] args) {

        int hiddenLayerNum = 1;
        int hiddenLayerSize = 20;
        int epoch = 1000;

        NeuralNetwork nn = new NeuralNetwork(X[0].length,hiddenLayerSize,Y[0].length);

        List<Double>output;

        nn.fit(X, Y, epoch);
        double [][] input = {
                {60,20,60,60},{60,0,20,40}
        };


        for(double d[]:input)
        {
            output = nn.predict(d);
            int max = 0;
            int largestIndex = 0;
            int x;
            int y;
            for (int i=0;i<output.size();i++){
                if (output.get(i)>max){
                    largestIndex=i;
                }
            }
            x = largestIndex/2; //integer division
            y = largestIndex%2; //modulo division
            System.out.println("Result is");
            System.out.println(x);
            System.out.println(y);
        }
    }

}
