package com.cabral.marinho.meusfilmes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity {

    private FilmeDao filmeDao;
    private Button buttonLista;
    private Button buttonHistorico;
    private Button buttonFavotitos;
    private Button buttonCreditos;
    private List<Filme> filmes = new ArrayList<Filme>();
    private String link = "https://api.themoviedb.org/3/discover/movie?api_key=bad51705c7756f9ffdc7d3dc37b7aad2&sort_by=popularity.desc&language=pt-BR&page=";
    private int httpResponse;
    private static final int qtdPag = 25;
    private static final int freqAtualizacao = 7; //em dias

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        buttonLista = (Button) findViewById(R.id.buttonLista);
        buttonHistorico = (Button) findViewById(R.id.buttonHistorico);
        buttonFavotitos = (Button) findViewById(R.id.buttonFavoritos);
        buttonCreditos = (Button) findViewById(R.id.buttonCreditos);

        class DownloaderTask extends AsyncTask<String, Void, List<JSONObject>> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                InicioActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                progressDialog = new ProgressDialog(InicioActivity.this);
                progressDialog.setTitle("Atualizando");
                progressDialog.setMessage("Aguarde...");
                progressDialog.show();
            }

            protected void onPostExecute(List<JSONObject> paginas) {
                int qtdPaginas = paginas.size();
                if(qtdPaginas == qtdPag){
                    boolean atualizar = true;
                    Long id = Long.valueOf(1);
                    if(qtdFilmes() == 0)
                        atualizar = false;
                    for(int i = 0; i < qtdPaginas; i++){
                        try {
                            JSONArray jsonArray = paginas.get(i).getJSONArray("results");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject obj = jsonArray.getJSONObject(j);
                                if(atualizar) {
                                    filmeDao.atualize(Filme.fromJSON(obj), id);
                                    id++;
                                }
                                else
                                    filmeDao.insert(Filme.fromJSON(obj));
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(qtdPaginas == qtdPag){
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InicioActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        Data data = new Data();
                        editor.putString("dataAtualizacao", data.toString());
                        editor.commit();
                        progressDialog.setMessage("Concluído!");
                        progressDialog.dismiss();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(InicioActivity.this, "Falha ao atualizar: a base de dados está incompleta", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    progressDialog.dismiss();
                    buttonLista.setEnabled(false);
                    buttonHistorico.setEnabled(false);
                    buttonFavotitos.setEnabled(false);
                    Toast.makeText(InicioActivity.this, "Falha ao atualizar! Verifique suas conexões de rede.", Toast.LENGTH_LONG).show();
                }
                InicioActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }

            @Override
            protected List<JSONObject> doInBackground(String... params) {
                int qtdLinks = params.length;
                List<JSONObject> paginas = new ArrayList<>();
                for(int i = 0; i < qtdLinks; i++) {
                    try {
                        URL url = new URL(params[i]);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.connect();
                        httpResponse = httpURLConnection.getResponseCode();
                        if (httpResponse == 200) {
                            Log.i("BAIXANDO PÁGINA " + i + ": ", "Conexão bem sucedida!");
                            String json = getString(httpURLConnection.getInputStream());
                            Log.i("RESPOSTA: ", json);
                            paginas.add(new JSONObject(json));
                        } else
                            Log.i("BAIXANDO PÁGINA " + i + ": ", "Falha na conexão!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return paginas;
            }

            private String getString(InputStream in) throws IOException {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = buffer.readLine()) != null) {
                    str.append(line);
                }
                return str.toString();
            }
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String stringData = prefs.getString("dataAtualizacao", "00/00/0000");
        Data dataAnterior = new Data(stringData);
        Data dataAtual = new Data();
        if(dataAtual.compare(dataAnterior) >= freqAtualizacao){
            filmeDao = new FilmeDao(this);
            filmeDao.open();
            String[] parametros = new String[qtdPag];
            for(int i = 0; i < qtdPag; i++)
                parametros[i] = link + Integer.toString(i + 1);
            DownloaderTask downloaderTask =  new DownloaderTask();
            downloaderTask.execute(parametros);
        }
    }

    @Override
    protected void onDestroy() {
        if(filmeDao != null)
            filmeDao.close();
        super.onDestroy();
    }

    public void mostrarLista(View v) {
        Intent intent = new Intent(InicioActivity.this, ListaActivity.class);
        startActivity(intent);
    }

    public void mostrarHistorico(View v) {
        Intent intent = new Intent(InicioActivity.this, HistoricoActivity.class);
        startActivity(intent);
    }

    public void mostrarFavoritos(View v) {
        Intent intent = new Intent(InicioActivity.this, FavoritosActivity.class);
        startActivity(intent);
    }

    public void mostrarCréditos(View v) {
        Intent intent = new Intent(InicioActivity.this, CreditosActivity.class);
        startActivity(intent);
    }

    private void removeFilmes() {
        List<Filme> filmes = filmeDao.getAll();
        if (filmes != null && filmes.size() >= 1) {
            for(int i = 0; i < filmes.size(); i++){
                long id = filmes.get(i).getId();
                filmeDao.remove(id);
            }
        }
    }

    private int qtdFilmes(){
        List<Filme> filmes = filmeDao.getAll();
        return filmes.size();
    }
}