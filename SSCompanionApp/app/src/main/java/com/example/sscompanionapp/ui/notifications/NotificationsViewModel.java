package com.example.sscompanionapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.sscompanionapp.AWSClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    // private final MutableLiveData<List<String>> imageUrls;
    private final MutableLiveData<List<String>> imageUrls = new MutableLiveData<>();
    private final AmazonS3Client s3Client;
    private static final String BUCKET_NAME = "sscompanion-images"; // Replace with your bucket name


    public NotificationsViewModel() {
        // imageUrls = new MutableLiveData<>();
        // imageUrls.setValue(Arrays.asList(
        //         "https://sscompanion-images.s3.ap-southeast-1.amazonaws.com/uploaded-image.jpg",
        //         "https://sscompanion-images.s3.ap-southeast-1.amazonaws.com/images.jpg"
        // ));

        // Initialize AWS Credentials
        CognitoCachingCredentialsProvider credentialsProvider = AWSClient.getCredentialsProvider();
        s3Client = new AmazonS3Client(credentialsProvider);

        // Fetch image URLs from S3
        fetchImageUrlsFromS3();

    }


    private void fetchImageUrlsFromS3() {
        new Thread(() -> {
            List<String> urls = new ArrayList<>();
            try {
                // List objects in the specified bucket
                ListObjectsV2Result result = s3Client.listObjectsV2(BUCKET_NAME);
                List<S3ObjectSummary> objects = result.getObjectSummaries();

                for (S3ObjectSummary os : objects) {
                    String key = os.getKey();
                    // Construct the URL based on your bucket's configuration
                    String url = s3Client.getResourceUrl(BUCKET_NAME, key);
                    urls.add(url);
                }

                // Update LiveData on the main thread
                imageUrls.postValue(urls);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions (e.g., network errors, permissions issues)
                imageUrls.postValue(new ArrayList<>()); // Post an empty list or handle as needed
            }
        }).start();
    }


    public LiveData<List<String>> getImageUrls() {
        return imageUrls;
    }
}