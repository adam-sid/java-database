package edu.uob;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Launcher that starts DBServer in the background (hidden),
 * waits for a port to be ready, then runs DBClient in the foreground.
 *
 * Assumes DBServer and DBClient are on the same classpath/jar.
 */
public final class DBLauncher {
    private static final int SERVER_PORT = 8888;
    private static final int WAIT_SECONDS = 15;
    private static final long POLL_INTERVAL_MS = 250;

    public static void main(String[] args) throws Exception {
        String javaExe = findJavaExecutable();
        String classpath = findClasspath();

        List<String> serverCmd = new ArrayList<>();
        serverCmd.add(javaExe);
        serverCmd.add("-cp");
        serverCmd.add(classpath);
        serverCmd.add("edu.uob.DBServer");

        ProcessBuilder serverPb = new ProcessBuilder(serverCmd);
        serverPb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        serverPb.redirectError(ProcessBuilder.Redirect.DISCARD);

        System.out.println("Starting server...");
        Process serverProcess;
        try {
            serverProcess = serverPb.start();
        } catch (IOException e) {
            System.err.println("Failed to start server process: " + e.getMessage());
            throw e;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverProcess.isAlive()) {
                serverProcess.destroy();
            }
        }));

        boolean serverUp = waitForPortOpen("127.0.0.1", SERVER_PORT, WAIT_SECONDS);
        if (!serverUp) {
            System.err.printf("Warning: timed out waiting for server on port %d (waited %d sec).%n",
                    SERVER_PORT, WAIT_SECONDS);
            System.err.println("Continuing anyway — client may fail to connect.");
        } else {
            System.out.println("Server is accepting connections.");
        }

        List<String> clientCmd = new ArrayList<>();
        clientCmd.add(javaExe);
        clientCmd.add("-cp");
        clientCmd.add(classpath);
        clientCmd.add("edu.uob.DBClient");

        ProcessBuilder clientPb = new ProcessBuilder(clientCmd);
        clientPb.inheritIO();

        System.out.println("Starting client (foreground). Client output will appear here.");
        Process clientProcess = clientPb.start();

        int clientExitCode = clientProcess.waitFor();
        System.out.println("Client exited with code: " + clientExitCode);

        if (serverProcess.isAlive()) {
            System.out.println("Stopping server...");
            serverProcess.destroy();
            try {
                if (!serverProcess.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    serverProcess.destroyForcibly();
                }
            } catch (InterruptedException ignored) {
                serverProcess.destroyForcibly();
            }
        }

        System.exit(clientExitCode);
    }

    private static boolean waitForPortOpen(String host, int port, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, port), (int) POLL_INTERVAL_MS);
                return true;
            } catch (IOException ignored) {
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private static String findJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        Path javaBin = Paths.get(javaHome, "bin", "java");
        return javaBin.toString();
    }

    private static String findClasspath() {
        try {
            Path codeSource = Paths.get(DBLauncher.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (codeSource.toFile().isFile()) {
                return codeSource.toString();
            }
        } catch (Exception ignored) {
        }
        return System.getProperty("java.class.path");
    }
}
