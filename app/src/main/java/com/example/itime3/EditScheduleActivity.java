package com.example.itime3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 谭小二 on 2019/11/20.
 */

public class EditScheduleActivity extends AppCompatActivity {
private Button buttonCancel,buttonOk;
    private EditText editText_biaoti,editText_beizhu;
    int insertPosition;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
       buttonCancel=(Button)findViewById(R.id.button_cancel);
        buttonOk=(Button)findViewById(R.id.button_ok);
        editText_beizhu=(EditText)findViewById(R.id.edit_text_remark);
        editText_biaoti=(EditText)findViewById(R.id.edit_text_title);
       insertPosition=getIntent().getIntExtra("edit_position",0);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("edit_position",insertPosition);
                intent.putExtra("schedule_title",editText_biaoti.getText().toString());
                intent.putExtra("schedule_remark",editText_beizhu.getText().toString());
                setResult(RESULT_OK,intent);
                EditScheduleActivity.this.finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditScheduleActivity.this.finish();
            }
        });

    }
}
