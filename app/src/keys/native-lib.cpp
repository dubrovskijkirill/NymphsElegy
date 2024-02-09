#include <jni.h>
#include <string>

extern "C" jobject Java_com_nymp_phselgy_feature_1load_Son_adrNativeValues(
        JNIEnv* env,
        jobject /* this */) {
    jclass mapClass = env->FindClass("java/util/HashMap");
    if(mapClass == NULL) {
        return NULL;
    }
    jmethodID init = env->GetMethodID(mapClass, "<init>", "(I)V");
    jmethodID put = env->GetMethodID(mapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    jsize map_len = 2;
    jobject hashMap = env->NewObject(mapClass, init, map_len);
    env->CallObjectMethod(hashMap, put, env->NewStringUTF("strong"), env->NewStringUTF("f5e6aadd-9f97-480f-b15e-a5301fd84a48"));
    env->CallObjectMethod(hashMap, put, env->NewStringUTF("knock"), env->NewStringUTF("hung"));
    env->CallObjectMethod(hashMap, put, env->NewStringUTF("blade"), env->NewStringUTF("bell"));

    return hashMap;
}


