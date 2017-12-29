package com.cabral.marinho.meusfilmes;

import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by marinho on 16/12/17.
 */

public class Data {

    private int dia, mes, ano;

    public Data(){
        Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(MONTH) + 1;
        ano = calendar.get(YEAR);
    }

    public Data(String stringData){ // dd/mm/aaaa
        dia = Integer.parseInt(stringData.substring(0,2));
        mes = Integer.parseInt(stringData.substring(3,5));
        ano = Integer.parseInt(stringData.substring(6,10));
    }

    public String toString(){
        String sDia = Integer.toString(dia);
        String sMes = Integer.toString(mes);
        String sAno = Integer.toString(ano);
        if (dia < 10)
            sDia = '0' + sDia;
        if (mes < 10)
            sMes = '0' + sMes;
        if (ano < 10)
            sAno = "000" + sAno;
        else if (ano < 100)
            sAno = "00" + sAno;
        else if (ano < 1000)
            sAno = '0' + sAno;

        return sDia + "/" + sMes + "/" + sAno;
    }

    public int getDia(){
        return dia;
    }

    public int getMes(){
        return mes;
    }

    public int getAno(){
        return ano;
    }

    public int compare(Data antigaData){
        int dias = 0;
        int meses = 0;
        int anos = 0;

        if(dia >= antigaData.getDia())
            dias = (dia  - antigaData.getDia());
        else {
            int d;
            if(mes == 4 || mes == 6 || mes == 9 || mes == 11)
                d = 30;
            else if((mes == 2) && (ano % 4 == 0) && ((ano % 400 == 0) || (ano % 100 != 0))) //ano bissexto
                d = 29;
            else if(mes == 2)
                d = 28;
            else
                d = 31;
            dias = (d - antigaData.getDia()) + dia;
            meses -= 1;
        }

        if(mes >= antigaData.getMes())
            meses += (mes - antigaData.getMes());
        else {
            meses += (12 - antigaData.getMes()) + mes;
            anos -= 1;
        }

        anos += (ano - antigaData.getAno());

        return dias + (meses * 30) + anos * 365;
    }
}
