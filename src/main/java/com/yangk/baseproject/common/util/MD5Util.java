package com.yangk.baseproject.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description 使用md5进行签名
 *
 * 报文不加密,只做MD5摘要
 * 签名与验签涉及到几个关键点：
 *
 * 1． 字符集，目前支持GBK与UTF-8两种，CP在菜鸟备案字符集选项，通讯过程中，所有环境均使用备案的字符集
 * 2． 签名key与签名内容，其中签名key为菜鸟为CP生成。签名体为报文内容+签名Key
 * 3． 签名算法为：对签名内容进行md5，后将内容转换成base64编码
 *
 * @Author yangkun
 * @Date 2020/3/5
 * @Version 1.0
 * @blame yangkun
 */
@Slf4j
public final class MD5Util {

    private MD5Util() {
    }

    public static String md5Base64(String str) {
        String encodeStr = "";
        try {
            byte[] utfBytes = str.getBytes("UTF-8");
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            sun.misc.BASE64Encoder b64Encoder = new sun.misc.BASE64Encoder();
            encodeStr = b64Encoder.encode(md5Bytes);
        } catch (Exception e) {
            log.warn("MD5Util createSignByMd5 fail");
            return encodeStr;
        }
        return encodeStr;
    }

    public static String createSignByMd5(String content,String key){
        return md5Base64(content + key);
    }

    public static String encodeBase64MD5String(String source) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] encode2bytes = encode2bytes(source);
        return Base64.encodeBase64String(encode2bytes);
    }

    public static byte[] encode2bytes(String source) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] result = null;

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(source.getBytes("UTF-8"));
        result = md.digest();

        return result;
    }

    public static String encode2hex(String source) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] data = encode2bytes(source);
        StringBuilder hexString = new StringBuilder();
        int len = data == null ? 0 : data.length;
        for(int i = 0; i < len; ++i) {
            String hex = Integer.toHexString(255 & data[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static boolean validate(String unknown, String okHex) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return okHex.equals(encode2hex(unknown));
    }

    public static void main(String[] args) {
        String signByEncryptType = MD5Util.createSignByMd5("helloworld", "key123");
        System.out.println("uKriAhQmuSq112bCgHVV6g==".equals(signByEncryptType));

    }



}
