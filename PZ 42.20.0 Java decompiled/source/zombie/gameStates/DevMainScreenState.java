// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.gameStates;

import com.google.common.collect.Lists;
import generation.ScriptFileGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.scripting.objects.Registries;
import zombie.scripting.objects.Registry;

public class DevMainScreenState {
    static void main(String[] args) throws Exception {
        Core.IS_DEV = true;
        Registry var1 = Registries.REGISTRY;
        ScriptFileGenerator.main(args);
        MainScreenState.main(args);
    }

    private static String runGitCommand(String... extraArgs) throws IOException {
        ArrayList<String> args = Lists.newArrayList("git");
        Collections.addAll(args, extraArgs);
        Process process = new ProcessBuilder(args).redirectErrorStream(true).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return line == null ? "" : line.trim();
        }
    }

    public static String getDevSha() {
        try {
            return runGitCommand("rev-parse", "HEAD");
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Unable to get version info, is git on system path & is the game running from the repo?");
            return "missing git";
        }
    }

    public static String getDevVersion() {
        String name = "Missing Git";

        try {
            String localBranch = runGitCommand("symbolic-ref", "--short", "-q", "HEAD");
            if (localBranch.isEmpty()) {
                name = runGitCommand("name-rev", "--name-only", "HEAD");
                if (!name.isEmpty() && !"undefined".equals(name) && !name.contains("~")) {
                    name = "branch: " + name.replaceFirst("^remotes/", "");
                } else {
                    name = getDevSha();
                    name = "commit: " + name.substring(0, Math.min(20, name.length()));
                }
            } else {
                name = "branch: " + localBranch;
            }
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Unable to get version info, is git on system path & is the game running from the repo?");
        }

        return "Dev @ " + name;
    }
}
