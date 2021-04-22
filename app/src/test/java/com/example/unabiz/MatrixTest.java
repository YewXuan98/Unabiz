package com.example.unabiz;



import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MatrixTest {
    Matrix matrix1 = new Matrix(2,3,new double[]{1,2,3,4,5,6});
    Matrix matrix2 = new Matrix(3,2,new double[]{1,2,3,4,1,2});
    Matrix matrix3 = new Matrix(4,3,new double[]{2,4,6,8,1,3,5,7,1,2,3,4});
    Matrix matrix4 = new Matrix(3,4,new double[]{1,2,3,4,5,6,7,8,1,3,5,7});
    Matrix matrix5 = new Matrix(4,2,new double[]{1,2,3,4,5,6,7,8});
    Matrix matrix6 = new Matrix(2,3,new double[]{2,5,8,11,14,17});

    @Before
    public void beforeEachTest(){
    }


    @Test
    public void addTestPositive(){
        matrix1.add(4);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{5,6,7,8,9,10})));
    }

    @Test
    public void addTestNegative(){
        matrix1.add(-5);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{-4,-3,-2,-1,0,1})));
    }

    @Test
    public void addTestZero(){
        matrix1.add(0);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{1,2,3,4,5,6})));
    }

    @Test
    public void addTestMatrix(){
        matrix1.add(matrix6);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{3,7,11,15,19,23})));
    }

    @Test
    public void multiplyTestMatrix(){
        matrix1.multiply(matrix6);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{2,10,24,44,70,102})));
    }

    @Test
    public void multiplyTestMatrixZero(){
        matrix1.multiply(0);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{0,0,0,0,0,0})));
    }
    @Test
    public void multiplyTestMatrixOne(){
        matrix1.multiply(0);
        assertTrue(Matrix.isEquals(matrix1,matrix1));
    }

    @Test
    public void multiplyTestMatrixNegative(){
        matrix1.multiply(-1);
        assertTrue(Matrix.isEquals(matrix1,new Matrix(2,3,new double[]{-1,-2,-3,-4,-5,-6})));
    }
}
