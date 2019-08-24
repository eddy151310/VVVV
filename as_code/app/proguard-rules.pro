
-optimizationpasses 5  #指定代码的压缩级别 0 - 7，一般都是5，无需改变-----------
-dontusemixedcaseclassnames #不使用大小写混合-----------
#告诉Proguard 不要跳过对非公开类的处理，默认是跳过
-dontskipnonpubliclibraryclasses #如果应用程序引入的有jar包，并且混淆jar包里面的class-----------
-verbose #混淆时记录日志（混淆后生产映射文件 map 类名 -> 转化后类名的映射
#指定混淆时的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般也不会改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*-----------
#类型转换错误 添加如下代码以便过滤泛型（不写可能会出现类型转换错误，一般情况把这个加上就是了）,即避免泛型被混淆
-keepattributes Signature
#抛出异常时保留代码行数
-keepattributes SourceFile,LineNumberTable
-dontpreverify
-dontwarn
-dontnote
# 抑制警告
-ignorewarnings

#v4包下的文件都不要混淆 -dontwarn   如果有警告也不终止
-dontwarn android.support.v4.**
-keep class android.support.v4.app.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**

-keep public class * extends android.app.Fragment  #所有fragment的子类不要去混淆
-keep public class * extends android.app.Activity  #所有activity的子类不要去混淆
-keep public class * extends android.app.Application #用法同上
-keep public class * extends android.app.Service #用法同上
-keep public class * extends android.content.BroadcastReceiver #用法同上
-keep public class * extends android.content.ContentProvider #用法同上
-keep public class * extends android.app.backup.BackupAgentHelper #用法同上
-keep public class * extends android.preference.Preference #用法同上
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes Exceptions,InnerClasses

#保持指定规则的方法不被混淆（Android layout 布局文件中为控件配置的onClick方法不能混淆）
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保持自定义控件指定规则的方法不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持枚举 enum 不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持 Parcelable 不被混淆（aidl文件不能去混淆）
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keepnames class * implements java.io.Serializable
#保护实现接口Serializable的类中，指定规则的类成员不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}


#保持R文件不被混淆，否则，你的反射是获取不到资源id的
-keep class **.R$* { *; }

-keep class com.android.support.**{*;}
-dontwarn com.android.support.**

-keep class com.google.android.gms.**{*;}
-dontwarn com.google.android.gms.**

-keep class com.google.firebase.**{*;}
-dontwarn com.google.firebase.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#防止自定义相机相关代码被混淆
-keep class com.ahdi.lib.utils.takephoto.widget.**{
  public <fields>;
  public <methods>;
}

#umeng
-keep class com.umeng.** {*;}