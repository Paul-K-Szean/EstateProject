package designpattern;

/**
 * Created by Paul K Szean on 4/11/2016.
 * NOT FULLY IMPLEMENTED
 */

public interface Observer {

    // method to update the observer, used by subject
    void update(String notification);

    // attach with subject to observe
    void setSubject(Subject sub);
}