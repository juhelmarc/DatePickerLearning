package fr.marc.datepickertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //We will get and set the date on this button int his format "JUIL.23.2021" / he his set by default with currentDate (new Dates().getTime)
    @BindView(R.id.datePickerButton)
    Button mDate;
    //with timePickerDialog we will get and set the time on this : hh:mm
    @BindView(R.id.hour)
    EditText mHour;
    //for show the time in milliseconds picked by the user // calendar.getTimeMillis()
    @BindView( R.id.milli )
    TextView mMilli;
    //juillet.ven..2021 ; 23/07/2021 ; 14:00"  we format the time in millis with simpleDateFormat
    @BindView( R.id.date )
    TextView mDateFormated;
    //Default = String "45" // when we chose à duration in minutes we want to show our : calendar time (in millis) + the duration (in millis) = calendar.getTimeMillis() + Long.parseLong(mDuration.getText().toString) * 60 (sec) * 1000 (milliSeconds)
    @BindView( R.id.duration_minute )
    EditText mDuration;
    //And show on this TextView the update of this second date / with the same format as the TextView mMilli;
    @BindView( R.id.end_date )
    TextView mEndDate;


    private Long currentDatePicker;
    private Long datePickedMilli;
    private Long timePickedMilli;
    private Long endDatePickedMilli;
    private Long endTimePickedMilli;
    private Long duration;

    private String currentDate;
    private String datePicked;
    private String timePicked;

    private DatePickerDialog mDatePickerDialog;

    //---------------Important : pour les heures il faut bien prendre HOUR_OF_DAY ; HOUR renvoie l'heure choisi + 12h00--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ButterKnife.bind(this);

        currentDatePicker = new Date().getTime();
        currentDate = formatDate( currentDatePicker );
        mDate.setText( currentDate );
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get( Calendar.YEAR );
        int m = calendar.get( Calendar.MONTH );
        int d = calendar.get( Calendar.DAY_OF_MONTH );
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        mDuration.setText("45");
        duration = Long.parseLong( mDuration.getText().toString() ) * 60L * 1000L;
            Log.d( "sos", "duration in milli = " + duration );
        setEditText( currentDatePicker );
        setEndDate(currentDatePicker + duration );
        // datePickerDialog creat and set
        mDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    //C'est au niveau du onDateSet que nous récupérons les entrées de l'utilisateur et qu'il faut les set à notre calendar pour pouvoir exploiter les valeurs
                    //set value to calendar
                    calendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                    calendar.set(Calendar.MONTH, view.getMonth());
                    calendar.set(Calendar.YEAR, view.getYear());
                    //récupérer la date de clalendar en milliseconde
                    datePickedMilli = calendar.getTimeInMillis() ;
                    endDatePickedMilli = datePickedMilli + duration ;

                    datePicked = formatDate( datePickedMilli );
                    mDate.setText( datePicked );

                    setEditText( datePickedMilli);
                    setEndDate( endDatePickedMilli );

                    }
                };
                // valorisation de mDatePickerDialog par l'instance de DatePickerDialog avec en paramètre l'activité courrante, le dateSetListner et l'année, le mois et le jour
                mDatePickerDialog = new DatePickerDialog( MainActivity.this, dateSetListener, y, m, d );
                mDatePickerDialog.show();
            }
        } );
        mHour.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, view.getHour());
                        calendar.set(Calendar.MINUTE, view.getMinute());
                        timePickedMilli = calendar.getTimeInMillis();
                        endTimePickedMilli = timePickedMilli + duration;
                        String format = "kk:mm";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
                        timePicked = simpleDateFormat.format(timePickedMilli);
                        mHour.setText( timePicked );
                        //to update our 3 TextView
                        setEditText( timePickedMilli);
                        setEndDate( endTimePickedMilli );
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener, h, min, true);
                timePickerDialog.show();
            }
        } );
        mDuration.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textDuration = mDuration.getText().toString();
                mDuration.setText( textDuration  );
                duration = Long.parseLong( textDuration ) * 60L * 1000L;
                endDatePickedMilli = calendar.getTimeInMillis() + duration;
                setEndDate( endDatePickedMilli );
            }

        } );
    }
    // method for formate our dateMillisecond in string with SimpleDateFormat
    public String formatDate(Long dateMilli) {
        String format = "MMMdd.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
        return simpleDateFormat.format( dateMilli );
    }
    public void setEditText(Long timeMilli) {
        mMilli.setText( "Time in milli is : " + timeMilli );
        String dateFormat = "MMMM.EEE.yyyy ; dd/MM/yyyy ; kk:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.FRANCE);
        mDateFormated.setText(simpleDateFormat.format( timeMilli ));


    }
    public void setEndDate(Long timeMilli) {
        String endDateFormat = "MMMM.EEE.yyyy ; dd/MM/yyyy ; kk:mm";
        SimpleDateFormat simpleEndDateFormat = new SimpleDateFormat(endDateFormat, Locale.FRANCE);
        mEndDate.setText(simpleEndDateFormat.format( timeMilli ));
    }
}