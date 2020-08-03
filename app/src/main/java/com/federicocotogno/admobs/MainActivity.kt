package com.federicocotogno.admobs

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //hello

    private val TAG = "MainActivity"
    private var totalCoins = 0
    private var adLoadProgress = 0

    private lateinit var mAdView : AdView
    private lateinit var rewardedAd: RewardedAd
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        bannerAd()
        rewardedAd()
        interstitialAd()
    }

    private fun adsLoadedProgress() {
        ++adLoadProgress

        if (adLoadProgress == 3) {
            Toast.makeText(this, "All ads are loaded", Toast.LENGTH_SHORT).show()
        }

    }

    private fun interstitialAd() {
        fun nextActivity() {
            val i = Intent(this, Interstitial::class.java)
            startActivity(i)
        }

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        btn_interstitial_ad.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("TAG", "Interstitial ad loaded")
                adsLoadedProgress()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.d("TAG", "Interstitial ad failed to load")
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d("TAG", "Interstitial ad opened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("TAG", "Interstitial ad clicked")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("TAG", "User left app on interstitial ad")
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Log.d("TAG", "Interstitial ad closed")
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                nextActivity()
            }
        }

    }

    private fun bannerAd() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG,"Ad loaded")
                adsLoadedProgress()
            }

            override fun onAdFailedToLoad(errorCode : Int) {
                // Code to be executed when an ad request fails.
                Log.d(TAG,"Ad failed to load")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG,"Ad opened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG,"Ad clicked")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG,"User left app on ad")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG,"Ad closed")
            }
        }
    }

    private fun rewardedAd() {

        fun createAndLoadRewardedAd(): RewardedAd {
            rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
            val adLoadCallback = object: RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                    Log.d(TAG,"Rewarded Ad loaded")
                    adsLoadedProgress()
                }
                override fun onRewardedAdFailedToLoad(errorCode: Int) {
                    // Ad failed to load.
                    Log.d(TAG,"Rewarded Ad failed to load")
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }
        fun onRewardedAdClosed() {
            this.rewardedAd = createAndLoadRewardedAd()
        }
        createAndLoadRewardedAd()

        btn_rewarded_ad.setOnClickListener {
            Log.d(TAG,"Button clicked")

            if (rewardedAd.isLoaded) {
                val activityContext: Activity = this@MainActivity
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        // Ad opened.
                        Log.d(TAG,"Rewarded Ad opened")
                    }
                    override fun onRewardedAdClosed() {
                        // Ad closed.
                        Log.d(TAG,"Rewarded Ad closed")
                        onRewardedAdClosed()
                    }
                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        // User earned reward.
                        Log.d(TAG,"Reward earned successfully")
                        totalCoins+=5
                        tv_total_coins.text = "Total coins: $totalCoins "
                        Toast.makeText(applicationContext, "5 coins successfully earned", Toast.LENGTH_SHORT).show()
                    }
                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                        Log.d(TAG,"Rewarded Ad failed to show")
                        // Ad failed to display.
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            }
            else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
        }
    }


}
