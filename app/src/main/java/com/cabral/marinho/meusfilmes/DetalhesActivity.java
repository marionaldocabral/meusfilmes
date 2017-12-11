package com.cabral.marinho.meusfilmes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetalhesActivity extends AppCompatActivity {

    private FilmeDao filmeDao;
    private ConstraintLayout layoutFundo;
    private LinearLayout layoutCamada;
    private ImageView imageView;
    private TextView textTitulo;
    private TextView textAvaliacao;
    private TextView textIdioma;
    private TextView textTOriginal;
    private TextView textGeneros;
    private TextView textSinopse;
    private TextView textData;
    private List<Filme> filmes;
    private String link = "https://image.tmdb.org/t/p/w500";
    private Button button;
    private Long idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        class DownloaderTask extends AsyncTask<String, Void, List<Bitmap>> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(DetalhesActivity.this);
                progressDialog.setTitle("Aguarde");
                progressDialog.setMessage("Baixando...");
                progressDialog.show();
            }

            @SuppressLint("NewApi")
            protected void onPostExecute(List<Bitmap> imagens) {
                boolean b1, b2;
                if(imagens.get(0) != null) {
                    Bitmap bitmap = imagens.get(0);
                    imageView.setImageBitmap(bitmap);
                    b1 = true;
                }
                else
                    b1 =false;
                if(imagens.get(1) != null) {
                    layoutFundo.setBackground(new BitmapDrawable(getApplicationContext().getResources(), imagens.get(1)));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    boolean baixou = true;
                    editor.putBoolean("background", baixou);
                    editor.commit();
                    b2 = true;
                }
                else
                    b2 =false;
                if(b1 && b2)
                    progressDialog.setMessage("Concluído!");
                else
                    progressDialog.setMessage("Falha ao baixar imagens!");
                progressDialog.dismiss();
            }

            @Override
            protected List<Bitmap> doInBackground(String... params) {
                List<Bitmap> imagens = new ArrayList<>();
                //baixar poster
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    int httpResponse = httpURLConnection.getResponseCode();
                    if (httpResponse == 200) {
                        Log.i("BAIXANDO IMAGEM " + 0 + ": ", "Conexão bem sucedida!");
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        imagens.add(bitmap);
                    } else
                        Log.i("BAIXANDO IMAGEM " + 0 + ": ", "Falha na conexão!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //baixar background
                try {
                    URL url = new URL(params[1]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    int httpResponse = httpURLConnection.getResponseCode();
                    if (httpResponse == 200) {
                        Log.i("BAIXANDO IMAGEM " + 1 + ": ", "Conexão bem sucedida!");
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        imagens.add(bitmap);
                    } else
                        Log.i("BAIXANDO IMAGEM " + 1 + ": ", "Falha na conexão!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imagens;
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        layoutFundo = (ConstraintLayout)findViewById(R.id.layoutFundo);
        layoutCamada = (LinearLayout)findViewById(R.id.layoutCamada);
        layoutCamada.setBackgroundColor(Color.BLACK);
        layoutCamada.getBackground().setAlpha(200);
        imageView = (ImageView)findViewById(R.id.imageView);
        textTitulo = (TextView)findViewById(R.id.textTitulo);
        textAvaliacao = (TextView)findViewById(R.id.textAvaliacao);
        textIdioma = (TextView)findViewById(R.id.textIdioma);
        textTOriginal = (TextView)findViewById(R.id.textTOriginal);
        textGeneros = (TextView)findViewById(R.id.textGeneros);
        textSinopse = (TextView)findViewById(R.id.textSinopse);
        textData = (TextView)findViewById(R.id.textData);
        button = (Button)findViewById(R.id.button);
        filmeDao = new FilmeDao(this);
        filmeDao.open();
        filmes = filmeDao.getAll();
        int indice = 0;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long id = bundle.getLong("id");
        while(indice < filmes.size()){
            if(filmes.get(indice).getId() == id)
                break;
            else
                indice++;
        }
        Filme filme = filmes.get(indice);
        idSelecionado = filme.getId();
        textTitulo.setText(filme.getTitle());
        int avaliacao = (int)(filme.getVote_average() * 10);
        textAvaliacao.setText("Avaliação dos usuários: " + avaliacao + "%");
        String idioma = filme.getOriginal_language();
        if(idioma.equals("en"))
            idioma = "Inglês";
        else if(idioma.equals("pt") || idioma.equals("pt"))
            idioma = "Português";
        else if (idioma.equals("es"))
            idioma = "Espanhol";
        else if (idioma.equals("fr"))
            idioma = "Francês";
        textIdioma.setText("Idioma: " + idioma);
        textTOriginal.setText("Título Original: " + filme.getOriginal_title());
        textGeneros.setText("Gêneros: " + filme.getGenre_ids());
        textSinopse.setText(filme.getOverview() + "\n");
        textData.setText("Lançamento: " + dataConvertida(filme.getRelease_date()));
        DownloaderTask downloaderTask =  new DownloaderTask();
        downloaderTask.execute(link + filme.getPoster_path(), link + filme.getBackdrop_path());
    }

    private String dataConvertida(String data){
        String dia, mes, ano;
        dia = data.substring(8, 10);
        mes = data.substring(5, 7);
        ano = data.substring(0, 4);
        return dia + "/" + mes + "/" + ano;
    }

    @Override
    protected void onDestroy() {
        if(filmeDao != null)
            filmeDao.close();
        super.onDestroy();
    }

    public void adicionarFavorito(View v){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "");
        boolean achei = false;
        if(!favoritos.equals("")) {
            listaIdFavoritos(favoritos);
            /*Long[] listaId = listaIdFavoritos(favoritos);
            for(int i = 0; i < listaId.length; i++){
                if(listaId[i] == idSelecionado)
                    achei = true;
            }
            if(!achei){
                favoritos = favoritos + Long.toString(idSelecionado) + ',';
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("favoritos", favoritos);
                editor.commit();
                Toast.makeText(this, "Adicionado1!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "O título já pertence aos favoritos", Toast.LENGTH_SHORT).show();*/
        }
        else {/*
            favoritos = Long.toString(idSelecionado) + ',';
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("favoritos", favoritos);
            editor.commit();
            Toast.makeText(this, "Adicionado2!", Toast.LENGTH_SHORT).show();*/
        }
    }

    private Long[] listaIdFavoritos(String stringFavoritos){
        int qtdFavoritos = 0;
        for(int i = 0; i < stringFavoritos.length(); i++){
            if(stringFavoritos.charAt(i) == ',')
                qtdFavoritos++;
        }
        Long[] lista = new Long[qtdFavoritos];
        int inicio, fim;
        inicio = 0;
        fim = stringFavoritos.indexOf(',') - 1;
       // for(int i = 0; i < qtdFavoritos; i++){
            //lista[0] = Long.parseLong(stringFavoritos.substring(inicio, fim));
            inicio = fim + 2;
            stringFavoritos = stringFavoritos.substring(inicio);
        Toast.makeText(this, stringFavoritos, Toast.LENGTH_SHORT).show();
            inicio = 0;
            fim = stringFavoritos.indexOf(',') -1;
        //}
        return lista;
    }


}