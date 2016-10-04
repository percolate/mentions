# Mentions

[![Circle CI](https://circleci.com/gh/percolate/mentions.svg?style=svg&circle-token=82fa2c37e303a6d5c44baa2e64199d6b06141aaf)](https://circleci.com/gh/percolate/mentions)
[![codecov.io](http://codecov.io/github/percolate/mentions/coverage.svg?branch=master&token=U8DlJgcAzs)](http://codecov.io/github/percolate/mentions?branch=master)
[![](https://jitpack.io/v/percolate/mentions.svg)](https://jitpack.io/#percolate/mentions)

This library provides a simple and customizable away to setup @ mentions on any EditText. Here's all it takes to get started.

## Usage Examples

We provide a builder through which you can setup different options for @ mentions.
Here is an example:

```java
EditText commentField = findViewById(activity, R.id.my_edit_text);

Mentions mentions = new Mentions.Builder(activity, commentField)
    .highlightColor(R.color.blue)
    .maxCharacters(5)
    .queryListener(new QueryListener() {
        void onQueryReceived(final String query) {
           // Get and display results for query.
        }
    })
    .suggestionsListener(new SuggestionsListener() {
        void displaySuggestions(final boolean display) {
          // Hint that can be used to show or hide your list of @ mentions".
        }
    })
    .build();
```

The library allows you to display suggestions as you see fit. Here is an example in the sample app [Display Suggestions](https://github.com/percolate/mentions/blob/master/Mentions/sample/src/main/java/com/percolate/mentions/sample/activities/MainActivity.java#L95).
When the user chooses a suggestion to @ mention, show it in the `EditText` view by:
```java
final Mention mention = new Mention();
mention.setMentionName(user.getFullName());
mentions.insertMention(mention);
```
Inserting the mention will highlight it in the `EditText` view and the library will keep track of its offset. As the user types more text in the view, the library will update the offset and maintain the highlighting for you.

If you need to get the mentions currently shown in your `EditText` view (to send to your API or do further processing):
```java
final List<Mentionable> mentions = mentions.getInsertedMentions();
for (Mentionable mention : mentions) {
    println("Position of 1st Character in EditText " + mention.getMentionOffset());
    println("Text " + mention.getMentionName())
    println("Length " + mention.getMentionLength());
}

```

### Builder methods

*highlightColor(int color)*

- After a mention is chosen from a suggestions list, it is inserted into the
  `EditText` view and the mention is highlighted with a default color of orange.
  You may change the highlight color by providing a color resource id.

*maxCharacters(int maxCharacters)*

- The user may type @ followed by some letters. You may want to set a threshold to
only consider certain number of characters after the @ symbol as valid search
queries. The default value 13 characters. You may configure to any number
of characters.

*suggestionsListener(SuggestionsListener suggestionsListener)*

- The SuggestionsListener interface has the method displaySuggestions(final boolean display).
It will inform you on whether to show or hide a suggestions drop down.

*queryListener(QueryListener queryListener)*

- The QueryListener interface has the method onQueryReceived(final String query). The library
will provide you with a valid query that you could use to filter and search for mentions. For example, if the user
types @Tes, the callback will receive "Tes" as the query.

# Adding to your application
Simply add Mentions as a gradle dependency.  Distribution is done through jitpack.io.

See https://jitpack.io/com/github/percolate/mentions/ for instructions

[![](https://jitpack.io/v/percolate/mentions.svg)](https://jitpack.io/#percolate/mentions)

# Running Tests
The library contains unit tests written in [Kotlin](https://kotlinlang.org/) with [Mockito](http://mockito.org/) and
[Robolectric](http://robolectric.org/).

To run the tests and generate a coverage, please execute the command
```gradlew clean coverage```.

## License

Open source.  Distributed under the BSD 3 license.  See [LICENSE.txt](https://github.com/percolate/mentions/blob/master/LICENSE.txt) for details.
