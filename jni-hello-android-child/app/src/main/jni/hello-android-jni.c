#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>


# define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))


JNIEXPORT void JNICALL
Java_com_example_lukp_helloandroidjni_MyService_goNative(JNIEnv *env, jobject instance) {

    jclass service_class;
    jmethodID nasty_method;
    jmethodID service_method;

    // Find class and methods
    service_class = (*env)->FindClass(env, "com/example/lukp/helloandroidjni/MyService");
    nasty_method = (*env)->GetMethodID(env, service_class,
                                       "doNastyJavaStuffFromNative", "()V");
    service_method = (*env)->GetMethodID(env, service_class,
                                         "findAndStartIdleService", "()V");


    // Nasty-sleep for 10 seconds ...
    int i;
    for (i = 0; i < 5; i++) {
        LOGI("%i. (pid  %i)", i, getpid());
        (*env)->CallVoidMethod(env, instance, nasty_method);
        if ((*env)->ExceptionOccurred(env)){
            LOGI("Exception occurred while being nasty.");
        }
        sleep(1);
    }

    // ... then start new Service, i.e. fork
    (*env)->CallVoidMethod(env, instance, service_method);
    if ((*env)->ExceptionOccurred(env)){
        LOGI("Exception occurred while 'forking'.");
    }
}

