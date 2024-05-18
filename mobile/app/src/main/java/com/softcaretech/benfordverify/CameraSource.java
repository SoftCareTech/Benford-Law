package com.softcaretech.benfordverify;



import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraSource {

    private Camera camera;
    private SurfaceView surfaceView;
    private FrameProcessor frameProcessor;

    public interface FrameProcessor {
        void processFrame(byte[] data, int width, int height);
    }

    public CameraSource(Context context, SurfaceView surfaceView, FrameProcessor frameProcessor) {
        this.surfaceView = surfaceView;
        this.frameProcessor = frameProcessor;
    }

    public void start() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.setPreviewCallback((data, camera) -> {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                frameProcessor.processFrame(data, size.width, size.height);
            });
            camera.startPreview();
        } catch (IOException e) {
            Log.e("CameraSource", "Error setting up preview display", e);
        }
    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }


}

