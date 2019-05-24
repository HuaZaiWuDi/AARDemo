package com.wesmarclothing.kotlintools.kotlin

import com.wesmarclothing.kotlintools.java.ByteUtil

/**
 * @Package com.wesmarclothing.kotlintools
 * @FileName ByteExt
 * @Date 2019/5/23 16:44
 * @Author JACK
 * @Describe 字节操作工具类
 * @Project WeiMiBra
 */

/** byte操作 **/

/**
 * 16进制字节数组转Hex字符串
 * */
fun ByteArray.toHexString(): String {
    return ByteUtil.bytesToHexString(this)
}

/**
 * 16进制字节数组转Ascii字符串
 * */
fun ByteArray.toAsciiString(): String {
    return ByteUtil.byteToString(this)
}

/**
 * 大端模式
 * */
fun ByteArray.toBigInt(): Int {
    return ByteUtil.bytesToIntBig(0, this)
}

/**
 * 小端模式
 * */
fun ByteArray.toLittlent(): Int {
    return ByteUtil.bytesToIntLittle(0, this)
}


/** Int操作 **/
/**
 * 大端模式
 * */
fun Int.toBigBytes(): ByteArray {
    return ByteUtil.intToBytesBig(this)
}

/**
 * 小端模式
 * */
fun Int.toLittleBytes(): ByteArray {
    return ByteUtil.intToBytesLittle(this)
}


/** String操作 **/
/**
 * Hex字符串转16进制字节数组
 * */
fun String.toHexByteArray(): ByteArray {
    return ByteUtil.hexStringToBytes(this)
}





