package gr.ellak.ma.emergencyroad;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Created by billiout on 11/7/2015.
 */
public class MapSuggestionProvider extends ContentProvider {

    private static final String[] COLUMNS = {
            "_id", // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID };

    public static final String AUTHORITY = "gr.ellak.ma.emergencyroad.MapSuggestionProvider";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String query = uri.getLastPathSegment();
        if (query == null || query.length() == 0) {
            return null;
        }
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        try {
            cursor.addRow(createRow(0, query, "hi", "helloo"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    private Object[] createRow(Integer id, String text1, String text2, String name) {
        return new Object[] { id, // _id
                text1, // text1
                text2, // text2
                name, "android.intent.action.SEARCH", // action
                SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT };
    }
}
