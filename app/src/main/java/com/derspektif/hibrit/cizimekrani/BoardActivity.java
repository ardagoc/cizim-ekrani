package com.derspektif.hibrit.cizimekrani;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.derspektif.hibrit.cizimekrani.view.CanvasView;
import com.derspektif.hibrit.cizimekrani.view.DrawableImageView;
import com.derspektif.hibrit.cizimekrani.view.ViewPort;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BoardActivity extends AppCompatActivity {

    Context context = BoardActivity.this;
    private final int PICK_IMAGE = 1;

    @Bind(R.id.canvasView)
    CanvasView canvasView;
    @Bind(R.id.viewPort)
    ViewPort viewPort;
//    @Bind(R.id.questionImage)
//    DrawableImageView questionImage;
    @Bind(R.id.menuButton)
    Button menuButton;
    @Bind(R.id.pickImageButton)
    Button pickImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.board_layout);
        ButterKnife.bind(this);
    }
/*
    @OnTouch(R.id.questionImage)
    public boolean drawOnImage(View v, MotionEvent event){
        DrawableImageView drawView = (DrawableImageView) v;

        // set start coords
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawView.left = event.getX();
            drawView.top = event.getY();
            // set end coords
        } else {
            drawView.right = event.getX();
            drawView.bottom = event.getY();
        }
        // draw
        drawView.invalidate();
        drawView.drawRect = true;

        return true;
    }*/

    @OnClick(R.id.pickImageButton)
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @OnClick(R.id.menuButton)
    public void clearContent() {
//        canvasView.clearCanvas();
        viewPort.setOnGestureMode(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                viewPort.setImageBitmap(bitmap);
                viewPort.setBitmap(context, bitmap);
                viewPort.setOnGestureMode(true);
                viewPort.clearCanvas();

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        canvasView.paintBitmap(bitmap);
//                    }
//                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


}
