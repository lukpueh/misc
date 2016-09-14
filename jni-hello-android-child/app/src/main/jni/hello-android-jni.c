#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>

#include <Python.h>


# define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))

static PyObject *android_log(PyObject *self, PyObject *args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "s", &logstr)) {
        return NULL;
    }
    LOGI(logstr);
    Py_RETURN_NONE;
}

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

static PyMethodDef AndroidLogMethods[] = {
        {"log", android_log, METH_VARARGS, "Log on android platform"},
        {NULL, NULL, 0, NULL}};

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


    // Nasty-sleep for some seconds ...
//    int i;
//    for (i = 0; i < 5; i++) {
//        LOGI("%i. (pid  %i)", i, getpid());
//        (*env)->CallVoidMethod(env, instance, nasty_method);
//        if ((*env)->ExceptionOccurred(env)){
//            LOGI("Exception occurred while being nasty.");
//        }
//        sleep(1);
//    }

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





    int i;
    for (i = 0; i < 5; i++) {
        LOGI( "PyRun returns %i\n", Verbose_PyRun_SimpleString(
                "import sys\n" \
                "print sys.path\n" \
                "import os\n" \
                "print os.getpid()\n" \
                "try:\n" \
                "  import ctypes\n" \
                "except Exception, e:\n" \
                "  print 'Could not import ctypes'\n" \
                "else:\n" \
                "  print 'Imported ctypes'\n" \
                "\n" \
                "\n" \
                "try:\n" \
                "  import _ctypes\n" \
                "except Exception, e:\n" \
                "  print 'Could not import _ctypes'\n" \
                "else:\n" \
                "  print 'Imported _ctypes'\n"
                "print 'the larch'"));
//                "print 'the larch, THEEE LAAARCH'\n"\
//            "import os\n"\
//            "print 'PID: ' + str(os.getpid())"));
    }


    // ... then start new Service, i.e. fork
    (*env)->CallVoidMethod(env, instance, service_method);
    if ((*env)->ExceptionOccurred(env)){
        LOGI("Exception occurred while 'forking'.");
    }
}

