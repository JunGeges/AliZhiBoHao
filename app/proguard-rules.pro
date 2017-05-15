# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Bingo/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn javax.annotation.**
-keep class com.alibaba.**
-keepclassmembers class com.alibaba.** {
    *;
}

-dontobfuscate
-ignorewarnings


-keep class * extends com.duanqu.qupai.jni.ANativeObject
-keep @com.duanqu.qupai.jni.AccessedByNative class *
-keep class com.duanqu.qupai.bean.DIYOverlaySubmit
-keepclassmembers @com.duanqu.qupai.jni.AccessedByNative class * {
    *;
}
-keepclassmembers class * {
    @com.duanqu.qupai.jni.AccessedByNative *;
}
-keepclassmembers class * {
    @com.duanqu.qupai.jni.CalledByNative *;
}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclassmembers class * {
    native <methods>;
}

-keepclassmembers class com.duanqu.qupai.** {
    *;
}
