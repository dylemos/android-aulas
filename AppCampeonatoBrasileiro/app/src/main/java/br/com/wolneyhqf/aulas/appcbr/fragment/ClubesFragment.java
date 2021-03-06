package br.com.wolneyhqf.aulas.appcbr.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.wolneyhqf.aulas.appcbr.R;
import br.com.wolneyhqf.aulas.appcbr.adapter.ClubesAdapter;
import br.com.wolneyhqf.aulas.appcbr.model.Clube;
import br.com.wolneyhqf.aulas.appcbr.util.HttpHelper;
import br.com.wolneyhqf.aulas.appcbr.util.LogUtil;

/**
 * Created by wolney on 21/02/17.
 */

public class ClubesFragment extends Fragment {

    public static final String TAG = "ClubesFragment";
    public final String LOG_TAG = "appclubesfutebol";
    public final String BASE_URL = "http://172.16.0.92:8080/AppCampeonatoBrasileiro/api/v1";

    private RecyclerView recyclerView;
    private ClubesAdapter clubesAdapter;
    private List<Clube> clubes = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubes, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Carregando Dados");
        progressDialog.setMessage("Aguarde um momento...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_clubes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        clubesAdapter = new ClubesAdapter(getContext(), clubes, onClickClube());
        recyclerView.setAdapter(clubesAdapter);

        carregar();

        return view;
    }

    public void carregar(){
        if(HttpHelper.hasConnection(getContext())){
            new ClubesAsyncTask().execute();
        }else{
            Toast.makeText(getContext(), "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void carregarClubes(){
        try {
            HttpHelper httpHelper = new HttpHelper(BASE_URL);
            String response = httpHelper.doGET("clubes");
            if(response != null){
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Clube clube = new Clube();
                    clube.setNome(jsonObject.getString("nome"));
                    clube.setEstado(jsonObject.getString("estado"));
                    clube.setEstadio(jsonObject.getString("estadio"));
                    clube.setCidade(jsonObject.getString("cidade"));
                    clube.setUrlEscudo(jsonObject.getString("urlEscudo"));
                    clubes.add(clube);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ClubesAdapter.ClubeOnClickListener onClickClube(){
        return new ClubesAdapter.ClubeOnClickListener() {
            @Override
            public void onClickClube(View view, int index) {
                Toast.makeText(getContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
            }
        };
    }

    class ClubesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... strings){
            carregarClubes();
            LogUtil.writeLog(getActivity(), "ClubesFragment.carregarClubes()");
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(v);
            clubesAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

    }

}
