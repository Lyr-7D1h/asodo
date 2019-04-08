package com.example.ninja.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ninja.Controllers.Routetracking.Route;
import com.example.ninja.Controllers.Routetracking.Startroute;
import com.example.ninja.Controllers.Stats.HistoryList;
import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.util.ActivityUtils;

import com.example.ninja.R;

public class AhNiffo extends BackButtonActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahniffo);
    }
}