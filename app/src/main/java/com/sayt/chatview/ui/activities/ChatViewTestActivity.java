package com.sayt.chatview.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
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
import com.sayt.chatview.ui.widget.ChatView;
import com.sayt.chatview.models.Message;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sayt.chatview.adapters.MessageAdapter.stopMediaPlayer;

public class ChatViewTestActivity extends AppCompatActivity implements ChatView.RecordingListener{

    private static final String TAG = "MainActivity";
    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    private static InputMethodManager inputMethodManager;
    private MediaPlayer mMediaPlayer;

    public static int imagePickerRequestCode = 10;
    public static int SELECT_VIDEO = 11;
    public static int CAMERA_REQUEST = 12;
    public static int SELECT_AUDIO = 13;
    ChatView chatView;
    boolean switchbool = true;
    List<Uri> mSelected;
    List<String> mSelectedLocal;

    private MaterialRippleLayout sendBtn;
    private MaterialRippleLayout recordBtn;
    private EmoticonEditText messageEditText;
    private MaterialRippleLayout emojiKeyboardToggler;
    private FrameLayout keyboard_container;
    private long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_test);

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatView = findViewById(R.id.chatView);
        chatView.requestFocus();
        mSelected = new ArrayList<>();
        mSelectedLocal = new ArrayList<>();
        recordBtn = chatView.getRecordARL();
        sendBtn = chatView.getSendMRL();
        messageEditText = chatView.getMessageET();
        emojiKeyboardToggler = chatView.getEmojiToggle();
        keyboard_container = chatView.getKeyboard_container();


        chatView.setRecordingListener(this);

//        messageEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().trim().length() == 0){
//                    recordBtn.setVisibility(View.VISIBLE);
//                    sendBtn.setVisibility(View.GONE);
//                    sendBtn.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
//                    recordBtn.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
//
//                }else {
//                    recordBtn.setVisibility(View.GONE);
//                    sendBtn.setVisibility(View.VISIBLE);
//                    sendBtn.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
//                    recordBtn.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
//                }
//            }
//        });
        initializeEmojiGifStickerKeyBoard();
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
//                                .placeholder(R.mipmap.ic_launcher)
//                                .into();
                        Toast.makeText(ChatViewTestActivity.this, gif.getGifUrl(), Toast.LENGTH_SHORT).show();
                    }
                });

        EmoticonGIFKeyboardFragment.STICKERConfig stickerConfig = new EmoticonGIFKeyboardFragment.STICKERConfig()
                .setStickerSelectedListener(new StickerSelectListener() {
                    @Override
                    public void onStickerSelectListner(@NonNull File sticker) {
                        Log.d(TAG, "stickerSelected: " + sticker);
                        Toast.makeText(ChatViewTestActivity.this, sticker.getName(), Toast.LENGTH_SHORT).show();
//                        Glide.with(MainActivity.this)
//                                .load(sticker)
//                                .placeholder(R.drawable.sticker_placeholder)
//                                .into(selectedImageView);
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


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(messageEditText.getText()).toString().trim())) {
                    sendMessageText(messageEditText.getText().toString().trim());
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


    private void initializeEmojiGifStickerKeyBoard() {

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
                    if (switchbool) {
                        Message message = new Message();
                        message.setMessageType(Message.MessageType.RightAudio);
                        message.setTime(getTime());
                        message.setUserName("Groot");
                        message.setAudioUri(Objects.requireNonNull(data.getData()));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                        chatView.addMessage(message);
                        switchbool = false;
                    } else {
                        Message message = new Message();

                        message.setMessageType(Message.MessageType.LeftAudio);
                        message.setTime(getTime());
                        message.setUserName("Hodor");
                        message.setAudioUri(Objects.requireNonNull(data.getData()));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                        chatView.addMessage(message);
                        switchbool = true;
                    }
                }
                break;
            }
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
        showToast("started");
        debug("started");

        time = System.currentTimeMillis() / (1000);
    }

    @Override
    public void onRecordingLocked() {
        showToast("locked");
        debug("locked");
    }

    @Override
    public void onRecordingCompleted() {
        showToast("completed");
        debug("completed");

        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);

        if (recordTime > 2) {
            showToast(String.valueOf(recordTime));
        }
    }

    @Override
    public void onRecordingCanceled() {
        showToast("canceled");
        debug("canceled");
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void debug(String log) {
        Log.d("VarunJohn", log);
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
}

