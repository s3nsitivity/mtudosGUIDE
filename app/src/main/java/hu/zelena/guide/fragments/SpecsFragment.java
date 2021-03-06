package hu.zelena.guide.fragments;

/**
 * Created by patrik on 2016.02.13..
 */

/**
 * Copyright (C) <2017>  <Patrik G. Zelena>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import hu.zelena.guide.ErrorActivity;
import hu.zelena.guide.R;
import hu.zelena.guide.SearchBrandActivity;
import hu.zelena.guide.modell.Specs;
import hu.zelena.guide.util.ActivityHelper;
import hu.zelena.guide.util.MyScrollView;
import hu.zelena.guide.util.SpecsReader;

public class SpecsFragment extends Fragment {

    View rootView;
    private String brand;
    private Specs specs;
    private String phone;
    private String baseURL;
    private boolean offline;
    private FloatingActionButton fab;

    public SpecsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActivityHelper.initialize(getActivity());

        rootView = inflater.inflate(R.layout.fragment_specs, container, false);

        Bundle bundle = this.getArguments();
        brand = bundle.getString("brand");
        phone = bundle.getString("phone");

        getActivity().setTitle("Specifikáció");


        PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        offline = preferences.getBoolean("offline", false);

        if (offline) {
            baseURL = Environment.getExternalStorageDirectory() + "/Android/data/hu.zelena.guide/data/offline/" + brand + "/specs/" + phone + ".xml";
            new GetOfflineSpecs().execute(baseURL);
        } else {
            baseURL = "http://users.iit.uni-miskolc.hu/~zelena5/work/telekom/mobiltud/phones/" + brand + "/specs/" + phone;
            new HttpRequestSpecs().execute();
        }
        if(!brand.equals("tablet")){

        fab = (FloatingActionButton) rootView.findViewById(R.id.Comparefab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchBrandActivity.class);
                i.putExtra("device", phone);
                i.putExtra("name", (String) getActivity().getTitle());
                i.putExtra("brand", brand);
                Log.d("FAB", phone);
                startActivity(i);
            }
        });

        final MyScrollView scrollView = (MyScrollView) rootView.findViewById(R.id.specsScroll);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                fab.hide();
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.startScrollerTask();
                }

                return false;
            }
        });

        scrollView.setOnScrollStoppedListener(new MyScrollView.OnScrollStoppedListener() {

            public void onScrollStopped() {
                fab.show();
            }
        });
        }

        return rootView;
    }

    private class HttpRequestSpecs extends AsyncTask<Void, Void, Specs> {
        @Override
        protected Specs doInBackground(Void... params) {
            try {
                final String url = baseURL;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Specs specs = restTemplate.getForObject(url, Specs.class);
                return specs;
            } catch (Exception e) {
                Intent i = new Intent(getActivity(), ErrorActivity.class);
                i.putExtra("error", "HTTPAsyncTask: " + e.getMessage());
                startActivity(i);
                Log.e("Async ERROR:", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Specs specs) {

            getActivity().setTitle(specs.getName());

            // Display
            TextView DisplayType = (TextView) rootView.findViewById(R.id.displayType);
            DisplayType.setText(specs.getDisplayType());

            TextView DisplaySize = (TextView) rootView.findViewById(R.id.displaySize);
            DisplaySize.setText(specs.getDisplaySize());

            TextView DisplayRes = (TextView) rootView.findViewById(R.id.displayRes);
            DisplayRes.setText(specs.getDisplayRes());

            TextView DisplayProt = (TextView) rootView.findViewById(R.id.displayProtect);
            DisplayProt.setText(specs.getDisplayProtect());

            //Platform
            TextView OsSpec = (TextView) rootView.findViewById(R.id.os_spec);
            OsSpec.setText(specs.getOs_spec());

            TextView Chipset = (TextView) rootView.findViewById(R.id.chipset);
            Chipset.setText(specs.getChipset());

            TextView CPU = (TextView) rootView.findViewById(R.id.cpu);
            CPU.setText(specs.getCpu());

            TextView GPU = (TextView) rootView.findViewById(R.id.gpu);
            GPU.setText(specs.getGpu());


            //Memroy
            TextView RAM = (TextView) rootView.findViewById(R.id.ram);
            RAM.setText(specs.getRam());

            TextView ROM = (TextView) rootView.findViewById(R.id.rom);
            ROM.setText(specs.getRom());

            TextView expand = (TextView) rootView.findViewById(R.id.expand);
            expand.setText(specs.getExpand());

            //Kamera
            TextView rCam = (TextView) rootView.findViewById(R.id.rcam);
            rCam.setText(specs.getrCam());

            TextView fCam = (TextView) rootView.findViewById(R.id.fcam);
            fCam.setText(specs.getfCam());

            //Other
            TextView batt = (TextView) rootView.findViewById(R.id.batt);
            batt.setText(specs.getBatt());

            TextView speaker = (TextView) rootView.findViewById(R.id.speaker);
            speaker.setText(specs.getSpeaker());

            TextView NFC = (TextView) rootView.findViewById(R.id.nfc);
            NFC.setText(specs.getNfc());

            TextView radio = (TextView) rootView.findViewById(R.id.radio);
            radio.setText(specs.getRadio());

            TextView IPcer = (TextView) rootView.findViewById(R.id.ipcertified);
            IPcer.setText(specs.getIpCertified());

        }
    }

    private class GetOfflineSpecs extends AsyncTask<String, Void, Specs> {
        @Override
        protected Specs doInBackground(String... params) {
            try {
                SpecsReader specsReader = new SpecsReader(baseURL);
                Specs spec = specsReader.getSpecs();
                return spec;
            } catch (Exception e) {
                Intent i = new Intent(getActivity(), ErrorActivity.class);
                i.putExtra("error", "SAXAsyncTask: " + e.getMessage());
                startActivity(i);
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Specs specs) {

            getActivity().setTitle(specs.getName());

            // Display
            TextView DisplayType = (TextView) rootView.findViewById(R.id.displayType);
            DisplayType.setText(specs.getDisplayType());

            TextView DisplaySize = (TextView) rootView.findViewById(R.id.displaySize);
            DisplaySize.setText(specs.getDisplaySize());

            TextView DisplayRes = (TextView) rootView.findViewById(R.id.displayRes);
            DisplayRes.setText(specs.getDisplayRes());

            TextView DisplayProt = (TextView) rootView.findViewById(R.id.displayProtect);
            DisplayProt.setText(specs.getDisplayProtect());

            //Platform
            TextView OsSpec = (TextView) rootView.findViewById(R.id.os_spec);
            OsSpec.setText(specs.getOs_spec());

            TextView Chipset = (TextView) rootView.findViewById(R.id.chipset);
            Chipset.setText(specs.getChipset());

            TextView CPU = (TextView) rootView.findViewById(R.id.cpu);
            CPU.setText(specs.getCpu());

            TextView GPU = (TextView) rootView.findViewById(R.id.gpu);
            GPU.setText(specs.getGpu());


            //Memroy
            TextView RAM = (TextView) rootView.findViewById(R.id.ram);
            RAM.setText(specs.getRam());

            TextView ROM = (TextView) rootView.findViewById(R.id.rom);
            ROM.setText(specs.getRom());

            TextView expand = (TextView) rootView.findViewById(R.id.expand);
            expand.setText(specs.getExpand());

            //Kamera
            TextView rCam = (TextView) rootView.findViewById(R.id.rcam);
            rCam.setText(specs.getrCam());

            TextView fCam = (TextView) rootView.findViewById(R.id.fcam);
            fCam.setText(specs.getfCam());

            //Other
            TextView batt = (TextView) rootView.findViewById(R.id.batt);
            batt.setText(specs.getBatt());

            TextView speaker = (TextView) rootView.findViewById(R.id.speaker);
            speaker.setText(specs.getSpeaker());

            TextView NFC = (TextView) rootView.findViewById(R.id.nfc);
            NFC.setText(specs.getNfc());

            TextView radio = (TextView) rootView.findViewById(R.id.radio);
            radio.setText(specs.getRadio());

            TextView IPcer = (TextView) rootView.findViewById(R.id.ipcertified);
            IPcer.setText(specs.getIpCertified());

        }
    }

}
