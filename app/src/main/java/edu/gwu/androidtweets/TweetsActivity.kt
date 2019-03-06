package edu.gwu.androidtweets

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.location.Address
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

class TweetsActivity : AppCompatActivity() {

    private val twitterManager: TwitterManager = TwitterManager()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweets)

        recyclerView = findViewById(R.id.recyclerView)

        // Set the direction of our list to be vertical
        recyclerView.layoutManager = LinearLayoutManager(this)

        // From lecture 3
        val intent: Intent = intent
        val location: Address = intent.getParcelableExtra("location")

        title = getString(R.string.tweets_title, location.getAddressLine(0))

        twitterManager.retrieveOAuthToken(
            successCallback = { token ->

                twitterManager.retrieveTweets(
                    oAuthToken = token,
                    address = location,
                    successCallback = { tweets ->
                        runOnUiThread {
                            // Create the adapter and assign it to recyclerView
                            recyclerView.adapter = TweetsAdapter(tweets)
                        }
                    },
                    errorCallback = {
                        runOnUiThread {
                            Toast.makeText(this, "Error retrieving Tweets", Toast.LENGTH_LONG).show()
                        }
                    }
                )
                // Runs if we successfully retrieved a token
                runOnUiThread {
                    Toast.makeText(this, "Token: $token", Toast.LENGTH_LONG).show()
                }
            },
            errorCallback = {
                // Runs if we have an error
                runOnUiThread {
                    Toast.makeText(this, "Error performing OAuth", Toast.LENGTH_LONG).show()
                }
            }
        )

        // $ replaces concatenation with +
        // {} allows you to call functions within
        // title = "Android Tweets in ${location.toUpperCase()}"

        // Get the string from strings.xml
//        title = getString(R.string.tweets_title, location)
    }

    private fun generateFakeTweets(): List<Tweet> {
        return listOf(
            Tweet(
                username = "Nick Capurso",
                handle = "@nickcapurso",
                content = "We're learning lists!",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "Android Central",
                handle = "@androidcentral",
                content = "NVIDIA Shield TV vs. Shield TV Pro: Which should I buy?",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "DC Android",
                handle = "@DCAndroid",
                content = "FYI - another great integration for the @Firebase platform",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "KotlinConf",
                handle = "@kotlinconf",
                content = "Can't make it to KotlinConf this year? We have a surprise for you. We'll be live streaming the keynotes, closing panel and an entire track over the 2 main conference days. Sign-up to get notified once we go live!",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "Android Summit",
                handle = "@androidsummit",
                content = "What a #Keynote! @SlatteryClaire is the Director of Performance at Speechless, and that's exactly how she left us after her amazing (and interactive!) #keynote at #androidsummit. #DCTech #AndroidDev #Android",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "Fragmented Podcast",
                handle = "@FragmentedCast",
                content = ".... annnnnnnnnd we're back!\n\nThis week @donnfelker talks about how it's Ok to not know everything and how to set yourself up mentally for JIT (Just In Time [learning]). Listen in here: \nhttp://fragmentedpodcast.com/episodes/135/ ",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "Jake Wharton",
                handle = "@JakeWharton",
                content = "Free idea: location-aware physical password list inside a password manager. Mostly for garage door codes and the like. I want to open my password app, switch to the non-URL password section, and see a list of things sorted by physical distance to me.",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "Droidcon Boston",
                handle = "@droidconbos",
                content = "#DroidconBos will be back in Boston next year on April 8-9!",
                iconUrl = "https://...."
            ),
            Tweet(
                username = "AndroidWeekly",
                handle = "@androidweekly",
                content = "Latest Android Weekly Issue 327 is out!\nhttp://androidweekly.net/ #latest-issue  #AndroidDev",
                iconUrl = "https://...."
            ),
            Tweet(
                username = ".droidconSF",
                handle = "@droidconSF",
                content = "Drum roll please.. Announcing droidcon SF 2018! November 19-20 @ Mission Bay Conference Center. Content and programming by @tsmith & @joenrv.",
                iconUrl = "https://...."
            )
        )
    }
}