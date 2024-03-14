package com.example.exp_24_mad;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTurnOn = findViewById(R.id.btnTurnOn);
        Button btnGetVisible = findViewById(R.id.btnGetVisible);
        Button btnListDevices = findViewById(R.id.btnListDevices);
        Button btnTurnOff = findViewById(R.id.btnTurnOff);
        ListView lvDevices = findViewById(R.id.lvDevices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pairedDevices);
        lvDevices.setAdapter(arrayAdapter);

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });

        btnGetVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeVisible();
            }
        });

        btnListDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPairedDevices();
            }
        });

        btnTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBluetooth();
            }
        });
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.BLUETOOTH_CONNECT) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.BLUETOOTH_CONNECT},1);
                return;
            }
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    private void makeVisible() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth must be turned on to make the device visible",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.BLUETOOTH_ADVERTISE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(discoverableIntent);
    }

    private void listPairedDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth must be turned on to list paired devices",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.BLUETOOTH_CONNECT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
        pairedDevices.clear();
        if (pairedDeviceSet.size() > 0) {
            for (BluetoothDevice device : pairedDeviceSet) {
                pairedDevices.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevices.add("No paired devices found");
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void disableBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.disable();
        }
    }
}