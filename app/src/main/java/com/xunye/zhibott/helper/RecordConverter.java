package com.xunye.zhibott.helper;

import com.iermu.opensdk.lan.model.CamRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wcy on 2016/12/21.
 */
public class RecordConverter {

    public static List<CamRecord> converterLyyJson(String jsonStr) throws JSONException {
        JSONObject json   = new JSONObject(jsonStr);
        JSONObject object = json.getJSONObject(Field.RESULTS);
        JSONArray array   = object.getJSONArray(Field.VIDEOS);
        String diskInfo   = object.toString();

        List<CamRecord> records = fromLyyJsonArray(array, diskInfo);
        List<CamRecord> events  = new ArrayList<CamRecord>();
        if(object.has(Field.EVENTS)){
            JSONArray eArray = object.getJSONArray(Field.EVENTS);
            events = fromLyyEventsJsonArray(eArray, diskInfo);
        }

        records.addAll(events);
        Collections.sort(records, new RecordComparator<CamRecord>());
        return records;
    }

    public static List<CamRecord> converterBaiduJson(String jsonStr) throws JSONException {
        JSONObject json   = new JSONObject(jsonStr);
        JSONObject object = json.getJSONObject(Field.RESULTS);
        JSONArray array   = object.getJSONArray(Field.VIDEOS);

        List<CamRecord> records = new ArrayList<CamRecord>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                JSONArray itemA = array.getJSONArray(i);
                int startTime   = itemA.getInt(0);
                int endTime     = itemA.getInt(1);
                CamRecord item = new CamRecord();
                item.setStartTime(startTime);
                item.setEndTime(endTime);
                records.add(item);
            }
        }
        Collections.sort(records, new RecordComparator<CamRecord>());
        return records;
    }

    private static List<CamRecord> fromLyyJsonArray(JSONArray array, String diskInfo) throws JSONException {
        List<CamRecord> list = new ArrayList<CamRecord>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int start = object.optInt(Field.FROM);
                int end = object.optInt(Field.TO);

                CamRecord record = new CamRecord();
                record.setStartTime(start);
                record.setEndTime(end);
                record.setRecType(CloudRecType.COMMON_REC);
                record.setDiskInfo(diskInfo);
                record.setCardRecord(false);
                record.setLyy(true);
                list.add(record);
            }
        }
        return list;
    }

    private static List<CamRecord> fromLyyEventsJsonArray(JSONArray array, String diskInfo) throws JSONException {
        List<CamRecord> list = new ArrayList<CamRecord>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int begin = object.optInt(Field.BEGIN);
                int end = object.optInt(Field.END);
                String url = object.optString(Field.URL);

                CamRecord record = new CamRecord();
                record.setStartTime(begin);
                record.setEndTime(end);
                record.setEventsUrl(url);
                record.setRecType(CloudRecType.EVENTS_REC);
                record.setDiskInfo(diskInfo);
                list.add(record);
            }
        }
        return list;
    }

    private static class RecordComparator<T> implements Comparator<CamRecord> {
        @Override
        public int compare(CamRecord lhs, CamRecord rhs) {
            String time = String.valueOf(lhs.getStartTime());
            String time1 = String.valueOf(rhs.getStartTime());
            return time.compareTo(time1);
        }
    }
    class Field {
        static final String RESULTS = "results";
        static final String VIDEOS = "videos";
        static final String EVENTS = "events";

        static final String FROM = "from";
        static final String TO = "to";

        static final String BEGIN = "begin";
        static final String END = "end";
        static final String URL = "url";
    }
}
