package com.hqy.cloud.util;

import com.hqy.cloud.util.concurrent.ConsistentHash;

import java.util.List;
import java.util.Random;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 22:09
 */
public class MathUtil {

    private MathUtil() {}


    /**
     * 是否有概率发生
     * @param molecule 分子
     * @param denominator 分母
     * @return boolean
     */
    public static boolean mathIf(int molecule, int denominator) {
        int nextInt = new Random().nextInt(denominator);
        return nextInt % molecule == 0;
    }

    /**
     * 改进的32位FNV算法
     * @param data 待hash字符串
     * @return     hash result code
     */
    public static int fnvHash(String data) {
        AssertUtil.notEmpty(data, "Hash data should not be empty.");

        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash ^ data.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }

    /**
     * 基因提取, 提取id的基因值 </br>
     * 即对id进行2^n取余，即取id二进制最后的n + 1位, 因此基因因子 geneFactor必须为2^n
     * @param id         被基因提取的id值
     * @param geneFactor 基因因子
     * @return           基因值
     */
    public static String fetchGene(Long id, int geneFactor) {
        AssertUtil.notNull(id, "Id should not be null.");
        AssertUtil.isTrue(geneFactor % 2 == 0, "Gene factor must be 2^n");
        int index = (geneFactor / 2) + 1;
        String binaryValue = String.format("%64s", Long.toBinaryString(id)).replace("\\s", "0");
        return binaryValue.substring(64 - index);
    }

    /**
     * 根据基因值生成新的id值
     * @param id           原id
     * @param binarySuffix 基因二进制后缀
     * @return             新的id
     */
    public static long newIdWithGene(Long id, String binarySuffix) {
        String binaryString = Long.toBinaryString(id);
        String substring = binaryString.substring(0, binaryString.length() - binarySuffix.length() + 1);
        return Long.parseLong(substring + binarySuffix, 2);
    }



    public static long abs(int data) {
        return data == Integer.MIN_VALUE ? Math.abs((long) data) : Math.abs(data);

    }

    public static void main(String[] args) {
        String s = fetchGene(10000123123L, 4);
        System.out.println(s);

        long newId = newIdWithGene(11111123123123L, s);
        long newId2 = newIdWithGene(111111231231234444L, s);
        System.out.println(newId);

        ConsistentHash<String> consistentHash = new ConsistentHash<>(List.of("1", "2", "3", "4"));
        System.out.println(consistentHash.get(newId));
        System.out.println(consistentHash.get(newId2));
        System.out.println(newId % 4);
        System.out.println(newId2 % 4);
        System.out.println(3505151410519933056L % 4);
    }

}
