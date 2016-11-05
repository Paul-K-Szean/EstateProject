package designpattern;

/**
 * Created by Paul K Szean on 4/11/2016.
 * NOT FULLY IMPLEMENTED
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import entities.User;

public class Publisher implements Subject {
    private static final String TAG = Publisher.class.getSimpleName();
    private List<Observer> observers = new ArrayList<>();


    @Override
    public void registerObserver(Observer observer, User user) {
        Log.i(TAG, ("Registered observer"));
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        Log.i(TAG, ("Un-register observer"));
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        Log.i(TAG, ("Notifying all observers"));
        for (Observer observer : observers) {
            observer.update("Thanks for subscribing to my property!");
        }
    }

    @Override
    public Object getUpdate(Observer observer) {
        return null;
    }


}