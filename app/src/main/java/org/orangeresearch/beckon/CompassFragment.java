package org.orangeresearch.beckon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


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
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private ImageView mCompassImage;
    private Context mContext;
    private LocationManager mLocationManager;
    private float mCurrentDegree;
    private Double mCurLat;
    private Double mCurLon;
    private Location mCurDest;
    private Location mCurLoc;
    private float[] rMat = new float[9];
    private int mAzimuth = 0;
    float[] orientation = new float[3];

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
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeckon = new Beckon();
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mCurDest = new Location("PROVIDED");
        mCurLoc = new Location("GPS");
        mCurDest.setLatitude(Double.parseDouble(mBeckon.getLat()));
        mCurDest.setLongitude(Double.parseDouble(mBeckon.getLon()));

        int hasGpsPermission = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if ( hasGpsPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        if ( mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
        }


        mCurrentDegree = 0f;

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
        mBearing.setText("Calculating...");

        mCompassImage = (ImageView)v.findViewById(R.id.imageViewCompass);

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

    @Override
    public void onSensorChanged(SensorEvent event){


        float azimuth = event.values[0];
       // azimuth = (float) Math.toDegrees(azimuth);

        GeomagneticField geoField = new GeomagneticField( Double
                .valueOf( mCurLoc.getLatitude() ).floatValue(), Double
                .valueOf( mCurLoc.getLongitude() ).floatValue(),
                Double.valueOf( mCurLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        float[] v = event.values;

        double lat=mCurDest.getLatitude();
        double lon=mCurDest.getLongitude();

        //The current location of the device, retrieved from another class managing GPS
        double ourlat=  mCurLoc.getLatitude();
        double ourlon=  mCurLoc.getLongitude();

        //Manually calculate the direction of the pile from the device
        double a= Math.abs((lon-ourlon));
        double b= Math.abs((lat-ourlat));
        //archtangent of a/b is equal to the angle of the device from 0-degrees in the first quadrant. (Think of a unit circle)
        double thetaprime= Math.atan(a/b);
        double theta= 0;

        if((lat<ourlat)&&(lon>ourlon)){//-+
            //theta is 180-thetaprime because it is in the 2nd quadrant
            theta= ((Math.PI)-thetaprime);

            //subtract theta from the compass value retrieved from the sensor to get our final direction
            theta=theta - Math.toRadians(v[0]);

        }else if((lat<ourlat)&&(lon<ourlon)){//--
            //Add 180 degrees because it is in the third quadrant
            theta= ((Math.PI)+thetaprime);

            //subtract theta from the compass value retreived from the sensor to get our final direction
            theta=theta - Math.toRadians(v[0]);

        }else if((lat>ourlat)&&(lon>ourlon)){ //++
            //No change is needed in the first quadrant
            theta= thetaprime;

            //subtract theta from the compass value retreived from the sensor to get our final direction
            theta=theta - Math.toRadians(v[0]);

        }else if((lat>ourlat)&&(lon<ourlon)){ //+-
            //Subtract thetaprime from 360 in the fourth quadrant
            theta= ((Math.PI*2)-thetaprime);

            //subtract theta from the compass value retreived from the sensor to get our final direction
            theta=theta - Math.toRadians(v[0]);

        }

        azimuth = (float) Math.toDegrees(theta);

        /*
        float myBearing = mCurLoc.bearingTo(mCurDest);
        //azimuth += geoField.getDeclination();
        azimuth = (myBearing - azimuth) * -1;

        while (azimuth < 0)
            azimuth += 360;
        while (azimuth >= 360)
            azimuth -= 360;
*/
        mCompassImage.setRotation(azimuth);

        //This is where we choose to point it

/*
        RotateAnimation ra = new RotateAnimation(mCurrentDegree, direction,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        mCompassImage.startAnimation(ra);
*/

        Log.d(TAG, "Azimuth: "+String.valueOf(azimuth));
        String setText = "Azimuth: "+String.valueOf(azimuth);
        mBearing.setText(setText);


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

        mCurLoc = location;
        //mCurLoc.setLongitude(location.getLongitude());
        //mCurLoc.setLatitude(location.getLatitude());

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


}
