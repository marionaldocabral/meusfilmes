package com.cabral.marinho.meusfilmes;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private FilmeDao filmeDao;
    private Button buttonInicio;
    private ListView listViewLista;
    private ArrayAdapter<String> adapter;
    private List<Filme> filmes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        buttonInicio = (Button)findViewById(R.id.buttonInicio);
        listViewLista = (ListView)findViewById(R.id.listViewLista);
        filmeDao = new FilmeDao(this);
        filmeDao.open();
        filmes = filmeDao.getAll();
        String[] titulos = new String[filmes.size()];
        for(int i = 0; i < filmes.size(); i++){
            titulos[i] = Integer.toString(i + 1) + ". " + filmes.get(i).getTitle();
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titulos);
        listViewLista.setAdapter(adapter);
        listViewLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", filmes.get(position).getId());
                filmeDao.close();
                Intent intent = new Intent(ListaActivity.this, DetalhesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(filmeDao != null)
            filmeDao.close();
        super.onDestroy();
    }

    public void mostrarInicio(View v){
        finish();
    }
}
