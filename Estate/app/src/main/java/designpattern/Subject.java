package designpattern;

import entities.User;

/**
 * Created by Paul K Szean on 4/11/2016.
 * NOT FULLY IMPLEMENTED
 */

public interface Subject {

    // method to register observers
    void registerObserver(Observer observer, User user);

    // method to unregister observers
    void unregisterObserver(Observer observer);

    // method to notify observers of change
    void notifyObservers();

    //method to get updates from subject
    Object getUpdate(Observer observer);

}