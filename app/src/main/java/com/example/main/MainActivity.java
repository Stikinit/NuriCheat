package com.example.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.tools.board.Board;
import com.tools.board.BoardOps;
import com.tools.board.Cell;
import com.tools.solver.Solver;
import com.tools.solver.SolverStrategy;
import com.tools.solver.strategy.AllValidIslandsStrategy;
import com.tools.solver.strategy.BlackConnectStrategy;
import com.tools.solver.strategy.ExpandStrategy;
import com.tools.solver.strategy.NoBlackBlockStrategy;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private static final int GALLERY_REQUEST = 0;
    private List<Integer> grid = null;
    private EditText single_cell = null;


    final SolverStrategy[] strategies =
            { new AllValidIslandsStrategy(), new ExpandStrategy(), new NoBlackBlockStrategy(),
                    new BlackConnectStrategy() };
    final Solver solver = new Solver(Arrays.asList(strategies));

    Button loadButton = null;
    Button solveButton = null;
    GridLayout gridLayout = null;
    TextView alertView = null;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Opencv installed succesfully");
        }
        else {
            Log.d(TAG, "Opencv not installed");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadButton = findViewById(R.id.loadButton);
        solveButton = findViewById(R.id.solveButton);
        gridLayout = findViewById(R.id.grid25);
        alertView = findViewById(R.id.alertView);
        alertView.setText("");

        loadButton.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });

        solveButton.setOnClickListener(v -> {
            final Board board = new Board(grid);
            final boolean success = solver.tryToSolve(board);
            if(success) {
                for (Cell cell: board) {
                    if (cell.isBlack()) {
                        single_cell = (EditText) gridLayout.getChildAt(BoardOps.coordsToIndex(cell.getX(), cell.getY()));
                        single_cell.setBackgroundColor(Color.BLACK);
                    }
                }
                alertView.setText("Puzzle successfully solved!");
            } else {
                alertView.setText("The grid could not be solved...");
            }

        });

    }

    private void clearGrid() {
        for(int cell_idx=0; cell_idx<25; cell_idx++) {
            single_cell = (EditText) gridLayout.getChildAt(cell_idx);
            single_cell.setText("");
            single_cell.setBackground(getDrawable(R.drawable.cell_borders));

            alertView.setText("");
        }
    }

    private void showGrid() {
        for(int cell_idx=0; cell_idx<25; cell_idx++) {
            single_cell = (EditText) gridLayout.getChildAt(cell_idx);
            if (grid.get(cell_idx) != 0) {
                single_cell.setText(grid.get(cell_idx)+"");
            }
        }
        alertView.setText("Grid loaded!");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    ImageProcessor ip = new ImageProcessor(this);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Mat in = new Mat();
                        Utils.bitmapToMat(bitmap, in);

                        try {
                            grid = ip.getMatrix(in);
                        } catch (IOException io) {
                            alertView.setText("There was an error during the loading phase...");
                        } catch (IllegalStateException is) {
                            alertView.setText("The app couldn't detect 25 cells from the loaded image...");
                        }

                        clearGrid();
                        showGrid();
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }

}