# 移除 OPPO USB 模式选择弹窗及 ADB 安装应用限制

> 核心原理及代码全部来自：https://www.52pojie.cn/thread-1258568-1-1.html 和 https://leadroyal.cn/p/1151/ ，此 repo 仅做整合。

## 主要功能：

1. 移除 USB 模式选择弹窗，默认选择文件管理以使能 adb
2. 移除通过 USB 安装应用时的检查页面及要求登录账号的功能

## 使用方法：

1. 安装 LSPosed 后，安装 release 页面提供的 apk，在 LSPosed 管理页面启用本模块后，重启系统即可。
2. (可选)因安装无 activity 的应用时会要求登录账户，因此默认的 apk 有一个Demo页面和桌面图标，如果不想要，可以在重启系统后再次安装 **-no-icon** 版本。


# [ENG] Remove OPPO ColorOS USB mode select modal and limit for install apk with adb.

> All core code from https://www.52pojie.cn/thread-1258568-1-1.html 和 https://leadroyal.cn/p/1151/ , this repo just merge them into one app.

## Functions

* Remove USB mode modal when plug in USB cable, set mode to file transfer.
* Remove package install page when install apk with adb.

## Usage:

1. Install LSPosed, download apk from release and install via adb, then enable this module in LSPosed management page. Reboot.
2. (Optional) Before install and enable this module, install no activity apk you will be asked to login to OPPO account, so this repo's release has two apk, with icon and without icon. After you install with icon version, enable module and reboot device, you can install without icon version to hide this app's ugly default Android icon.
