package com.cabral.marinho.meusfilmes;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
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

public class ListaActivity extends AppCompatActivity {

    private FilmeDao filmeDao;
    private Button buttonInicio;
    private ListView listViewLista;
    private ArrayAdapter<String> adapter;
    private List<Filme> filmes;
    private JSONArray jsonArray;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        buttonInicio = (Button)findViewById(R.id.buttonInicio);
        listViewLista = (ListView)findViewById(R.id.listViewLista);
        filmeDao = new FilmeDao(this);
        filmeDao.open();
        filmes = filmeDao.getAll();
        if(filmes.size() == 0)
            Toast.makeText(this, "Nenhum título encontrado.", Toast.LENGTH_LONG).show();
        String[] titulos = new String[filmes.size()];
        for(int i = 0; i < filmes.size(); i++){
            titulos[i] = Integer.toString(i + 1) + ". " + filmes.get(i).getTitle();
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titulos);
        listViewLista.setAdapter(adapter);
        listViewLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ListaActivity.this);
                String historico = prefs.getString("historico", "[]");
                try {
                    jsonArray = new JSONArray(historico);
                    removeAnteriores(filmes.get(position).getCodigo());
                    if(jsonArray.length() > 19)
                        jsonArray.remove(0);
                    JSONArray tempArray = new JSONArray();
                    tempArray.put(filmes.get(position).getCodigo());
                    if(jsonArray.length() > 0)
                        for(int i = 0; i < jsonArray.length(); i++)
                            tempArray.put(jsonArray.getString(i));
                    prefs = PreferenceManager.getDefaultSharedPreferences(ListaActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    historico = tempArray.toString();
                    editor.putString("historico", historico);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putLong("id", filmes.get(position).getId());
                filmeDao.close();
                Intent intent = new Intent(ListaActivity.this, DetalhesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void removeAnteriores(String codigo) throws JSONException {
        for(int i = 0; i < jsonArray.length(); i++){
            if(jsonArray.getString(i).equals(codigo))
                jsonArray.remove(i);
        }
    }

    @Override
    protected void onDestroy() {
        if(filmeDao != null)
            filmeDao.close();
        super.onDestroy();
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

}
