package com.cabral.marinho.meusfilmes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class FavoritosActivity extends AppCompatActivity {

    private Button buttonInicio;
    private ListView listViewFavoritos;
    private ArrayAdapter<String> adapter;
    private FilmeDao filmeDao;
    private JSONArray jsonArray;
    private Long[] listaId;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        listViewFavoritos = (ListView) findViewById(R.id.listViewFavoritos);

    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String favoritos = prefs.getString("favoritos", "[]");
        if (favoritos.equals("[]"))
            Toast.makeText(this, "Nenhum título na lista", Toast.LENGTH_SHORT).show();
        filmeDao = new FilmeDao(this);
        filmeDao.open();
        List<Filme> filmes = filmeDao.getAll();
        try {
            jsonArray = new JSONArray(favoritos);
            int qtd = jsonArray.length();
            String[] listaTitulos = new String[qtd];
            listaId = new Long[qtd];
            for (int i = 0; i < qtd; i++) {
                String codigo = jsonArray.getString(i);
                int indice = 0;
                while (indice < filmes.size()) {
                    Filme filme = filmes.get(indice);
                    if (filme.getCodigo().equals(codigo)) {
                        listaTitulos[i] = filme.getTitle();
                        listaId[i] = filme.getId();
                        break;
                    }
                    indice++;
                }
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaTitulos);
            listViewFavoritos.setAdapter(adapter);
            listViewFavoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", listaId[position]);
                    filmeDao.close();
                    Intent intent = new Intent(FavoritosActivity.this, DetalhesActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                });
            } catch (JSONException e) {
                e.printStackTrace();

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menuvoltar, menu);
        return true;
    }

    @SuppressLint("NewApi")
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.menu_inicio){
            startActivity(new Intent(this, InicioActivity.class));
            finish();
            return true;
        }
        return false;
    }

    public void mostrarInicio(View v){
        finish();
    }
}