package hu.zelena.guide.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hu.zelena.guide.ErrorActivity;
import hu.zelena.guide.MainActivity;
import hu.zelena.guide.R;
import hu.zelena.guide.SpecsAvtivity;
import hu.zelena.guide.adapter_sax.PhonesAdapter;
import hu.zelena.guide.modell.PhonesModell;
import hu.zelena.guide.util.PhoneListReader;


/**
 * Created by patrik on 2016.11.26..
 */

/**
 Copyright (C) <2017>  <Patrik G. Zelena>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public class PhonesFragment extends Fragment {

    PhonesAdapter adapter;
    PhoneListReader phoneListReader;
    String path;
    List<PhonesModell> aList = new ArrayList<PhonesModell>();
    private String brand;
    private boolean darkMode;
    private boolean offline;
    private ListView mList;

    public PhonesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.phone_list_main, container, false);
        mList = (ListView) rootView.findViewById(R.id.list);

        Bundle bundle = this.getArguments();
        brand = bundle.getString("brand");
        darkMode = bundle.getBoolean("darkMode");

        MainActivity mActivity = (MainActivity) getActivity();

        if (mActivity.checkInternet() == false) {
            Intent i = new Intent(getActivity(), ErrorActivity.class);
            i.putExtra("darkMode", darkMode);
            i.putExtra("error", "Ellenőrizd az internet kapcsolatot");
            startActivity(i);
        } else {

            if (mActivity.getOfflineMode()) {
                offline = true;
            }
            if (offline) {
                path = Environment.getExternalStorageDirectory() + "/Android/data/hu.zelena.guide/data/offline/" + brand + "/phones.xml";
            } else {
                path = "http://users.iit.uni-miskolc.hu/~zelena5/work/telekom/mobiltud/phones/" + brand + "/phones.xml";
            }

            new GetPhoneList().execute(path);

        }
        return rootView;
    }


    private class GetPhoneList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                phoneListReader = new PhoneListReader(params[0], offline);
                aList = phoneListReader.getItems();
            } catch (Exception e) {
                Intent i = new Intent(getActivity(), ErrorActivity.class);
                i.putExtra("error", "Feldolgozási hiba: " + e.getMessage());
                startActivity(i);
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new PhonesAdapter(getActivity(), R.layout.phone_list_item, aList);
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            mList.setAdapter(adapter);

            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    PhonesModell data = aList.get(arg2);
                    String phone = aList.get(arg2).getName();

                    Intent i = new Intent(getActivity(), SpecsAvtivity.class);
                    i.putExtra("brand", brand);
                    i.putExtra("phone", phone);
                    i.putExtra("name", data.getName());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            };
            mList.setOnItemClickListener(onItemClickListener);
        }
    }
}