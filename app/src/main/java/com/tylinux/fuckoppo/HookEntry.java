package com.tylinux.fuckoppo;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * USB 安装应用校验来自：https://leadroyal.cn/p/1151/
 * 自动开启 oppo usb 调试 来自：https://www.52pojie.cn/thread-1258568-1-1.html
 */

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 去掉 USB 安装应用校验
        if (lpparam.packageName.equals("android")) {
            ClassLoader classLoader = lpparam.classLoader;
            XposedBridge.log("Patch oppo usb alert START");
            XposedHelpers.findAndHookMethod("com.android.server.pm.ColorPackageInstallInterceptManager", classLoader, "allowInterceptAdbInstallInInstallStage", int.class, "android.content.pm.PackageInstaller$SessionParams", "java.io.File", "java.lang.String", "android.content.pm.IPackageInstallObserver2", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(false);
                }
            });
            XposedBridge.log("Patch oppo usb alert END");
        }

        // 自动开启 oppo usb 调试
        ClassLoader classLoader = lpparam.classLoader;
        if (lpparam.packageName.equals("com.android.systemui")) {
            XposedBridge.log("Patch oppo usb dialog START");
            XposedHelpers.findAndHookMethod("com.coloros.systemui.notification.usb.UsbService", classLoader, "onUsbConnected", Context.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Object thiz = param.thisObject;
                    thiz.getClass().getField("sNeedShowUsbDialog").set(null, false);
                    thiz.getClass().getMethod("updateUsbNotification", Context.class, int.class).invoke(thiz, (Context) param.args[0], 1);
                    return null;
                }
            });
            XposedBridge.log("Patch oppo usb dialog END");
        }
    }
}
