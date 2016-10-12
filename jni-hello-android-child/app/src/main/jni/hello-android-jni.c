#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>
#include <pthread.h>

#include <Python.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))
int Verbose_PyRun_SimpleString(const char *code) {
    // Used for globals and locals
    PyObject *module = PyImport_AddModule("__main__");
    PyObject *d = PyDict_New();
    if (module == NULL)
        return -1;
    d = PyModule_GetDict(module);
    PyRun_StringFlags(code, Py_file_input, d, d, NULL);
    if (PyErr_Occurred()) {
        PyObject *errtype, *errvalue, *traceback;
        PyObject *errstring = NULL;

        PyErr_Fetch(&errtype, &errvalue, &traceback);

        if(errtype != NULL) {
            errstring = PyObject_Str(errtype);
            LOGI("Errtype: %s\n", PyString_AS_STRING(errstring));
        }
        if(errvalue != NULL) {
            errstring = PyObject_Str(errvalue);
            LOGI("Errvalue: %s\n", PyString_AS_STRING(errstring));
        }

        Py_XDECREF(errvalue);
        Py_XDECREF(errtype);
        Py_XDECREF(traceback);
        Py_XDECREF(errstring);
    }
    return 0;
}

JavaVM* cached_vm;
pthread_key_t mThreadKey;


jclass the_class;
jmethodID the_method;

static PyObject *android_log(PyObject *self, PyObject *args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "s", &logstr)) {
        return NULL;
    }
    LOGI(logstr);
    Py_RETURN_NONE;
}
JNIEnv *get_jni_env(void) {
    JNIEnv *env;

    int status = (*cached_vm)->AttachCurrentThread(cached_vm, &env, NULL);
    if(status < 0) {
        LOGI("failed to attach current thread");
        return 0;
    }
    pthread_setspecific(mThreadKey, (void*) env);

    return env;
}

PyObject* android_test(PyObject *self, PyObject *args) {
    JNIEnv* jni_env = get_jni_env();
    (*jni_env)->CallStaticVoidMethod(jni_env, the_class, the_method);
    Py_RETURN_NONE;
}



static void detach_current_thread(void *value) {
    JNIEnv *env = (JNIEnv*) value;
    if (env != NULL) {
        (*cached_vm)->DetachCurrentThread(cached_vm);
        pthread_setspecific(mThreadKey, NULL);
    }
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("############################ ONLOAD IS CALLED");
    cached_vm = vm;

    if (pthread_key_create(&mThreadKey, detach_current_thread) != 0) {
        LOGI( "Error initializing pthread key");
    }

    JNIEnv* jni_env = get_jni_env();

    jclass the_class_local  = (*jni_env)->FindClass(jni_env, "com/example/lukp/helloandroidjni/MyService");
    the_class = (jclass)(*jni_env)->NewGlobalRef(jni_env, the_class_local);
    the_method = (*jni_env)->GetStaticMethodID(jni_env, the_class, "test", "()V");
    return JNI_VERSION_1_6;
}

static PyMethodDef AndroidLogMethods[] = {
        {"log", android_log, METH_VARARGS, "Log on android platform"},
        {"test", android_test, METH_VARARGS, "test"},
        {NULL, NULL, 0, NULL}};



JNIEXPORT void JNICALL
Java_com_example_lukp_helloandroidjni_MyService_goNative(JNIEnv *env, jobject instance) {

    // And now for something completely different (Python)
    Py_SetPythonHome("/data/user/0/com.google.sample.helloandroidjni/files/python/");

    Py_Initialize();
    PyEval_InitThreads();
    Py_InitModule("androidlog", AndroidLogMethods);

    LOGI("PyRun returns %i\n", Verbose_PyRun_SimpleString(
            "import sys\n" \
            "import androidlog\n" \
            "class LogFile(object):\n" \
            "    def __init__(self):\n" \
            "        self.buffer = ''\n" \
            "    def write(self, s):\n" \
            "        s = self.buffer + s\n" \
            "        lines = s.split(\"\\n\")\n" \
            "        for l in lines[:-1]:\n" \
            "            androidlog.log(l)\n" \
            "        self.buffer = lines[-1]\n" \
            "    def flush(self):\n" \
            "        return\n" \
            "sys.stdout = sys.stderr = LogFile()"));


    LOGI( "PyRun returns %i\n", Verbose_PyRun_SimpleString(
          "import androidlog\n" \
          "import time\n" \
          "from threading import Thread\n" \
          "def myfunc():\n" \
          "    androidlog.test()\n" \
          "t = Thread(target=myfunc)\n" \
          "t.start()\n" \
          "time.sleep(10)"));

}

