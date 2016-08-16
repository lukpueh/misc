#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>


# define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))


JNIEXPORT jstring JNICALL
Java_com_example_lukp_helloandroidjni_MyService_getMsgFromJni(JNIEnv *env, jobject instance) {

    LOGI("Hellloow sister native");

    jclass class = (*env)->FindClass(env, "com/example/lukp/helloandroidjni/MyService");
    jmethodID method = (*env)->GetStaticMethodID(env, class, "callFromJni", "()V");
    (*env)->CallStaticVoidMethod(env, class, method);

    return (*env)->NewStringUTF(env, "Hello From Jni");
}