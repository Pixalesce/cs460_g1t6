package com.example.sscompanionapp;

import android.content.Context;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

public class AWSClient {

    private static CognitoCachingCredentialsProvider credentialsProvider;

    public static void initialize(Context context) {
        // Initialize the AWS Mobile Client
        AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                // Initialization successful
            }

            @Override
            public void onError(Exception e) {
                // Handle initialization errors
                e.printStackTrace();
            }
        });

        // Initialize the Cognito Sync Client
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "ap-southeast-1:d888d632-c125-44e4-9562-5d51f786c9a2", // Identity Pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
    }

    public static CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}