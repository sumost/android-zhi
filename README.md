# Android-Zhi 小智AI安卓客户端

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-24%2B-brightgreen.svg)](https://developer.android.com/)

Android-Zhi 是一个使用 Kotlin 和 Jetpack Compose 实现的小智语音客户端，旨在帮助那些没有硬件却想体验小智AI语音功能的用户。本项目基于 [py-xiaozhi](https://github.com/huangjunsen0406/py-xiaozhi) 移植。

## 功能特点

### 🎯 核心AI功能

- **AI语音交互** - 支持语音输入与识别，实现智能人机交互
- **自动对话模式** - 实现连续对话体验

### 🔧 通信协议

- **WebSocket 协议** - 支持 WSS 加密音频传输
- **自动重连** - 断线自动重连机制
- **设备激活** - 支持设备激活流程

### 🎵 音频处理

- **Opus 编解码** - 高效的音频压缩
- **低延迟处理** - 20ms 帧长度，流畅对话

### 🖥️ 用户界面

- **Jetpack Compose** - 现代化的声明式UI
- **Material Design 3** - 遵循 Material You 设计规范
- **深色模式** - 支持浅色/深色主题

## 技术架构

```
android-zhi/
├── app/src/main/java/com/androidzhi/app/
│   ├── AndroidZhiApplication.kt    # 应用入口
│   ├── MainActivity.kt             # 主Activity
│   ├── data/
│   │   ├── model/                  # 数据模型
│   │   └── local/                  # 本地存储
│   ├── protocol/                   # 通信协议
│   ├── audio/                      # 音频处理
│   ├── service/                    # 后台服务
│   ├── di/                         # 依赖注入
│   └── ui/                         # 用户界面
├── build.gradle.kts
└── README.md
```

## 系统要求

- **Android 版本** - Android 7.0+ (API 24+)
- **Kotlin 版本** - 2.0+
- **网络连接** - 稳定的互联网连接

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/weijia/android-zhi.git
cd android-zhi
```

### 2. 构建项目

```bash
./gradlew assembleDebug
```

### 3. 运行应用

使用 Android Studio 打开项目，点击 "Run" 按钮。

## 致谢

- [py-xiaozhi](https://github.com/huangjunsen0406/py-xiaozhi) - Python 版本的小智客户端
- [xiaozhi-esp32](https://github.com/78/xiaozhi-esp32) - 小智 ESP32 固件

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件
