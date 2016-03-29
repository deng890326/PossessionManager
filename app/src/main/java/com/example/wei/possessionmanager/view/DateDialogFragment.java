package com.example.wei.possessionmanager.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.example.wei.possessionmanager.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wei on 2016/2/29 0029.
 */
public class DateDialogFragment extends DialogFragment {

    private static final String ARGS_DATE = "args_date";
    public static final String EXTRA_DATE = "com.example.wei.possessionmanager.extra_date";

    @InjectView(R.id.date_picker)
    DatePicker mDatePicker;

    public static DateDialogFragment newInstance(Date date) {

        Bundle args = new Bundle();
        if (date == null) {
            date = new Date();
        }
        args.putSerializable(ARGS_DATE, date);
        DateDialogFragment fragment = new DateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARGS_DATE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_fragment_data, null);
        ButterKnife.inject(this, v);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);


        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        sendResult(Activity.RESULT_CANCELED, null);
                        break;
                }
                dialogInterface.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, dialogListener)
                .setNegativeButton(android.R.string.cancel, dialogListener);

        return builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent data = new Intent();
        if (date != null) {
            data.putExtra(EXTRA_DATE, date);
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
    }
}
