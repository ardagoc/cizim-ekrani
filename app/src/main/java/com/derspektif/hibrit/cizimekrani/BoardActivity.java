package com.derspektif.hibrit.cizimekrani;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.derspektif.hibrit.cizimekrani.view.ViewPort;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BoardActivity extends AppCompatActivity {

    Context context = BoardActivity.this;
    private final int PICK_IMAGE = 1;

    @Bind(R.id.viewPort) ViewPort viewPort;
    @Bind(R.id.clearButton) Button clearButton;
    @Bind(R.id.menuButton) Button menuButton;
    @Bind(R.id.pickImageButton) Button pickImageButton;
    @Bind(R.id.menuLayout) LinearLayout menuLayout;
    @Bind(R.id.menuColorItem1) Button menuColorItem1;
    @Bind(R.id.menuColorItem2) Button menuColorItem2;
    @Bind(R.id.menuColorItem3) Button menuColorItem3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.board_layout);
        ButterKnife.bind(this);

        GradientDrawable gradientDrawable;
        gradientDrawable = (GradientDrawable) menuColorItem1.getBackground();
        gradientDrawable.setColor(getResources().getColor(R.color.color1));
        gradientDrawable = (GradientDrawable) menuColorItem2.getBackground();
        gradientDrawable.setColor(getResources().getColor(R.color.color2));
        gradientDrawable = (GradientDrawable) menuColorItem3.getBackground();
        gradientDrawable.setColor(getResources().getColor(R.color.color3));

    }

    @OnClick(R.id.clearButton)
    public void clearPage() {
        viewPort.clearCanvas();
    }

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
        menuLayout.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.menuColorItem1, R.id.menuColorItem2, R.id.menuColorItem3})
    public void changeColor(Button menuItem) {

        String tagColor = (String) menuItem.getTag();
        Log.d("menu_item", tagColor);
        viewPort.changeDrawColor(getResources().getIdentifier(tagColor, "color", getPackageName()));

        //if (menuItem.getId() == R.id.menuColorItem1) {
        //}
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
