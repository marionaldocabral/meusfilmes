package com.cabral.marinho.meusfilmes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetalhesActivity extends AppCompatActivity {

    private FilmeDao filmeDao;
    private ConstraintLayout layoutFundo;
    private LinearLayout layoutCamada;
    private ImageButton imageButton;
    private TextView textTitulo;
    private TextView textAvaliacao;
    private TextView textIdioma;
    private TextView textTOriginal;
    private TextView textGeneros;
    private TextView textSinopse;
    private TextView textData;
    private List<Filme> filmes;
    private String link = "https://image.tmdb.org/t/p/w500";
    private String linkTrailler = "https://www.youtube.com/watch?v=";
    private String codigo;
    private Menu menu;
    private String compartilhar = "https://www.themoviedb.org/movie/";
    private ProgressBar progressBar;
    private TextView pAvaliacao;

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        class DownloaderTask extends AsyncTask<String, Void, List<String>> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                DetalhesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                progressDialog = new ProgressDialog(DetalhesActivity.this);
                progressDialog.setTitle("Aguarde");
                progressDialog.setMessage("Baixando...");
                progressDialog.show();
            }

            @SuppressLint("NewApi")
            protected void onPostExecute(List<String> downloads) {
                boolean b1, b2, b3;
                if(downloads.size() > 0 && downloads.get(0) != null && !downloads.get(0).equals("")) {
                    imageButton.setImageBitmap(stringToBitmap(downloads.get(0)));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("poster" + codigo, downloads.get(0));
                    editor.commit();
                    b1 = true;
                }
                else
                    b1 =false;
                if(downloads.size() > 1 && downloads.get(1) != null  && !downloads.get(0).equals("")) {
                    layoutFundo.setBackground(new BitmapDrawable(getApplicationContext().getResources(), stringToBitmap(downloads.get(1))));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("background" + codigo, downloads.get(1));
                    editor.commit();
                    b2 = true;
                }
                else
                    b2 = false;
                if(downloads.size() > 2 && downloads.get(2) != null  && !downloads.get(0).equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(downloads.get(2));
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        JSONObject json = (JSONObject) jsonArray.get(0);
                        String site = json.getString("site");
                        String key = json.getString("key");
                        Log.i("BAIXANDO LINK: ", "Site: " + site + "Key: " + key);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("site" + codigo, site);
                        editor.putString("key" + codigo, key);
                        editor.commit();
                        b3 = true;
                    } catch (JSONException e) {
                        b3 = false;
                        e.printStackTrace();
                    }
                }
                else
                    b3 = false;
                if(b1 && b2)
                    progressDialog.setMessage("Concluído!");
                else
                    Toast.makeText(DetalhesActivity.this, "Falha ao baixar conteúdo! Verifique suas conexões.", Toast.LENGTH_LONG).show();
                if(!b3)
                    Toast.makeText(DetalhesActivity.this, "Trailer indisponível.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                DetalhesActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }

            @Override
            protected List<String> doInBackground(String... params) {
                List<String> downloads = new ArrayList<>();
                //baixar poster
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    int httpResponse = httpURLConnection.getResponseCode();
                    if (httpResponse == 200) {
                        Log.i("BAIXANDO IMAGEM " + 0 + ": ", "Conexão bem sucedida!");
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        String stringImagem = bitmapToString(bitmap);
                        downloads.add(stringImagem);
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
                        String stringImagem = bitmapToString(bitmap);
                        downloads.add(stringImagem);
                    } else
                        Log.i("BAIXANDO IMAGEM " + 1 + ": ", "Falha na conexão!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //baixar link do trailler
                try {
                    URL url = new URL("https://api.themoviedb.org/3/movie/" + params[2] + "/videos?api_key=bad51705c7756f9ffdc7d3dc37b7aad2&language=pt-BR");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    int httpResponse = httpURLConnection.getResponseCode();
                    if (httpResponse == 200) {
                        Log.i("BAIXANDO LINK: ", "Conexão bem sucedida!");
                        String json = getString(httpURLConnection.getInputStream());
                        downloads.add(json);
                    } else
                        Log.i("BAIXANDO LINK: ", "Falha na conexão!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return downloads;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        layoutFundo = (ConstraintLayout)findViewById(R.id.layoutFundo);
        layoutCamada = (LinearLayout)findViewById(R.id.layoutCamada);
        layoutCamada.setBackgroundColor(Color.BLACK);
        layoutCamada.getBackground().setAlpha(200);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        textTitulo = (TextView)findViewById(R.id.textTitulo);
        textAvaliacao = (TextView)findViewById(R.id.textAvaliacao);
        textIdioma = (TextView)findViewById(R.id.textIdioma);
        textTOriginal = (TextView)findViewById(R.id.textTOriginal);
        textGeneros = (TextView)findViewById(R.id.textGeneros);
        textSinopse = (TextView)findViewById(R.id.textSinopse);
        textData = (TextView)findViewById(R.id.textData);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pAvaliacao = (TextView)  findViewById(R.id.myTextProgress);
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
        codigo = filme.getCodigo();
        textTitulo.setText(filme.getTitle());
        int avaliacao = (int)(filme.getVote_average() * 10);
        textAvaliacao.setText("Avaliação dos usuários: ");
        String idioma = filme.getOriginal_language();
        if(idioma.equals("en"))
            idioma = "Inglês";
        else if(idioma.equals("pt") || idioma.equals("pt-BR"))
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
        progressBar.setMax(100);
        progressBar.setProgress(avaliacao);
        pAvaliacao.setText(avaliacao + "%");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String stringBackground = prefs.getString("background" + codigo, "");
        String stringPoster = prefs.getString("poster" + codigo, "");
        String key = prefs.getString("key" + codigo, "");
        if(stringBackground.equals("")  || stringPoster.equals("")){
            DownloaderTask downloaderTask =  new DownloaderTask();
            downloaderTask.execute(link + filme.getPoster_path(), link + filme.getBackdrop_path(), filme.getCodigo());
        }
        else{
            Bitmap bitmapPoster = stringToBitmap(stringPoster);
            Bitmap bitmapBackground = stringToBitmap(stringBackground);
            imageButton.setImageBitmap(bitmapPoster);
            layoutFundo.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmapBackground));
        }
    }

    public void mostrarTrailler(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String site = prefs.getString("site" + codigo, "");
        String key = prefs.getString("key" + codigo, "");
        if(site.equals("YouTube")){
            Uri uri = Uri.parse(linkTrailler + key);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Não disponível!", Toast.LENGTH_LONG).show();
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
    private void adicionaFavorito() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "[]");
        JSONArray jsonArray = new JSONArray(favoritos);
        jsonArray.put(codigo);
        prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        favoritos = jsonArray.toString();
        editor.putString("favoritos", favoritos);
        editor.commit();
        Toast.makeText(this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show();
        menu.clear();
        getMenuInflater().inflate(R.menu.menucompartilhar, menu);
        getMenuInflater().inflate(R.menu.menudesfavoritar, menu);
        getMenuInflater().inflate(R.menu.menuvoltar, menu);
    }

    private boolean eFavorito() throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetalhesActivity.this);
        String favoritos = prefs.getString("favoritos", "[]");
        JSONArray jsonArray = new JSONArray(favoritos);
        boolean estaNaLista = false;
        for(int i = 0; i < jsonArray.length(); i++){
            if(jsonArray.getString(i).equals(codigo))
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
            if (jsonArray.getString(indice).equals(codigo))
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
        Toast.makeText(this, "Removido dos favoritos!", Toast.LENGTH_SHORT).show();
        menu.clear();
        getMenuInflater().inflate(R.menu.menucompartilhar, menu);
        getMenuInflater().inflate(R.menu.menufavoritar, menu);
        getMenuInflater().inflate(R.menu.menuvoltar, menu);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menucompartilhar, menu);
        try {
            if(eFavorito()){
                inflater.inflate(R.menu.menudesfavoritar, menu);

            }else{
                inflater.inflate(R.menu.menufavoritar, menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        inflater.inflate(R.menu.menuvoltar, menu);

        return true;
    }

    @SuppressLint("NewApi")
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menufavoritar:
                try {
                    adicionaFavorito();
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            case R.id.menudesfavoritar:
                try {
                    removeFavorito();
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case R.id.menu_inicio:
                startActivity(new Intent(this, InicioActivity.class));
                finish();
                return true;

            case R.id.menu_item_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String sharesub = "Confira os detalhes do filme \"" + textTitulo.getText() + "\" no site: " + compartilhar + codigo;
                myIntent.putExtra(Intent.EXTRA_TEXT, sharesub);
                startActivity(Intent.createChooser(myIntent, "Compartilhar usando:"));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}