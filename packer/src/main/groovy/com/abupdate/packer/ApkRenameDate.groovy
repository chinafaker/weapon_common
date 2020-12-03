package com.abupdate.packer

class ApkRenameDate {
    /**
     * 待拷贝文件的原路径
     */
    String srcFilePath
    /**
     * 拷贝后文件的文件名
     */
    String targetFileName

    @Override
    String toString() {
        return String.format('{%s, %s}', srcFilePath, targetFileName)
    }
}