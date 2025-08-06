// C Program to Use Print Hello World
#include <jni.h>
#include <stdio.h>
#include "GFG.h"

// Implementation of the native method print_Hello()
JNIEXPORT void JNICALL Java_GFG_print_1Hello(JNIEnv *env, jobject obj) {
   printf("Hello World!\n");
   return;
}