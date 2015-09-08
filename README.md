Creator: Brent Watson

# Mentions

Use the simple & powerful API to easily setup @ mentions in your EditText.

# Usage

We provide a builder through which you could setup different options for the at
mentions. Here is example:

```
Mentions mentions = new Mentions.Builder(context, commentField)
                                .highlightColor(R.color.blue)
                                .maxCharacters(5)
                                .suggestionsListener(suggestionsListener)
                                .queryListener(queryListener)
                                .build();
```

# Builder Methods

*highlightColor(int color)*

- After a mention is chosen from a suggestions list, it is inserted into the
  EditText view and the mention is highlight with a default color of orange.
  You may change the highlight color by providing a color resource id.

*maxCharacters(int maxCharacters)*


- The user may type @ followed by some letters. You may want to set a threshold to
only consider certain number of characters after the @ symbol as valid search
queries. The default value 13 characters. You may configure to any number
of characters.

*suggestionsListener(SuggestionsListener suggestionsListener)*


- The SuggestionsListener interface has the method displaySuggestions(boolean display).
It will inform you on whether to show or hide a suggestions drop down.

*queryListener(QueryListener queryListener)*


- The QueryListener interface has the method onQueryReceived(String query). The library
will provide you with a valid query that you could use to filter and search for mentions.
