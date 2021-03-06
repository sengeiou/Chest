# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn org.apache.http.**

#集成bugly的异常统计功能4-3
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#集成umeng的移动统计功能5-5
#applied for umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.stur.lib.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn android.net.**
-keep class android.net.** { *;}
-dontwarn com.android.server.**
-keep class com.android.server.** { *;}




