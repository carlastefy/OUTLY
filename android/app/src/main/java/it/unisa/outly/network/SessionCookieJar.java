package it.unisa.outly.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SessionCookieJar implements CookieJar {
    private static final String TAG = "SessionCookieJar";
    private static final String PREFERENCES = "outly_http_session";
    private static final String COOKIES_KEY = "cookies";
    private static final String SESSION_COOKIE = "JSESSIONID";

    private final SharedPreferences preferences;

    public SessionCookieJar(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> storedCookies = readCookies();
        for (Cookie cookie : cookies) {
            if (!SESSION_COOKIE.equals(cookie.name())) {
                continue;
            }
            removeMatchingCookie(storedCookies, cookie);
            if (cookie.expiresAt() > System.currentTimeMillis()) {
                storedCookies.add(cookie);
            }
        }
        writeCookies(storedCookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> matchingCookies = new ArrayList<>();
        List<Cookie> storedCookies = readCookies();
        for (Cookie cookie : storedCookies) {
            if (cookie.matches(url)) {
                matchingCookies.add(cookie);
            }
        }
        return matchingCookies;
    }

    public synchronized boolean hasSessionCookie(HttpUrl url) {
        for (Cookie cookie : readCookies()) {
            if (SESSION_COOKIE.equals(cookie.name()) && cookie.matches(url)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void clearSessionCookie() {
        preferences.edit().remove(COOKIES_KEY).apply();
    }

    private void removeMatchingCookie(List<Cookie> cookies, Cookie replacement) {
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie existing = iterator.next();
            if (existing.name().equals(replacement.name())
                    && existing.domain().equals(replacement.domain())
                    && existing.path().equals(replacement.path())) {
                iterator.remove();
            }
        }
    }

    private List<Cookie> readCookies() {
        String encodedCookies = preferences.getString(COOKIES_KEY, "[]");
        List<Cookie> cookies = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(encodedCookies);
            for (int i = 0; i < array.length(); i++) {
                JSONObject value = array.getJSONObject(i);
                Cookie.Builder builder = new Cookie.Builder()
                        .name(value.getString("name"))
                        .value(value.getString("value"))
                        .expiresAt(value.getLong("expiresAt"))
                        .path(value.getString("path"));
                String domain = value.getString("domain");
                if (value.getBoolean("hostOnly")) {
                    builder.hostOnlyDomain(domain);
                } else {
                    builder.domain(domain);
                }
                if (value.getBoolean("secure")) {
                    builder.secure();
                }
                if (value.getBoolean("httpOnly")) {
                    builder.httpOnly();
                }
                Cookie cookie = builder.build();
                if (SESSION_COOKIE.equals(cookie.name())
                        && cookie.expiresAt() > System.currentTimeMillis()) {
                    cookies.add(cookie);
                }
            }
        } catch (JSONException | IllegalArgumentException exception) {
            Log.e(TAG, "Unable to restore the saved HTTP session cookie", exception);
            preferences.edit().remove(COOKIES_KEY).apply();
        }
        return cookies;
    }

    private void writeCookies(List<Cookie> cookies) {
        JSONArray array = new JSONArray();
        try {
            for (Cookie cookie : cookies) {
                JSONObject value = new JSONObject();
                value.put("name", cookie.name());
                value.put("value", cookie.value());
                value.put("expiresAt", cookie.expiresAt());
                value.put("domain", cookie.domain());
                value.put("path", cookie.path());
                value.put("secure", cookie.secure());
                value.put("httpOnly", cookie.httpOnly());
                value.put("hostOnly", cookie.hostOnly());
                array.put(value);
            }
            preferences.edit().putString(COOKIES_KEY, array.toString()).apply();
        } catch (JSONException exception) {
            Log.e(TAG, "Unable to persist the HTTP session cookie", exception);
        }
    }
}
