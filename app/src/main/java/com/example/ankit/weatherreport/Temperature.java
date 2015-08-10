package com.example.ankit.weatherreport;

import com.orm.SugarRecord;

import java.sql.Timestamp;

/**
 * Created by ankit on 10/08/15.
 */
public class Temperature extends SugarRecord<Temperature>{
    protected String temp;
    protected Long ts;

    public Temperature(){

    }
    public Temperature (String temp, Long ts){
       this.temp = temp;
       this.ts = ts;
    }

    protected String getTemp(){
        return this.temp;
    }

    protected Long getTime(){
        return this.ts;
    }
}
