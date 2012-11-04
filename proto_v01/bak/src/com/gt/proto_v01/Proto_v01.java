package com.gt.proto_v01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



public class Proto_v01 extends Activity {
	
	Button lvl1, lvl2, lvl3;
	TextView tv1,tv2;
	LayoutInflater linflater;
    LinearLayout l;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL); 
        
        tv1 = new TextView(this);
        tv1.setText("Choose a level");
        l.addView(tv1);
        
        lvl1 = new Button(this);
        lvl1.setText("Level 1");
        lvl1.setOnClickListener(new lvl1Listener());
        
        l.addView(lvl1);
        setContentView(l);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_proto_v01, menu);
        return true;
    }
    
    class lvl1Listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tv1.setText("Launching level 1...");
            Intent intent = new Intent(v.getContext(), Level1.class);
            startActivity(intent);
        }
    }
}












