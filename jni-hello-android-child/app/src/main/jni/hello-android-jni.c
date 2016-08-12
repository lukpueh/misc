#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>


# define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))


JNIEXPORT jstring JNICALL
Java_com_example_lukp_helloandroidjni_MainActivity_getMsgFromJni(JNIEnv *env, jobject instance) {

    LOGI("Hellloow sister native");

    pid_t pid;
    pid = fork();
    if (pid == 0) {

        LOGI("Child PID - %i", getpid());
        jclass class = (*env)->FindClass(env, "com/example/lukp/helloandroidjni/MainActivity");
        jmethodID method = (*env)->GetStaticMethodID(env, class, "callFromJni",
                                                     "()V");
        (*env)->CallStaticVoidMethod(env, class, method);

//        while (1) {
//            LOGI("Child busy sleeping forever");
//            sleep(5);
//        }

    } else {
        LOGI("Parent PID - %i", getpid());
        int status;
        waitpid(pid, &status, 0);
    }


    return (*env)->NewStringUTF(env, "Hello From Jni");
}