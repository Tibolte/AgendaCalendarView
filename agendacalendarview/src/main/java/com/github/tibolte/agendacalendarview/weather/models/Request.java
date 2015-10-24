package com.github.tibolte.agendacalendarview.weather.models;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private static final String UNITS_KEY = "units";

    public enum Units {
        SI("si"),
        US("us"),
        CA("ca"),
        UK("uk"),
        AUTO("auto");
        private String mValue;

        private Units(String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    private static final String LANGUAGE_KEY = "lang";

    public enum Language {
        BOSNIAN("bs"),
        GERMAN("de"),
        ENGLISH("en"),
        SPANISH("es"),
        FRENCH("fr"),
        ITALIAN("it"),
        DUTCH("nl"),
        POLISH("pl"),
        PORTUGUESE("pt"),
        RUSSIAN("ru"),
        TETUM("tet"),
        PIG_LATIN("x-pig-latin");
        private String mValue;

        private Language(String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    private static final String EXCLUDE_KEY = "exclude";

    public enum Block {
        CURRENTLY("currently"),
        MINUTELY("minutely"),
        HOURLY("hourly"),
        DAILY("daily"),
        ALERTS("alerts"),
        FLAGS("flags");
        String mValue;

        private Block(String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    // region Attributes

    private String mLat;
    private String mLng;
    private String mTime;
    private Units mUnits;
    private Language mLanguage;
    private List<Block> mExcludeBlocks = new ArrayList<Block>();

    // endregion

    // region Getters/setters

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String lng) {
        mLng = lng;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    private Boolean useTime() {
        return mTime != null && !mTime.equals("");
    }

    public Units getUnits() {
        return mUnits;
    }

    public void setUnits(Units units) {
        mUnits = units;
    }

    public Language getLanguage() {
        return mLanguage;
    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    public void addExcludeBlock(Block exclude) {
        mExcludeBlocks.add(exclude);
    }

    public void removeExcludeBlock(Block exclude) {
        int index = mExcludeBlocks.indexOf(exclude);
        if (index != -1) mExcludeBlocks.remove(index);
    }

    public Map<String, String> getQueryParams() {
        Map<String, String> query = new HashMap<String, String>();
        query.put(UNITS_KEY, mUnits.toString());
        query.put(LANGUAGE_KEY, mLanguage.toString());
        query.put(EXCLUDE_KEY, getExcludeBlock());
        return query;
    }

    private String getExcludeBlock() {
        return mExcludeBlocks.size() > 0 ? Joiner.on(",").join(mExcludeBlocks) : null;
    }

    // endregion

    @Override
    public String toString() {
        String params = mLat + "," + mLng;
        return useTime() ? params + "," + mTime : params;
    }
}
