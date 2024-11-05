// [app/src/main/java/com/example/sscompanionapp/ui/dashboard/DashboardFragment.java](app/src/main/java/com/example/sscompanionapp/ui/dashboard/DashboardFragment.java)
package com.example.sscompanionapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sscompanionapp.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private VideoListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewVideos; // Updated ID
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new VideoListAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        dashboardViewModel.getVideoUrls().observe(getViewLifecycleOwner(), videoUrls -> {
            adapter.updateData(videoUrls);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}