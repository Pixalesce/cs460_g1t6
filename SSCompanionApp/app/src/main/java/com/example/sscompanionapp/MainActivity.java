package com.example.sscompanionapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.sscompanionapp.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import android.content.DialogInterface;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Notification permission granted");
                } else {
                    Log.w(TAG, "Notification permission denied");
                }
            });

    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice piDevice;
    private BluetoothSocket socket;
    private BluetoothLeScanner bluetoothLeScanner;
    private TextView statusTextView;
    private final String piName = "raspberryp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Check and request notification permission for Android 13+ (including Android 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }


        // Retrieve FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // You can log or send the token to your server for testing
                });

        statusTextView = findViewById(R.id.statusTextView);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        checkPermissions();
        scanForDevices();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                }, PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi();
            if (device.getName() == null) {
                return;
            }
            Log.d(TAG, "Found device: " + device.getName() + "Address: " + device.getAddress() + " with RSSI: " + rssi);

            if (device.getName().equals(piName)) {
                double distance = calculateDistance(rssi);
                statusTextView.setText("Raspberry Pi found! Approximate distance: " + distance + " meters");
            }
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                int rssi = result.getRssi();
                if (device.getName().equals(piName)) {
                    double distance = calculateDistance(rssi);
                    statusTextView.setText("Raspberry Pi found! Approximate distance: " + distance + " meters");
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Bluetooth scan failed with error code: " + errorCode);
        }
    };

    private void scanForDevices() {
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
        Log.d(TAG, "Bluetooth is not enabled.");
        statusTextView.setText("Bluetooth is not enabled. Please enable Bluetooth.");
        return;
    }

    bluetoothLeScanner.startScan(scanCallback);
}


    private double calculateDistance(int rssi) {
        int txPower = -59; // Assumed RSSI at 1 meter distance
        return Math.pow(10, (txPower - rssi) / (10 * 2.0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanForDevices();
            } else {
                statusTextView.setText("Permission denied. Cannot scan for Bluetooth devices.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionRequestDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("This app needs notification permissions to keep you informed about important updates. Please allow notifications.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        }
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w(TAG, "User denied the notification permission.");
                    }
                })
                .show();
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth turned off");
                        statusTextView.setText("Bluetooth is off. Please enable Bluetooth.");
                        // Stop scanning when Bluetooth is off
                        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                            if (bluetoothLeScanner != null) {
                                bluetoothLeScanner.stopScan(scanCallback);
                            }
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Bluetooth turning on...");
                        statusTextView.setText("Turning Bluetooth on...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Bluetooth is on");
                        statusTextView.setText("Bluetooth is on. Scanning for devices...");
                        // Ensure the adapter is non-null and start scanning
                        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                            scanForDevices();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Bluetooth turning off...");
                        statusTextView.setText("Turning Bluetooth off...");
                        break;
                    default:
                        Log.e(TAG, "Unknown Bluetooth state: " + state);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
    }
}