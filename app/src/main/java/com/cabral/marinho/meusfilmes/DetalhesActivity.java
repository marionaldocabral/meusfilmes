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
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    String stringBitmap = bitmapToString(bitmap);
                    editor.putString("poster" + idSelecionado, stringBitmap);
                    editor.commit();
                    b1 = true;
                }
                else
                    b1 =false;
                if(imagens.get(1) != null) {
                    Bitmap bitmap = imagens.get(1);
                    layoutFundo.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    String stringBitmap = bitmapToString(bitmap);
                    editor.putString("background" + idSelecionado, stringBitmap);
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
        String generos = "";
        try {
            JSONArray idGenreros = new JSONArray(filme.getGenre_ids());
            for(int i = 0; i < idGenreros.length(); i++){
                generos = generos + getGeneroPorId(idGenreros.getString(i));
                if(i < (idGenreros.length() - 1))
                    generos = generos + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textGeneros.setText(generos);
        textSinopse.setText(filme.getOverview() + "\n");
        textData.setText("Lançamento: " + dataConvertida(filme.getRelease_date()));
        try {
            if(eFavorito()){
                button.setText("Remover Favorito");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String stringBackground = prefs.getString("background" + idSelecionado, "");
        String stringPoster = prefs.getString("poster" + idSelecionado, "");
        if(stringBackground.equals("")  || stringPoster.equals("")){
            DownloaderTask downloaderTask =  new DownloaderTask();
            downloaderTask.execute(link + filme.getPoster_path(), link + filme.getBackdrop_path());
        }
        else{
            Bitmap bitmapPoster = stringToBitmap(stringPoster);
            Bitmap bitmapBackground = stringToBitmap(stringBackground);
            imageView.setImageBitmap(bitmapPoster);
            layoutFundo.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmapBackground));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Início");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, InicioActivity.class));
                finish();
                break;
            default:break;
        }
        return true;
    }

    private  String getGeneroPorId(String id) {
        switch (id) {
            case "28":
                return "Ação";
            case "12":
                return "Aventura";
            case "16":
                return "Animação";
            case "35":
                return "Comédia";
            case "80":
                return "Crime";
            case "99":
                return "Documentário";
            case "18":
                return "Drama";
            case "10751":
                return "Família";
            case "14":
                return "Fantasia";
            case "36":
                return "História";
            case "27":
                return "Terror";
            case "10402":
                return "Música";
            case "9648":
                return "Mistério";
            case "10749":
                return "Romance";
            case "878":
                return "Ficção científica";
            case "10770":
                return "Cinema TV";
            case "53":
                return "Thriller";
            case "10752":
                return "Guerra";
            case "37":
                return "Faroeste";
            default:
                return "";
        }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClick(View v) throws JSONException {
        if(!eFavorito()){
            adicionaFavorito();
        }
        else
            removeFavorito();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void adicionaFavorito() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "[]");
        JSONArray jsonArray = new JSONArray(favoritos);
        jsonArray.put(idSelecionado);
        prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        favoritos = jsonArray.toString();
        editor.putString("favoritos", favoritos);
        editor.commit();
        button.setText("Remover Favorito");
        Toast.makeText(this, "Adicionado!", Toast.LENGTH_SHORT).show();
    }

    private boolean eFavorito() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "[]");
        JSONArray jsonArray = new JSONArray(favoritos);
        boolean estaNaLista = false;
        for(int i = 0; i < jsonArray.length(); i++){
            if(jsonArray.getLong(i) == idSelecionado)
                estaNaLista = true;
        }
        return estaNaLista;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void removeFavorito() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "[]");
        JSONArray jsonArray = new JSONArray(favoritos);
        boolean achei = false;
        int indice = 0;
        while (indice < jsonArray.length() && !achei) {
            if (jsonArray.getLong(indice) == idSelecionado)
                achei = true;
            else
                indice++;
        }
        jsonArray.remove(indice);
        prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        favoritos = jsonArray.toString();
        editor.putString("favoritos", favoritos);
        editor.commit();
        button.setText("Adicionar Favorito");
        Toast.makeText(this, "Removido!", Toast.LENGTH_SHORT).show();
    }

    public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String stringByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return stringByte;
    }

    public Bitmap stringToBitmap(String stringBitmap){
        try {
            byte [] encodeByte = Base64.decode(stringBitmap,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}