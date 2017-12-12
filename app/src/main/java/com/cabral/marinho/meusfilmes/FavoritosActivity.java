package com.cabral.marinho.meusfilmes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        listViewFavoritos = (ListView) findViewById(R.id.listViewFavoritos);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String favoritos = prefs.getString("favoritos", "[]");
        if (favoritos.equals("[]"))
            Toast.makeText(this, "Nenhum título na lista", Toast.LENGTH_SHORT).show();
        else {
            filmeDao = new FilmeDao(this);
            filmeDao.open();
            List<Filme> filmes = filmeDao.getAll();
            try {
                jsonArray = new JSONArray(favoritos);
                int qtd = jsonArray.length();
                String[] lista = new String[qtd];
                for (int i = 0; i < qtd; i++) {
                    Long id = jsonArray.getLong(i);
                    int indice = 0;
                    while (indice < filmes.size()) {
                        Filme filme = filmes.get(indice);
                        if (filme.getId() == id) {
                            lista[i] = filme.getTitle();
                            break;
                        }
                        indice++;
                    }
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista);
                listViewFavoritos.setAdapter(adapter);
                listViewFavoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putLong("id", jsonArray.getLong(position));
                            filmeDao.close();
                            Intent intent = new Intent(FavoritosActivity.this, DetalhesActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            filmeDao.close();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Início");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();
                break;
            default:break;
        }
        return true;
    }

    public void mostrarInicio(View v){
        finish();
    }
}