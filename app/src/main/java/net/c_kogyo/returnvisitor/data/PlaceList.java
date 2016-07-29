package net.c_kogyo.returnvisitor.data;

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

    public ArrayList<String> getHistoricalPersonIds(String placeId) {

        ArrayList<String> personIds = new ArrayList<>();
        for ( Place place : list ) {

            if (place.getId().equals(placeId)) {
                for ( String id : place.getPersonIds() ) {
                    if (!personIds.contains(id)) personIds.add(id);
                }
            }
        }
        return personIds;
    }

    @Override
    public Iterator iterator() {
        return list.iterator();
    }
}
