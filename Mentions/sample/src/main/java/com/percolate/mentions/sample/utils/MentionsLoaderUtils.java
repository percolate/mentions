package com.percolate.mentions.sample.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.percolate.mentions.sample.R;
import com.percolate.mentions.sample.models.User;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Converts all the user data in the file users.json to an array of {@link User} objects.
 */
public class MentionsLoaderUtils {

    private final Context context;
    private final List<User> userList;

    public MentionsLoaderUtils(final Context context) {
        this.context = context;
        userList = loadUsers();
    }

    /**
     * Loads users from JSON file.
     */
    private List<User> loadUsers() {
        final Gson gson = new Gson();
        List<User> users = new ArrayList<>();

        try {
            final InputStream fileReader = context.getResources().openRawResource(R.raw.users);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileReader, "UTF-8"));
            users = gson.fromJson(bufferedReader, new TypeToken<List<User>>(){}.getType());
        } catch (IOException ex) {
            Log.e("Mentions Sample", "Error: Failed to parse json file.");
        }

        return users;
    }

    /**
     * Search for user with name matching {@code query}.
     *
     * @return a list of users that matched {@code query}.
     */
    public List<User> searchUsers(String query) {
        final List<User> searchResults = new ArrayList<>();
        if (StringUtils.isNotBlank(query)) {
            query = query.toLowerCase(Locale.US);
            if (userList != null && !userList.isEmpty()) {
                for (User user : userList) {
                    final String firstName = user.getFirstName().toLowerCase();
                    final String lastName = user.getLastName().toLowerCase();
                    if (firstName.startsWith(query) || lastName.startsWith(query)) {
                        searchResults.add(user);
                    }
                }
            }

        }
        return searchResults;
    }

}
