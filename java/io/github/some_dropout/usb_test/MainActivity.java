package io.github.some_dropout.usb_test;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("app_jni");
  }

  private UsbManager mUsbMan;
  private EditText mOut;

  private native int nativeTryOpen(String path);

  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.main_activity);

    mUsbMan = (UsbManager)getSystemService(Context.USB_SERVICE);
    mOut = findViewById(R.id.out);

    findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        HashMap<String, UsbDevice> usbs;
        int count;
        String firstPath;
        UsbDevice firstDev;
        Boolean firstHasPerm;

        usbs = mUsbMan.getDeviceList();
        count = usbs.size();

        if (count == 0)
        {
          mOut.append("No connected device.\n");
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
          return;
        }
      }
    });
  }
}
