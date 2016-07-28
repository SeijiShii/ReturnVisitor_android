package net.c_kogyo.returnvisitor.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SeijiShii on 2016/07/27.
 */

public abstract class PlaceList extends DataList {

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

    @Override
    public void onDataChanged(BaseDataItem data) {
        onDataChanged((Place) data);
    }

    abstract void onDataChanged(Place place);

    @Override
    public Iterator iterator() {
        return ((ArrayList<Place>)list).iterator();
    }
}
