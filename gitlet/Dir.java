package gitlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.File;
import java.io.Serializable;

/** Our repo.
 *  @author Abel Yagubyan */

public class Dir implements Serializable {

    /** Our current file. */
    private File curDir;
    /** Our branches. */
    private TreeMap<String, String> _treebr;
    /** Our cache. */
    private Ourcommit ourcache;
    /** Our latest commit. */
    private String latestcomms;
    /** Our object file. */
    private File objectFolder;
    /** Our path (absolute). */
    private File wayabs = new File(System.getProperty("user.dir"));
    /** Our commit list. */
    private List<String> commitlst;
    /** Our name of the branch. */
    private String _currbr;
    /** Our setup. */
    private Image _setuploc;
    /** Our commit to its message. */
    private TreeMap<String, String> _commtoresp;
    /** Our directory (for remote use). */
    private TreeMap<String, File> _dirrem;

    /** Init function. */
    public Dir() {
        curDir = new File(".");
        objectFolder = Utils.join(curDir, ".gitlet");
        _dirrem = new TreeMap<String, File>();
        _commtoresp = new TreeMap<String, String>();
        commitlst = new ArrayList<String>();
        ourcache = new Ourcommit();
        _treebr = new TreeMap<String, String>();
        latestcomms = ourcache.hashRet();
        _currbr = "master";
        latecomm();
        _setuploc = new Image(ourcache);
    }

    /** Merges our commit.
     * @param cop cop
     * @param strl strl*/
    private void commerge(String strl, String cop) {
        if (_setuploc.lstRet().equals(ourcache.lstret())) {
            throw new GitletException("No changes added to the commit.");
        }
        ourcache = new Ourcommit(_setuploc, strl, ourcache.hashRet());
        ourcache.addparentco(_treebr.get(cop));
        byte[] bytef = Utils.serialize(ourcache);
        latestcomms = ourcache.hashRet();
        commitlst.add(latestcomms);
        _commtoresp.put(latestcomms, strl);
        File directoryou = Utils.join(objectFolder, latestcomms);
        Utils.writeContents(directoryou, bytef);
        _setuploc = new Image(ourcache);
        _treebr.put(_currbr, latestcomms);
    }

    /** Reverts our file to a previous commit.
     * @param strf strf
     * @param strsub strsub */
    public void movefileback(String strsub, String strf) {
        String strho = ourcache.checked(strf);
        String strhb = subget(strsub).checked(strf);
        if (strhb.equals("")) {
            throw new GitletException("File does not exist in that commit.");
        }
        fileex(strf, strhb);
    }

    /** Adds a file.
     * @param strf strf */
    public void fileimp(String strf) {
        File dirf = Utils.join(curDir, strf);
        byte[] bytefc = Utils.readContents(dirf);
        String strh = hashread(strf);
        File directoryou = Utils.join(objectFolder, strh);
        Utils.writeContents(directoryou, bytefc);
        _setuploc.addfilename(strf, strh);
    }

    /** Extracts a file.
     * @param strf strf
     * @param strh strh*/
    private void fileex(String strf, String strh) {
        File directorybl = Utils.join(objectFolder, strh);
        File directorywr = Utils.join(curDir, strf);
        Utils.writeContents(directorywr, Utils.readContents(directorybl));
    }

    /** Removes a file.
     * @param strf strf*/
    public void filedel(String strf) {
        if (_setuploc.checked(strf).equals("")
                && ourcache.checked(strf).equals("")) {
            throw new GitletException("No reason to remove the file.");
        }
        boolean hard = _setuploc.remove(strf);
        if (hard) {
            quickdel(strf);
        }
    }

    /** Removes a file from a given folder.
     * @param strf strf*/
    private void quickdel(String strf) {
        Utils.restrictedDelete(Utils.join(curDir, strf));
    }

    /** Displays our logs. */
    public void logdisplay() {
        String logf = latestcomms;
        while (!logf.equals("")) {
            Ourcommit commits = subget(logf);
            System.out.println(commits.toString());
            logf = commits.parentret();
        }
    }

    /** Displays our global logs. */
    public void globlogdisp() {
        for (String str : commitlst) {
            System.out.println(subget(str.toString()));
        }
    }

    /** Returns modified yet not staged files of ours. */
    private TreeSet<String> modyetns() {
        TreeSet<String> trees = new TreeSet<String>();
        for (HashMap.Entry<String, String> entrys
                : _setuploc.lstRet().entrySet()) {
            String strf = entrys.getKey();
            String strh = entrys.getValue();
            if (!strh.equals(hashread(strf))) {
                trees.add(strf);
            }
        }
        return trees;
    }


    /** Displays our status when the command is called. */
    public void dispstat() {
        System.out.print("=== Branches ===\n");
        for (String str : _treebr.keySet()) {
            if (str.equals(_currbr)) {
                System.out.printf("*%s\n", str);
            } else {
                System.out.printf("%s\n", str);
            }
        }
        System.out.println();
        System.out.print("=== Staged Files ===\n");
        for (String str : stagedfiles()) {
            System.out.printf("%s\n", str);
        }
        System.out.println();
        System.out.print("=== Removed Files ===\n");
        for (String str : remfilesreceive()) {
            System.out.printf("%s\n", str);
        }
        System.out.println();
        TreeSet<String> treesfile
                = new TreeSet<>(Utils.plainFilenamesIn(curDir));
        System.out.print("=== Modifications Not Staged For Commit ===\n");
        for (String str : modyetns()) {
            if (treesfile.contains(str)) {
                System.out.printf("%s (modified)\n", str);
            } else {
                System.out.printf("%s (deleted)\n", str);
            }
        }
        System.out.println();
        System.out.print("=== Untracked Files ===\n");
        treesfile.removeAll(_setuploc.getKeys());
        for (String str : treesfile) {
            System.out.printf("%s\n", str);
        }
    }

    /** Returns the commit.
     * @param strh strh*/
    public Ourcommit subget(String strh) {
        if (!commitlst.contains(strh)) {
            throw new GitletException("No commit with that id exists.");
        }
        File filedirectory = Utils.join(objectFolder, strh);
        return Utils.readObject(filedirectory, Ourcommit.class);
    }

    /** Resets to a commit.
     * @param strc strc*/
    public void resetbadly(String strc) {
        HashMap<String, String> prevt = subget(strc).lstret();
        Set<String> prevk = prevt.keySet();
        Set<String> currk = _setuploc.lstRet().keySet();
        for (String strf : currk) {
            quickdel(strf);
        }
        for (String strf : prevk) {
            fileex(strf, prevt.get(strf));
        }
        ourcache = subget(strc);
        latestcomms = strc;
        _treebr.put(_currbr, latestcomms);
        _setuploc = new Image(ourcache);
    }

    /** Checks out our file to a branch.
     * @param strn strn */
    public void branchleave(String strn) {
        if (strn.equals(_currbr)) {
            throw new GitletException("No need to "
                    + "checkout the current branch.");
        }
        if (!_treebr.keySet().contains(strn)) {
            throw new GitletException("No such branch exists.");
        }
        _currbr = strn;
        moveworkfileback(_treebr.get(strn));
    }

    /** Reverts our file to a previous version.
     * @param strf strf*/
    public void movefileback(String strf) {
        String strhb = ourcache.checked(strf);
        if (strhb.equals("")) {
            throw new GitletException("File does not exist in that commit.");
        }
        fileex(strf, strhb);
    }

    /** Merges branches.
     * @param strb strb*/
    public void combine(String strb) {
        combinationcheck(strb);
        String sp = changepoint(strb);
        Ourcommit spc = subget(sp);
        Ourcommit brg = subget(_treebr.get(strb));
        Ourcommit currbr = subget(_treebr.get(_currbr));
        TreeSet<String> tbd = new TreeSet<String>();
        TreeSet<String> tbco = new TreeSet<String>();
        TreeSet<String> conf = new TreeSet<String>();
        if (sp.equals(_treebr.get(_currbr))) {
            throw new GitletException("Current branch fast-forwarded.");
        }
        if (sp.equals(_treebr.get(strb))) {
            latestcomms = _treebr.get(strb);
            _treebr.put(_currbr, latestcomms);
            ourcache = subget(latestcomms);
            throw new GitletException(
                    "Given branch is an ancestor of the current branch.");
        }
        movethrough(brg, currbr, spc,
                tbco, conf);
        movethrough2(brg, currbr, spc,
                tbd, conf);
        for (String strf : tbco) {
            movefileback(brg.hashRet(), strf); fileimp(strf);
        }
        for (String strf : tbd) {
            filedel(strf);
        }
        if (!conf.isEmpty()) {
            System.out.println("Encountered a merge conflict.");
        }
        for (String strf : conf) {
            String un, deux;
            String gfh = brg.checked(strf);
            String cfh = currbr.checked(strf);
            if (!cfh.equals("")) {
                un = Utils.readContentsAsString(
                        Utils.join(objectFolder, cfh));
            } else {
                un = "";
            }
            if (!gfh.equals("")) {
                deux = Utils.readContentsAsString(
                        Utils.join(objectFolder, gfh));
            } else {
                deux = "";
            }
            String trois =
                    "<<<<<<< HEAD\n" + un + "=======\n" + deux + ">>>>>>>\n";
            Utils.writeContents(Utils.join(curDir, strf), trois);
            fileimp(strf);
        }
        commerge(String.format("Merged %s into %s.",
                strb, _currbr), strb);
    }

    /** Returns our staged files from the commits. */
    public TreeSet<String> stagedfiles() {
        TreeSet<String> treeres = new TreeSet<String>();
        for (HashMap.Entry<String, String> hment
                : _setuploc.lstRet().entrySet()) {
            String strf = hment.getKey();
            String strh = hment.getValue();
            if (!strh.equals(ourcache.checked(strf))) {
                treeres.add(strf);
            }
        }
        return treeres;
    }

    /** Goes through our given branches.
     * @param conf conf
     * @param currbr currbr
     * @param gbr gbr
     * @param spc spc
     * @param tbd tbd
     */
    private void movethrough2(Ourcommit gbr,
                              Ourcommit currbr,
                              Ourcommit spc,
                              Set<String> tbd,
                              Set<String> conf) {
        for (String strf : currbr.keyret()) {
            String gfh = gbr.checked(strf);
            String currfh = currbr.checked(strf);
            if (gfh.equals("")) {
                if ((!spc.checked(strf).equals("")
                        &&
                        (!currfh.equals(
                                spc.checked(strf))))) {
                    conf.add(strf);
                    continue;
                }
                if (currfh.equals(spc.checked(strf))) {
                    if (!_setuploc.checked(strf).equals("")) {
                        tbd.add(strf);
                    } else {
                        if (!hashread(strf).equals("")) {
                            throw new GitletException(
                                    "There is an untracked file in the way;"
                                            + " delete it or add it first.");
                        }
                    }
                }
            }
        }
    }

    /** Returns a set of our previously removed files. */
    public TreeSet<String> remfilesreceive() {
        TreeSet<String> treeres = new TreeSet<String>();
        for (HashMap.Entry<String, String> hment
                : ourcache.lstret().entrySet()) {
            String strf = hment.getKey();
            if (_setuploc.checked(strf).equals("")) {
                treeres.add(strf);
            }
        }
        return treeres;
    }

    /** Resets our working folder.
     * @param strc strc*/
    public void moveworkfileback(String strc) {
        if (!commitlst.contains(strc)) {
            throw new GitletException("No commit with that id exists.");
        }
        HashMap<String, String> prevt = subget(strc).lstret();
        Set<String> prevk = prevt.keySet();
        Set<String> currk = _setuploc.lstRet().keySet();
        for (String strf : prevk) {
            if (_setuploc.checked(strf).equals("")) {
                File directoryf = Utils.join(curDir, strf);
                if (directoryf.exists()) {
                    throw new GitletException(
                            "There is an untracked file in the way; "
                                    + "delete it or add it first.");
                }
            }
        }
        for (String strf : currk) {
            quickdel(strf);
        }
        for (String strf : prevk) {
            fileex(strf, prevt.get(strf));
        }
        ourcache = subget(strc);
        latestcomms = ourcache.hashRet();
        _treebr.put(_currbr, latestcomms);
        _setuploc = new Image(ourcache);
    }

    /** Produces a new branch.
     * @param strb strb*/
    public void treebranchmake(String strb) {
        if (_treebr.containsKey(strb)) {
            throw
                    new GitletException("A branch with "
                            + "that name already exists.");
        }
        _treebr.put(strb, latestcomms);
    }

    /** Deletes a branch.
     * @param strb strb*/
    public void treebranchdelete(String strb) {
        if (strb.equals(_currbr)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        if (!_treebr.containsKey(strb)) {
            throw
                    new GitletException("A branch with "
                            + "that name does not exist.");
        }
        _treebr.remove(strb);
    }

    /** Reads our file's information.
     * @param strf strf
     * @return  content of file. */
    private String hashread(String strf) {
        File directoryf = Utils.join(curDir, strf);
        if (!directoryf.exists()) {
            return "";
        }
        return Utils.sha1(Utils.readContents(directoryf));
    }

    /** Creates a new commit.
     * @param strl strl
     */
    public void latecomm(String strl) {
        if (_setuploc.lstRet().equals(ourcache.lstret())) {
            throw new GitletException("No changes added to the commit.");
        }
        ourcache = new Ourcommit(_setuploc, strl, ourcache.hashRet());
        byte[] bytef = Utils.serialize(ourcache);
        latestcomms = ourcache.hashRet();
        commitlst.add(latestcomms);
        _commtoresp.put(latestcomms, strl);
        File directoryou = Utils.join(objectFolder, latestcomms);
        Utils.writeContents(directoryou, bytef);
        _setuploc = new Image(ourcache);
        _treebr.put(_currbr, latestcomms);
    }


    /** Remotely removes a name.
     * @param strn strn*/
    public void remdel(String strn) {
        if (!_dirrem.containsKey(strn)) {
            throw
                    new GitletException("A remote "
                            + "with that name does not exist.");
        }
        _dirrem.remove(strn);
    }

    /** Goes through our given branches.
     * @param brcurr brcurr
     * @param brg brg
     * @param conf conf
     * @param spc spc
     * @param tbco tbco
     */
    private void movethrough(Ourcommit brg,
                                    Ourcommit brcurr,
                                    Ourcommit spc,
                                    Set<String> tbco,
                                    Set<String> conf) {
        for (String strf : brg.keyret()) {
            String strfhg = brg.checked(strf);
            String strfhc = brcurr.checked(strf);
            if (!strfhg.equals(strfhc)) {
                String sph = spc.checked(strf);
                if ((!strfhc.equals(sph))
                        && (!strfhg.equals(sph))) {
                    conf.add(strf);
                    continue;
                }
                if (strfhc.equals(spc.checked(strf))) {
                    if ((!hashread(strf).equals(""))
                            && (strfhc.equals(""))) {
                        throw
                                new GitletException(
                                        "There is an"
                                                + " untracked file in the way; "
                                                + "delete it or add it first.");
                    }
                    tbco.add(strf);
                }
            }
        }
    }

    /** Creates a new commit. */
    public void latecomm() {
        byte[] bytef = Utils.serialize(ourcache);
        String strh = ourcache.hashRet();
        File directoryou = Utils.join(objectFolder, strh);
        Utils.writeContents(directoryou, bytef);
        commitlst.add(strh);
        _commtoresp.put(strh, "initial commit");
        _treebr.put(_currbr, latestcomms);
    }

    /** Checks if merge passes conditions.
     * @param strb strb*/
    private void combinationcheck(String strb) {
        if (!_treebr.containsKey(strb)) {
            throw
                    new GitletException("A branch "
                            + "with that name does not exist.");
        }
        if (strb.equals(_currbr)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        if ((!remfilesreceive().isEmpty()) || (!stagedfiles().isEmpty())) {
            throw new GitletException("You have uncommitted changes.");
        }
    }

    /** Finds the splitting point of two branches.
     * @param strb strb
     * @return split-point. */
    private String changepoint(String strb) {
        TreeSet<String> pgb = new TreeSet<String>();
        String un = _treebr.get(strb);
        while (!un.equals("")) {
            Ourcommit commits = subget(un);
            pgb.add(un);
            un = commits.parentret();
        }
        un = _treebr.get(_currbr);
        while (!un.equals("")) {
            Ourcommit commits = subget(un);
            if (pgb.contains(un)) {
                return un;
            }
            un = commits.parentret();
        }
        return "NOT FOUND";

    }

    /** Looks for a commit using a string message left.
     * @param str str*/
    public void lookcheck(String str) {
        Boolean check = false;
        for (HashMap.Entry<String, String> hment : _commtoresp.entrySet()) {
            String str1 = hment.getKey();
            String str2 = hment.getValue();
            if (str2.equals(str)) {
                System.out.println(str1);
                check = true;
            }
        }
        if (!check) {
            throw new GitletException("Found no commit with that message.");
        }
    }

    /** Changes the UID to original.
     * @param strval strval
     * @return original. */
    public String changeUID(String strval) {
        for (String str : commitlst) {
            if (str.startsWith(strval)) {
                return str;
            }
        }
        return "Please check again!";
    }

    /** Finds the hash given by the branch.
     * @param strb strb
     * @return hash*/
    private String hashtotreebr(String strb) {
        return _treebr.get(strb);
    }

    /** Gets our remote branch.
     * @param strb strb
     * @param strn strn*/
    public void remtreebr(String strn, String strb) {
        File directoryrem = _dirrem.get(strn);
        if (!directoryrem.exists()) {
            throw new GitletException("Remote directory not found.");
        }
        File directoryobj = directoryrem;
        Dir remrp =
                Utils.readObject(Utils.join(directoryobj,
                        "REPO"), Dir.class);
        String rembr = remrp.hashtotreebr(strb);
        if (rembr == null) {
            throw new GitletException("That remote does not have that branch.");
        }
        String un = remrp.hashtotreebr(strb);
        while (!un.equals("")) {
            byte[] bytef =
                    Utils.readContents(Utils.join(directoryobj, un));
            File directoryou = Utils.join(objectFolder, un);
            Utils.writeContents(directoryou, bytef);
            commitlst.add(un);
            remrp.changeposblob(un, objectFolder);
            un = remrp.subget(un).parentret();
        }
        String newbr = String.format("%s/%s", strn, strb);
        _treebr.put(newbr, rembr);
    }

    /** Remotely pulls the branch.
     * @param strb strb
     * @param strn strn*/
    public void pull(String strn, String strb) {
        remtreebr(strn, strb);
        String newbr = String.format("%s/%s", strn, strb);
        combine(newbr);
    }

    /** Remotely pushes the branch.
     * @param strb strb
     * @param strn strn*/
    public void push(String strn, String strb) {
        File directoryrem = _dirrem.get(strn);
        if (!directoryrem.exists()) {
            throw new GitletException("Remote directory not found.");
        }
        File directoryobj = Utils.join(directoryrem);
        Dir remrp
                = Utils.readObject(Utils.join(directoryobj,
                "REPO"), Dir.class);
        String rembr = remrp.hashtotreebr(strb);
        String un = _treebr.get(_currbr);
        TreeSet<String> commtr = new TreeSet<String>();
        Boolean check = false;
        while (!un.equals("")) {
            commtr.add(un);
            if (un.equals(rembr)) {
                check = true;
                break;
            }
            un = subget(un).parentret();
        }
        if (!check) {
            throw
                    new GitletException(
                            " Please pull down remote changes before pushing.");
        }
        for (String strc : commtr) {
            byte[] fileContent =
                    Utils.readContents(Utils.join(objectFolder, strc));
            File directoryou = Utils.join(directoryobj, strc);
            Utils.writeContents(directoryou, fileContent);
            changeposblob(strc, directoryobj);
            remrp.commitlst.add(strc);
        }
        remrp.resetbadly(_treebr.get(_currbr));
        Utils.writeObject(Utils.join(directoryobj, "REPO"), remrp);
    }

    /** Remotely moves all of the blobs.
     * @param strc strc
     * @param directoryou directoryou*/
    private void changeposblob(String strc, File directoryou) {
        Ourcommit commc = subget(strc);
        File objfabs = Utils.join(wayabs, ".gitlet");
        for (String str : commc.keyret()) {
            String strh = commc.checked(str);
            byte[] bytef
                    = Utils.readContents(Utils.join(objfabs, strh));
            Utils.writeContents(Utils.join(directoryou, strh), bytef);
        }
    }
    /** Adds a remote.
     * @param strn strn
     * @param strf strf*/
    public void remadd(String strn, File strf) {
        if (_dirrem.containsKey(strn)) {
            throw
                    new GitletException("A remote "
                            + "with that name already exists.");
        }
        _dirrem.put(strn, strf);
    }
}



