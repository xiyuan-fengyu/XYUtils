package com.xiyuan.util;

/**
 * Created by xiyuan_fengyu on 2017/8/17.
 */
public class StringUtil {

    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        int[][] dp = new int[n+1][m+1];
        for(int i=0; i< m+1; i++){
            dp[0][i] = i;
        }
        for(int i=0; i<n+1; i++){
            dp[i][0] = i;
        }


        for(int i = 1; i<n+1; i++){
            for(int j=1; j<m+1; j++){
                if(word1.charAt(i-1) == word2.charAt(j-1)){
                    dp[i][j] = dp[i-1][j-1];
                }else{
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],Math.min(dp[i-1][j],dp[i][j-1]));
                }
            }
        }
        return dp[n][m];
    }

    public static int longestCommonSubstring(String word1, String word2) {
        // state: f[i][j] is the length of the longest lcs
        // ended with A[i - 1] & B[j - 1] in A[0..i-1] & B[0..j-1]
        int n = word1.length();
        int m = word2.length();
        int[][] f = new int[n + 1][m + 1];

        // initialize: f[i][j] is 0 by default

        // function: f[i][j] = f[i - 1][j - 1] + 1 or 0
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    f[i][j] = f[i - 1][j - 1] + 1;
                } else {
                    f[i][j] = 0;
                }
            }
        }

        // answer: max{f[i][j]}
        int max = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                max = Math.max(max, f[i][j]);
            }
        }

        return max;
    }

    private static final char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String randomStr(int len) {
        if (len <= 0) return "";

        char[] randChars = new char[len];
        for (int i = 0; i < len; i++) {
            randChars[i] = chars[(int) (Math.random() * chars.length)];
        }
        return String.valueOf(randChars);
    }

    public static String randomStrWithTime(int len, String divider) {
        StringBuilder builder = new StringBuilder();
        int charsLen = chars.length;
        for (int i = 0; i < len; i++) {
            builder.append(chars[(int) (Math.random() * charsLen)]);
        }
        builder.append(divider);

        long now = System.currentTimeMillis();
        while (now > 0) {
            builder.append(chars[(int) (now % charsLen)]);
            now /= charsLen;
        }
        return builder.toString();
    }

}
