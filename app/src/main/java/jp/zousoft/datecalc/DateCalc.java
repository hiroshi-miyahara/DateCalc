package jp.zousoft.datecalc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;

import java.util.Calendar;

public class DateCalc extends AppCompatActivity {
    private static String[] mWeek;
    private Calendar mDateBase;
    private Calendar mDateComp;
    private EditText mLog;
    private int mLines;
    private int mDiff;
    private float mSize;
    private ScrollView mScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_calc);

        mWeek = getResources().getStringArray(R.array.stat_week);
        mDiff = 0;
        mDateBase = Now();
        mDateComp = Now();

        // 基準日の変更
        findViewById(R.id.date_base).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputDate(mDateBase);
            }
        });
        // 比較日の変更
        findViewById(R.id.date_comp).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputDate(mDateComp);
            }
        });
        // 基準日を今日にする
        findViewById(R.id.btn_now).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateBase = Now();
                updateData(true);
            }
        });
        // 比較日を今日にする
        findViewById(R.id.btn_now2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateComp = Now();
                updateData(true);
            }
        });
        // 基準日を１日進める
        findViewById(R.id.btn_inc).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateBase.add(Calendar.DAY_OF_MONTH, 1);
                updateData(true);
            }
        });
        // 比較日を１日進める
        findViewById(R.id.btn_inc2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateComp.add(Calendar.DAY_OF_MONTH, 1);
                updateData(true);
            }
        });
        // 基準日を１日戻す
        findViewById(R.id.btn_dec).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateBase.add(Calendar.DAY_OF_MONTH, -1);
                updateData(true);
            }
        });
        // 比較日を１日戻す
        findViewById(R.id.btn_dec2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateComp.add(Calendar.DAY_OF_MONTH, -1);
                updateData(true);
            }
        });
        // 差分日数の入力
        findViewById(R.id.date_diff).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View aLayout = getLayoutInflater().inflate(R.layout.layout_date_dialog, null);
                final EditText aInput = (EditText) aLayout.findViewById(R.id.dlog_find_text);
                new AlertDialog.Builder(DateCalc.this)
                        .setTitle(R.string.dlog_find_title)
                        .setMessage(R.string.dlog_find_msg)
                        .setView(aLayout)
                        .setNegativeButton(R.string.finder_btn_cancel, null)
                        .setPositiveButton(R.string.finder_btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    mDiff = Integer.parseInt(aInput.getText().toString());
                                } catch (NumberFormatException e) {
                                    mDiff = 0;
                                }
                                ((TextView) findViewById(R.id.date_diff)).setText(Num(mDiff));
                                mDateComp.setTimeInMillis(mDateBase.getTimeInMillis());
                                mDateComp.add(Calendar.DAY_OF_MONTH, mDiff);
                                updateData(false);
                            }
                        })
                        .show();
            }
        });
        // ログのコピー
        findViewById(R.id.log_btn_copy).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ClipboardManager) DateCalc.this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("TEXT", mLog.getText().toString()));
            }
        });
        // ログのクリア
        findViewById(R.id.log_btn_clear).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLog.setText("");
                mLines = 0;
            }
        });

        ((TextView) findViewById(R.id.date_base)).setText(C2D(mDateBase));
        ((TextView) findViewById(R.id.date_comp)).setText(C2D(mDateComp));
        ((TextView) findViewById(R.id.date_diff)).setText(Num(mDiff));
        mLog = (EditText) findViewById(R.id.calc_log);
        mLog.setText("");
        mLines = 0;
        mSize = mLog.getTextSize();
        mScroll = (ScrollView) findViewById(R.id.log_scroll);
    }

    private Calendar Now() {
        Calendar aCal = Calendar.getInstance();
        aCal.set(Calendar.HOUR, 0);
        aCal.set(Calendar.MINUTE, 0);
        aCal.set(Calendar.SECOND, 0);
        aCal.set(Calendar.MILLISECOND, 0);

        return aCal;
    }

    private String Num(int cVal) {
        String S = "          " + cVal;
        return S.substring(S.length() - 10);
    }

    private void inputDate(final Calendar cCal) {
        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        cCal.set(Calendar.YEAR, year);
                        cCal.set(Calendar.MONTH, monthOfYear);
                        cCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateData(true);
                    }
                },
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateData(boolean cDate) {
        String DB = C2D(mDateBase);
        String DC = C2D(mDateComp);
        ((TextView) findViewById(R.id.date_base)).setText(DB);
        ((TextView) findViewById(R.id.date_comp)).setText(DC);
        String T = DB;
        if (cDate) {
            mDiff = (int) ((mDateComp.getTimeInMillis() - mDateBase.getTimeInMillis()) / 1000 / 3600 / 24);
            ((TextView) findViewById(R.id.date_diff)).setText(Num(mDiff));
            T += " -> " + DC + " = " + mDiff;
        } else {
            T += " + " + mDiff + " = " + DC;
        }
        // log
        mLog.append(T + "\n");
        mLog.setSelection(mLog.getText().length());
        mLines += mSize;
        mScroll.scrollTo(0, mLines);
    }

    private String C2D(Calendar C) {
        int W = C.get(Calendar.DAY_OF_WEEK);
        return C.get(Calendar.YEAR) + "/" + (1 + C.get(Calendar.MONTH)) + "/" + C.get(Calendar.DAY_OF_MONTH) + "(" + mWeek[W - 1] + ")";
    }
}
