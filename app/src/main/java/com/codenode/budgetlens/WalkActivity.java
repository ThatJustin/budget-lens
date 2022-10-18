package com.codenode.budgetlens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class WalkActivity extends AppCompatActivity  {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_walk);
      Intent intent=getIntent();
      String result=intent.getStringExtra("flag");
      Log.i("1111111111","112222"+result);
      Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT).show();
      if("true".equals(result)){
         showDialog();
      }

      RadioButton home = findViewById(R.id.home);
      RadioButton dashboard = findViewById(R.id.dashboard);
      RadioButton notification = findViewById(R.id.notifications);
      RadioButton walk = findViewById(R.id.walk);
      RadioButton profile = findViewById(R.id.profile);

      home.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(WalkActivity.this,MainActivity.class);
            startActivity(intent);
         }
      });
      dashboard.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(WalkActivity.this,DashboardActivity.class);
            startActivity(intent);
         }
      });
      notification.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(WalkActivity.this,NotificationActivity.class);
            startActivity(intent);
         }
      });
      profile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(WalkActivity.this,ProfileActivity.class);
            startActivity(intent);
         }
      });
   }

   private void showDialog() {
      AlertDialog dialog = new AlertDialog.Builder(this)
              .setMessage("CHANGED SUCCESSFULLY")
              .setPositiveButton("OK", null)
              .create();
      dialog.show();

      Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
      LinearLayout.LayoutParams cancelBtnPara = (LinearLayout.LayoutParams) button.getLayoutParams();
      //设置按钮的大小
      cancelBtnPara.height = LinearLayout.LayoutParams.WRAP_CONTENT;
      cancelBtnPara.width = LinearLayout.LayoutParams.MATCH_PARENT;
      //设置文字居中
      cancelBtnPara.gravity = Gravity.CENTER;
      //设置按钮左上右下的距离
      cancelBtnPara.setMargins(125, 10, 125, 10);
      button.setLayoutParams(cancelBtnPara);
      button.setBackground(ContextCompat.getDrawable(this, R.color.white));
      button.setTextSize(16);
      button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(WalkActivity.this,ProfileActivity.class);
            startActivity(intent);
         }
      });
   }
}
