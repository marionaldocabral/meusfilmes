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

public class HistoricoActivity extends AppCompatActivity {

    private Button buttonInicio;
    private ListView listViewHistorico;
    private ArrayAdapter<String> adapter;
    private FilmeDao filmeDao;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        buttonInicio = (Button)findViewById(R.id.buttonInicio);
        listViewHistorico = (ListView)findViewById(R.id.listViewHistorico);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String historico = prefs.getString("historico", "[]");
        if (historico.equals("[]"))
            Toast.makeText(this, "Nenhum título na lista", Toast.LENGTH_SHORT).show();
        else {
            filmeDao = new FilmeDao(this);
            filmeDao.open();
            List<Filme> filmes = filmeDao.getAll();
            try {
                jsonArray = new JSONArray(historico);
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
                listViewHistorico.setAdapter(adapter);
                listViewHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putLong("id", jsonArray.getLong(position));
                            filmeDao.close();
                            Intent intent = new Intent(HistoricoActivity.this, DetalhesActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
}