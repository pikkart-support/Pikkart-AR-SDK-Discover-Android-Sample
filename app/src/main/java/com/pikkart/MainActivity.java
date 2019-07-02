package com.pikkart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.pikkart.ar.recognition.IRecognitionListener;
import com.pikkart.ar.recognition.RecognitionOptions;
import com.pikkart.ar.recognition.data.CloudRecognitionInfo;
import com.pikkart.ar.recognition.items.Marker;
import com.pikkart.discover.IDiscoverListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, IDiscoverListener, IRecognitionListener {
    private int m_permissionCode = 100; // unique permission request code
    private com.pikkart.discover.DiscoverFragment _discoverFragment;
    private String m_Model_Name = "model.dat"; // Name of Discover's model
    private CameraTextureView _textureView;
    private InterestPointView _ipView;
    Handler _ipdraw_handler;


    private void initLayout() {
        setContentView(R.layout.activity_main);
        doRecognition();
    }

    public void doRecognition() {
        if(_discoverFragment==null) {
            _discoverFragment = ((com.pikkart.discover.DiscoverFragment) getFragmentManager().findFragmentById(R.id.discofragment));
            boolean loaded =  _discoverFragment.LoadDiscoverModel(m_Model_Name);
            if(loaded)
                _discoverFragment.StartDiscover(this);
            else
                _discoverFragment.StopDiscover();
            _discoverFragment.getView().setOnTouchListener(this);
        }

        _discoverFragment.startRecognition(
                new RecognitionOptions(
                        RecognitionOptions.RecognitionStorage.LOCAL,
                        RecognitionOptions.RecognitionMode.CONTINUOUS_SCAN,
                        new CloudRecognitionInfo(new String[]{})
                ),
                this);

        _textureView = new CameraTextureView(this);
        _ipView = new InterestPointView(this);

        ((ViewGroup)_discoverFragment.getView()).addView(_textureView);
        ((ViewGroup)_discoverFragment.getView()).addView(_ipView);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        _ipView.camera_orientation = _discoverFragment.getCameraOrientation();
        _ipView.device_orientation = display.getRotation();
        _ipView.changeOrientation();

        _ipdraw_handler = new Handler();
        _ipdraw_handler.post(runnableCode);
    }

    private void checkPermissions(int code) {
        String[] permissions_required = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE };

        List permissions_not_granted_list = new ArrayList<>();
        for (String permission : permissions_required) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_not_granted_list.add(permission);
            }
        }
        if (permissions_not_granted_list.size() > 0) {
            String[] permissions = new String[permissions_not_granted_list.size()];
            permissions_not_granted_list.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, code);
        }
        else {
            initLayout();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode==m_permissionCode) {
            boolean ok = true;
            for(int i=0;i<grantResults.length;++i) {
                ok = ok && (grantResults[i]==PackageManager.PERMISSION_GRANTED);
            }
            if(ok) {
                initLayout();
            }
            else {
                Toast.makeText(this, "Error: required permissions not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if not Android 6+ run the app
        if (Build.VERSION.SDK_INT < 23) {
            initLayout();
        }
        else {
            checkPermissions(m_permissionCode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(_discoverFragment!=null) {
            _discoverFragment.StartDiscover(this);
            if (_ipdraw_handler == null)
                _ipdraw_handler = new Handler();
            _ipdraw_handler.post(runnableCode);
        }
        if(_textureView!=null)
            _textureView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(_discoverFragment!=null) {
            _discoverFragment.StopDiscover();
        }
        if(_textureView!=null)
            _textureView.onPause();
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if(_ipView!=null) {
                _ipView.invalidate();
                _ipdraw_handler.postDelayed(runnableCode, 30);
            }
        }
    };

    @Override
    public void InterestPointFound(int i) {

    }

    @Override
    public void InterestPointLost(int i) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if(_ipView != null) {
            _ipView.invalidate();
            _ipView.device_orientation = display.getRotation();
            _ipView.camera_orientation = _discoverFragment.getCameraOrientation();
            // Change orientation
            _ipView.changeOrientation();
        }
    }

    @Override
    public void executingCloudSearch() {

    }

    @Override
    public void cloudMarkerNotFound() {

    }

    @Override
    public void internetConnectionNeeded() {

    }

    @Override
    public void markerFound(Marker marker) {

    }

    @Override
    public void markerNotFound() {

    }

    @Override
    public void markerTrackingLost(String s) {

    }

    @Override
    public void ARLogoFound(String s, int i) {

    }

    @Override
    public void markerEngineToUpdate(String s) {

    }

    @Override
    public boolean isConnectionAvailable(Context context) {
        return false;
    }
}
