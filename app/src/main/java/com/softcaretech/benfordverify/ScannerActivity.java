package com.softcaretech.benfordverify;

import static com.auth0.jwt.RegisteredClaims.ISSUER;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class ScannerActivity extends AppCompatActivity {
    String key = "softcaretech";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private SurfaceView cameraPreview;
    private TextView qrCodeText;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        cameraPreview = findViewById(R.id.cameraPreview);
        qrCodeText = findViewById(R.id.qrCodeText);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraSource();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startCameraSource() {
        qrCodeText.setText("Scanning...");
        cameraSource = new CameraSource(this, cameraPreview, new CameraSource.FrameProcessor() {
            @Override
            public void processFrame(byte[] data, int width, int height) {
                InputImage image = InputImage.fromByteArray(data, width, height, 0, InputImage.IMAGE_FORMAT_NV21);
                BarcodeScanner scanner = BarcodeScanning.getClient();
                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            if (!barcodes.isEmpty()) {
                                try {
                                    String token = barcodes.get(0).getRawValue();
                                     DecodedJWT decodedJWT = JWTUtils.verifyToken(token);
                                     if(ISSUER.equals(decodedJWT.getIssuer()) ){
                                         showMsg("Verification pass",  decodedJWT.getSubject() );
                                         qrCodeText.setText("Verification pass: "+ decodedJWT.getSubject());
                                     }else{
                                         showMsg("Verification failed", "Invalid token" );
                                         qrCodeText.setText("Verification failed");
                                     }

                                } catch (Exception e) {
                                    Log.e("JWT", "Verification failed", e);
                                    showMsg("Verification failed", "Invalid signature/claims" );
                                    qrCodeText.setText("Verification failed");
                                }
                                cameraSource.stop();
                            } else {
                                qrCodeText.setText("No QR code found.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            qrCodeText.setText("Scanning failed.");
                        });
            }
        });

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                cameraSource.start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraSource();
            } else {
                qrCodeText.setText("Camera permission is required.");
                showPermissionDialog();

            }
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Camera Permission")
                .setMessage("This app needs camera permission to scan QR codes. Please grant the permission.")
                .setPositiveButton("Request Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ScannerActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void showResult(String t, String m) {
        new AlertDialog.Builder(this)
                .setTitle(t)
                .setMessage(m)
                .setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         startCameraSource();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }


    private void showMsg(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}