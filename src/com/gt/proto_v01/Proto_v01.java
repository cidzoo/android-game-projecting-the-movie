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
	
	Button lvl1, lvl2, lvl3, lvl4;
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
        
        lvl2 = new Button(this);
        lvl2.setText("Level 2");
        lvl2.setOnClickListener(new lvl2Listener());
        l.addView(lvl2);
        
        lvl3 = new Button(this);
        lvl3.setText("Level 3");
        lvl3.setOnClickListener(new lvl3Listener());
        l.addView(lvl3);
        
        lvl4 = new Button(this);
        lvl4.setText("Level 4");
        lvl4.setOnClickListener(new lvl4Listener());
        l.addView(lvl4);
        
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
    
    class lvl2Listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tv1.setText("Launching level 2...");
            Intent intent = new Intent(v.getContext(), Level2.class);
            startActivity(intent);
        }
    }
    
    class lvl3Listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tv1.setText("Launching level 3...");
            Intent intent = new Intent(v.getContext(), Level3.class);
            startActivity(intent);
        }
    }
    
    class lvl4Listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tv1.setText("Launching level 4...");
            Intent intent = new Intent(v.getContext(), Level4.class);
            startActivity(intent);
        }
    }
}












