package io.github.some_dropout.usb_test;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("app_jni");
  }

  private native int nativeTryOpen(String path);

  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.main_activity);
  }
}
