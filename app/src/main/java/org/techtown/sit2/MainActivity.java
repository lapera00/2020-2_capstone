package org.techtown.sit2;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.app.*;
import android.bluetooth.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.*;

import java.io.*;
import java.util.*;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import static android.os.SystemClock.elapsedRealtime;
import static androidx.core.app.NotificationCompat.*;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback {


    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;

    DrawerLayout drawer;
    Toolbar toolbar;

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    private TextView textViewReceive; // 수신 된 데이터를 표시하기 위한 텍스트 뷰
    private EditText editTextSend; // 송신 할 데이터를 작성하기 위한 에딧 텍스트
    private Button buttonSend; // 송신하기 위한 버튼


    private TextView textViewSP1;

    private TextView mtv_PostureState;

    private ImageView imageViewSit1;

    String sitPc = new String();

    static int[] cellValues = new int[32];
    static int[] seatValues_Row0 = new int[6];
    static int[] seatValues_Row1 = new int[15];
    static int[] seatValues_Row2 = new int[10];


    private TextView[] ChairCells_Row0 = new TextView[6];
    private TextView[] ChairCells_Row1 = new TextView[15];
    private TextView[] ChairCells_Row2 = new TextView[10];

    private SeekBar seekbar1;


    Switch notify;
    private TextView swtext;

    NotificationManager manager;
    Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    long sittimenotify = 1;

    int notion = 0;
    int notiontime = 10;

    public static Context mC;
    //int spc = sitPositionCkv2();

    BarChart chart2;

    private TextView[] sss1 = new TextView[5];
    private Button sitdel;
    private Button sitoff;


    int dbs1 = 0;
    int dbs2 = 0;
    int dbs3 = 0;
    int dbs4 = 0;
    int dbs5 = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                } else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mC = this;

        //firstLoadDB();


        ////
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragment5 = new Fragment5();

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment2).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment3).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment4).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment5).commit();
        ///

        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment4 = new Fragment4().setCreateViewListener(new Fragment4.CreatedViewListener() {
            @Override
            public void createView(View view) {
                notify = (Switch) view.findViewById(R.id.push_switch);
                swtext = (TextView) view.findViewById(R.id.sw_text);
                notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            notion = 1;
                        }
                        else {
                            notion = 0;
                        }

                    }
                });
                seekbar1 = (SeekBar) view.findViewById(R.id.seek_bar1);
                seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // onProgressChange - Seekbar 값 변경될때마다 호출
                        //Log.d(TAG, String.format("onProgressChanged 값 변경 중 : progress [%d] fromUser [%b]", progress, fromUser));
                        if (progress % 10 == 0) {
                            swtext.setText(String.valueOf(seekBar.getProgress()));
                        } else {
                            seekBar.setProgress((progress / 5) * 5);
                            swtext.setText(String.valueOf((progress / 5) * 5));
                        }
                        swtext.setText(String.valueOf(seekBar.getProgress()));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // onStartTeackingTouch - SeekBar 값 변경위해 첫 눌림에 호출
                        //Log.d(TAG, String.format("onStartTrackingTouch 값 변경 시작 : progress [%d]", seekBar.getProgress()));
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // onStopTrackingTouch - SeekBar 값 변경 끝나고 드래그 떼면 호출
                        //Log.d(TAG, String.format("onStopTrackingTouch 값 변경 종료: progress [%d]", seekBar.getProgress()));
                        notiontime = seekBar.getProgress();

                    }
                });

                sitdel = (Button) findViewById(R.id.sit_del);     //데이터 리셋 버튼
                sitdel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetDB();
                    }
                });

                sitoff = (Button) findViewById(R.id.sit_off);     //블루투스 및 어플 종료 버튼
                sitoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bluetoothAdapter.disable(); //블루투스 종료
                        exitT();
                    }
                });

            }
        });

        fragment2 = new Fragment2().setCreateViewListener(new Fragment2.CreatedViewListener() {
            @Override
            public void createView(View view) {

                ChairCells_Row0[0] = (TextView) view.findViewById(R.id.textView_Row00);
                ChairCells_Row0[1] = (TextView) view.findViewById(R.id.textView_Row01);
                ChairCells_Row0[2] = (TextView) view.findViewById(R.id.textView_Row02);
                ChairCells_Row0[3] = (TextView) view.findViewById(R.id.textView_Row03);
                ChairCells_Row0[4] = (TextView) view.findViewById(R.id.textView_Row04);
                ChairCells_Row0[5] = (TextView) view.findViewById(R.id.textView_Row05);

                ChairCells_Row1[0] = (TextView) view.findViewById(R.id.textView_Row10);
                ChairCells_Row1[1] = (TextView) view.findViewById(R.id.textView_Row11);
                ChairCells_Row1[2] = (TextView) view.findViewById(R.id.textView_Row12);
                ChairCells_Row1[3] = (TextView) view.findViewById(R.id.textView_Row13);
                ChairCells_Row1[4] = (TextView) view.findViewById(R.id.textView_Row14);
                ChairCells_Row1[5] = (TextView) view.findViewById(R.id.textView_Row15);
                ChairCells_Row1[6] = (TextView) view.findViewById(R.id.textView_Row16);
                ChairCells_Row1[7] = (TextView) view.findViewById(R.id.textView_Row17);
                ChairCells_Row1[8] = (TextView) view.findViewById(R.id.textView_Row18);
                ChairCells_Row1[9] = (TextView) view.findViewById(R.id.textView_Row19);
                ChairCells_Row1[10] = (TextView) view.findViewById(R.id.textView_Row110);
                ChairCells_Row1[11] = (TextView) view.findViewById(R.id.textView_Row111);
                ChairCells_Row1[12] = (TextView) view.findViewById(R.id.textView_Row112);
                ChairCells_Row1[13] = (TextView) view.findViewById(R.id.textView_Row113);
                ChairCells_Row1[14] = (TextView) view.findViewById(R.id.textView_Row114);

                ChairCells_Row2[0] = (TextView) view.findViewById(R.id.textView_Row20);
                ChairCells_Row2[1] = (TextView) view.findViewById(R.id.textView_Row21);
                ChairCells_Row2[2] = (TextView) view.findViewById(R.id.textView_Row22);
                ChairCells_Row2[3] = (TextView) view.findViewById(R.id.textView_Row23);
                ChairCells_Row2[4] = (TextView) view.findViewById(R.id.textView_Row24);
                ChairCells_Row2[5] = (TextView) view.findViewById(R.id.textView_Row25);
                ChairCells_Row2[6] = (TextView) view.findViewById(R.id.textView_Row26);
                ChairCells_Row2[7] = (TextView) view.findViewById(R.id.textView_Row27);
                ChairCells_Row2[8] = (TextView) view.findViewById(R.id.textView_Row28);
                ChairCells_Row2[9] = (TextView) view.findViewById(R.id.textView_Row29);



                textViewSP1 = (TextView) view.findViewById(R.id.textView_SP1);

                mtv_PostureState = (TextView) view.findViewById(R.id.mtv_Posture_State);


            }
        });


        fragment3 = new Fragment3().setCreateViewListener(new Fragment3.CreatedViewListener() {
            @Override
            public void createView(View view) {
                firstLoadDB();
                chart2 = (BarChart) view.findViewById(R.id.tab1_chart_2);
                sss1[0] = (TextView) view.findViewById(R.id.sss_1);
                sss1[1] = (TextView) view.findViewById(R.id.sss_2);
                sss1[2] = (TextView) view.findViewById(R.id.sss_3);
                sss1[3] = (TextView) view.findViewById(R.id.sss_4);
                sss1[4] = (TextView) view.findViewById(R.id.sss_5);

                sss1[0].setText(String.format(" %d 회",dbs1));
                sss1[1].setText(String.format(" %d 회",dbs2));
                sss1[2].setText(String.format(" %d 회",dbs3));
                sss1[3].setText(String.format(" %d 회",dbs4));
                sss1[4].setText(String.format(" %d 회",dbs5));
                //sss1[4].setText(dbss1);


                chart2.clearChart();

                chart2.addBar(new BarModel("S1", dbs1, 0xFF56B7F1));
                chart2.addBar(new BarModel("S2", dbs2, 0xFF56B7F1));
                chart2.addBar(new BarModel("S3", dbs3, 0xFF56B7F1));
                chart2.addBar(new BarModel("S4", dbs4, 0xFF56B7F1));
                chart2.addBar(new BarModel("S5", dbs5, 0xFF56B7F1));

                chart2.startAnimation();

            }
        });



        //textViewSP1 = (TextView) findViewById(R.id.textView_SP1);
        //mtv_PostureState = (TextView) findViewById(R.id.mtv_Posture_State);
        //imageViewSit1 = (ImageView) findViewById(R.id.imageView_Sit1);



        // 각 컨테이너들의 id를 매인 xml과 맞춰준다.
        //textViewReceive = (TextView) findViewById(R.id.textView_receive); //받는
        //editTextSend = (EditText) findViewById(R.id.editText_send);   //보낼 문자 쓰는
        //buttonSend = (Button) findViewById(R.id.button_send);     //보내는 버튼
        /*buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendData(editTextSend.getText().toString());
            }
        });*/


        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정

        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            list.add("취소");

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void receiveData() {
        final Handler handler = new Handler(Looper.getMainLooper());

        //final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                while (!(Thread.currentThread().isInterrupted())) {
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if (byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                            for (int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if (tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    String[] stringValues = text.split(",");    // , 기준으로 쪼개어 문자배열로 저장
                                    cellValues = Arrays.asList(stringValues).stream().mapToInt(Integer::parseInt).toArray();
                                    //문자배열 저장 된 수신값을 정수형 배열로 변환
                                    reorderCellValues_32chToSeat(cellValues);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 텍스트 뷰에 출력
                                            //textViewReceive.setText(text);  //최초 받는
                                            DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
                                            mDbHelper.createDatabase();
                                            mDbHelper.open();

                                            firstLoadDB();  //db데이터 불러오기

                                            ChairCells_Row0[0].setBackgroundColor(0x00FF0000 | (seatValues_Row0[0] << 24));
                                            ChairCells_Row0[1].setBackgroundColor(0x00FF0000 | (seatValues_Row0[1] << 24));
                                            ChairCells_Row0[2].setBackgroundColor(0x00FF0000 | (seatValues_Row0[2] << 24));
                                            ChairCells_Row0[3].setBackgroundColor(0x00FF0000 | (seatValues_Row0[3] << 24));
                                            ChairCells_Row0[4].setBackgroundColor(0x00FF0000 | (seatValues_Row0[4] << 24));
                                            ChairCells_Row0[5].setBackgroundColor(0x00FF0000 | (seatValues_Row0[5] << 24));

                                            ChairCells_Row1[0].setBackgroundColor(0x00FF0000 | (seatValues_Row1[0] << 24));
                                            ChairCells_Row1[1].setBackgroundColor(0x00FF0000 | (seatValues_Row1[1] << 24));
                                            ChairCells_Row1[2].setBackgroundColor(0x00FF0000 | (seatValues_Row1[2] << 24));
                                            ChairCells_Row1[3].setBackgroundColor(0x00FF0000 | (seatValues_Row1[3] << 24));
                                            ChairCells_Row1[4].setBackgroundColor(0x00FF0000 | (seatValues_Row1[4] << 24));
                                            ChairCells_Row1[5].setBackgroundColor(0x00FF0000 | (seatValues_Row1[5] << 24));
                                            ChairCells_Row1[6].setBackgroundColor(0x00FF0000 | (seatValues_Row1[6] << 24));
                                            ChairCells_Row1[7].setBackgroundColor(0x00FF0000 | (seatValues_Row1[7] << 24));
                                            ChairCells_Row1[8].setBackgroundColor(0x00FF0000 | (seatValues_Row1[8] << 24));
                                            ChairCells_Row1[9].setBackgroundColor(0x00FF0000 | (seatValues_Row1[9] << 24));
                                            ChairCells_Row1[10].setBackgroundColor(0x00FF0000 | (seatValues_Row1[10] << 24));
                                            ChairCells_Row1[11].setBackgroundColor(0x00FF0000 | (seatValues_Row1[11] << 24));
                                            ChairCells_Row1[12].setBackgroundColor(0x00FF0000 | (seatValues_Row1[12] << 24));
                                            ChairCells_Row1[13].setBackgroundColor(0x00FF0000 | (seatValues_Row1[13] << 24));
                                            ChairCells_Row1[14].setBackgroundColor(0x00FF0000 | (seatValues_Row1[14] << 24));

                                            ChairCells_Row2[0].setBackgroundColor(0x00FF0000 | (seatValues_Row2[0] << 24));
                                            ChairCells_Row2[1].setBackgroundColor(0x00FF0000 | (seatValues_Row2[1] << 24));
                                            ChairCells_Row2[2].setBackgroundColor(0x00FF0000 | (seatValues_Row2[2] << 24));
                                            ChairCells_Row2[3].setBackgroundColor(0x00FF0000 | (seatValues_Row2[3] << 24));
                                            ChairCells_Row2[4].setBackgroundColor(0x00FF0000 | (seatValues_Row2[4] << 24));
                                            ChairCells_Row2[5].setBackgroundColor(0x00FF0000 | (seatValues_Row2[5] << 24));
                                            ChairCells_Row2[6].setBackgroundColor(0x00FF0000 | (seatValues_Row2[6] << 24));
                                            ChairCells_Row2[7].setBackgroundColor(0x00FF0000 | (seatValues_Row2[7] << 24));
                                            ChairCells_Row2[8].setBackgroundColor(0x00FF0000 | (seatValues_Row2[8] << 24));
                                            ChairCells_Row2[9].setBackgroundColor(0x00FF0000 | (seatValues_Row2[9] << 24));

                                            int spc = sitPositionCkv2();

                                            if(spc == 0){sitPc = "미착석";}
                                            else if(spc ==1){sitPc = "바른자세";}
                                            else if(spc ==2){sitPc = "왼쪽 기울임";}
                                            else if(spc ==3){sitPc = "오른쪽 기울임";}
                                            else if(spc ==4){sitPc = "앞으로 기울임";}
                                            else if(spc ==5){sitPc = "뒤로 기울임";}
                                            else if(spc ==7){sitPc = "오른 다리 꼼";}
                                            else if(spc ==8){sitPc = "왼 다리 꼼";}

                                            textViewSP1.setText(sitPc);


                                            sitTime(spc);

                                            if(notion == 1 && spc != 0 ){
                                                if((sittimenotify % notiontime) == 0) {
                                                    //showNoti();
                                                    switch (spc){
                                                        case 1:
                                                            dbs1 += 1;
                                                            mDbHelper.situp(1, dbs1);
                                                            break;
                                                        case 2:
                                                        case 7:
                                                            showNoti();
                                                            dbs2 += 1;
                                                            mDbHelper.situp(2, dbs2);
                                                            break;
                                                        case 3:
                                                        case 8:
                                                            showNoti();
                                                            dbs3 += 1;
                                                            mDbHelper.situp(3, dbs3);
                                                            break;
                                                        case 4:
                                                            showNoti();
                                                            dbs4 += 1;
                                                            mDbHelper.situp(4, dbs4);
                                                            break;
                                                        case 5:
                                                            showNoti();
                                                            dbs5 += 1;
                                                            mDbHelper.situp(5, dbs5);
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }

                                            mDbHelper.close();


                                        }
                                    });
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }

    void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        text += "\n";
        try {
            // 데이터 송신
            outputStream.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reorderCellValues_32chToSeat(int[] cellValues) {    //센서값 정렬
        seatValues_Row0[0] = cellValues[10];
        seatValues_Row0[1] = cellValues[12];
        seatValues_Row0[2] = cellValues[14];
        seatValues_Row0[3] = cellValues[16];
        seatValues_Row0[4] = cellValues[18];
        seatValues_Row0[5] = cellValues[20];

        seatValues_Row1[0] = cellValues[5];
        seatValues_Row1[1] = cellValues[6];
        seatValues_Row1[2] = cellValues[7];
        seatValues_Row1[3] = cellValues[8];
        seatValues_Row1[4] = cellValues[9];
        seatValues_Row1[5] = cellValues[11];
        seatValues_Row1[6] = cellValues[13];
        seatValues_Row1[7] = cellValues[15];
        seatValues_Row1[8] = cellValues[17];
        seatValues_Row1[9] = cellValues[19];
        seatValues_Row1[10] = cellValues[21];
        seatValues_Row1[11] = cellValues[22];
        seatValues_Row1[12] = cellValues[23];
        seatValues_Row1[13] = cellValues[24];
        seatValues_Row1[14] = cellValues[25];

        seatValues_Row2[0] = cellValues[0];
        seatValues_Row2[1] = cellValues[1];
        seatValues_Row2[2] = cellValues[2];
        seatValues_Row2[3] = cellValues[3];
        seatValues_Row2[4] = cellValues[4];
        seatValues_Row2[5] = cellValues[26];
        seatValues_Row2[6] = cellValues[27];
        seatValues_Row2[7] = cellValues[28];
        seatValues_Row2[8] = cellValues[29];
        seatValues_Row2[9] = cellValues[30];
    }


    public int sitOnv2() {  //착석 여부  중간셀 총합 200이상
        int summation = 0;
        for (int i = 0; i < 15; i++) {
            summation += seatValues_Row1[i];

            if (200 < summation)
                return 1;   //착석
        }
        return 0; //미착석
    }

    public int sitPositionLegv2(){       //다리 꼬았는지 체크
        int sp = 0;
        int onL = 0;
        int onR = 0;
        int pspL = 0;
        int pspR = 0;

        for(int i=0; i<3; i++) {        //다리 꼼의 유무는  seatValues_Row0 3개 셀중 2개 셀 이상 무효셀 일시 판별
            if (seatValues_Row0[i] >= 5) {
                onL++;
            }
        }
        for(int i=3; i<6; i++) {
            if (seatValues_Row0[i] >= 5) {
                onR++;
            }
        }

        if(onL<=1){
            pspL = 1;   //1 이면 왼쪽 다리를 꼰 상태
        }
        else pspL = 2;  //2면 안꼼

        if(onR<=1) {
            pspR = 1;   //1이면 오른 다리를 꼰 상태
        }
        else pspR = 2;  //2면 안꼼

        sp = pspL + pspR;

        if(sp <= 3){
            if(pspL>pspR){
                return 3; //오른 다리 꼼
            }
            else if(pspL<pspR){
                return 2;    //왼 다리 꼼
            }
        }
        else if(sp >= 4){
            return 0;    //다리 안꼼
        }

        //0 다리안꼼, 2 왼쪽 다리 꼼, 3 오른쪽 다리 꼼
        return 0;
    }

    public int sitPositionWeiv2(){   //무게 중심아님, 걸터앉기, 한방향 쏠려앉기
        int weL0 = 0;
        int weR0 = 0;
        int weL1 = 0;
        int weR1 = 0;
        int weL2 = 0;
        int weR2 = 0;
        int sumL = 0;
        int sumR = 0;

        for(int i=0; i<3; i++) {    //왼쪽 첫행 더함
            weL0 = weL0 + seatValues_Row0[i];
        }
        for(int i=3; i<6; i++) {        //오른쪽 첫행 더함
            weR0 = weR0 + seatValues_Row0[i];
        }
        for(int i=0; i<7; i++) {        //왼쪽 중간행 더함
            weL1 = weL1 + seatValues_Row1[i];
        }
        for(int i=7; i<15; i++) {        //오른쪽 중간행 더함
            weR1 = weR1 + seatValues_Row1[i];
        }
        for(int i=0; i<5; i++) {        //왼쪽 뒷행 더함
            weL2 = weL2 + seatValues_Row2[i];
        }
        for(int i=5; i<10; i++) {        //오른쪽 뒷행 더함
            weR2 = weR2 + seatValues_Row2[i];
        }

        sumL = weL0 + weL1 + weL2;  //왼쪽총합
        sumR = weR0 + weR1 + weR2;  //오른총합

        if (sumL > sumR + 200) {    //왼쪽 쏠림
            return 1;
        }
        else if (sumR > sumL + 200) {    //오른쪽 쏠림
            return 2;
        }
        else if (weL2 + weR2 < 100 &&weL0 + weR0 > (weL1 + weR1)/2 && weL0 + weR0 > weL2+weR2){ //앞으로 숙임
            return 3;
        }
        else if (weL2 + weR2 > weL0 + weL0 + 200){ //뒤로숙임
            return 4;
        }
        else return 0;
        // 0바른자세 아마, 1왼쪽쏠림, 2,오른쪽쏠림, 3,앞으로숙임, 4뒤로숙임
    }

    public int sitPositionCkv2() {  //최종 자세  v2

        int s1 = sitOnv2(); //0미착석, 1착석
        int s2 = sitPositionWeiv2();   // 0바른자세 아마, 1왼쪽기울, 2,오른쪽기울, 3,앞으로기울, 4뒤로기울
        int s3 = sitPositionLegv2(); // 0다리안꼼, 2 왼쪽 다리 꼼, 3 오른쪽 다리 꼼

        if(s1 == 0){
            return 0;
        }
        else if(s1 == 1){
            if(s2 == 0){
                return 1;
            }
            else if(s2 == 1){
                if(s3 == 3){
                    return 7;
                }
                else return 2;
            }
            else if(s2 == 2){
                if(s3 == 2){
                    return 8;
                }
                else return 3;
            }
            else if(s2 == 3){
                return 4;
            }
            else if(s2 == 4){
                return 5;
            }

        }

        //0미착석 1바른착석 2왼쪽기울(7오른다리꼼) 3오르쪽기울(8왼다리꼼) 4앞으로기울  5뒤로기울
        return 1;

    }






    //자세태그 시간관리
    enum POSTURE_tag{
        POSTURE_GOOD,
        POSTURE_NO_LOG,
        POSTURE_LEFT_BAD,
        POSTURE_RIGHT_BAD,
        POSTURE_FRONT_BAD,
        POSTURE_BACK_BAD
    }
    POSTURE_tag m_PostureState;

    long m_PostureOriginTimeMS = elapsedRealtime();

    private long getPostureElapsedSecond() { //현재 자세를 취한 시간 길이 측정 반환 (초단위)
        return (elapsedRealtime() - m_PostureOriginTimeMS) / 1000;
    }

    // 현재의 자세를 태그로 저장, 추가로 현재의 자세를 취한 시점의 시간을 저장
    // 이 값은 현재의 자세를 유지할 경우, 몇초간 유지하고 있는 지를 UI에서 보여줄 때 사용
    private void setPostureState(POSTURE_tag posture_state) {
        if (m_PostureState != posture_state) {
            m_PostureOriginTimeMS = elapsedRealtime();
        }
        m_PostureState = posture_state;
    }

    //재의 자세 태그를 반환
    private POSTURE_tag getPostureState(){
        return m_PostureState;
    }

    //자세별 취한 시간 출력, 이미지 출력
    public void sitTime(int spc){
        if(spc == 0){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            setPostureState(POSTURE_tag.POSTURE_NO_LOG);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }
        else if(spc ==1){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            setPostureState(POSTURE_tag.POSTURE_GOOD);
            //imageViewSit1.setImageResource(R.drawable.siton);
        }
        else if(spc ==2||spc == 7){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_LEFT_BAD);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }
        else if(spc ==3||spc==8){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_RIGHT_BAD);
            //imageViewSit1.setImageResource(R.drawable.siton);
        }
        else if(spc ==4){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_FRONT_BAD);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }
        else if(spc ==5){
            mtv_PostureState.setText(String.format(" %d 초", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_BACK_BAD);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }/*
        else if(spc ==7){
            mtv_PostureState.setText(String.format(" %d sec", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_FRONT);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }
        else if(spc ==8){
            mtv_PostureState.setText(String.format(" %d sec", getPostureElapsedSecond()));
            sittimenotify = getPostureElapsedSecond();
            setPostureState(POSTURE_tag.POSTURE_FRONT_BAD);
            //imageViewSit1.setImageResource(R.drawable.nosit);
        }*/

        //mtv_PostureState.setText(String.format("POSTURE : %d sec", getPostureElapsedSecond()));
        //setPostureState(POSTURE_tag.POSTURE_TEST);
    }


    public void showNoti(){
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
            builder = new Builder(this,CHANNEL_ID);

        }
        else{   //하위 버전일 경우
            builder = new Builder(this);
        }
        Intent fullScreenIntent = new Intent(this, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(1000);
        builder.setFullScreenIntent(fullScreenPendingIntent, true);

        builder.setContentTitle("바른자세로 앉아 주세요");   //알림창 제목
        builder.setContentText("바른자세로 앉아 주세요");      //알림창 메시지
        builder.setSmallIcon(R.drawable.ic_launcher_background);//알림창 아이콘
        builder.setTicker("바른자세로 앉아 주세요"); // 상태바에 표시될 한줄 출력
        builder.setAutoCancel(true);    //누르면 꺼짐 아마
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_CALL);

        Notification notification = builder.build();
        manager.notify(1,notification);  //알림창 실행
    }




    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.now) {
            Toast.makeText(this, "실시간 자세", Toast.LENGTH_LONG).show();
            onFragmentSelected(1, null);
        } else if (id == R.id.chart) {
            Toast.makeText(this, "통계", Toast.LENGTH_LONG).show();
            onFragmentSelected(2, null);
        } else if (id == R.id.set) {
            Toast.makeText(this, "설정", Toast.LENGTH_LONG).show();
            onFragmentSelected(3, null);
        } else if (id == R.id.guide) {
            Toast.makeText(this, "이용 방법", Toast.LENGTH_LONG).show();
            onFragmentSelected(4, null);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        if (position == 1) {
            curFragment = fragment2;
            toolbar.setTitle("실시간 자세");
        } else if (position == 2) {
            curFragment = fragment3;
            toolbar.setTitle("통계");
        } else if (position == 3) {
            curFragment = fragment4;
            toolbar.setTitle("설정");
        } else if (position == 4) {
            curFragment = fragment5;
            toolbar.setTitle("이용 방법");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }




    private void firstLoadDB() {    // db로드

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTableData();
        testdata.moveToFirst();
        dbs1 = testdata.getInt(0);
        dbs2 = testdata.getInt(1);
        dbs3 = testdata.getInt(2);
        dbs4 = testdata.getInt(3);
        dbs5 = testdata.getInt(4);

        mDbHelper.close();
    }

    private void resetDB() {    //데이터 리셋

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        mDbHelper.reset();

        mDbHelper.close();
    }

    public void exitT(){    //어플종료
        ActivityCompat.finishAffinity(this);
        System.exit(0);
    }



}

