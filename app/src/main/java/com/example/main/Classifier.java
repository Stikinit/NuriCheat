package com.example.main;

import android.app.Activity;

import com.example.main.ml.Mnist;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.opencv.core.Mat;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Classifier {

    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE =1;
    private static final int  DIM_HEIGHT =28;
    private static final int DIM_WIDTH = 28;
    private static final int BYTES = 4;

    private Mnist mnist;

    private ByteBuffer imgData = null;
    private @NonNull float[] probArray = null;
    private String modelFile = "mnist.tflite";
    private Activity activity;

    public Classifier(Activity activity) {
        this.activity = activity;
        imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_HEIGHT * DIM_WIDTH * DIM_PIXEL_SIZE * BYTES);
        imgData.order(ByteOrder.nativeOrder());
        probArray = new float[10];
    }

    public int classifyMat(Mat mat) throws IOException {
        mnist = Mnist.newInstance(this.activity);
        int result = 0;
        if(mnist!=null) {
            convertMat(mat);
            result = runInference();
        }
        // Releases model resources if no longer used.
        mnist.close();
        return result;
    }

    private int runInference() {
        // Creates inputs for reference.
        TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 28, 28, 1}, DataType.FLOAT32);
        inputFeature.loadBuffer(imgData);

        // Runs model inference and gets result.
        Mnist.Outputs outputs = mnist.process(inputFeature);
        TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();
        probArray = outputFeature.getFloatArray();

        for (int i=0; i<probArray.length; i++) {
            System.out.print(probArray[i]+" : ");
        }
        System.out.println("");
        // Populates probArray
        return pos2int(outputFeature.getFloatArray());
    }

    private int pos2int(float[] positional_array) {
        int idx;
        for(idx = 0 ; idx < positional_array.length; idx++) {
            if (positional_array[idx] == 1.0) {
                return idx;
            }
        }
        return idx;
    }

    private void convertMat(Mat mat) {
        imgData.rewind();
        for (int i = 0; i < DIM_HEIGHT; ++i) {
            for (int j = 0; j < DIM_WIDTH; ++j) {
                imgData.putFloat((float)mat.get(i,j)[0]);
            }
        }
    }

}
