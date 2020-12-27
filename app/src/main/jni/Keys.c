#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_emikhalets_sunnydayapp_utils_Keys_getApiKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "8fbaabbed7d7c1b4a221bac148d3328d");
}