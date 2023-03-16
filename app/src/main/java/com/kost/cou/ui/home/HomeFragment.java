package com.kost.cou.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kost.cou.R;
import com.kost.cou.base.DataBaseHelper;
import com.kost.cou.databinding.FragmentHomeBinding;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class HomeFragment extends Fragment {
        private FragmentHomeBinding binding;
        private DataBaseHelper dataBaseHelper;
        private SQLiteDatabase db;
        Cursor cursor;
        private ListView listView;
        private String dateForDB = "";
        private  String timeNow;



        @Override
        public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }



        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            HomeViewModel homeViewModel =
                    new ViewModelProvider(this).get(HomeViewModel.class);

            binding = FragmentHomeBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            dataBaseHelper = new DataBaseHelper(getActivity(), "records", null, 1);

            db = dataBaseHelper.getWritableDatabase();
            listView = binding.listData;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Query(simpleDateFormat.format(new Date()));
            // отображение данных из б-

            binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    try {
                        if (dayOfMonth < 10 && month < 10) {
                            dateForDB = year + "-0" + (month + 1) + "-0" + dayOfMonth;
                        } else if (dayOfMonth < 10) {
                            dateForDB =  year + "-" + (month + 1) + "-0" + dayOfMonth;
                        } else if (month < 10) {
                            dateForDB = year + "-0" + (month + 1) + "-" + dayOfMonth;
                        } else {
                            dateForDB = year + "-" + (month + 1) + "-" + dayOfMonth;
                        }
                        // Взятие даты с календаря
                        Query(dateForDB); // Взятие  данных из бд в listView
                        Log.i(null, "Взятие ------------------------------------------------------------------------------------------");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db = dataBaseHelper.getWritableDatabase();

                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(getContext());
                    View addDialog = inflater.inflate(R.layout.add_dialog, null);
                    builderDialog.setView(addDialog);
                    EditText upPres = (EditText) addDialog.findViewById(R.id.input_up_pres);
                    upPres.requestFocus();
                    upPres.setFocusableInTouchMode(true);
                    InputMethodManager imm = (InputMethodManager) getLayoutInflater().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

//                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    EditText dwPres = (EditText) addDialog.findViewById(R.id.input_dw_pres);
                    EditText puls = (EditText) addDialog.findViewById(R.id.input_puls);

                    final boolean[] boolEdit = {false, false, false};

                    builderDialog.setCancelable(false)
                            .setPositiveButton("Добавить",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                Log.i(null, "Ввод--------------------------------------------------------------------------------------------");
                                                Insert(upPres, dwPres, puls);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    })
                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builderDialog.create();

                    upPres.addTextChangedListener(new TextWatcher() {
                        int length;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            length = after;
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (length != s.length() ) {
                                boolEdit[0] = true;
                                if (boolEdit[0] && boolEdit[1] && boolEdit[2]) {
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                                }
                            }
                        }
                    });

                    dwPres.addTextChangedListener(new TextWatcher() {
                        int length;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            length = after;
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            if (length != s.length() ) {
                                boolEdit[1] = true;
                                if (boolEdit[0] && boolEdit[1] && boolEdit[2]) {
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                                }
                            }
                        }
                    });

                    puls.addTextChangedListener(new TextWatcher() {
                        int length;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            length = after;
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (length != s.length() ) {
                                boolEdit[2] = true;
                                if (boolEdit[0] && boolEdit[1] && boolEdit[2]) {
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN);

                                }
                            }
                        }
                    });

                    puls.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE && boolEdit[0] && boolEdit[1] && boolEdit[2]) {
                                Insert(upPres, dwPres, puls);      //match this behavior to your 'Send' (or Confirm) button
                                alertDialog.cancel();                                       
                            }
                            return true;
                        }
                    });
//                    puls.setOnKeyListener(new View.OnKeyListener() {
//                        @Override
//                        public boolean onKey(View v, int keyCode, KeyEvent event) {
//                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                                Toast.makeText(getLayoutInflater().getContext(), "СРАБОТАЛО", Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
//
//                            return false;
//                        }
//                    });

                    alertDialog.show();

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);



                }
            });

            // Удаление данных с бд с помощью listView
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    db = dataBaseHelper.getWritableDatabase();

                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(getContext());

                    builderDialog.setTitle("Вы точно хотите удалить?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TextView textView = view.findViewById(R.id.ID);
                                    long _id = Long.parseLong(textView.getText().toString());
                                    Delete(_id);
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builderDialog.create();
                    alertDialog.show();
                }
            });


            return root;
        }


        /**
         *  Взятие данных из бд
         */
        public void Query(String date /* Поиск данных по дате: date */){

            cursor = db.query("records",null, "date = ?",
                    new String[]{date},null,null,null);
            if(cursor.moveToFirst()){
                do{
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") String date_db = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") int up_pres_dp = cursor.getInt(cursor.getColumnIndex("up_pres"));
                    @SuppressLint("Range") int dw_pres_dp = cursor.getInt(cursor.getColumnIndex("dw_pres"));
                    @SuppressLint("Range") int puls_dp = cursor.getInt(cursor.getColumnIndex("puls"));
                }while(cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list,
                        cursor, new String[]{"date", "up_pres", "dw_pres", "puls", "_id"}, new int[]{R.id.DATE, R.id.UPPRES,
                        R.id.DWPRES, R.id.PULS, R.id.ID}, 0);
                listView.setAdapter(adapter);
            }
            Log.i(null, "Взятие Query--------------------------------------------------------------------------------------------");
        }

        /**
         *   Вставка данных в бд
         */

        @SneakyThrows
        public void Insert(EditText upPres, EditText dwPres, EditText puls){
            ContentValues values = new ContentValues();
            String localDate;
            if (!dateForDB.isEmpty()) {
                localDate = dateForDB;
                Log.i(null, dateForDB + " dateForDB------------------------------------");
            } else {
                timeNow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                localDate = timeNow;
                Log.i(null, timeNow + " timeNow------------------------------------");
            }
            values.put("date", localDate);
            values.put("up_pres", upPres.getText().toString());
            values.put("dw_pres", dwPres.getText().toString());
            values.put("puls", puls.getText().toString());
            db.insert("records", null, values);
            Query(localDate);
            Log.i(null, "Ввод INSERT--------------------------------------------------------------------------------------------");
        }

        public void Delete(long id){
            db.delete("records", "_id = ?", new String[]{String.valueOf(id)});
            Query(dateForDB);
            Log.i(null, id + " deleted ---------------------------------------------------------------------");
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
            if (db != null) {
                db.close();
            }

        }
    }