package com.example.medisafe;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.medisafe.databinding.ActivityMainBinding;
import com.example.medisafe.worker.LowStockWorker;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean showNav = destination.getId() == R.id.homeFragment
                    || destination.getId() == R.id.statsFragment;
            binding.bottomNav.setVisibility(showNav ? View.VISIBLE : View.GONE);
        });

        // Planifier les notifications périodiques
        LowStockWorker.schedule(this);
    }
}