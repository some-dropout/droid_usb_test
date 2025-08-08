#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

#include "io_github_some_dropout_usb_test_MainActivity.h"

JNIEXPORT jint JNICALL
Java_io_github_some_1dropout_usb_1test_MainActivity_nativeTryOpen (JNIEnv *env, jobject thiz, jstring path)
{
  const char *c_path;
  int num;

  (void)thiz;

  c_path = (*env)->GetStringUTFChars (env, path, NULL);
  num = open (c_path, O_RDWR);
  (*env)->ReleaseStringUTFChars (env, path, c_path);
  if (num < 0)
    return -errno;

  close (num);

  return num;
}
