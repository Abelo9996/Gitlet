package gitlet;

import java.io.Serializable;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Formatter;


/**
 * Handles our given commits.
 *  @author Abel Yagubyan */

public class Ourcommit implements Serializable {
    /** Initializes the given commit. */
    public Ourcommit() {
        _ourlst = new HashMap<String, String>();
        _response = "initial commit";
        _timecom = ZonedDateTime.ofInstant(
                Instant.EPOCH, java.time.ZoneId.systemDefault());

        _hashp = "";
        lochash();
    }
    /** Returns our parent. */
    public String parentret() {
        return _hashp;
    }

    /** Log's date format (according to spec). */
    static final DateTimeFormatter DATE
            = DateTimeFormatter.ofPattern(
            "EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);

    /** Checks whether the file has been checked or not.
     * @param ourfile ourfile
     * @return Check */
    public String checked(String ourfile) {
        if (_ourlst.containsKey(ourfile)) {
            return _ourlst.get(ourfile);
        } else {
            return "";
        }
    }

    /** Returns our given hash. */
    public String hashRet() {
        return _hash;
    }


    /** Diplays hash value of the local file. */
    private void lochash() {
        _hash = Utils.sha1(Utils.serialize(_ourlst), _response,
                Utils.serialize(_timecom));
    }

    /** Returns our response. */
    public String retresponse() {
        return _response;
    }

    /** Changes the date format to a string.
     * @return string version of date. */
    public String datetostr() {
        return _timecom.format(DATE);
    }

    /** Return our list. */
    public HashMap<String, String> lstret() {
        return _ourlst;
    }

    /** Returns our set of keys. */
    public Set<String> keyret() {
        return _ourlst.keySet();
    }

    /** Create a new commit using index, adds a message and a head.
     * @param head head
     * @param image image
     * @param response message */
    public Ourcommit(Image image, String response, String head) {
        _ourlst = new HashMap<String, String>(image.lstRet());
        _response = response;
        _timecom = ZonedDateTime.now();
        _hashp = head;
        lochash();
    }

    /** Overriding our toString() method for our log. */
    @Override
    public String toString() {
        Formatter output = new Formatter();
        output.format("===\n");
        output.format("commit %s\n", _hash);
        if (_mergebranch != null) {
            output.format("Merge: %s %s\n",
                    _hashp.substring(0, 7),
                    _mergebranch.substring(0, 7));
        }
        output.format("Date: %s\n", datetostr());
        output.format("%s\n", _response);
        return output.toString();
    }

    /** Adds the co-parent to the mergebranch.
     * @param cop co-parent */
    public void addparentco(String cop) {
        _mergebranch = cop;
    }

    /** The hash of our commit's parent. */
    private String _hashp;
    /** File list. */
    private HashMap<String, String> _ourlst;
    /** The given message of our commit. */
    private String _response;
    /** The hash of our own commit. */
    private String _hash;
    /** The time of our commit. */
    private ZonedDateTime _timecom;
    /** Merge Branch. */
    private String _mergebranch;
}

