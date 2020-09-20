package com.eyecantcu.focus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    private static final String TAG = "CameraActivity";

    // Initialize OpenCVLoader
    static {
        if (!(OpenCVLoader.initDebug())) {
            Log.d(TAG, "  OpenCVLoader.initDebug(), working.");
        } else {
            Log.d(TAG, "  OpenCVLoader.initDebug(), not working.");
        }
    }

    JavaCameraView javaCameraView;
    Mat usedMat;

    // Override the BaseLoaderCallback-method from OpenCV, to manage initialization properly
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

        // Bypass installation of OpenCV Manager. We include the library within the app
        @Override
        public void onPackageInstall(final int operation, final InstallCallbackInterface callback) {
            switch (operation) {
                case InstallCallbackInterface.NEW_INSTALLATION: {
                    Log.i(TAG, "Tried to install OpenCV Manager package, but I still don't believe that you need it...");
                    break;
                }
                default: {
                    super.onPackageInstall(operation, callback);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        javaCameraView = findViewById(R.id.cam_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallBack)) {
            Log.e(TAG, "  OpenCVLoader.initAsync(), not working.");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "  OpenCVLoader.initAsync(), working.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallBack);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        usedMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        javaCameraView.disableView();
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        usedMat = inputFrame;
        return usedMat;
    }
}
