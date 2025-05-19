package com.dtt.dynamicfeature;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dtt.face.FaceEngine;
import com.dtt.face.FaceSDKClass;
import com.dtt.face.MyFaceKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity {
    private static final int LAUNCH_SECOND_ACTIVITY = 2;
    Button btnVerify;
    Activity mActivity;
    ImageView ivSelfie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamic);

        mActivity = MainActivity.this;

        try {
            FaceEngine.createInstance(mActivity).init();
        } catch (Exception e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnVerify = findViewById(R.id.btnVerify);
        ivSelfie = findViewById(R.id.iv_real_image);
        btnVerify.setOnClickListener(v -> {
            FaceSDKClass faceSDKClass = new FaceSDKClass();
            faceSDKClass.launchCameraActivity(mActivity, LAUNCH_SECOND_ACTIVITY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {

                String result = data.getStringExtra(MyFaceKey.INTENT_RESULT);


                if (MyFaceKey.RESULT_SUCCESS.equals(result)) {

                    String resultPath = data.getStringExtra(MyFaceKey.INTENT_MESSAGE);
                    if (resultPath != null){
                        Bitmap bitmap=getImageBitmap(mActivity, "imageDir", "USER_SELFIE.jpg");
                        ivSelfie.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(mActivity, "Result is getting empty please check", Toast.LENGTH_SHORT).show();
                    }



                } else if (MyFaceKey.RESULT_FAIL.equals(result)) {

                    String message = data.getStringExtra(MyFaceKey.INTENT_MESSAGE);

                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(mActivity, "Cancelled by user.", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(mActivity, "Unknown result.", Toast.LENGTH_SHORT).show();

            }
        }
    }
  /*  public static Bitmap convertToBitmap(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }*/

    public Bitmap getImageBitmap(Context mContext, String photoPath, String fileName) {
        Bitmap mBitmap = null;
        File mDirectory = getBaseDirectoryFromPathString(photoPath, mContext);
        File file = new File(mDirectory, fileName);
        try {
            mBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {


        }
        return mBitmap;
    }
    public File getBaseDirectoryFromPathString(String mPath, Context mContext) {
        ContextWrapper mContextWrapper = new ContextWrapper(mContext);
        return mContextWrapper.getDir(mPath, 0);
    }

}