package org.orangeresearch.beckon;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class CompassFragment extends Fragment implements SensorEventListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // JAMES Added Member Variables
    private Beckon mBeckon;
    private TextView mTitleField;
    private TextView mBearing;
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private ImageView mCompassImage;
    private Context mContext;
    private LocationManager mLocationManager;
    private float mCurrentDegree;




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
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeckon = new Beckon();
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
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
        float degree = Math.round(event.values[0]);
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(210);
        ra.setFillAfter(true);


        mCompassImage.startAnimation(ra);
        mCurrentDegree = -degree;

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
        
        Double curLat = location.getLatitude();
        Double curLon = location.getLongitude();
        String curLoc = curLat.toString() + " " + curLon.toString();
        mBearing.setText(curLoc);
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
