package com.tylinux.fuckoppo;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    final private static String packageInstallInterceptManagerClassNameBeforeCOS12 = "com.android.server.pm.ColorPackageInstallInterceptManager";
    final private static String packageInstallInterceptManagerClassName = "com.android.server.pm.OplusPackageInstallInterceptManager";

    final private static String usbServiceClassBeforeCOS12 = "com.coloros.systemui.notification.usb.UsbService";

    final private static String usbServiceClass = "com.oplusos.systemui.notification.usb.UsbService";

    final private static String usbServiceClassCOS14 = "com.oplus.systemui.usb.UsbService";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 去掉 USB 安装应用校验
        if (lpparam.packageName.equals("android")) {
            this.hookOppoUSBInstallDialog(lpparam);
        }

        // 自动开启 oppo usb 调试
        ClassLoader classLoader = lpparam.classLoader;
        if (lpparam.packageName.equals("com.android.systemui")) {
            this.hookOppoUSBModeDialog(lpparam);
        }
    }

    private void hookOppoUSBInstallDialog(XC_LoadPackage.LoadPackageParam lpparam) {
        ClassLoader classLoader = lpparam.classLoader;
        XposedBridge.log("Patch oppo usb alert START");
        // check if the class exists
        String className = getExistsClassName(classLoader, packageInstallInterceptManagerClassNameBeforeCOS12, packageInstallInterceptManagerClassName);

        if (className == null) {
            XposedBridge.log("Hook oppo usb alert failed, please check the class name");
            return;
        }

        XposedBridge.log("Found class: " + className);
        XposedHelpers.findAndHookMethod(className, classLoader, "allowInterceptAdbInstallInInstallStage", int.class, "android.content.pm.PackageInstaller$SessionParams", "java.io.File", "java.lang.String", "android.content.pm.IPackageInstallObserver2", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(false);
            }
        });
        XposedBridge.log("Patch oppo usb alert END");
    }

    private void hookOppoUSBModeDialog(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Patch oppo usb dialog START");
        ClassLoader classLoader = lpparam.classLoader;

        // check if the class exists
        String className = getExistsClassName(classLoader, usbServiceClassBeforeCOS12, usbServiceClass, usbServiceClassCOS14);

        if (className == null) {
            XposedBridge.log("Hook oppo usb dialog failed, please check the class name");
            return;
        }

        XposedBridge.log("Found class: " + className);
        XposedHelpers.findAndHookMethod(className, classLoader, "onUsbConnected", Context.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Object thiz = param.thisObject;
                try {
                    Field field = thiz.getClass().getField("sNeedShowUsbDialog");
                    if (field != null) {
                        field.set(thiz, false);
                        XposedBridge.log("Set sNeedShowUsbDialog to false");
                    }
                } catch (NoSuchFieldException e) {
                    XposedBridge.log("Field not found: " + e.getMessage());
                }
                    // for ColorOS 14
                try {
                    Field field = thiz.getClass().getDeclaredField("mNeedShowUsbDialog");
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(thiz, false);
                        XposedBridge.log("Set mNeedShowUsbDialog to false");
                    }
                } catch (NoSuchFieldException e) {
                    XposedBridge.log("Field not found: " + e.getMessage());
                }


                // update Notification
                Method method = thiz.getClass().getMethod("updateUsbNotification", Context.class, int.class);
                if (method != null) {
                    method.invoke(thiz, (Context) param.args[0], 1);
                    XposedBridge.log("Call updateUsbNotification");
                }
                return null;
            }
        });
        XposedBridge.log("Patch oppo usb dialog END");
    }

    private String getExistsClassName(ClassLoader classLoader, String ...classNames) {
        for (String className : classNames) {
            try {
                XposedHelpers.findClass(className, classLoader);
                return className;
            } catch (XposedHelpers.ClassNotFoundError e) {
                XposedBridge.log("Class not found: " + className);
            }
        }
        return null;
    }
}

