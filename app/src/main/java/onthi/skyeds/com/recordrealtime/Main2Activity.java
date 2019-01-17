package onthi.skyeds.com.recordrealtime;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                Toast.makeText(Main2Activity.this, "Done", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Toast.makeText(Main2Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }).check();
        RecordThread rec = new RecordThread();
        rec.start();
    }

    class RecordThread extends Thread{
        static final int frequency = 44100;
        static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int recBufSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding)*2;
            int plyBufSize = AudioTrack.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding)*2;

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, recBufSize);

            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    channelConfiguration, audioEncoding, plyBufSize, AudioTrack.MODE_STREAM);

            byte[] recBuf = new byte[recBufSize];
            audioRecord.startRecording();
            audioTrack.play();
            while(true){
                int readLen = audioRecord.read(recBuf, 0, recBufSize);
                audioTrack.write(recBuf, 0, readLen);
            }
//            audioTrack.stop();
//            audioRecord.stop();
        }
    }
}

