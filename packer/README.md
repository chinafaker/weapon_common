# 自动化打包脚本

## 使用

### 1.在待打包项目的module下的gradle中配置如下内容：
```
buildscript {
    repositories {
        jcenter()
        maven {
            url uri('../repo')
        }
    }
    dependencies {
        classpath 'com.abupdate.weaponry:packer:0.0.5'
    }
}
apply plugin: 'auto-packer'
```

### 2.自定义打包规则：
```
autoPack {
    taskConfig {
        //设置禁止打包的ProductFlavor
        disableProductFlavors = ['Type2']
        //设置禁止打包的BuildType
        disableBuildTypes = ['debug']
    }
    packOutputConfig {
        //设置打包输出路径
        outputDir "${project.getProjectDir()}/build/autoPack"
        //是否压缩
        compress true
        //打包其他的文件
        otherFiles = ["${project.getProjectDir()}/README.md", "${project.getProjectDir()}/ReleaseNote.txt"]
        //对APK文件进行重命名
        renameApkFile { project, variant ->
            project.name + '_' + variant.name + '_' + variant.versionName + '.apk'
        }
        //命名打包目录的文件夹，若不设置，默认为APK的文件名
//        packDirName { project ->
//            project.name
//        }
    }
```

### 3.使用：
方式1：在Terminal输入命令：gradlew autoPack

方式2：点开AndroidStudio右侧gradle插件栏，找到对应的module，找到autopack插件，点击即可。
