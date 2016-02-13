package org.orangeresearch.beckon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompassFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompassFragment extends Fragment implements  LocationListener, SensorEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // JAMES Added Member Variables
    private static final String TAG = "COMPASS FRAGMENT";

    private Beckon mBeckon;

    private TextView mTitleField;
    private TextView mBearing;
    private TextView mSentBy;
    private TextView mDistanceFromHere;
    private TextView mDistanceFromOrigin;
    private TextView mDirection;



    private ImageView mCompassImage;
    private Context mContext;
    private LocationManager mLocationManager;
    private Location mCurDest;
    private Location mCurLoc;
    private Location mOrigin;
    private Double mTotalDistance;
    private ProgressBar mProgressBar;


    /* NEW VERSION OF COMPASS CODE */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mField;


    private float[] mGravity;
    private float[] mMagnetic;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CompassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompassFragment newInstance(String param1, String param2) {
        CompassFragment fragment = new CompassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void registerSensorListener() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mField, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new Beckon to hold the destination information
        mBeckon = new Beckon();

        // Initialize the Sensor Manager and Sensors needed
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Create Location objects for current location and destination
        mCurDest = new Location("PROVIDED");
        mOrigin = new Location("GPS");
        mCurLoc = new Location("GPS");

        mCurDest.setLatitude(Double.parseDouble(mBeckon.getLat()));
        mCurDest.setLongitude(Double.parseDouble(mBeckon.getLon()));

        // Ensure GPS permissions are enabled
        int hasGpsPermission = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if ( hasGpsPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        // Get Location Service
        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Try for GPS first, fall back to network provided location
        if ( mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
        }


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_compass, container, false);

        mTitleField = (TextView)v.findViewById(R.id.beacon_title);
        mTitleField.setText(mBeckon.getTitle());

        mBearing = (TextView)v.findViewById(R.id.beacon_bearing);
        mBearing.setText(R.string.calculating);

        mSentBy = (TextView)v.findViewById(R.id.sentBy);
        mSentBy.setText(new StringBuilder().append("  ").append(mBeckon.getSender()).toString());

        mDistanceFromOrigin = (TextView)v.findViewById(R.id.distanceFromOrigin);
        mDistanceFromHere = (TextView)v.findViewById(R.id.distanceFromHere);
        mDirection = (TextView)v.findViewById(R.id.direction);

        //mCompassImage = (ImageView)v.findViewById(R.id.imageViewCompass);

        mProgressBar = (ProgressBar)v.findViewById(R.id.distanceProgressBar);
        mProgressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);


          return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;


        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // PURPOSELY BLANK
    }


    private void updateDirection() {

        float[] temp = new float[9];
        float[] R = new float[9];

        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);

        //Return the orientation values
        float[] values = new float[3];

        SensorManager.getOrientation(R, values);

        //Convert to degrees
        for (int i=0; i < values.length; i++) {
            Double degrees = (values[i] * 180) / Math.PI;
            values[i] = degrees.floatValue();
        }
        //Display the compass direction
        //directionView.setText( getDirectionFromDegrees(values[0]) );
        //Display the raw values
        mBearing.setText(String.format("Azimuth: %1$1.2f, Pitch: %2$1.2f, Roll: %3$1.2f",
                values[0], values[1], values[2]));
    }


    @Override
    public void onSensorChanged(SensorEvent event){

        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetic = event.values.clone();
                break;
            default:
                return;
        }

        if(mGravity != null && mMagnetic != null) {
            updateDirection();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if(this.getUserVisibleHint()) {
            this.registerSensorListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterSensorListener();
    }


    @Override
    public void onLocationChanged(Location location){

        double distance;

        if (mOrigin.getLongitude() == 0) {
            mOrigin = location;
           distance = mOrigin.distanceTo(mCurDest);
            mDistanceFromOrigin.setText(Double.toString(convertMetersToFeet(distance)));
            mTotalDistance = distance;
        }

        mCurLoc = location;
        distance = mCurLoc.distanceTo(mCurDest);
        mDistanceFromHere.setText(Double.toString(convertMetersToFeet(distance)));

        Double pComplete = 100 * (1 - distance / mTotalDistance);
        if (pComplete < 0.0)
            pComplete = 0.0;

        mProgressBar.setProgress(pComplete.intValue());
        setDirection(mCurLoc.bearingTo(mCurDest));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Nothing Yet
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Nothing Yet
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Nothing Yet
    }

    private double convertMetersToFeet(double meters)
    {
        //function converts Feet to Meters.
        double toFeet;
        toFeet = meters*3.2808;  // official conversion rate of Meters to Feet
        String formattedNumber = new DecimalFormat("0").format(toFeet); //return with 4 decimal places
        return Double.valueOf(formattedNumber.trim());
    }

    private void setDirection ( float bearTo ){
        //Set the field

        if ( bearTo < 0 )
            bearTo += 360;

        String bearingText;

        if ( (360 >= bearTo && bearTo >= 337.5) || (0 <= bearTo && bearTo <= 22.5) ) bearingText = "N";
        else if (bearTo > 22.5 && bearTo < 67.5) bearingText = "NE";
        else if (bearTo >= 67.5 && bearTo <= 112.5) bearingText = "E";
        else if (bearTo > 112.5 && bearTo < 157.5) bearingText = "SE";
        else if (bearTo >= 157.5 && bearTo <= 202.5) bearingText = "S";
        else if (bearTo > 202.5 && bearTo < 247.5) bearingText = "SW";
        else if (bearTo >= 247.5 && bearTo <= 292.5) bearingText = "W";
        else if (bearTo > 292.5 && bearTo < 337.5) bearingText = "NW";
        else bearingText = "?";

        mDirection.setText(bearingText);
    }


}
