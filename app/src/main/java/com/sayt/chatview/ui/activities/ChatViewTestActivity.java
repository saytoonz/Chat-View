package com.sayt.chatview.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.gifpack.giphy.GiphyGifProvider;
import com.nsromapa.say.emogifstickerkeyboard.EmoticonGIFKeyboardFragment;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.Emoticon;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.EmoticonSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.gifs.Gif;
import com.nsromapa.say.emogifstickerkeyboard.gifs.GifSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sound.SoundSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sticker.StickerSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.sayt.chatview.R;
import com.sayt.chatview.helpers.PicassoEngine;
import com.sayt.chatview.models.AudioChannel;
import com.sayt.chatview.models.AudioSampleRate;
import com.sayt.chatview.models.AudioSource;
import com.sayt.chatview.models.Message;
import com.sayt.chatview.ui.widget.ChatView;
import com.sayt.chatview.utils.Utils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static com.sayt.chatview.adapters.MessageAdapter.pauseMediaPlayer;
import static com.sayt.chatview.adapters.MessageAdapter.resumeMediaPlayer;
import static com.sayt.chatview.adapters.MessageAdapter.stopMediaPlayer;

public class ChatViewTestActivity extends AppCompatActivity implements ChatView.RecordingListener, PullTransport.OnAudioChunkPulledListener {

    private static final String TAG = "ChatViewTestActivity";
    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    private static InputMethodManager inputMethodManager;
    private MediaPlayer mMediaPlayer;

    public static int imagePickerRequestCode = 10;
    public static int SELECT_VIDEO = 11;
    public static int CAMERA_REQUEST = 12;
    public static int SELECT_AUDIO = 13;
    private ChatView chatView;
    boolean switchbool = true;
    List<Uri> mSelected;
    List<String> mSelectedLocal;

    private MaterialRippleLayout sendBtn;
    private EmoticonEditText messageEditText;
    private MaterialRippleLayout emojiKeyboardToggler;
    private Recorder recorder;

    private String filePath;
    private AudioSource source;
    private AudioChannel channel;
    private AudioSampleRate sampleRate;
    private int recorderSecondsElapsed;
    private boolean isRecording;
    private boolean isRecordingPaused = false;
    private boolean isStillHold = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_test);

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatView = findViewById(R.id.chatView);
        chatView.requestFocus();
        mSelected = new ArrayList<>();
        mSelectedLocal = new ArrayList<>();
        sendBtn = chatView.getSendMRL();
        messageEditText = chatView.getMessageET();
        emojiKeyboardToggler = chatView.getEmojiToggle();


        chatView.setRecordingListener(this);
        initializeEmojiGifStickerKeyBoard();
        chatView.getPauseResumeARL().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });
        source = AudioSource.MIC;
        channel = AudioChannel.STEREO;
        sampleRate = AudioSampleRate.HZ_16000;


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(messageEditText.getText()).toString().trim())) {
                    String message = messageEditText.getText().toString().trim();
                    messageEditText.setText("");
                    sendMessageText(message);
                }
            }
        });


        //Gallery button click listener
        chatView.setOnClickGalleryButtonListener(new ChatView.OnClickGalleryButtonListener() {
            @Override
            public void onGalleryButtonClick() {
                Matisse.from(ChatViewTestActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .theme(R.style.Matisse_Dracula)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(imagePickerRequestCode);
            }
        });

        //Video button click listener
        chatView.setOnClickVideoButtonListener(new ChatView.OnClickVideoButtonListener() {
            @Override
            public void onVideoButtonClick() {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, SELECT_VIDEO);
            }
        });

        //Camera button click listener
        chatView.setOnClickCameraButtonListener(new ChatView.OnClickCameraButtonListener() {
            @Override
            public void onCameraButtonClicked() {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                file.delete();
                File file1 = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");

                Uri uri = FileProvider.getUriForFile(ChatViewTestActivity.this, getApplicationContext().getPackageName() + ".provider", file1);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        chatView.setOnClickAudioButtonListener(new ChatView.OnClickAudioButtonListener() {
            @Override
            public void onAudioButtonClicked() {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, SELECT_AUDIO);
            }
        });


    }


    private void sendMessageText(String body) {
        if (switchbool) {
            Message message = new Message();
            message.setBody(body);
            message.setMessageType(Message.MessageType.RightSimpleImage);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);

            switchbool = false;
        } else {
            Message message1 = new Message();
            message1.setBody(body);
            message1.setMessageType(Message.MessageType.LeftSimpleMessage);
            message1.setTime(getTime());
            message1.setUserName("Hodor");
            message1.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message1);

            switchbool = true;
        }
    }

    private void resumeRecording() {
        if (!isRecordingPaused) {
            String location = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/Audios/Sent/Rec/";
            File dir = new File(location);
            if (!dir.exists())
                dir.mkdirs();
            filePath = location + "/FRZMGS" + System.currentTimeMillis() + ".wav";
        }
        isRecording = true;
        ImageView playPauseIcon = chatView.getPauseResumeARL().findViewById(R.id.pause_resume_imageView);
        playPauseIcon.setImageResource(R.drawable.pause_microphone_100);

        if (recorder == null) {
            chatView.getTimeText().setText("00:00");

            recorder = OmRecorder.wav(
                    new PullTransport.Default(Utils.getMic(source, channel, sampleRate), this),
                    new File(filePath));
        }
        isRecordingPaused = false;
        recorder.resumeRecording();

        startTimer();
    }


    private void pauseRecording() {
        isRecording = false;
        isRecordingPaused = true;

        ImageView playPauseIcon = chatView.getPauseResumeARL().findViewById(R.id.pause_resume_imageView);
        playPauseIcon.setImageResource(R.drawable.play_microphone_100);

        if (recorder != null) {
            recorder.pauseRecording();
        }

        stopTimer();
    }

    private void stopRecording() {
        isRecordingPaused = false;
        recorderSecondsElapsed = 0;
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
        stopTimer();
    }

    public void toggleRecording() {
//        stopPlaying();
        Utils.wait(100, new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    pauseRecording();
                } else {
                    resumeRecording();
                }
            }
        });
    }


    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void updateTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    recorderSecondsElapsed++;
                    chatView.getTimeText().setText(Utils.formatSeconds(recorderSecondsElapsed));
                }
            }
        });
    }


    private void initializeEmojiGifStickerKeyBoard() {
        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(SamsungEmoticonProvider.create())
                .setEmoticonSelectListener(new EmoticonSelectListener() {
                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
                        Log.d(TAG, "emoticonSelected: " + emoticon.getUnicode());
                        messageEditText.append(emoticon.getUnicode(),
                                messageEditText.getSelectionStart(),
                                messageEditText.getSelectionEnd());
                    }

                    @Override
                    public void onBackSpace() {
                    }
                });


        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this, "564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {
                        Log.d(TAG, "onGifSelected: " + gif.getGifUrl());
//                        Glide.with(ChatViewTestActivity.this)
//                                .asGif()
//                                .load(gif.getGifUrl())
//                                .into();
                        Toast.makeText(ChatViewTestActivity.this, gif.getGifUrl(), Toast.LENGTH_SHORT).show();

                        sendNewGIF(gif);
                    }
                });

        EmoticonGIFKeyboardFragment.STICKERConfig stickerConfig = new EmoticonGIFKeyboardFragment.STICKERConfig()
                .setStickerSelectedListener(new StickerSelectListener() {
                    @Override
                    public void onStickerSelectListner(@NonNull File sticker) {
                        Log.d(TAG, "stickerSelected: " + sticker);
                        sendNewSticker(sticker);
                    }
                });


        EmoticonGIFKeyboardFragment.SoundConfig soundConfig = new EmoticonGIFKeyboardFragment.SoundConfig()
                .setSoundImageSelectedListener(new SoundSelectListener() {
                    @Override
                    public void onSoundSelectListner(@NonNull File soundImage) {
                        Log.d(TAG, "soundImage Selected: " + soundImage.getName());

                        String soundName = soundImage.getName().replace(".png", ".mp3");
                        File file = new File(Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/FrenzApp/Media/sounds/SoundAudios/" + soundName);
                        if (!(file.exists()) || (!file.isFile())) {
                            downloadSound(soundName);
                        } else if (file.exists() && file.isFile()) {
                            playAndSendAudio(file);
                        } else {
                            Toast.makeText(ChatViewTestActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        mEmoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.keyboard_container), emoticonConfig, gifConfig, stickerConfig, soundConfig);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.keyboard_container, mEmoticonGIFKeyboardFragment)
                .commit();
        mEmoticonGIFKeyboardFragment.hideKeyboard();

        emojiKeyboardToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmoticonGIFKeyboardFragment.isOpen()) {

                    mEmoticonGIFKeyboardFragment.toggle();
                    ImageView imageView = emojiKeyboardToggler.findViewById(R.id.emoji_keyboad_iv);
                    imageView.setImageResource(R.drawable.ic_smiley);

                    if (inputMethodManager != null) {
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                } else {
                    //Check if keyboard is open and close it if it is
                    if (inputMethodManager.isAcceptingText()) {
                        inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
                    }

                    ImageView imageView = emojiKeyboardToggler.findViewById(R.id.emoji_keyboad_iv);
                    imageView.setImageResource(R.drawable.sp_ic_keyboard);
                    mEmoticonGIFKeyboardFragment.toggle();
                }
            }
        });
    }

    private void sendNewGIF(Gif gif) {
        Log.e(TAG, "sendNewSticker: " + gif.describeContents());

        mSelected.clear();
        mSelected.add(Uri.parse(gif.getGifUrl()));

        if (switchbool) {
            Message message = new Message();
            message.setBody(String.valueOf(System.currentTimeMillis()));
            message.setMessageType(Message.MessageType.RightGIF);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setImageLocalLocation(gif.getGifUrl());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();
            message.setBody(String.valueOf(System.currentTimeMillis()));
            message.setMessageType(Message.MessageType.LeftGIF);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setImageLocalLocation(gif.getGifUrl());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
    }

    private void sendNewSticker(File sticker) {
        Log.e(TAG, "sendNewSticker: " + sticker.getName());

        mSelected.clear();
        mSelected.add(Uri.parse(sticker.getAbsolutePath()));

        if (switchbool) {
            Message message = new Message();
            message.setBody(sticker.getName());
            message.setMessageType(Message.MessageType.RightSticker);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setImageLocalLocation(sticker.getAbsolutePath());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();
            message.setBody(sticker.getName());
            message.setMessageType(Message.MessageType.LeftSticker);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setImageLocalLocation(sticker.getAbsolutePath());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
    }

    public String getTime() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = mdformat.format(calendar.getTime());
        return time;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 10: {

                //Image Selection result
                if (resultCode == RESULT_OK) {
                    mSelected = Matisse.obtainResult(data);
                    mSelectedLocal.clear();
                    for (int i = 0; i < mSelected.size(); i++) {
                        mSelectedLocal.add("1579225193965.jpg");
                    }

                    if (switchbool) {
                        if (mSelected.size() == 1) {
                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.RightSingleImage);
                            message.setTime(getTime());
                            message.setUserName("Groot");
                            message.setImageList(mSelected);
                            message.setImageLocalLocation("");
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                            chatView.addMessage(message);
                            switchbool = false;
                        } else {

                            Message message = new Message();
                            message.setBody("");
                            message.setMessageType(Message.MessageType.RightMultipleImages);
                            message.setTime(getTime());
                            message.setUserName("Groot");
                            message.setImageList(mSelected);
                            message.setImageListNames(mSelectedLocal);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                            chatView.addMessage(message);
                            switchbool = false;
                        }
                    } else {

                        if (mSelected.size() == 1) {
                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.LeftSingleImage);
                            message.setTime(getTime());
                            message.setUserName("Hodor");
                            message.setImageList(mSelected);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                            chatView.addMessage(message);
                            switchbool = true;
                        } else {

                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.LeftMultipleImages);
                            message.setTime(getTime());
                            message.setUserName("Hodor");
                            message.setImageList(mSelected);
                            message.setImageListNames(mSelectedLocal);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                            chatView.addMessage(message);
                            switchbool = true;
                        }

                    }
                }
                break;
            }
            case 11: {

                //Video Selection Result
                if (resultCode == RESULT_OK) {
                    if (switchbool) {
                        Message message = new Message();
                        message.setMessageType(Message.MessageType.RightVideo);
                        message.setTime(getTime());
                        message.setUserName("Groot");
                        message.setVideoUri(Uri.parse(getPathVideo(data.getData())));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                        chatView.addMessage(message);
                        switchbool = false;
                    } else {
                        Message message = new Message();

                        message.setMessageType(Message.MessageType.LeftVideo);
                        message.setTime(getTime());
                        message.setUserName("Hodor");
                        message.setVideoUri(Uri.parse(getPathVideo(data.getData())));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                        chatView.addMessage(message);
                        switchbool = true;
                    }
                }
                break;
            }
            case 12: {

                //Image Capture result

                if (resultCode == RESULT_OK) {


                    if (switchbool) {
                        Message message = new Message();
                        message.setMessageType(Message.MessageType.RightSingleImage);
                        message.setTime(getTime());
                        message.setUserName("Groot");
                        mSelected.clear();
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        //Uri of camera image
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                        mSelected.add(uri);
                        message.setImageList(mSelected);
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                        chatView.addMessage(message);
                        switchbool = false;
                    } else {
                        Message message = new Message();

                        message.setMessageType(Message.MessageType.LeftSingleImage);
                        message.setTime(getTime());
                        message.setUserName("Hodor");
                        mSelected.clear();
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        //Uri of camera image
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                        mSelected.add(uri);
                        message.setImageList(mSelected);
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                        chatView.addMessage(message);
                        switchbool = true;
                    }
                }
                break;
            }
            case 13: {
                if (resultCode == RESULT_OK) {
                    sendAudio(data.getData(), "");
                }
                break;
            }
        }

    }


    private void sendAudio(Uri uri, String localPath) {
        if (uri == null) return;

        if (switchbool) {
            Message message = new Message();
            message.setMessageType(Message.MessageType.RightAudio);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setAudioUri(uri);
            message.setAudioLocalLocation(localPath);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();

            message.setMessageType(Message.MessageType.LeftAudio);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setAudioUri(uri);
            message.setAudioLocalLocation(localPath);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
    }


    public String getPathVideo(Uri uri) {
        System.out.println("getpath " + uri.toString());
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    public String getPathAudio(Uri uri) {
        System.out.println("getpath " + uri.toString());
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        int columnIndex = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        if (cursor != null) {
            return cursor.getString(columnIndex);
        } else return null;
    }


    private void playAndSendAudio(final File file) {
        final ImageView playImg = new ImageView(this);
        playImg.setImageResource(R.drawable.eighth_note);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(" ");
        alertDialog.setView(playImg);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
                Toast.makeText(ChatViewTestActivity.this, "Send sound " + file.getName() + " as message", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });

        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });

        alertDialog.show();

        mMediaPlayer = MediaPlayer.create(ChatViewTestActivity.this, Uri.fromFile(file));
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.start();

    }


    private void downloadSound(String soundName) {
        Toast.makeText(this, "Download: " + soundName, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mEmoticonGIFKeyboardFragment.hideKeyboard();
    }

//    @Override
//    protected void onPause() {
//        stopMediaPlayer();
//        super.onPause();
//    }


    @Override
    public void onRecordingStarted() {
        pauseMediaPlayer();
        isStillHold = true;
        Log.e(TAG, "onRecordingStarted");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStillHold) {
                    resumeRecording();
                }
            }
        }, 1500);

    }

    @Override
    public void onRecordingLocked() {
        Log.e(TAG, "onRecordingLocked");
    }

    @Override
    public void onRecordingCompleted() {
        isStillHold = false;
        isRecordingPaused = false;
        Log.e(TAG, "onRecordingCompleted");
        if (recorderSecondsElapsed > 1) {
            stopRecording();
            Utils.wait(2000, new Runnable() {
                @Override
                public void run() {
                    sendAudio(Uri.parse(filePath), filePath);
                }
            });

        }

        resumeMediaPlayer();
    }

    @Override
    public void onRecordingCanceled() {
        isStillHold = false;
        isRecordingPaused = false;
        resumeMediaPlayer();
        Log.e(TAG, "onRecordingCanceled");
    }


    @Override
    protected void onDestroy() {
        stopMediaPlayer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mEmoticonGIFKeyboardFragment == null || !mEmoticonGIFKeyboardFragment.handleBackPressed())
            super.onBackPressed();
    }

    @Override
    public void onAudioChunkPulled(AudioChunk audioChunk) {
        float amplitude = isRecording ? (float) audioChunk.maxAmplitude() : 0f;
//        visualizverHandler.onDataReceived(amplitude);
    }
}

