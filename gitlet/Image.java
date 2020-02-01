package gitlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Copy of our latest image that we received.
 *  @author Abel Yagubyan */

public class Image implements Serializable {
    /** Creating the image using the commit that we received.
     * @param sub */
    public Image(Ourcommit sub) {
        _image = new HashMap<String, String>(sub.lstret());
        _ourlst = new HashMap<String, String>(sub.lstret());
    }

    /** Adds the filename with our given hash.
     * @param filename given filename
     * @param hash given hash*/
    public void addfilename(String filename, String hash) {
        _ourlst.put(filename, hash);
    }

    /** Checks whether the file has been checked or not.
     * @return Our given file.
     * @param  ourfile .*/
    public String checked(String ourfile) {
        if (_ourlst.containsKey(ourfile)) {
            return _ourlst.get(ourfile);
        } else {
            return "";
        }
    }

    /** Removes the give name of the file from our list,
     * returns a boolean if it needs to be removed.
     * @param name our given name of the file. */
    public boolean remove(String name) {
        String last = _image.get(name);
        String curr = _ourlst.get(name);
        if (last == null) {
            _ourlst.remove(name);
            return false;
        } else {
            if (_ourlst.containsKey(name)) {
                _ourlst.remove(name);
                return true;
            } else {
                return false;
            }
        }
    }

    /** Returns our last list. */
    public HashMap<String, String> lstRet() {
        return _ourlst;
    }
    /** Return a key set. */
    public Set<String> getKeys() {
        return _ourlst.keySet();
    }
    /** HashMap image of our last commit. */
    private HashMap<String, String> _image;
    /** HashMap of current list. */
    private HashMap<String, String> _ourlst;
}



