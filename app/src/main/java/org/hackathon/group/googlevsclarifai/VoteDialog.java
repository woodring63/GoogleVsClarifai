package org.hackathon.group.googlevsclarifai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.achartengine.GraphicalView;

/**
 * Created by jellio on 9/17/16.
 */
public class VoteDialog extends DialogFragment {

    private SharedPreferences prefs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout chartContainer = (LinearLayout) inflater.inflate(R.layout.dialog_vote, null);

        builder.setView(chartContainer);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int clarifaiTotal = prefs.getInt("clarifaiTotal", 0);
        int visionTotal = prefs.getInt("visionTotal", 0);

        GraphicalView chartView = PieChartView.getNewInstance(builder.getContext(), visionTotal, clarifaiTotal);

        chartContainer.addView(chartView);

        return builder.create();
    }

}
