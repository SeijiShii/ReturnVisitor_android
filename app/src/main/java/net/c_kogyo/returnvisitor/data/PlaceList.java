package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SeijiShii on 2016/07/27.
 */

public abstract class PlaceList extends DataList<Place> {

    PlaceList() {
        super(Place.class);
    }

    public Place getByMarkerId(String markerId) {

        for ( Object item : this) {

            Place place = (Place) item;
            if (place.getMarkerId().equals(markerId)) {
                return place;
            }
        }
        return null;
    }

    public ArrayList<Place> getListByPersonId(String personId) {

        ArrayList<Place> placesOfPerson = new ArrayList<>();

        for (Place place : list) {

            if (place.hasPersonId(personId)) {
                placesOfPerson.add(place);
            }
        }
        return placesOfPerson;
    }

    public ArrayList<Place> getListByPersonIds(ArrayList<String> personIds) {

        ArrayList<Place> placesOfPersons = new ArrayList<>();

        for (String id : personIds) {

            ArrayList<Place> places = getListByPersonId(id);

            for (Place place : places) {

                if (!placesOfPersons.contains(place)) {
                    placesOfPersons.add(place);
                }
            }
        }
        return placesOfPersons;
    }

    public ArrayList<Place> getSearchedPlaces(String searchString, Context context) {

        String[] words = searchString.split(" ");

        ArrayList<Place> places = new ArrayList<>();

        for (Place place : list) {

            for (String word : words) {
                if (StringUtils.containsIgnoreCase(place.toStringForSearch(context), word)) {
                    if (!places.contains(place.getId())) {
                        places.add(place);
                    }
                }
            }
        }
        return places;
    }


    @Override
    public Iterator<Place> iterator() {
        return list.iterator();
    }


}
