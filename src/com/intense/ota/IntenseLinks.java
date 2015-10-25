/*=========================================================================
 *
 *  PROJECT:  SlimRoms
 *            Team Slimroms (http://www.slimroms.net)
 *
 *  COPYRIGHT Copyright (C) 2013 Slimroms http://www.slimroms.net
 *            All rights reserved
 *
 *  LICENSE   http://www.gnu.org/licenses/gpl-2.0.html GNU/GPL
 *
 *  AUTHORS:     fronti90, mnazim, tchaari, kufikugel, blk_jack
 *  DESCRIPTION: SlimOTA keeps our rom up to date
 *
 *=========================================================================
 */

package com.intense.ota;

import com.intense.ota.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IntenseLinks extends Fragment {

    private LinearLayout mDownload;
    private LinearLayout mDownloadGapps;
    private LinearLayout mGoogleplus;
    private LinearLayout mXda;
    private LinearLayout mSource;

    private TextView mDownloadTitle;
    private TextView mDownloadSummary;;

    private String mStrFileNameNew;
    private String mStrFileURLNew;
    private String mStrCurFile;
    private String mStrDevice;

    private final int STARTUP_DIALOG = 1;
    protected ArrayAdapter<String> adapter;

    private boolean su = false;
    private boolean startup = true;
    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String LOG_TAG = "DeviceInfoSettings";
    private static Intent IRC_INTENT = new Intent(Intent.ACTION_VIEW, Uri.parse("ccircslim:1"));

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intense_ota_links, container, false);
        return view;
    }

    private final View.OnClickListener mActionLayouts = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mDownload) {
                if (mStrFileURLNew != null
                        && mStrFileURLNew != "") {
                    launchUrl(mStrFileURLNew);
                } else {
                    launchUrl(getString(R.string.download_url));
                }
            } else if (v == mDownloadGapps) {
            } else if (v == mGoogleplus) {
                launchUrl("https://plus.google.com/communities/111307265018615581007");
            } else if (v == mSource) {
                launchUrl("http://github.com/IntenseOS");
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set LinearLayouts and onClickListeners

        mDownload = (LinearLayout) getView().findViewById(R.id.short_cut_download);
        mDownloadTitle = (TextView) getView().findViewById(R.id.short_cut_download_title);
        mDownloadSummary = (TextView) getView().findViewById(R.id.short_cut_download_summary);
        mDownload.setOnClickListener(mActionLayouts);

        mDownloadGapps = (LinearLayout) getView().findViewById(R.id.short_cut_download_gapps);
        mDownloadGapps.setOnClickListener(mActionLayouts);

        mGoogleplus = (LinearLayout) getView().findViewById(R.id.googleplus);
        mGoogleplus.setOnClickListener(mActionLayouts);

        mSource = (LinearLayout) getView().findViewById(R.id.source);
        mSource.setOnClickListener(mActionLayouts);

        try {
            FileInputStream fstream = new FileInputStream("/system/build.prop");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] line = strLine.split("=");
                if (line[0].equals("eos.ota.version")) {
                    mStrCurFile = line[1];
                }
            }
            in.close();
        } catch (Exception e) {
            Toast.makeText(getActivity().getBaseContext(), getString(R.string.system_prop_error),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        SharedPreferences shPrefs = getActivity().getSharedPreferences("UpdateChecker", 0);
        mStrFileNameNew = shPrefs.getString("Filename", "");
        mStrFileURLNew = shPrefs.getString("DownloadUrl", "");

        updateView();
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, uriUrl);
        urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(urlIntent);
    }

    public void updateView() {
        if (!mStrFileNameNew.equals("") && !(mStrFileNameNew.compareToIgnoreCase(mStrCurFile)<=0)) {
            mDownloadSummary.setTextColor(0xff009688);
            mDownloadSummary.setText(getString(R.string.short_cut_download_summary_update_available));
        }
    }

    private void toast(String text) {
        // easy toasts for all!
        Toast toast = Toast.makeText(getView().getContext(), text,
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
