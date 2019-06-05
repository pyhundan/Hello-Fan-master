/*
说明：一繁对多简中选择开始学习后弹出来的Activity。
*/

package com.test.fan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.dialog.ConfirmDialog;
import com.test.fan.DBhelper.DBHelper;

import java.util.ArrayList;

public class LearnS2TActivity extends AppCompatActivity {


    private DBHelper dbHelper;

    SQLiteDatabase sqLiteDatabase = null;

    private ArrayList List ;
    private ArrayList S_List;
    private ArrayList R_List;

    private String Right_text;

    private int flag1=0,flag2=0;
    private TextView S_textview;
    private TextView E_textview;
    private TextView W_textview;


    private LinearLayout mBtnListLayout=null;
    private LinearLayout S_text_lin=null;
    private LinearLayout E_text_lin=null;
    private LinearLayout W_text_lin=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_s2t);

        findAllView();
        dbHelper=new DBHelper(this);

        setShare();

        //存放繁体字
        ArrayList<String> T_List=getBtnContentList();
        generateBtnList(T_List);
        generateS_TextList(S_List);
        search_words(T_List);

    }

    private void findAllView()
    {

        S_textview=(TextView)this.findViewById(R.id.show_s);
        E_textview=(TextView)this.findViewById(R.id.show_exp);
        E_textview.setMovementMethod(ScrollingMovementMethod.getInstance());//添加滚动

        W_textview=(TextView)this.findViewById(R.id.show_words);

        S_text_lin=(LinearLayout)findViewById(R.id.Show_SLin);
        E_text_lin=(LinearLayout)findViewById(R.id.Show_expLin);
        W_text_lin=(LinearLayout)findViewById(R.id.show_wordsLin);

        mBtnListLayout=(LinearLayout)findViewById(R.id.btnlist);
    }

    private ArrayList<String> getBtnContentList()
    {   //这里根据数据库创建一个Arraylist

        List=new ArrayList<String>();
        S_List=new ArrayList<String>();
        R_List=new ArrayList<String>();

        ArrayList<String> btnContentList=new ArrayList<String>();

        sqLiteDatabase=dbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from s2taW", null);

        while (cursor.moveToNext()) {
            String tr = cursor.getString(cursor.getColumnIndex("ts"));
            String s=cursor.getString(cursor.getColumnIndex("simple"));
            String rt=cursor.getString(cursor.getColumnIndex("t"));

            R_List.add(rt);
            S_List.add(s+"→"+tr.replace(';',','));
            List.add(tr);

        }

        Right_text=R_List.get(flag1).toString();

        for (int index=0;index<List.get(flag1).toString().length();index++)
        {
            String temp=List.get(flag1).toString();
           // System.out.println(temp);
            //System.out.println(temp.charAt(index));
            btnContentList.add(String.valueOf(temp.charAt(index)));
        }
        flag1++;
        return btnContentList;
    }

    private void generateS_TextList(ArrayList<String> S_List)
    {
        if(S_List==null)
            return;

        for(int i=0;i<S_List.get(flag2).toString().length();i++)
        {
            String temp=S_List.get(flag2).toString();
            setS_textview(temp);
        }
        flag2++;

    }

    private void generateBtnList(ArrayList<String> btnContentList)
    {
        if(btnContentList==null)
        {
            return;
        }

        mBtnListLayout.removeAllViews();
        int index=0;
        for(String btnContent : btnContentList)
        {

            Button codeBtn=new Button(this);
            setBtnAttribute( codeBtn, btnContent, index, Color.TRANSPARENT, Color.BLACK, 24 );
            mBtnListLayout.addView( codeBtn );
            index++;

        }


    }

    private void setBtnAttribute(final Button codeBtn, final String btnContent, int id, int backGroundColor, int textColor, int textSize ){
        if( null == codeBtn ){
            return;
        }

        codeBtn.setBackgroundColor( ( backGroundColor >= 0 )?backGroundColor: Color.GRAY );
        codeBtn.setTextColor( ( textColor >= 0 )?textColor:Color.BLACK );
        codeBtn.setTextSize( ( textSize > 16 )?textSize:24 );
        codeBtn.setId( id );

        codeBtn.setBackgroundResource(R.drawable.shape_button);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        codeBtn.setWidth(dm.widthPixels);


        codeBtn.setText( String.valueOf(id+1)+"."+btnContent );

        codeBtn.setGravity( Gravity.CENTER );

        codeBtn.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                // btn click process
                if(btnContent.equals(Right_text))
                {
                    showRightDialog();
                }
                else{
                    showErrorDialog();
                }

            }
        });

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        rlp.addRule( RelativeLayout.CENTER_HORIZONTAL );

        codeBtn.setLayoutParams( rlp );
    }


    private void showErrorDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("提示");
        builder.setMessage("选择错误！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void showRightDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("提示");
        builder.setMessage("选择正确！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences userSettings = getSharedPreferences("setting", MODE_PRIVATE);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putInt("flag",flag1);
                        editor.commit();

                        ArrayList<String> T_List=getBtnContentList();

                        generateBtnList(T_List);

                        generateS_TextList(S_List);

                        search_words(T_List);


                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void setS_textview(String text)
    {

        S_text_lin.removeAllViews();
        S_textview.setText(text);
        S_textview.setTextSize(24);
        S_text_lin.addView(S_textview);
    }

    private void setE_textview(String text)
    {
        E_text_lin.removeAllViews();
        E_textview.setText(text);
        E_textview.setTextSize(24);
        E_text_lin.addView(E_textview);
    }

    private void setW_textview(String text)
    {
        W_text_lin.removeAllViews();
        W_textview.setText(text);
        W_textview.setTextSize(24);
        W_text_lin.addView(W_textview);
    }

    private void search_words(ArrayList<String> T_list)
    {

            String tr="",s="";
            sqLiteDatabase = dbHelper.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("select * from dict where words like '%" + Right_text + "%'", null);
            if (cursor.getCount()==0)
            {
                //System.out.println("空");
                setW_textview("\""+Right_text+"\""+"在繁体中不存在组词");
                setE_textview("请选择"+"\""+Right_text+"\"");

            }
            else{

                while (cursor.moveToNext()) {
                    tr = cursor.getString(cursor.getColumnIndex("words"));

                    s=cursor.getString(cursor.getColumnIndex("express"));

                }
                tr=tr.replaceFirst(Right_text,"__");
                setW_textview(tr);
                setE_textview(s);
            }




    }
    private void setShare()
    {
        SharedPreferences userSettings = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();

        int result = userSettings.getInt("flag", 0);

        if (result==0)
        {
            editor.putInt("flag", 0);
        }
        else
        {
            flag1=flag2=result;

        }

    }
    protected void onRestart() {
        super.onRestart();
    }

}
