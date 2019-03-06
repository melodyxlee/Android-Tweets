package edu.gwu.androidtweets

import android.location.Address
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

// Networking logic
class TwitterManager {

    // Okhttp is a library used to make network calls
    private val okHttpClient: OkHttpClient

    private var oAuthToken: String? = null

    // This runs extra code when TwitterManager is created
    init {

        // This sets network timeouts (in case phone can't connect
        // to the server of the server is down)
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)

        // This causes all network traffic to be logged to the console
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        okHttpClient = builder.build()
    }

//    interface OAuthTokenListener {
//        fun onSuccess(token: String)
//        fun onError(exception: Exception)
//    }

    fun retrieveOAuthToken(
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ){
        if (oAuthToken != null) {
            successCallback(oAuthToken!!)
            return
        }
        // 1. Build the request
        val encodedKey = "TEtwV0l1ZlJ4bWpOY1kwSUlCeVJVblR2NDo1UGY4SXVvdEdjSHJpelZncHRNSVlkOGI2SHlRTGNvbXBjeTNZd1Q4WkFMbU9zandBNA=="

        val request: Request = Request.Builder()
            .url("https://api.twitter.com/oauth2/token")
            .header("Authorization", "Basic $encodedKey")
            .post(
                RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded"),
                    "grant_type=client_credentials"
                )
            )
            .build()

        // 2. Execute the request
        okHttpClient.newCall(request).enqueue(
            object : Callback {
                // 3. Handle any network failures
                override fun onFailure(call: Call, e: IOException) {
                    errorCallback(e)
                }

                // 4. Handle a server response (good or bad)
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    if (response.isSuccessful && responseBody != null) {

                        val jsonObject = JSONObject(responseBody)
                        val token = jsonObject.getString("access_token")

                        oAuthToken = token
                        successCallback(token)

                    } else {
                        errorCallback(Exception("OAuth call failed"))
                    }
                }

            }
        )
//        try {
//           val response: Response = okHttpClient.newCall(request).execute()
//            // Same as onResponse
//        } catch (exception: Exception){
//            // Same as onFailure
//            errorCallback(exception)
//        }
//        val responseBody = response.body()?.string()
//
//        if (response.isSuccessful && responseBody != null) {
//
//            val jsonObject = JSONObject(responseBody)
//            val token = jsonObject.getString("access_token")
//
//            oAuthToken = token
//            successCallback(token)
//
//        } else {
//            errorCallback(Exception("OAuth call failed"))
//        }
    }

    fun retrieveTweets(
        oAuthToken: String,
        address: Address,
        successCallback: (List<Tweet>) -> Unit,
        errorCallback: (Exception) -> Unit
    ){
        // Data setup
        val lat = address.latitude
        val lon = address.longitude
        val topic = "Android"
        val radius = "30mi"

        // Building the request, passing the OAuth token as a header
        val request = Request.Builder()
            .url("https://api.twitter.com/1.1/search/tweets.json?q=$topic&geocode=$lat,$lon,$radius")
            .header("Authorization", "Bearer $oAuthToken")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Similar error handling to last time
            }

            override fun onResponse(call: Call, response: Response) {
                // Similar success / error handling to last time
                val tweets = mutableListOf<Tweet>()
                val responseString = response.body()?.string()

                if (response.isSuccessful && responseString != null) {
                    // Parse JSON to only get the info we want
                    val statuses = JSONObject(responseString).getJSONArray("statuses")
                    for (i in 0 until statuses.length()) {
                        val curr = statuses.getJSONObject(i)
                        val text = curr.getString("text")
                        val user = curr.getJSONObject("user")
                        val name = user.getString("name")
                        val handle = user.getString("screen_name")
                        val profilePictureUrl = user.getString("profile_image_url")
                        // Build up array of Tweet objects that will be returned
                        tweets.add(
                            Tweet(
                                iconUrl = profilePictureUrl,
                                username = name,
                                handle = handle,
                                content = text
                            )
                        )
                    }
                    successCallback(tweets)

                } else {
                    errorCallback(Exception("Search Tweets call failed"))
                }
            }
        })


    }
}