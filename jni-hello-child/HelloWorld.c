#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include "HelloWorld.h"


jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  printf("System.loadLibrary get's called! \n");
  cached_vm = vm;
  return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL 
Java_HelloWorld_goNative(JNIEnv *env, jobject obj)
{
  printf("I have gone native - PID %i \n", getpid());

  pid_t pid;
  pid = fork();

  if (pid == 0) {
    printf("Child will go Java - PID %i \n", getpid());

    JNIEnv *jni_env;
    jclass class;
    jmethodID method;
    (*cached_vm)->AttachCurrentThread(cached_vm,(void**) &jni_env, NULL);
    class = (*jni_env)->FindClass(jni_env, "HelloWorld");
    method = (*jni_env)->GetStaticMethodID(jni_env, class, "goJava", "()V");

    (*jni_env)->CallStaticVoidMethod(jni_env, class, method);

  } else {

      int status;
      waitpid(pid, &status, 0);
  }

  return;
}
