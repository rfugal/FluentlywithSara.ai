package ai.sara.fluentlywithsaraai;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class TwitterActivity extends AppCompatActivity {
    private Context mContext;
    private ViewGroup screen;
    private TwitterLoginButton loginButton;
    private LinearLayout loginSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        mContext = this;
        screen = (ViewGroup) findViewById(R.id.tweets);
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginSplash = (LinearLayout) findViewById(R.id.twitter_splash);
        TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (activeSession!=null) {
            createTimeline();
            loginSplash.setVisibility(View.GONE);
        } else {
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                    createTimeline();
                    loginSplash.setVisibility(View.GONE);
/*
                    //Session storage and recovery.
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    TwitterAuthToken authToken = session.getAuthToken();
                    long userId = session.getUserId();
                    String userName = session.getUserName();
                    String token = authToken.token;
                    String secret = authToken.secret;
                    TwitterAuthToken auth = new TwitterAuthToken(token,secret);
                    TwitterSession ses = new TwitterSession(auth,userId,userName);
                    TwitterCore.getInstance().getSessionManager().setActiveSession(ses);
*/
                }
                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    createTimeline();
                    loginSplash.setVisibility(View.GONE);
                    Log.d("TwitterKit", "Authentication failure", exception);
                }

            });
            (findViewById(R.id.twitter_guest)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createTimeline();
                    loginSplash.setVisibility(View.GONE);
                }
            });
        }
    }
    private void createTimeline() {
        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("#learning2read")
                .languageCode(Locale.ENGLISH.getLanguage())
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .build();
        ListView test = new ListView(mContext);
        test.setAdapter(adapter);
        screen.addView(test);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
/*
    private class populateTweets extends android.os.AsyncTask<String,Void,String> {
        protected String doInBackground(String... query) {
            try {
                SearchService twitterSearch = TwitterCore.getInstance().getApiClient().getSearchService();
                Call<Search> search_tweets = twitterSearch.tweets("spacex filter:safe",null,"en",null,null,null,null,null,null,null);
                Response<Search> result = search_tweets.execute();
                latest_tweets = result.body().tweets;
            } catch (TwitterException e) {
                Log.d("TwitterKit", "Search Tweet failure", e);
            } catch (IOException e) {
                Log.d("TwitterKit", "Search Tweet IO failure", e);
            }
            return "";
        }
        protected void onProgressUpdate() {
        }
        protected void onPostExecute(String e) {
            for (int i=0; i<latest_tweets.size(); i++){
                embedTweet(screen,latest_tweets.get(i).getId());
            }
            if (latest_tweets.size()==0) {
                TextView test = new TextView(mContext);
                test.setText("No recent #learning2read Tweets");
                screen.addView(test);
            }
        }
    }
    public void embedTweet(final ViewGroup parentView, long tweetId) {
        // tweetId =590545296691748864L;
        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                TweetView tweetView = new TweetView(mContext, result.data);
                parentView.addView(tweetView);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Load Tweet failure", exception);
            }
        });
    }
*/
