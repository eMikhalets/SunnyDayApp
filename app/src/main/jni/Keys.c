#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_emikhalets_sunnydayapp_utils_Keys_getApiKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "e1a0feec25754f7aa615945766b156b");
//    return (*env)->  NewStringUTF(env, "e1a0feec25754f7aa615945766b156b6");
}