package gitlet;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Control system for our project.
 * @author Abel Yagubyan
 */
public class Main {

    /** Does the required command mainly.
     * @param args args. */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String message = args[0];
        directorywork = new File(".");
        directoryobj = Utils.join(directorywork, ".gitlet");
        if (message.equals("init")) {
            initrepo(args);
        }
        if (!Utils.join(directoryobj, "REPO").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        reposm = Utils.readObject(Utils.join(directoryobj, "REPO"), Dir.class);
        try {
            if (message.equals("rm-remote")) {
                reremoterepo(args); System.exit(0);
            } else if (message.equals("pull")) {
                pullrepo(args); System.exit(0);
            } else if (message.equals("push")) {
                pushrepo(args); System.exit(0);
            } else if (message.equals("fetch")) {
                fetchrepo(args); System.exit(0);
            } else if (message.equals("add-remote")) {
                addremoterepo(args); System.exit(0);
            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        if (message.equals("log")) {
            logrepo(args);
        } else if (message.equals("find")) {
            findrepo(args);
        } else if (message.equals("commit")) {
            commitrepo(args);
        } else if (message.equals("rm")) {
            rmrepo(args);
        } else if (message.equals("add")) {
            addrepo(args);
        } else if (message.equals("status")) {
            statusrepo(args);
        } else if (message.equals("merge")) {
            mergerepo(args);
        } else if (message.equals("rm-branch")) {
            rmbranchrepo(args);
        } else if (message.equals("global-log")) {
            globallogrepo(args);
        } else if (message.equals("reset")) {
            resetrepo(args);
        } else if (message.equals("branch")) {
            branchrepo(args);
        } else if (message.equals("checkout")) {
            checkoutrepo(args);
        }
        System.out.println("No command with that name exists.");
        System.exit(0);
    }

    /** Does the required command.
     * @param args args. */
    private static void fetchrepo(String... args) {
        reposm.remtreebr(args[1], args[2]);
        Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
    }

    /** Does the required command.
     * @param args args. */
    private static void statusrepo(String... args) {
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        reposm.dispstat();
        System.exit(0);
    }

    /** Does the required command.
     * @param args args.*/
    private static void commitrepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String reqcomm = args[1];
        try {
            if (reqcomm.equals("")) {
                System.out.println("Please enter a commit message.");
                System.exit(0);
            }
            reposm.latecomm(reqcomm);
            Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
            System.exit(0);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void globallogrepo(String... args) {
        if (args.length != 1) {
            System.out.println("Incorrect operands:");
            System.exit(0);
        }
        reposm.globlogdisp();
        System.exit(0);
    }

    /** Does the required command.
     * @param args args.*/
    private static void reremoterepo(String... args) {
        reposm.remdel(args[1]);
        Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
    }

    /** Does the required command.
     * @param args args.*/
    private static void resetrepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else {
            try {
                String key = reposm.changeUID(args[1]);
                reposm.moveworkfileback(key);
                Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
                System.exit(0);
            } catch (GitletException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void branchrepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        try {
            reposm.treebranchmake(args[1]);
            Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
            System.exit(0);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    /** Does the required command.
     * @param args args. */
    private static void checkoutrepo(String... args) {
        if (args[1].equals("--")) {
            if (args.length != 3) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else {
                try {
                    reposm.movefileback(args[2]);
                    Utils.writeObject(Utils.join(directoryobj,
                            "REPO"), reposm);
                    System.exit(0);
                } catch (GitletException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            }
        }
        if (!args[1].equals("--")) {
            Pattern ourpattern = Pattern.compile("[a-f0-9]+");
            if (args.length != 2) {
                if (args.length != 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!Pattern.matches("[a-f0-9]+", args[1])) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                try {
                    String key = reposm.changeUID(args[1]);
                    reposm.movefileback(key, args[3]);
                    Utils.writeObject(Utils.join(directoryobj,
                            "REPO"), reposm);
                    System.exit(0);
                } catch (GitletException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            } else {
                try {
                    reposm.branchleave(args[1]);
                    Utils.writeObject(Utils.join(directoryobj,
                            "REPO"), reposm);
                    System.exit(0);
                } catch (GitletException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            }
        }
    }


    /** Does the required command.
     * @param args args.*/
    private static void mergerepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        try {
            reposm.combine(args[1]);
            Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
            System.exit(0);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void addremoterepo(String... args) {
        reposm.remadd(args[1], new File(args[2]));
        Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
    }

    /** Does the required command.
     * @param args args.*/
    private static void addrepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        File ourfile = Utils.join(directorywork, args[1]);
        if (!ourfile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            try {
                reposm.fileimp(args[1]);
                Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
                System.exit(0);
            } catch (GitletException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void initrepo(String... args) {
        if (!Utils.join(directoryobj, "REPO").exists()) {
            directoryobj.mkdir();
            reposm = new Dir();
            Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
            System.exit(0);
        } else {
            System.out.print("A Gitlet version-control system");
            System.out.println(" already exists in the current directory.");
            System.exit(0);
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void pushrepo(String... args) {
        reposm.push(args[1], args[2]);
        Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
    }

    /** Does the required command.
     * @param args args.*/
    private static void pullrepo(String... args) {
        reposm.pull(args[1], args[2]);
        Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
    }

    /** Does the required command.
     * @param args args.*/
    private static void rmrepo(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else {
            try {
                reposm.filedel(args[1]);
                Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
                System.exit(0);
            } catch (GitletException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void logrepo(String... args) {
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        reposm.logdisplay();
        System.exit(0);
    }

    /** Does the required command.
     * @param args args.*/
    private static void rmbranchrepo(String... args) {
        if (args.length == 2) {
            try {
                reposm.treebranchdelete(args[1]);
                Utils.writeObject(Utils.join(directoryobj, "REPO"), reposm);
                System.exit(0);
            } catch (GitletException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Does the required command.
     * @param args args.*/
    private static void findrepo(String... args) {
        if (args.length == 2) {
            try {
                reposm.lookcheck(args[1]);
                System.exit(0);
            } catch (GitletException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        } else {
            System.out.println("Incorrect operands:");
            System.exit(0);
        }
    }

    /** Our working directory. */
    private static File directorywork;
    /** Our repository. */
    private static Dir reposm;
    /** Our object directory. */
    private static File directoryobj;
}









