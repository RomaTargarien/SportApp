package com.example.sportapp.other.states

sealed class DbState(val rssQuery: String) {
    class Empty(rssQuery: String = "news.rss") : DbState(rssQuery)
    class Fulled(rssQuery: String = "news.rss") : DbState(rssQuery)
}
