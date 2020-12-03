package com.abupdate.packer

class Log {
    static getTag() {
        return "AUtoPackerPlugin"
    }

    static D(String msg) {
        println("${getTag()}  ${msg}")
    }
}