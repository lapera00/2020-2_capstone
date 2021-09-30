package org.techtown.sit2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Fragment2 extends Fragment {
    Fragment2.CreatedViewListener createdViewListener;

    interface CreatedViewListener{
        void createView(View view);
    }

    public Fragment2 setCreateViewListener(Fragment2.CreatedViewListener createdViewListener){
        this.createdViewListener = createdViewListener;
        return this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(createdViewListener != null) createdViewListener.createView(getView());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    //private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view=inflater.inflate(R.layout.fragment2,container,false); //container <-부모 사이즈를 주고 , false=아직 붙이지 않는다.
/*
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                ((MainActivity)MainActivity.mC).selectBluetoothDevice();; // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
        */
        ((MainActivity)MainActivity.mC).selectBluetoothDevice();


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);




        return rootView;
    }


}
