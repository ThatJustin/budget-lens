package com.codenode.budgetlens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);


      RadioButton home = findViewById(R.id.home);
      RadioButton dashboard = findViewById(R.id.dashboard);
      RadioButton notification = findViewById(R.id.notifications);
      RadioButton walk = findViewById(R.id.walk);
      RadioButton profile = findViewById(R.id.profile);

      TextView dateOfBirth = (TextView) findViewById(R.id.dateOfBirth);
      dateOfBirth.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            initCalendar(dateOfBirth);
         }
      });

      Button confirmButton = findViewById(R.id.confirm);
      confirmButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this,WalkActivity.class);
            intent.putExtra("flag","true");
            /*Bundle bundle=new Bundle();
            bundle.putString("flag", "true");
            intent.putExtras(bundle);*/
            startActivity(intent);
         }
      });

      home.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(intent);
         }
      });
      dashboard.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this,DashboardActivity.class);
            startActivity(intent);
         }
      });
      notification.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this,NotificationActivity.class);
            startActivity(intent);
         }
      });
      walk.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this,WalkActivity.class);
            startActivity(intent);
         }
      });
   }


   private void initCalendar(TextView textView) {

      Calendar calendar = Calendar.getInstance();

      DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener(){
         @Override
         public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String text = year + "/" + (month + 1) + "/" + dayOfMonth;
            textView.setText(text);
         }
      }
              , calendar.get(Calendar.YEAR)
              , calendar.get(Calendar.MONTH)
              , calendar.get(Calendar.DAY_OF_MONTH));

      dialog.show();
   }
}
