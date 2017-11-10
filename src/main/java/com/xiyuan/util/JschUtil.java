package com.xiyuan.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class JschUtil {

    private static final JSch jsch = new JSch();

    public static SshResult exec(String ip, int port, String user, String password, String cmd) {
        SshResult result = new SshResult();

        if (ip == null || ip.trim().isEmpty()
                || user == null || user.trim().isEmpty()
                || cmd == null || cmd.trim().isEmpty()) {
            return result;
        }

        Session session = null;
        try {
            session = jsch.getSession(user, ip, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(60000);
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.setInputStream(null);
            channelExec.setErrStream(null);
            channelExec.connect();

            CountDownLatch countDownLatch = new CountDownLatch(2);

            openStdThread(channelExec.getInputStream(), countDownLatch, result.stdouts);
            openStdThread(channelExec.getExtInputStream(), countDownLatch, result.stderrs);

            countDownLatch.await();
        }
        catch (Exception e) {
            result.success = false;
            result.exception = e;
        }
        finally {
            try {
                if (session != null) {
                    session.disconnect();
                }
            }
            catch (Exception e) {}
        }
        return result;
    }

    private static void openStdThread(InputStream inputStream, CountDownLatch countDownLatch, List<String> result) {
        Thread outThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.add(line);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e) {}
                countDownLatch.countDown();
            }
        });
        outThread.setDaemon(true);
        outThread.start();
    }

    public static class SshResult {

        public boolean success = true;

        public List<String> stdouts = new ArrayList<>();

        public List<String> stderrs = new ArrayList<>();

        public Exception exception = null;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            if (success) {
                for (String stdout : stdouts) {
                    builder.append(stdout).append("\n");
                }
            }
            else {
                builder.append("success: ").append(success).append("\n");

                builder.append("stdouts:\n");
                for (String stdout : stdouts) {
                    builder.append(stdout).append("\n");
                }
                builder.append("stderrs:\n");

                for (String stderr : stderrs) {
                    builder.append(stderr).append("\n");
                }

                if (exception != null) {
                    builder.append(exception).append("\n");
                }
            }

            return builder.toString();
        }
    }

}
