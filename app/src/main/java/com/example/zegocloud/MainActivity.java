package com.example.zegocloud;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutPictureInPictureConfig;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;


public class MainActivity extends AppCompatActivity {
    EditText userIdEditText;
    Button startButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userIdEditText = findViewById(R.id.user_id_edit_text);
        startButton = findViewById(R.id.start_btn);


        startButton.setOnClickListener(v -> {
            String userID = userIdEditText.getText().toString().trim();
            if(userID.isEmpty()){
                return;
            }

            startService(userID);
            Intent intent = new Intent(MainActivity.this,CallActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);

        });

    }

    void startService(String userID){
        Application application = getApplication() ; // Android's application context
        long appID = 503961098;   // yourAppID
        String appSign = "ce6e6d908a5026964445a940f8fce9cab1d03c7634654ef2cc6f0e900fec60d7";  // yourAppSign
        String userName = userID;   // yourUserName




        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
//...
        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = null;
                boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
                boolean isGroupCall = invitationData.invitees.size() > 1;
                if (isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
                } else if (!isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
                } else if (!isVideoCall) {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
                } else {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                }
                config.topMenuBarConfig.isVisible = true;
                config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);
                return config;
            }
        };
// ...


        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }


    protected void onDestroy(){
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}