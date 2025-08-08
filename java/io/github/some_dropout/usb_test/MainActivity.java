package io.github.some_dropout.usb_test;

import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("app_jni");
  }

  private static final String ACTION_USB_PERM = "io.github.some_dropout.usb_test.USB_PERMISSION";

  private Button mBtn;
  private UsbManager mUsbMan;
  private EditText mOut;
  private BroadcastReceiver mReceiver;

  private native int nativeTryOpen(String path);

  private void initReceiver()
  {
    IntentFilter filter;

    filter = new IntentFilter();
    filter.addAction(ACTION_USB_PERM);

    mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context ctx, Intent intent)
      {
        String action;

        action = intent.getAction();
        if (action.equals(ACTION_USB_PERM))
        {
          boolean granted;

          granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
          mOut.append(String.format("User %s permission request.\n", granted ? "granted" : "rejected"));
          if (!granted)
          {
            mBtn.setEnabled(true);
            return;
          }
        }
      }
    };

    registerReceiver(mReceiver, filter, RECEIVER_NOT_EXPORTED);
  }

  private void requestPermission(UsbDevice dev)
  {
    PendingIntent intent;

    intent = PendingIntent.getBroadcast(this, 0,
                                        new Intent(ACTION_USB_PERM).setPackage(getPackageName()),
                                        PendingIntent.FLAG_MUTABLE);
    mUsbMan.requestPermission(dev, intent);
  }

  @Override
  public boolean onKeyDown(int key, KeyEvent ev)
  {
    if (key == KeyEvent.KEYCODE_BACK)
      finish();

    return super.onKeyDown(key, ev);
  }

  @Override
  public void onCreate(Bundle bundle)
  {
    ClipboardManager clipMan;
    CheckBox chkBox;

    super.onCreate(bundle);
    setContentView(R.layout.main_activity);

    mBtn = findViewById(R.id.btn);
    mUsbMan = (UsbManager)getSystemService(Context.USB_SERVICE);
    mOut = findViewById(R.id.out);
    clipMan = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    chkBox = findViewById(R.id.chkbox);

    mBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        HashMap<String, UsbDevice> usbs;
        int count;
        String firstPath;
        UsbDevice firstDev;
        Boolean firstHasPerm;

        mBtn.setEnabled(false);

        usbs = mUsbMan.getDeviceList();
        count = usbs.size();

        if (count == 0)
        {
          mOut.append("No connected device.\n");
          mBtn.setEnabled(true);
          return;
        }

        mOut.append("Connected device(s): ");
        firstPath = null;
        firstDev = null;
        firstHasPerm = null;
        for (HashMap.Entry<String, UsbDevice> ent : usbs.entrySet())
        {
          String path;
          UsbDevice dev;
          Boolean hasPerm;

          path = ent.getKey();
          dev = ent.getValue();
          hasPerm = Boolean.valueOf(mUsbMan.hasPermission(dev));

          if (firstPath == null)
            firstPath = path;
          if (firstDev == null)
            firstDev = dev;
          if (firstHasPerm == null)
            firstHasPerm = hasPerm;

          mOut.append(String.format("%s (%s) ", path, hasPerm.booleanValue() ? "Granted" : "Not granted"));
        }
        mOut.append("\n");

        if (count > 1)
        {
          mOut.append("More than one device connected, abort.\n");
          mBtn.setEnabled(true);
          return;
        }

        if (!firstHasPerm || chkBox.isChecked())
        {
          requestPermission(firstDev);
          return;
        }
      }
    });

    findViewById(R.id.copie).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        ClipData data;

        data = ClipData.newPlainText("", mOut.getText());
        clipMan.setPrimaryClip(data);
      }
    });

    findViewById(R.id.wiper).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        mOut.setText("");
      }
    });

    initReceiver();
  }

  @Override
  public void onDestroy()
  {
    mBtn = null;
    mUsbMan = null;
    mOut = null;
    unregisterReceiver(mReceiver);
    mReceiver = null;

    super.onDestroy();
  }
}
