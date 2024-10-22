package com.sayt.chatview.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.adprogressbarlib.AdCircleProgress;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.ohoussein.playpause.PlayPauseView;
import com.sayt.chatview.R;
import com.sayt.chatview.helpers.ChatDownloadTask;
import com.sayt.chatview.models.Message;
import com.sayt.chatview.ui.activities.ChatViewTestActivity;
import com.sayt.chatview.ui.activities.ImageFFActivity;
import com.sayt.chatview.ui.activities.VideoFFActivity;
import com.sayt.chatview.ui.views.CollageView;
import com.sayt.chatview.ui.views.VideoPlayer;
import com.sayt.chatview.utils.FontChanger;
import com.sayt.chatview.utils.Settings;
import com.silencedut.expandablelayout.ExpandableLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.sayt.chatview.utils.Utils.downloadSoundAudio;

/**
 * Created by say on 16/01/20.
 */


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;
    private List<Message> filterList;
    private Context context;
    private ImageLoader imageLoader;
    private Typeface typeface;

    private static MediaPlayer mediaPlayer;
    private static MediaPlayer mMediaPlayer;

    private String playingPosition;
    private boolean showLeftBubbleIcon = true;
    private boolean showRightBubbleIcon = true;
    private boolean showSenderName = true;

    private int leftBubbleLayoutColor = R.color.colorAccent2;
    private int rightBubbleLayoutColor = R.color.colorAccent1;
    private int leftBubbleTextColor = android.R.color.black;
    private int rightBubbleTextColor = android.R.color.white;
    private int timeTextColor = android.R.color.tab_indicator_text;
    private int senderNameTextColor = android.R.color.tab_indicator_text;
    private float textSize = 20;


    public MessageAdapter(List<Message> verticalList, Context context) {
        this.messageList = verticalList;
        this.context = context;
        this.filterList = verticalList;
        imageLoader = ImageLoader.getInstance();
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/product_san_regular.ttf");

    }

    public static void stopMediaPlayer() {
        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
    }

    public static void pauseMediaPlayer() {
        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
    }

    public static void resumeMediaPlayer() {
        if (mediaPlayer != null)
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        switch (messageList.get(position).getMessageType()) {
            case LeftSimpleMessage: {
                type = 1;
                break;
            }
            case RightSimpleImage: {
                type = 2;
                break;
            }
            case LeftSingleImage: {
                type = 3;
                break;
            }
            case RightSingleImage: {
                type = 4;
                break;
            }
            case LeftMultipleImages: {
                type = 5;
                break;
            }
            case RightMultipleImages: {
                type = 6;
                break;
            }
            case LeftVideo: {
                type = 7;
                break;
            }
            case RightVideo: {
                type = 8;
                break;
            }
            case LeftAudio: {
                type = 9;
                break;
            }
            case RightAudio: {
                type = 10;
                break;
            }
            case LeftSticker: {
                type = 11;
                break;
            }
            case RightSticker: {
                type = 12;
                break;
            }
            case LeftGIF: {
                type = 13;
                break;
            }
            case RightGIF: {
                type = 14;
                break;
            }
            case LeftSound: {
                type = 15;
                break;
            }
            case RightSound: {
                type = 16;
                break;
            }
        }
        if (type == 0) {
            throw new RuntimeException("Set Message Type ( Message Type is Null )");
        } else {
            return type;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;


        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_text_layout, parent, false);
            viewHolder = new LeftTextViewHolder(view);
        } else {
            if (viewType == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.right_text_layout, parent, false);
                viewHolder = new RightTextViewHolder(view);
            } else {
                if (viewType == 3) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.left_image_layout, parent, false);
                    viewHolder = new LeftImageViewHolder(view);
                } else {
                    if (viewType == 4) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.right_image_layout, parent, false);
                        viewHolder = new RightImageViewHolder(view);
                    } else {
                        if (viewType == 5) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.left_images_layout, parent, false);
                            viewHolder = new LeftImagesViewHolder(view);
                        } else {
                            if (viewType == 6) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.right_images_layout, parent, false);
                                viewHolder = new RightImagesViewHolder(view);
                            } else {
                                if (viewType == 20) {
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.left_typing_layout, parent, false);
                                    viewHolder = new LeftTypingViewHolder(view);
                                } else {
                                    if (viewType == 7) {
                                        View view = LayoutInflater.from(parent.getContext())
                                                .inflate(R.layout.left_video_layout, parent, false);

                                        viewHolder = new LeftVideoViewHolder(view);

                                    } else {
                                        if (viewType == 8) {

                                            View view = LayoutInflater.from(parent.getContext())
                                                    .inflate(R.layout.right_video_layout, parent, false);
                                            viewHolder = new RightVideoViewHolder(view);
                                        } else {
                                            if (viewType == 9) {
                                                View view = LayoutInflater.from(parent.getContext())
                                                        .inflate(R.layout.left_audio_layout, parent, false);
                                                viewHolder = new LeftAudioViewHolder(view);
                                            } else {

                                                if (viewType == 10) {
                                                    View view = LayoutInflater.from(parent.getContext())
                                                            .inflate(R.layout.right_audio_layout, parent, false);
                                                    viewHolder = new RightAudioViewHolder(view);
                                                } else {


                                                    if (viewType == 11) {
                                                        View view = LayoutInflater.from(parent.getContext())
                                                                .inflate(R.layout.left_sticker_layout, parent, false);
                                                        viewHolder = new LeftStickerViewHolder(view);
                                                    } else {

                                                        if (viewType == 12) {
                                                            View view = LayoutInflater.from(parent.getContext())
                                                                    .inflate(R.layout.right_sticker_layout, parent, false);
                                                            viewHolder = new RightStickerViewHolder(view);
                                                        } else {

                                                            if (viewType == 13) {
                                                                View view = LayoutInflater.from(parent.getContext())
                                                                        .inflate(R.layout.left_gif_layout, parent, false);
                                                                viewHolder = new LeftGIFViewHolder(view);
                                                            } else {
                                                                if (viewType == 14) {
                                                                    View view = LayoutInflater.from(parent.getContext())
                                                                            .inflate(R.layout.right_gif_layout, parent, false);
                                                                    viewHolder = new RightGIFViewHolder(view);
                                                                } else {

                                                                    if (viewType == 15) {
                                                                        View view = LayoutInflater.from(parent.getContext())
                                                                                .inflate(R.layout.left_sound_layout, parent, false);
                                                                        viewHolder = new LeftSoundViewHolder(view);
                                                                    } else {
//                                                                        if (viewType == 16){

                                                                        View view = LayoutInflater.from(parent.getContext())
                                                                                .inflate(R.layout.right_sound_layout, parent, false);
                                                                        viewHolder = new RightSoundViewHolder(view);
//                                                                        }else{

//                                                                    }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }


                                    }
                                }

                            }
                        }
                    }
                }
            }

        }


        if (viewHolder == null) {
            throw new RuntimeException("View Holder is null");
        }
        return viewHolder;
    }


    protected class LeftTextViewHolder extends RecyclerView.ViewHolder {

        public EmoticonTextView leftTV;
        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV;
        public CardView leftBubbleIconCV;

        public LeftTextViewHolder(View view) {
            super(view);

            leftTV = view.findViewById(R.id.leftTV);
            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            setBackgroundColor(leftBubbleLayoutColor);
            setTextColor(leftBubbleTextColor);
            setTimeTextColor(timeTextColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            setTextSize(textSize);

            leftTV.setEmoticonProvider(SamsungEmoticonProvider.create());
            leftTV.setEmoticonSize(30);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(leftTV.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setTextColor(int color) {
            leftTV.setTextColor(color);
        }

        public void setTimeTextColor(int color) {
            leftTimeTV.setTextColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }

        public void setTextSize(float size) {
            leftTV.setTextSize(size);
        }


    }

    protected class RightTextViewHolder extends RecyclerView.ViewHolder {

        public EmoticonTextView rightTV;
        public TextView rightTimeTV, senderNameTV;
        public ImageView rightMessageStatusIV, rightBubbleIconIV;
        public ExpandableLayout rightEL;
        public CardView rightBubbleIconCV;

        public RightTextViewHolder(View view) {
            super(view);

            rightTV = view.findViewById(R.id.rightTV);
            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            setBackgroundColor(rightBubbleLayoutColor);
            setTextColor(rightBubbleTextColor);
            setTimeTextColor(timeTextColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            setTextSize(textSize);


            rightTV.setEmoticonProvider(SamsungEmoticonProvider.create());
            rightTV.setEmoticonSize(30);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(rightTV.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setTextColor(int color) {
            rightTV.setTextColor(color);
        }

        public void setTimeTextColor(int color) {
            rightTimeTV.setTextColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }

        public void setTextSize(float size) {
            rightTV.setTextSize(size);
        }
    }


    protected class LeftImageViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV, downloadLeftImage;
        public CardView leftBubbleIconCV;
        public CardView leftIVCV;
        public ImageView leftIV;
        private AdCircleProgress adCircleProgressLeftIV;

        public LeftImageViewHolder(View view) {
            super(view);

            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            leftIV = view.findViewById(R.id.leftIV);
            leftIVCV = view.findViewById(R.id.leftIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            downloadLeftImage = view.findViewById(R.id.downloadLeftImage);
            adCircleProgressLeftIV = view.findViewById(R.id.left_image_pgb_progress);

            setBackgroundColor(leftBubbleLayoutColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(leftIV.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightImageViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTV, rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightImage;
        public CardView rightBubbleIconCV;
        public CardView rightIVCV;
        public ImageView rightIV;
        public AdCircleProgress adCircleProgressRightIV;

        public RightImageViewHolder(View view) {
            super(view);


            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightIV = view.findViewById(R.id.rightIV);
            rightIVCV = view.findViewById(R.id.rightIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            downloadRightImage = view.findViewById(R.id.downloadRightImage);
            adCircleProgressRightIV = view.findViewById(R.id.right_image_pgb_progress);


            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            setBackgroundColor(rightBubbleLayoutColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(rightIV.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }


    protected class LeftGIFViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV, downloadLeftImage;
        public CardView leftBubbleIconCV;
        public CardView leftIVCV;
        public ImageView leftIV;
        private AdCircleProgress adCircleProgressLeftIV;

        public LeftGIFViewHolder(View view) {
            super(view);

            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            leftIV = view.findViewById(R.id.leftIV);
            leftIVCV = view.findViewById(R.id.leftIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            downloadLeftImage = view.findViewById(R.id.downloadLeftImage);
            adCircleProgressLeftIV = view.findViewById(R.id.left_image_pgb_progress);

            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
//            Drawable backgroundDrawable = DrawableCompat.wrap(leftIV.getBackground()).mutate();
//            DrawableCompat.setTint(backgroundDrawable, color);
            leftIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightGIFViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTV, rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightImage;
        public CardView rightBubbleIconCV;
        public CardView rightIVCV;
        public ImageView rightIV;
        public AdCircleProgress adCircleProgressRightIV;

        public RightGIFViewHolder(View view) {
            super(view);


            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightIV = view.findViewById(R.id.rightIV);
            rightIVCV = view.findViewById(R.id.rightIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            downloadRightImage = view.findViewById(R.id.downloadRightImage);
            adCircleProgressRightIV = view.findViewById(R.id.right_image_pgb_progress);


            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            rightIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class LeftImagesViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV;
        public CardView leftBubbleIconCV;
        public CollageView leftCollageView;

        public LeftImagesViewHolder(View view) {
            super(view);

            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            leftCollageView = view.findViewById(R.id.leftCollageView);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightImagesViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV;
        public CardView rightBubbleIconCV;
        public CollageView rightCollageView, leftCollageView;

        public RightImagesViewHolder(View view) {
            super(view);

            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightCollageView = view.findViewById(R.id.rightCollageView);
            leftCollageView = view.findViewById(R.id.leftCollageView);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }


    protected class LeftVideoViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV, downloadLeftVideo;
        public CardView leftBubbleIconCV;
        public CardView leftIVCV;
        public ImageView leftIV;
        public LinearLayout videoLL;
        AdCircleProgress adCircleProgressLV;

        public LeftVideoViewHolder(View view) {
            super(view);


            leftIVCV = view.findViewById(R.id.leftIVCV);
            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            videoLL = view.findViewById(R.id.videoLL);
            downloadLeftVideo = view.findViewById(R.id.downloadLeftVideo);
            adCircleProgressLV = view.findViewById(R.id.left_pgb_progress);

            setBackgroundColor(leftBubbleLayoutColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            leftIVCV.setCardBackgroundColor(color);
        }


        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightVideoViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightVideo;
        public CardView rightBubbleIconCV, rightIVCV;
        public LinearLayout videoLL;
        AdCircleProgress adCircleProgressRV;


        public RightVideoViewHolder(View view) {
            super(view);

            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightIVCV = view.findViewById(R.id.rightIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            videoLL = view.findViewById(R.id.videoLL);
            downloadRightVideo = view.findViewById(R.id.downloadRightVideo);
            adCircleProgressRV = view.findViewById(R.id.right_pgb_progress);

            setBackgroundColor(rightBubbleLayoutColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            rightIVCV.setCardBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }


    protected class LeftAudioViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView leftBubbleIconIV, downloadLeftAudio;
        public CardView leftBubbleIconCV;
        public SeekBar audioSeekbar;
        public PlayPauseView playPauseView;
        public AdCircleProgress adCircleProgressAudioLV;
        public Message message;
        public android.os.Handler handler;

        public LeftAudioViewHolder(View view) {
            super(view);

            audioSeekbar = view.findViewById(R.id.audioSeekbar);
            playPauseView = view.findViewById(R.id.play_pause_view);
            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);

            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            downloadLeftAudio = view.findViewById(R.id.downloadLeftAudio);
            adCircleProgressAudioLV = view.findViewById(R.id.left_audio_pgb_progress);

            setBackgroundColor(leftBubbleLayoutColor);
            setSeekBarLineColor(leftBubbleTextColor);
            setSeekBarThumbColor(rightBubbleLayoutColor);
            setTimeTextColor(timeTextColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            handler = new android.os.Handler();


            audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (playingPosition == message.getAudioLocalLocation()) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (playingPosition == message.getAudioLocalLocation()) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            });
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message != null) {


                        if (playingPosition == message.getAudioLocalLocation()) {
                            if (mediaPlayer != null) {

                                if (mediaPlayer.isPlaying()) {

                                    audioSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                                    if (playPauseView.isPlay()) {
                                        playPauseView.change(false);
                                    }
                                } else {
                                    playPauseView.change(true);
                                }
                            } else {
                                playPauseView.change(true);
                            }

                        } else {

                            audioSeekbar.setProgress(0);
                            playPauseView.change(true);
                            playPauseView.change(true);
                        }


                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                        if (playingPosition == message.getAudioLocalLocation()) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            playPauseView.change(true);
                        } else {

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(message.getAudioLocalLocation()));
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    mediaPlayer.start();
                                    playingPosition = message.getAudioLocalLocation();
                                    audioSeekbar.setMax(mediaPlayer.getDuration());
                                    playPauseView.change(false);
                                }
                            });

                        }
                    } else {

                        mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(message.getAudioLocalLocation()));
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {

                                mediaPlayer.start();
                                playingPosition = message.getAudioLocalLocation();
                                audioSeekbar.setMax(mediaPlayer.getDuration());
                                playPauseView.change(false);
                            }
                        });
                    }
                }
            });

            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getLayoutPosition();
                    return true;
                }
            });
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(final Message message) {
            this.message = message;

        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(audioSeekbar.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setSeekBarLineColor(int color) {
            audioSeekbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        }

        public void setSeekBarThumbColor(int color) {
            Drawable backgroundDrawable1 = DrawableCompat.wrap(audioSeekbar.getThumb()).mutate();
            DrawableCompat.setTint(backgroundDrawable1, color);
        }


        public void setTimeTextColor(int color) {
            leftTimeTV.setTextColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }


    }

    protected class RightAudioViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTimeTV, senderNameTV;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightAudio;
        public ExpandableLayout rightEL;
        public CardView rightBubbleIconCV;
        public Message message;
        public SeekBar audioSeekbar;
        public PlayPauseView playPauseView;
        public AdCircleProgress adCircleProgressAudioRV;
        public android.os.Handler handler;

        public RightAudioViewHolder(View view) {
            super(view);


            audioSeekbar = view.findViewById(R.id.audioSeekbar);
            playPauseView = view.findViewById(R.id.play_pause_view);
            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            downloadRightAudio = view.findViewById(R.id.downloadRightAudio);
            adCircleProgressAudioRV = view.findViewById(R.id.right_audio_pgb_progress);

            setBackgroundColor(rightBubbleLayoutColor);
            setSeekBarLineColor(rightBubbleTextColor);
            setSeekBarThumbColor(leftBubbleLayoutColor);
            setTimeTextColor(timeTextColor);
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            handler = new android.os.Handler();
            audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null) {
                        if (playingPosition == message.getAudioLocalLocation()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null) {
                        if (playingPosition == message.getAudioLocalLocation()) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }
                }
            });
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message != null) {
                        if (playingPosition == message.getAudioLocalLocation()) {
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {

                                    audioSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                                    if (playPauseView.isPlay()) {
                                        playPauseView.change(false);
                                    }
                                } else {
                                    playPauseView.change(true);
                                }
                            } else {
                                playPauseView.change(true);
                            }

                        } else {

                            audioSeekbar.setProgress(0);
                            playPauseView.change(true);
                            playPauseView.change(true);
                        }
                    }
                    handler.postDelayed(this, 1000);

                }
            });

            playPauseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                        if (playingPosition == message.getAudioLocalLocation()) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            playPauseView.change(true);
                        } else {

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(message.getAudioLocalLocation()));
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    mediaPlayer.start();
                                    playingPosition = message.getAudioLocalLocation();
                                    audioSeekbar.setMax(mediaPlayer.getDuration());
                                    playPauseView.change(false);
                                }
                            });

                        }
                    } else {

                        mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(message.getAudioLocalLocation()));
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {

                                mediaPlayer.start();
                                playingPosition = message.getAudioLocalLocation();
                                audioSeekbar.setMax(mediaPlayer.getDuration());
                                playPauseView.change(false);
                            }
                        });
                    }


                }
            });
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(final Message message) {
            this.message = message;

        }

        public void setBackgroundColor(int color) {
            Drawable backgroundDrawable = DrawableCompat.wrap(audioSeekbar.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, color);
        }

        public void setSeekBarLineColor(int color) {
            audioSeekbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        }

        public void setSeekBarThumbColor(int color) {
            Drawable backgroundDrawable1 = DrawableCompat.wrap(audioSeekbar.getThumb()).mutate();
            DrawableCompat.setTint(backgroundDrawable1, color);
        }

        public void setTimeTextColor(int color) {
            rightTimeTV.setTextColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }

    }


    protected class LeftTypingViewHolder extends RecyclerView.ViewHolder {


        public LeftTypingViewHolder(View view) {
            super(view);


            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getLayoutPosition();
                    return true;
                }
            });
        }
    }


    protected class LeftStickerViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV, downloadLeftImage;
        public CardView leftBubbleIconCV;
        public CardView leftIVCV;
        public ImageView leftIV;
        private AdCircleProgress adCircleProgressLeftIV;

        public LeftStickerViewHolder(View view) {
            super(view);

            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            leftIV = view.findViewById(R.id.leftIV);
            leftIVCV = view.findViewById(R.id.leftIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            downloadLeftImage = view.findViewById(R.id.downloadLeftImage);
            adCircleProgressLeftIV = view.findViewById(R.id.left_image_pgb_progress);

            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
//            Drawable backgroundDrawable = DrawableCompat.wrap(leftIV.getBackground()).mutate();
//            DrawableCompat.setTint(backgroundDrawable, color);
            leftIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightStickerViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTV, rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightImage;
        public CardView rightBubbleIconCV;
        public CardView rightIVCV;
        public ImageView rightIV;
        public AdCircleProgress adCircleProgressRightIV;

        public RightStickerViewHolder(View view) {
            super(view);


            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightIV = view.findViewById(R.id.rightIV);
            rightIVCV = view.findViewById(R.id.rightIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            downloadRightImage = view.findViewById(R.id.downloadRightImage);
            adCircleProgressRightIV = view.findViewById(R.id.right_image_pgb_progress);


            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            rightIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }


    protected class LeftSoundViewHolder extends RecyclerView.ViewHolder {

        public TextView leftTimeTV, senderNameTV;
        public ExpandableLayout leftEL;
        public ImageView lefttMessageStatusIV, leftBubbleIconIV, downloadLeftImage;
        public CardView leftBubbleIconCV;
        public CardView leftIVCV;
        public ImageView leftIV;
        private AdCircleProgress adCircleProgressLeftIV;

        public LeftSoundViewHolder(View view) {
            super(view);

            leftTimeTV = view.findViewById(R.id.leftTimeTV);
            leftEL = view.findViewById(R.id.leftEL);
            leftIV = view.findViewById(R.id.leftIV);
            leftIVCV = view.findViewById(R.id.leftIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            leftBubbleIconIV = view.findViewById(R.id.leftBubbleIconIV);
            leftBubbleIconCV = view.findViewById(R.id.leftBubbleIconCV);
            downloadLeftImage = view.findViewById(R.id.downloadLeftImage);
            adCircleProgressLeftIV = view.findViewById(R.id.left_image_pgb_progress);

            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showLeftBubbleIcon(showLeftBubbleIcon);
            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
//            Drawable backgroundDrawable = DrawableCompat.wrap(leftIV.getBackground()).mutate();
//            DrawableCompat.setTint(backgroundDrawable, color);
            leftIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showLeftBubbleIcon(boolean b) {
            if (b) {
                leftBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                leftBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }

    protected class RightSoundViewHolder extends RecyclerView.ViewHolder {

        public TextView rightTV, rightTimeTV, senderNameTV;
        public ExpandableLayout rightEL;
        public ImageView rightMessageStatusIV, rightBubbleIconIV, downloadRightImage;
        public CardView rightBubbleIconCV;
        public CardView rightIVCV;
        public ImageView rightIV;
        public AdCircleProgress adCircleProgressRightIV;

        public RightSoundViewHolder(View view) {
            super(view);


            rightTimeTV = view.findViewById(R.id.rightTimeTV);
            rightEL = view.findViewById(R.id.rightEL);
            rightIV = view.findViewById(R.id.rightIV);
            rightIVCV = view.findViewById(R.id.rightIVCV);
            senderNameTV = view.findViewById(R.id.senderNameTV);
            rightBubbleIconCV = view.findViewById(R.id.rightBubbleIconCV);
            rightBubbleIconIV = view.findViewById(R.id.rightBubbleIconIV);
            downloadRightImage = view.findViewById(R.id.downloadRightImage);
            adCircleProgressRightIV = view.findViewById(R.id.right_image_pgb_progress);


            FontChanger fontChanger = new FontChanger(typeface);
            fontChanger.replaceFonts((ViewGroup) view);
            setBackgroundColor(context.getResources().getColor(R.color.transparent_white_percent_95));
            setSenderNameTextColor(senderNameTextColor);
            showSenderName(showSenderName);
            showRightBubbleIcon(showRightBubbleIcon);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    return true;
                }
            });
        }

        public void setBackgroundColor(int color) {
            rightIV.setBackgroundColor(color);
        }

        public void setSenderNameTextColor(int color) {
            senderNameTV.setTextColor(color);
        }

        public void showSenderName(boolean b) {
            if (b) {
                senderNameTV.setVisibility(View.VISIBLE);
            } else {
                senderNameTV.setVisibility(View.GONE);
            }
        }

        public void showRightBubbleIcon(boolean b) {
            if (b) {
                rightBubbleIconCV.setVisibility(View.VISIBLE);
            } else {
                rightBubbleIconCV.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        final Message message = messageList.get(position);
        messageList.get(position).setIndexPosition(position);


        if (holder instanceof LeftTextViewHolder) {
            final LeftTextViewHolder holder1 = (LeftTextViewHolder) holder;
            holder1.leftTV.setText(message.getBody());
            holder1.leftTimeTV.setText(message.getTime());

            if (message.getUserIcon() != null) {
                Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
            }
            holder1.senderNameTV.setText(message.getUserName());
        } else {


            if (holder instanceof RightTextViewHolder) {
                final RightTextViewHolder holder1 = (RightTextViewHolder) holder;
                holder1.rightTV.setText(message.getBody());
                holder1.rightTimeTV.setText(message.getTime());
                if (message.getUserIcon() != null) {
                    Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                }
                holder1.senderNameTV.setText(message.getUserName());
            } else {


                if (holder instanceof LeftImageViewHolder) {
                    final LeftImageViewHolder holder1 = (LeftImageViewHolder) holder;

                    if (message.getUserIcon() != null) {
                        Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                    }
                    holder1.senderNameTV.setText(message.getUserName());
                    holder1.leftTimeTV.setText(message.getTime());

                    if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                        holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ChatDownloadTask chatDownloadTask =
                                        new ChatDownloadTask(context,
                                                holder1.adCircleProgressLeftIV,
                                                holder1.downloadLeftImage,
                                                message);

                                final String localDir = "/FrenzApp/Media/Images/";
                                final String localFileName = System.currentTimeMillis() + ".jpg";
                                chatDownloadTask.execute("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                                        localDir, localFileName);

                                holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chatDownloadTask.cancel(true);
                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });


                        ////Check if auto download single Image is enabled
                        //// if so start downloading
                        if (Settings.autoSaveSingleImageToGallery()) {
                            final ChatDownloadTask chatDownloadTask =
                                    new ChatDownloadTask(context,
                                            holder1.adCircleProgressLeftIV,
                                            holder1.downloadLeftImage,
                                            message);

                            final String localDir = "/FrenzApp/Media/Images/";
                            final String localFileName = System.currentTimeMillis() + ".jpg";
                            chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                    "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                            holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    chatDownloadTask.cancel(true);
                                    holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                    holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                }
                            });
                        }


                        Glide.with(context)
                                .load("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg")
                                .into(holder1.leftIV);
                    } else {
                        if (!(new File(message.getImageLocalLocation()).exists())) {
                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                            ////Check if auto download single Image is enabled
                            //// if so start downloading
                            if (Settings.autoSaveSingleImageToGallery()) {
                                final ChatDownloadTask chatDownloadTask =
                                        new ChatDownloadTask(context,
                                                holder1.adCircleProgressLeftIV,
                                                holder1.downloadLeftImage,
                                                message);

                                final String localDir = "/FrenzApp/Media/Images/";
                                final String localFileName = System.currentTimeMillis() + ".jpg";
                                chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chatDownloadTask.cancel(true);
                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                    }
                                });
                            }


                            holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final ChatDownloadTask chatDownloadTask =
                                            new ChatDownloadTask(context,
                                                    holder1.adCircleProgressLeftIV,
                                                    holder1.downloadLeftImage,
                                                    message);

                                    final String localDir = "/FrenzApp/Media/Images/";
                                    final String localFileName = System.currentTimeMillis() + ".jpg";
                                    chatDownloadTask.execute("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                                            localDir, localFileName);


                                    holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            chatDownloadTask.cancel(true);
                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                        }
                                    });

                                }
                            });

                            Glide.with(context)
                                    .load("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg")
                                    .into(holder1.leftIV);

                        } else {
                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                            holder1.downloadLeftImage.setVisibility(View.GONE);
                            if (message.getImageLocalLocation() != null
                                    && !message.getImageLocalLocation().equals("")) {
                                final File image =
                                        DiskCacheUtils.findInCache(message.getImageLocalLocation(),
                                                imageLoader.getDiskCache());
                                if (image != null && image.exists()) {
                                    Picasso.get().load(image).into(holder1.leftIV);
                                } else {

                                    imageLoader.loadImage("file://" + message.getImageLocalLocation(), new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String s, View view) {
                                            holder1.leftIV.setImageBitmap(null);
                                            Log.e(TAG, "onLoadingStarted ----> " + s);
                                        }

                                        @Override
                                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                                            Log.e(TAG, failReason.toString());
                                        }

                                        @Override
                                        public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                                            Picasso.get().load(s).into(holder1.leftIV);
                                        }

                                        @Override
                                        public void onLoadingCancelled(String s, View view) {

                                        }
                                    });
                                }
                            } else {
                                holder1.leftIV.setImageBitmap(null);
                            }
                            holder1.leftIV.setTransitionName("photoTransition");
                            holder1.leftIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ImageFFActivity.class);
                                    intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
                                    ActivityOptionsCompat optionsCompat =
                                            ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.leftIV, holder1.leftIV.getTransitionName());
                                    context.startActivity(intent, optionsCompat.toBundle());
                                }
                            });

                        }
                    }


                } else {


                    if (holder instanceof RightImageViewHolder) {
                        final RightImageViewHolder holder1 = (RightImageViewHolder) holder;

                        if (message.getUserIcon() != null) {
                            Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                        }
                        holder1.senderNameTV.setText(message.getUserName());
                        holder1.rightTimeTV.setText(message.getTime());

                        if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                            holder1.downloadRightImage.setVisibility(View.VISIBLE);


                            ////Check if auto download single Image is enabled
                            //// if so start downloading
                            if (Settings.autoSaveSingleImageToGallery()) {
                                final ChatDownloadTask chatDownloadTask =
                                        new ChatDownloadTask(context,
                                                holder1.adCircleProgressRightIV,
                                                holder1.downloadRightImage,
                                                message);

                                final String localDir = "/FrenzApp/Media/Images/Sent/";
                                final String localFileName = System.currentTimeMillis() + ".jpg";
                                chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chatDownloadTask.cancel(true);
                                        holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                        holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                    }
                                });
                            }


                            holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final ChatDownloadTask chatDownloadTask =
                                            new ChatDownloadTask(context,
                                                    holder1.adCircleProgressRightIV,
                                                    holder1.downloadRightImage,
                                                    message);

                                    final String localDir = "/FrenzApp/Media/Images/Sent/";
                                    final String localFileName = System.currentTimeMillis() + ".jpg";
                                    chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                                    holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            chatDownloadTask.cancel(true);
                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });

                            Glide.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                    "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s").into(holder1.rightIV);

                        } else {
                            if (!(new File(message.getImageLocalLocation()).exists())) {
                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                holder1.downloadRightImage.setVisibility(View.VISIBLE);

                                ////Check if auto download single Image is enabled
                                //// if so start downloading
                                if (Settings.autoSaveSingleImageToGallery()) {
                                    final ChatDownloadTask chatDownloadTask =
                                            new ChatDownloadTask(context,
                                                    holder1.adCircleProgressRightIV,
                                                    holder1.downloadRightImage,
                                                    message);


                                    final String localDir = "/FrenzApp/Media/Images/Sent/";
                                    final String localFileName = System.currentTimeMillis() + ".jpg";
                                    chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                    holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            chatDownloadTask.cancel(true);
                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                        }
                                    });
                                }


                                holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final ChatDownloadTask chatDownloadTask =
                                                new ChatDownloadTask(context,
                                                        holder1.adCircleProgressRightIV,
                                                        holder1.downloadRightImage,
                                                        message);

                                        final String localDir = "/FrenzApp/Media/Images/Sent/";
                                        final String localFileName = System.currentTimeMillis() + ".jpg";
                                        chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                                        holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chatDownloadTask.cancel(true);
                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                                Glide.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s").into(holder1.rightIV);
                            } else {
                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                holder1.downloadRightImage.setVisibility(View.GONE);
                                if (message.getImageLocalLocation() != null
                                        && !message.getImageLocalLocation().equals("")) {
                                    final File image =
                                            DiskCacheUtils.findInCache(message.getImageLocalLocation(),
                                                    imageLoader.getDiskCache());
                                    if (image != null && image.exists()) {
                                        Picasso.get().load(image).into(holder1.rightIV);
                                    } else {

                                        imageLoader.loadImage("file://" + message.getImageLocalLocation(), new ImageLoadingListener() {
                                            @Override
                                            public void onLoadingStarted(String s, View view) {
                                                holder1.rightIV.setImageBitmap(null);
                                            }

                                            @Override
                                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                                Log.e(TAG, failReason.toString());
                                            }

                                            @Override
                                            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                                                Picasso.get().load(s).into(holder1.rightIV);

                                            }

                                            @Override
                                            public void onLoadingCancelled(String s, View view) {

                                            }
                                        });
                                    }
                                } else {
                                    holder1.rightIV.setImageBitmap(null);
                                }

                                holder1.rightIV.setTransitionName("photoTransition");
                                holder1.rightIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, ImageFFActivity.class);
                                        intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
                                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.rightIV, holder1.rightIV.getTransitionName());
                                        context.startActivity(intent, optionsCompat.toBundle());
                                    }
                                });

                            }
                        }


                    } else {


                        if (holder instanceof LeftImagesViewHolder) {
                            final LeftImagesViewHolder holder1 = (LeftImagesViewHolder) holder;

                            String localDir = "/FrenzApp/Media/Images/";
                            String dirPath = Environment.getExternalStorageDirectory().getPath()
                                    + localDir;

                            if (message.getUserIcon() != null) {
                                Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                            }
                            holder1.senderNameTV.setText(message.getUserName());
                            holder1.leftTimeTV.setText(message.getTime());


                            final List<String> imageList = new ArrayList<>();
                            for (int i = 0; i < message.getImageList().size(); i++) {

                                if (message.getImageListNames().get(i) != null
                                        && !TextUtils.isEmpty(message.getImageListNames().get(i))) {

                                    File file = new File(dirPath + message.getImageListNames().get(i));
                                    if (file.exists()) {
                                        imageList.add("file://" + dirPath + message.getImageListNames().get(i));
                                    } else {
                                        imageList.add(message.getImageList().get(i).toString());
                                        final ChatDownloadTask chatDownloadTask =
                                                new ChatDownloadTask(context, null, null, null);
                                        chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir,
                                                message.getImageListNames().get(i));
                                    }

                                } else {
                                    imageList.add(message.getImageList().get(i).toString());
                                    final ChatDownloadTask chatDownloadTask =
                                            new ChatDownloadTask(context, null, null, null);
                                    chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                    "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir,
                                            message.getImageListNames().get(i));
                                }
                            }

                            holder1.leftCollageView
                                    .photoMargin(8)
                                    .photoPadding(0)
                                    .backgroundColor(leftBubbleLayoutColor)
                                    .useFirstAsHeader(false) // makes first photo fit device widtdh and use full line
                                    .defaultPhotosForLine(2) // sets default photos number for line of photos (can be changed by program at runtime)
                                    .useCards(true)// adds cardview backgrounds to all photos
                                    .loadPhotos(imageList);

                            holder1.leftCollageView.setTransitionName("photoTransition");
                            holder1.leftCollageView.setOnPhotoClickListener(new CollageView.OnPhotoClickListener() {
                                @Override
                                public void onPhotoClick(int i) {
                                    Intent intent = new Intent(context, ImageFFActivity.class);
                                    intent.putExtra("photoURI", imageList.get(i));
                                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.leftCollageView, holder1.leftCollageView.getTransitionName());
                                    context.startActivity(intent, optionsCompat.toBundle());
                                }
                            });
                        } else {


                            if (holder instanceof RightImagesViewHolder) {
                                final RightImagesViewHolder holder1 = (RightImagesViewHolder) holder;

                                String localDir = "/FrenzApp/Media/Images/Sent/";
                                String dirPath = Environment.getExternalStorageDirectory().getPath()
                                        + localDir;

                                if (message.getUserIcon() != null) {
                                    Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                }
                                holder1.senderNameTV.setText(message.getUserName());
                                holder1.rightTimeTV.setText(message.getTime());

                                final List<String> imageList = new ArrayList<>();
                                for (int i = 0; i < message.getImageList().size(); i++) {

                                    if (message.getImageListNames().get(i) != null
                                            && !TextUtils.isEmpty(message.getImageListNames().get(i))) {

                                        File file = new File(dirPath + message.getImageListNames().get(i));
                                        if (file.exists()) {
                                            imageList.add("file://" + dirPath + message.getImageListNames().get(i));
                                        } else {
                                            imageList.add(message.getImageList().get(i).toString());
                                            final ChatDownloadTask chatDownloadTask =
                                                    new ChatDownloadTask(context, null, null, null);
                                            chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir,
                                                    message.getImageListNames().get(i));
                                        }

                                    } else {
                                        imageList.add(message.getImageList().get(i).toString());
                                        final ChatDownloadTask chatDownloadTask =
                                                new ChatDownloadTask(context, null, null, null);
                                        chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir,
                                                message.getImageListNames().get(i));
                                    }
                                }


                                holder1.rightCollageView
                                        .photoMargin(8)
                                        .photoPadding(0)
                                        .backgroundColor(rightBubbleLayoutColor)
                                        .useFirstAsHeader(false) // makes first photo fit device widtdh and use full line
                                        .defaultPhotosForLine(2) // sets default photos number for line of photos (can be changed by program at runtime)
                                        .useCards(true)// adds cardview backgrounds to all photos
                                        .loadPhotos(imageList);

                                holder1.rightCollageView.setTransitionName("photoTransition");
                                holder1.rightCollageView.setOnPhotoClickListener(new CollageView.OnPhotoClickListener() {
                                    @Override
                                    public void onPhotoClick(int i) {
                                        Intent intent = new Intent(context, ImageFFActivity.class);
                                        intent.putExtra("photoURI", imageList.get(i));
                                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.rightCollageView, holder1.rightCollageView.getTransitionName());
                                        context.startActivity(intent, optionsCompat.toBundle());
                                    }

                                });
                            } else {


                                if (holder instanceof LeftTypingViewHolder) {
                                    //Do nothing for now... just Inflate
                                } else {

                                    if (holder instanceof LeftVideoViewHolder) {

                                        final LeftVideoViewHolder holder1 = (LeftVideoViewHolder) holder;

                                        if (message.getUserIcon() != null) {
                                            Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                                        }
                                        holder1.senderNameTV.setText(message.getUserName());
                                        holder1.leftTimeTV.setText(message.getTime());

                                        if (TextUtils.isEmpty(message.getVideoLocalLocation())) {

                                            holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                            holder1.adCircleProgressLV.setVisibility(View.GONE);

                                            ////Check if auto download single Video is enabled
                                            //// if so start downloading
                                            if (Settings.autoSaveSingleVideoToGallery()) {
                                                final ChatDownloadTask chatDownloadTask =
                                                        new ChatDownloadTask(context,
                                                                holder1.adCircleProgressLV,
                                                                holder1.downloadLeftVideo,
                                                                message);
                                                final String localDir = "/FrenzApp/Media/Videos/";
                                                final String localFileName = System.currentTimeMillis() + ".mp4";
                                                chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                        "kering-heat-5334.appspot.com" +
                                                        "/o/demo1.mp4?alt=media&token=f6d" +
                                                        "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localFileName);


                                                holder1.adCircleProgressLV.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        chatDownloadTask.cancel(true);
                                                        holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                        holder1.adCircleProgressLV.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                            holder1.downloadLeftVideo.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final ChatDownloadTask chatDownloadTask =
                                                            new ChatDownloadTask(context,
                                                                    holder1.adCircleProgressLV,
                                                                    holder1.downloadLeftVideo,
                                                                    message);
                                                    final String localDir = "/FrenzApp/Media/Videos/";
                                                    final String localFileName = System.currentTimeMillis() + ".mp4";
                                                    chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                            "kering-heat-5334.appspot.com" +
                                                            "/o/demo1.mp4?alt=media&token=f6d" +
                                                            "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localFileName);


                                                    holder1.adCircleProgressLV.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            chatDownloadTask.cancel(true);
                                                            holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                            holder1.adCircleProgressLV.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            });

                                            final ImageView imageView = new ImageView(context);
                                            RelativeLayout.LayoutParams params =
                                                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                            RelativeLayout.LayoutParams.MATCH_PARENT);
                                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            imageView.setLayoutParams(params);
                                            holder1.videoLL.addView(imageView);

                                            Glide.with(context)
                                                    .load("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                            "kering-heat-5334.appspot.com" +
                                                            "/o/demo1.mp4?alt=media&token=f6d" +
                                                            "82bb0-f61f-45bc-ab13-16970c7432c4")
                                                    .apply(new RequestOptions().placeholder(R.drawable.video_clip))
                                                    .into(imageView);

                                        } else {

                                            if (!(new File(message.getVideoLocalLocation()).exists())) {

                                                holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                holder1.adCircleProgressLV.setVisibility(View.GONE);


                                                ////Check if auto download single Video is enabled
                                                //// if so start downloading
                                                if (Settings.autoSaveSingleVideoToGallery()) {
                                                    final ChatDownloadTask chatDownloadTask =
                                                            new ChatDownloadTask(context,
                                                                    holder1.adCircleProgressLV,
                                                                    holder1.downloadLeftVideo,
                                                                    message);

                                                    final String localDir = "/FrenzApp/Media/Videos/";
                                                    final String localFileName = System.currentTimeMillis() + ".mp4";
                                                    chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                            "kering-heat-5334.appspot.com" +
                                                            "/o/demo1.mp4?alt=media&token=f6d" +
                                                            "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localFileName);

                                                    holder1.adCircleProgressLV.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                            holder1.adCircleProgressLV.setVisibility(View.GONE);
                                                            chatDownloadTask.cancel(true);
                                                        }
                                                    });
                                                }


                                                holder1.downloadLeftVideo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final ChatDownloadTask chatDownloadTask =
                                                                new ChatDownloadTask(context,
                                                                        holder1.adCircleProgressLV,
                                                                        holder1.downloadLeftVideo,
                                                                        message);

                                                        final String localDir = "/FrenzApp/Media/Videos/";
                                                        final String localFileName = System.currentTimeMillis() + ".mp4";
                                                        chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                "kering-heat-5334.appspot.com" +
                                                                "/o/demo1.mp4?alt=media&token=f6d" +
                                                                "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localFileName);

                                                        holder1.adCircleProgressLV.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                                holder1.adCircleProgressLV.setVisibility(View.GONE);
                                                                chatDownloadTask.cancel(true);
                                                            }
                                                        });
                                                    }
                                                });

                                                final ImageView imageView = new ImageView(context);
                                                RelativeLayout.LayoutParams params =
                                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                RelativeLayout.LayoutParams.MATCH_PARENT);
                                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                imageView.setLayoutParams(params);
                                                holder1.videoLL.addView(imageView);

                                                Glide.with(context)
                                                        .load("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                "kering-heat-5334.appspot.com" +
                                                                "/o/demo1.mp4?alt=media&token=f6d" +
                                                                "82bb0-f61f-45bc-ab13-16970c7432c4")
                                                        .apply(new RequestOptions().placeholder(R.drawable.video_clip))
                                                        .into(imageView);
                                            } else {
                                                holder1.adCircleProgressLV.setVisibility(View.GONE);
                                                holder1.downloadLeftVideo.setVisibility(View.VISIBLE);
                                                holder1.downloadLeftVideo.setImageResource(R.drawable.video_icon);
                                                final VideoPlayer videoPlayer = new VideoPlayer(context);
                                                RelativeLayout.LayoutParams params =
                                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                RelativeLayout.LayoutParams.MATCH_PARENT);
                                                videoPlayer.setLayoutParams(params);
                                                videoPlayer.setScaleType(VideoPlayer.ScaleType.CENTER_CROP);
                                                holder1.videoLL.addView(videoPlayer);

                                                videoPlayer.loadVideo(message.getVideoLocalLocation(), message);
                                                holder1.downloadLeftVideo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                            mediaPlayer.pause();
                                                        }
                                                        videoPlayer.setTransitionName("videoFF");
                                                        Intent intent = new Intent(context, VideoFFActivity.class);
                                                        intent.putExtra("videoURI", message.getVideoLocalLocation());
                                                        ActivityOptionsCompat optionsCompat =
                                                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                                                        videoPlayer, videoPlayer.getTransitionName());
                                                        context.startActivity(intent, optionsCompat.toBundle());
                                                    }
                                                });
                                                videoPlayer.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                            mediaPlayer.pause();
                                                        }
                                                        videoPlayer.setTransitionName("videoFF");
                                                        Intent intent = new Intent(context, VideoFFActivity.class);
                                                        intent.putExtra("videoURI", message.getVideoLocalLocation());
                                                        ActivityOptionsCompat optionsCompat =
                                                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                                                        videoPlayer, videoPlayer.getTransitionName());
                                                        context.startActivity(intent, optionsCompat.toBundle());
                                                    }
                                                });
                                            }

                                        }


                                    } else {


                                        if (holder instanceof RightVideoViewHolder) {
                                            final RightVideoViewHolder holder1 = (RightVideoViewHolder) holder;

                                            if (message.getUserIcon() != null) {
                                                Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                            }
                                            holder1.senderNameTV.setText(message.getUserName());
                                            holder1.rightTimeTV.setText(message.getTime());

                                            if (TextUtils.isEmpty(message.getVideoLocalLocation())) {

                                                holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                holder1.adCircleProgressRV.setVisibility(View.GONE);


                                                // Check auto video Download and download if enabled
                                                if (Settings.autoSaveSingleVideoToGallery()) {
                                                    final ChatDownloadTask chatDownloadTask =
                                                            new ChatDownloadTask(context,
                                                                    holder1.adCircleProgressRV,
                                                                    holder1.downloadRightVideo,
                                                                    message);

                                                    final String localDir = "/FrenzApp/Media/Videos/Sent/";
                                                    final String localfileName = System.currentTimeMillis() + ".mp4";
                                                    chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                            "kering-heat-5334.appspot.com" +
                                                            "/o/demo1.mp4?alt=media&token=f6d" +
                                                            "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localfileName);

                                                    holder1.adCircleProgressRV.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                            holder1.adCircleProgressRV.setVisibility(View.GONE);
                                                            chatDownloadTask.cancel(true);
                                                        }
                                                    });
                                                }


                                                holder1.downloadRightVideo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final ChatDownloadTask chatDownloadTask =
                                                                new ChatDownloadTask(context,
                                                                        holder1.adCircleProgressRV,
                                                                        holder1.downloadRightVideo,
                                                                        message);

                                                        final String localDir = "/FrenzApp/Media/Videos/Sent/";
                                                        final String localfileName = System.currentTimeMillis() + ".mp4";
                                                        chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                "kering-heat-5334.appspot.com" +
                                                                "/o/demo1.mp4?alt=media&token=f6d" +
                                                                "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localfileName);

                                                        holder1.adCircleProgressRV.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                                holder1.adCircleProgressRV.setVisibility(View.GONE);
                                                                chatDownloadTask.cancel(true);
                                                            }
                                                        });
                                                    }
                                                });

                                                final ImageView imageView = new ImageView(context);
                                                RelativeLayout.LayoutParams params =
                                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                RelativeLayout.LayoutParams.MATCH_PARENT);
                                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                imageView.setLayoutParams(params);
                                                holder1.videoLL.addView(imageView);

                                                Glide.with(context)
                                                        .load("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                "kering-heat-5334.appspot.com" +
                                                                "/o/demo1.mp4?alt=media&token=f6d" +
                                                                "82bb0-f61f-45bc-ab13-16970c7432c4")
                                                        .apply(new RequestOptions().placeholder(R.drawable.video_clip))
                                                        .into(imageView);

                                            } else {
                                                if (!(new File(message.getVideoLocalLocation()).exists())) {
                                                    holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                    holder1.adCircleProgressRV.setVisibility(View.GONE);


                                                    // Check auto video Download and download if enabled
                                                    if (Settings.autoSaveSingleVideoToGallery()) {
                                                        final ChatDownloadTask chatDownloadTask =
                                                                new ChatDownloadTask(context,
                                                                        holder1.adCircleProgressRV,
                                                                        holder1.downloadRightVideo,
                                                                        message);

                                                        final String localDir = "/FrenzApp/Media/Videos/Sent/";
                                                        final String localfileName = System.currentTimeMillis() + ".mp4";
                                                        chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                "kering-heat-5334.appspot.com" +
                                                                "/o/demo1.mp4?alt=media&token=f6d" +
                                                                "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localfileName);

                                                        holder1.adCircleProgressRV.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                                holder1.adCircleProgressRV.setVisibility(View.GONE);
                                                                chatDownloadTask.cancel(true);
                                                            }
                                                        });
                                                    }


                                                    holder1.downloadRightVideo.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final ChatDownloadTask chatDownloadTask =
                                                                    new ChatDownloadTask(context,
                                                                            holder1.adCircleProgressRV,
                                                                            holder1.downloadRightVideo,
                                                                            message);

                                                            final String localDir = "/FrenzApp/Media/Videos/Sent/";
                                                            final String localfileName = System.currentTimeMillis() + ".mp4";
                                                            chatDownloadTask.execute("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                    "kering-heat-5334.appspot.com" +
                                                                    "/o/demo1.mp4?alt=media&token=f6d" +
                                                                    "82bb0-f61f-45bc-ab13-16970c7432c4", localDir, localfileName);

                                                            holder1.adCircleProgressRV.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                                    holder1.adCircleProgressRV.setVisibility(View.GONE);
                                                                    chatDownloadTask.cancel(true);
                                                                }
                                                            });
                                                        }
                                                    });

                                                    final ImageView imageView = new ImageView(context);
                                                    RelativeLayout.LayoutParams params =
                                                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                    imageView.setLayoutParams(params);
                                                    holder1.videoLL.addView(imageView);

                                                    Glide.with(context)
                                                            .load("https://firebasestorage.googleapis.com/v0/b/flic" +
                                                                    "kering-heat-5334.appspot.com" +
                                                                    "/o/demo1.mp4?alt=media&token=f6d" +
                                                                    "82bb0-f61f-45bc-ab13-16970c7432c4")
                                                            .apply(new RequestOptions().placeholder(R.drawable.video_clip))
                                                            .into(imageView);

                                                } else {
                                                    holder1.adCircleProgressRV.setVisibility(View.GONE);
                                                    holder1.downloadRightVideo.setVisibility(View.VISIBLE);
                                                    holder1.downloadRightVideo.setImageResource(R.drawable.video_icon);
                                                    final VideoPlayer videoPlayer = new VideoPlayer(context);
                                                    RelativeLayout.LayoutParams params =
                                                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                                                    videoPlayer.setLayoutParams(params);
                                                    videoPlayer.setScaleType(VideoPlayer.ScaleType.CENTER_CROP);
                                                    holder1.videoLL.addView(videoPlayer);

                                                    videoPlayer.loadVideo(message.getVideoLocalLocation(), message);
                                                    holder1.downloadRightVideo.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                                mediaPlayer.pause();
                                                            }
                                                            videoPlayer.setTransitionName("videoFF");
                                                            Intent intent = new Intent(context, VideoFFActivity.class);
                                                            intent.putExtra("videoURI", message.getVideoLocalLocation());
                                                            ActivityOptionsCompat optionsCompat =
                                                                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                                                            videoPlayer, videoPlayer.getTransitionName());
                                                            context.startActivity(intent, optionsCompat.toBundle());
                                                        }
                                                    });
                                                    videoPlayer.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                                mediaPlayer.pause();
                                                            }
                                                            videoPlayer.setTransitionName("videoFF");
                                                            Intent intent = new Intent(context, VideoFFActivity.class);
                                                            intent.putExtra("videoURI", message.getVideoLocalLocation());
                                                            ActivityOptionsCompat optionsCompat =
                                                                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                                                            videoPlayer, videoPlayer.getTransitionName());
                                                            context.startActivity(intent, optionsCompat.toBundle());
                                                        }
                                                    });
                                                }

                                            }


                                        } else {


                                            if (holder instanceof LeftAudioViewHolder) {
                                                final LeftAudioViewHolder holder1 = (LeftAudioViewHolder) holder;

                                                holder1.leftTimeTV.setText(message.getTime());
                                                if (message.getUserIcon() != null) {
                                                    Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                                                }
                                                holder1.senderNameTV.setText(message.getUserName());

                                                if (TextUtils.isEmpty(message.getAudioLocalLocation())) {
                                                    holder1.playPauseView.setVisibility(View.GONE);
                                                    holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                    holder1.adCircleProgressAudioLV.setVisibility(View.GONE);


                                                    // Check auto Audio Download and download if enabled
                                                    if (Settings.autoSaveSingleAudioToPhone()) {
                                                        final ChatDownloadTask chatDownloadTask =
                                                                new ChatDownloadTask(context,
                                                                        holder1.adCircleProgressAudioLV,
                                                                        holder1.downloadLeftAudio,
                                                                        message);

                                                        final String localDir = "/FrenzApp/Media/Audios/";
                                                        final String localfileName = System.currentTimeMillis() + ".mp3";
                                                        chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                localDir, localfileName);

                                                        holder1.adCircleProgressAudioLV.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                                holder1.adCircleProgressAudioLV.setVisibility(View.GONE);
                                                                chatDownloadTask.cancel(true);
                                                            }
                                                        });

                                                    }

                                                    holder1.downloadLeftAudio.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final ChatDownloadTask chatDownloadTask =
                                                                    new ChatDownloadTask(context,
                                                                            holder1.adCircleProgressAudioLV,
                                                                            holder1.downloadLeftAudio,
                                                                            message);

                                                            final String localDir = "/FrenzApp/Media/Audios/";
                                                            final String localfileName = System.currentTimeMillis() + ".mp3";
                                                            chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                    localDir, localfileName);

                                                            holder1.adCircleProgressAudioLV.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                                    holder1.adCircleProgressAudioLV.setVisibility(View.GONE);
                                                                    chatDownloadTask.cancel(true);
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    if (!(new File(message.getAudioLocalLocation()).exists())) {
                                                        holder1.playPauseView.setVisibility(View.GONE);
                                                        holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                        holder1.adCircleProgressAudioLV.setVisibility(View.GONE);


                                                        // Check auto Audio Download and download if enabled
                                                        if (Settings.autoSaveSingleAudioToPhone()) {
                                                            final ChatDownloadTask chatDownloadTask =
                                                                    new ChatDownloadTask(context,
                                                                            holder1.adCircleProgressAudioLV,
                                                                            holder1.downloadLeftAudio,
                                                                            message);

                                                            final String localDir = "/FrenzApp/Media/Audios/";
                                                            final String localfileName = System.currentTimeMillis() + ".mp3";
                                                            chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                    localDir, localfileName);


                                                            holder1.adCircleProgressAudioLV.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                                    holder1.adCircleProgressAudioLV.setVisibility(View.GONE);
                                                                    chatDownloadTask.cancel(true);
                                                                }
                                                            });
                                                        }


                                                        holder1.downloadLeftAudio.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                final ChatDownloadTask chatDownloadTask =
                                                                        new ChatDownloadTask(context,
                                                                                holder1.adCircleProgressAudioLV,
                                                                                holder1.downloadLeftAudio,
                                                                                message);

                                                                final String localDir = "/FrenzApp/Media/Audios/";
                                                                final String localfileName = System.currentTimeMillis() + ".mp3";
                                                                chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB", localDir, localfileName);

                                                                holder1.adCircleProgressAudioLV.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        holder1.downloadLeftAudio.setVisibility(View.VISIBLE);
                                                                        holder1.adCircleProgressAudioLV.setVisibility(View.GONE);
                                                                        chatDownloadTask.cancel(true);
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        holder1.playPauseView.setVisibility(View.VISIBLE);
                                                        holder1.downloadLeftAudio.setVisibility(View.GONE);
                                                        holder1.adCircleProgressAudioLV.setVisibility(View.GONE);
                                                        holder1.downloadLeftAudio.setOnClickListener(null);
                                                        holder1.adCircleProgressAudioLV.setOnClickListener(null);
                                                        holder1.setMessage(message);
                                                    }
                                                }

                                            } else {


                                                if (holder instanceof RightAudioViewHolder) {
                                                    final RightAudioViewHolder holder1 = (RightAudioViewHolder) holder;

                                                    holder1.rightTimeTV.setText(message.getTime());
                                                    if (message.getUserIcon() != null) {
                                                        Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                                    }
                                                    holder1.senderNameTV.setText(message.getUserName());

                                                    if (TextUtils.isEmpty(message.getAudioLocalLocation())) {
                                                        holder1.playPauseView.setVisibility(View.GONE);
                                                        holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                        holder1.adCircleProgressAudioRV.setVisibility(View.GONE);


                                                        // Check auto Audio Download and download if enabled
                                                        if (Settings.autoSaveSingleAudioToPhone()) {
                                                            final ChatDownloadTask chatDownloadTask =
                                                                    new ChatDownloadTask(context,
                                                                            holder1.adCircleProgressAudioRV,
                                                                            holder1.downloadRightAudio,
                                                                            message);

                                                            final String localDir = "/FrenzApp/Media/Audios/Sent/";
                                                            final String localfileName = System.currentTimeMillis() + ".mp3";
                                                            chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                    localDir, localfileName);


                                                            holder1.adCircleProgressAudioRV.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                                    holder1.adCircleProgressAudioRV.setVisibility(View.GONE);
                                                                    chatDownloadTask.cancel(true);

                                                                }
                                                            });
                                                        }


                                                        holder1.downloadRightAudio.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                final ChatDownloadTask chatDownloadTask =
                                                                        new ChatDownloadTask(context,
                                                                                holder1.adCircleProgressAudioRV,
                                                                                holder1.downloadRightAudio,
                                                                                message);

                                                                final String localDir = "/FrenzApp/Media/Audios/Sent/";
                                                                final String localfileName = System.currentTimeMillis() + ".mp3";
                                                                chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                        localDir, localfileName);


                                                                holder1.adCircleProgressAudioRV.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                                        holder1.adCircleProgressAudioRV.setVisibility(View.GONE);
                                                                        chatDownloadTask.cancel(true);
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        if (!(new File(message.getAudioLocalLocation()).exists())) {
                                                            holder1.playPauseView.setVisibility(View.GONE);
                                                            holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                            holder1.adCircleProgressAudioRV.setVisibility(View.GONE);


                                                            // Check auto Audio Download and download if enabled
                                                            if (Settings.autoSaveSingleAudioToPhone()) {
                                                                final ChatDownloadTask chatDownloadTask =
                                                                        new ChatDownloadTask(context,
                                                                                holder1.adCircleProgressAudioRV,
                                                                                holder1.downloadRightAudio,
                                                                                message);

                                                                final String localDir = "/FrenzApp/Media/Audios/Sent/";
                                                                final String localfileName = System.currentTimeMillis() + ".mp3";
                                                                chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                        localDir, localfileName);

                                                                holder1.adCircleProgressAudioRV.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                                        holder1.adCircleProgressAudioRV.setVisibility(View.GONE);
                                                                        chatDownloadTask.cancel(true);
                                                                    }
                                                                });
                                                            }


                                                            holder1.downloadRightAudio.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final ChatDownloadTask chatDownloadTask =
                                                                            new ChatDownloadTask(context,
                                                                                    holder1.adCircleProgressAudioRV,
                                                                                    holder1.downloadRightAudio,
                                                                                    message);

                                                                    final String localDir = "/FrenzApp/Media/Audios/Sent/";
                                                                    final String localfileName = System.currentTimeMillis() + ".mp3";
                                                                    chatDownloadTask.execute("https://d.mp3-send.com/DvWBZB:Z3z1rB",
                                                                            localDir, localfileName);

                                                                    holder1.adCircleProgressAudioRV.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            holder1.downloadRightAudio.setVisibility(View.VISIBLE);
                                                                            holder1.adCircleProgressAudioRV.setVisibility(View.GONE);
                                                                            chatDownloadTask.cancel(true);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        } else {
                                                            holder1.playPauseView.setVisibility(View.VISIBLE);
                                                            holder1.downloadRightAudio.setVisibility(View.GONE);
                                                            holder1.adCircleProgressAudioRV.setVisibility(View.GONE);
                                                            holder1.downloadRightAudio.setOnClickListener(null);
                                                            holder1.adCircleProgressAudioRV.setOnClickListener(null);
                                                            holder1.setMessage(message);
                                                        }
                                                    }
                                                } else {

                                                    if (holder instanceof LeftStickerViewHolder) {
                                                        final LeftStickerViewHolder holder1 = (LeftStickerViewHolder) holder;

                                                        if (message.getUserIcon() != null) {
                                                            Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                                                        }
                                                        holder1.senderNameTV.setText(message.getUserName());
                                                        holder1.leftTimeTV.setText(message.getTime());

                                                        if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                            holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final ChatDownloadTask chatDownloadTask =
                                                                            new ChatDownloadTask(context,
                                                                                    holder1.adCircleProgressLeftIV,
                                                                                    holder1.downloadLeftImage,
                                                                                    message);

                                                                    final String localDir = "/FrenzApp/Media/stickers/";
                                                                    final String localFileName = message.getBody();
                                                                    chatDownloadTask.execute("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                                                                            localDir, localFileName);

                                                                    holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            chatDownloadTask.cancel(true);
                                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                        }
                                                                    });
                                                                }
                                                            });


                                                            ////Check if auto download single Image is enabled
                                                            //// if so start downloading
                                                            if (Settings.autoSaveSingleImageToGallery()) {
                                                                final ChatDownloadTask chatDownloadTask =
                                                                        new ChatDownloadTask(context,
                                                                                holder1.adCircleProgressLeftIV,
                                                                                holder1.downloadLeftImage,
                                                                                message);

                                                                final String localDir = "/FrenzApp/Media/stickers/";
                                                                final String localFileName = message.getBody();
                                                                chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                                                                holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        chatDownloadTask.cancel(true);
                                                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                    }
                                                                });
                                                            }


                                                            Glide.with(context)
                                                                    .load("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg")
                                                                    .into(holder1.leftIV);
                                                        } else {
                                                            if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                                ////Check if auto download single Image is enabled
                                                                //// if so start downloading
                                                                if (Settings.autoSaveSingleImageToGallery()) {
                                                                    final ChatDownloadTask chatDownloadTask =
                                                                            new ChatDownloadTask(context,
                                                                                    holder1.adCircleProgressLeftIV,
                                                                                    holder1.downloadLeftImage,
                                                                                    message);

                                                                    final String localDir = "/FrenzApp/Media/stickers/";
                                                                    final String localFileName = message.getBody();
                                                                    chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                                                    holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            chatDownloadTask.cancel(true);
                                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                        }
                                                                    });
                                                                }


                                                                holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                new ChatDownloadTask(context,
                                                                                        holder1.adCircleProgressLeftIV,
                                                                                        holder1.downloadLeftImage,
                                                                                        message);


                                                                        final String localDir = "/FrenzApp/Media/stickers/";
                                                                        final String localFileName = message.getBody();
                                                                        chatDownloadTask.execute("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                                                                                localDir, localFileName);


                                                                        holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                chatDownloadTask.cancel(true);
                                                                                holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                            }
                                                                        });

                                                                    }
                                                                });

                                                                Glide.with(context)
                                                                        .load("https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg")
                                                                        .into(holder1.leftIV);

                                                            } else {
                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                holder1.downloadLeftImage.setVisibility(View.GONE);
                                                                if (message.getImageLocalLocation() != null
                                                                        && !message.getImageLocalLocation().equals("")) {
                                                                    final File image =
                                                                            DiskCacheUtils.findInCache(message.getImageLocalLocation(),
                                                                                    imageLoader.getDiskCache());
                                                                    if (image != null && image.exists()) {
                                                                        Picasso.get().load(image).into(holder1.leftIV);
                                                                    } else {

                                                                        imageLoader.loadImage("file://" + message.getImageLocalLocation(), new ImageLoadingListener() {
                                                                            @Override
                                                                            public void onLoadingStarted(String s, View view) {
                                                                                holder1.leftIV.setImageBitmap(null);
                                                                                Log.e(TAG, "onLoadingStarted ----> " + s);
                                                                            }

                                                                            @Override
                                                                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                                                                Log.e(TAG, failReason.toString());
                                                                            }

                                                                            @Override
                                                                            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                                                                                Picasso.get().load(s).into(holder1.leftIV);
                                                                            }

                                                                            @Override
                                                                            public void onLoadingCancelled(String s, View view) {

                                                                            }
                                                                        });
                                                                    }
                                                                } else {
                                                                    holder1.leftIV.setImageBitmap(null);
                                                                }
                                                                holder1.leftIV.setTransitionName("photoTransition");
//                                                                holder1.leftIV.setOnClickListener(new View.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(View view) {
//                                                                        Intent intent = new Intent(context, ImageFFActivity.class);
//                                                                        intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
//                                                                        ActivityOptionsCompat optionsCompat =
//                                                                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.leftIV, holder1.leftIV.getTransitionName());
//                                                                        context.startActivity(intent, optionsCompat.toBundle());
//                                                                    }
//                                                                });

                                                            }
                                                        }

                                                    } else {

                                                        if (holder instanceof RightStickerViewHolder) {
                                                            final RightStickerViewHolder holder1 = (RightStickerViewHolder) holder;

                                                            if (message.getUserIcon() != null) {
                                                                Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                                            }
                                                            holder1.senderNameTV.setText(message.getUserName());
                                                            holder1.rightTimeTV.setText(message.getTime());

                                                            if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);


                                                                ////Check if auto download stickers is enabled
                                                                //// if so start downloading
                                                                if (Settings.autoSaveStickersToGallery()) {
                                                                    final ChatDownloadTask chatDownloadTask =
                                                                            new ChatDownloadTask(context,
                                                                                    holder1.adCircleProgressRightIV,
                                                                                    holder1.downloadRightImage,
                                                                                    message);

                                                                    final String localDir = "/FrenzApp/Media/stickers/";
                                                                    final String localFileName = message.getBody();
                                                                    chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                                                    holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            chatDownloadTask.cancel(true);
                                                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                        }
                                                                    });
                                                                }


                                                                holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                new ChatDownloadTask(context,
                                                                                        holder1.adCircleProgressRightIV,
                                                                                        holder1.downloadRightImage,
                                                                                        message);

                                                                        final String localDir = "/FrenzApp/Media/stickers/";
                                                                        final String localFileName = message.getBody();
                                                                        chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                                "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                                                                        holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                chatDownloadTask.cancel(true);
                                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                                Glide.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                        "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s").into(holder1.rightIV);

                                                            } else {
                                                                if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                    holder1.downloadRightImage.setVisibility(View.VISIBLE);

                                                                    ////Check if auto download stickers is enabled
                                                                    //// if so start downloading
                                                                    if (Settings.autoSaveStickersToGallery()) {
                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                new ChatDownloadTask(context,
                                                                                        holder1.adCircleProgressRightIV,
                                                                                        holder1.downloadRightImage,
                                                                                        message);


                                                                        final String localDir = "/FrenzApp/Media/stickers/";
                                                                        final String localFileName = message.getBody();
                                                                        chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                                "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);


                                                                        holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                chatDownloadTask.cancel(true);
                                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                            }
                                                                        });
                                                                    }


                                                                    holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            final ChatDownloadTask chatDownloadTask =
                                                                                    new ChatDownloadTask(context,
                                                                                            holder1.adCircleProgressRightIV,
                                                                                            holder1.downloadRightImage,
                                                                                            message);

                                                                            final String localDir = "/FrenzApp/Media/stickers/";
                                                                            final String localFileName = message.getBody();
                                                                            chatDownloadTask.execute("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                                    "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s", localDir, localFileName);

                                                                            holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    chatDownloadTask.cancel(true);
                                                                                    holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                    Glide.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:" +
                                                                            "ANd9GcRFwFaTk5hkuQAO89Oy0P8Jk9GSLXpwb9b4vgO6WZ-d3iRNW3KE&s").into(holder1.rightIV);
                                                                } else {
                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                    holder1.downloadRightImage.setVisibility(View.GONE);
                                                                    if (message.getImageLocalLocation() != null
                                                                            && !message.getImageLocalLocation().equals("")) {
                                                                        final File image =
                                                                                DiskCacheUtils.findInCache(message.getImageLocalLocation(),
                                                                                        imageLoader.getDiskCache());
                                                                        if (image != null && image.exists()) {
                                                                            Picasso.get().load(image).into(holder1.rightIV);
                                                                        } else {

                                                                            imageLoader.loadImage("file://" + message.getImageLocalLocation(), new ImageLoadingListener() {
                                                                                @Override
                                                                                public void onLoadingStarted(String s, View view) {
                                                                                    holder1.rightIV.setImageBitmap(null);
                                                                                }

                                                                                @Override
                                                                                public void onLoadingFailed(String s, View view, FailReason failReason) {
                                                                                    Log.e(TAG, failReason.toString());
                                                                                }

                                                                                @Override
                                                                                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                                                                                    Picasso.get().load(s).into(holder1.rightIV);

                                                                                }

                                                                                @Override
                                                                                public void onLoadingCancelled(String s, View view) {

                                                                                }
                                                                            });
                                                                        }
                                                                    } else {
                                                                        holder1.rightIV.setImageBitmap(null);
                                                                    }

//                                                                    holder1.rightIV.setTransitionName("photoTransition");
//                                                                    holder1.rightIV.setOnClickListener(new View.OnClickListener() {
//                                                                        @Override
//                                                                        public void onClick(View view) {
//                                                                            Intent intent = new Intent(context, ImageFFActivity.class);
//                                                                            intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
//                                                                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.rightIV, holder1.rightIV.getTransitionName());
//                                                                            context.startActivity(intent, optionsCompat.toBundle());
//                                                                        }
//                                                                    });

                                                                }
                                                            }

                                                        } else {

                                                            if (holder instanceof LeftGIFViewHolder) {
                                                                final LeftGIFViewHolder holder1 = (LeftGIFViewHolder) holder;

                                                                if (message.getUserIcon() != null) {
                                                                    Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                                                                }
                                                                holder1.senderNameTV.setText(message.getUserName());
                                                                holder1.leftTimeTV.setText(message.getTime());

                                                                if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                                    holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                    holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                                    holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            final ChatDownloadTask chatDownloadTask =
                                                                                    new ChatDownloadTask(context,
                                                                                            holder1.adCircleProgressLeftIV,
                                                                                            holder1.downloadLeftImage,
                                                                                            message);

                                                                            final String localDir = "/FrenzApp/Media/gifs/";
                                                                            final String localFileName = message.getBody();
                                                                            chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                            holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    chatDownloadTask.cancel(true);
                                                                                    holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                    holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                        }
                                                                    });


                                                                    ////Check if auto download gifs is enabled
                                                                    //// if so start downloading
                                                                    if (Settings.autoSaveGifsToGallery()) {
                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                new ChatDownloadTask(context,
                                                                                        holder1.adCircleProgressLeftIV,
                                                                                        holder1.downloadLeftImage,
                                                                                        message);

                                                                        final String localDir = "/FrenzApp/Media/gifs/";
                                                                        final String localFileName = message.getBody();
                                                                        chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                        holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                chatDownloadTask.cancel(true);
                                                                                holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                            }
                                                                        });
                                                                    }


                                                                    Glide.with(context)
                                                                            .asGif()
                                                                            .load(message.getSingleUrl())
                                                                            .into(holder1.leftIV);
                                                                } else {
                                                                    if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                                        ////Check if auto download gifs is enabled
                                                                        //// if so start downloading
                                                                        if (Settings.autoSaveGifsToGallery()) {
                                                                            final ChatDownloadTask chatDownloadTask =
                                                                                    new ChatDownloadTask(context,
                                                                                            holder1.adCircleProgressLeftIV,
                                                                                            holder1.downloadLeftImage,
                                                                                            message);

                                                                            final String localDir = "/FrenzApp/Media/gifs/";
                                                                            final String localFileName = message.getBody();
                                                                            chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);


                                                                            holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    chatDownloadTask.cancel(true);
                                                                                    holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                    holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                        }


                                                                        holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                final ChatDownloadTask chatDownloadTask =
                                                                                        new ChatDownloadTask(context,
                                                                                                holder1.adCircleProgressLeftIV,
                                                                                                holder1.downloadLeftImage,
                                                                                                message);


                                                                                final String localDir = "/FrenzApp/Media/gifs/";
                                                                                final String localFileName = message.getBody();
                                                                                chatDownloadTask.execute(message.getSingleUrl(),
                                                                                        localDir, localFileName);


                                                                                holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        chatDownloadTask.cancel(true);
                                                                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                    }
                                                                                });

                                                                            }
                                                                        });

                                                                        Glide.with(context)
                                                                                .asGif()
                                                                                .load(message.getSingleUrl())
                                                                                .into(holder1.leftIV);

                                                                    } else {
                                                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                        holder1.downloadLeftImage.setVisibility(View.GONE);
                                                                        if (message.getImageLocalLocation() != null
                                                                                && !message.getImageLocalLocation().equals("")) {
                                                                            Glide.with(context)
                                                                                    .asGif()
                                                                                    .load(message.getImageLocalLocation())
                                                                                    .into(holder1.leftIV);
                                                                        } else {
                                                                            holder1.leftIV.setImageBitmap(null);
                                                                        }

                                                                        holder1.leftIV.setTransitionName("photoTransition");
                                                                        holder1.leftIV.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                Intent intent = new Intent(context, ImageFFActivity.class);
                                                                                intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
                                                                                intent.putExtra("its_gif", true);
                                                                                ActivityOptionsCompat optionsCompat =
                                                                                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.leftIV, holder1.leftIV.getTransitionName());
                                                                                context.startActivity(intent, optionsCompat.toBundle());
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            } else {

                                                                if (holder instanceof RightGIFViewHolder) {
                                                                    final RightGIFViewHolder holder1 = (RightGIFViewHolder) holder;

                                                                    if (message.getUserIcon() != null) {
                                                                        Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                                                    }
                                                                    holder1.senderNameTV.setText(message.getUserName());
                                                                    holder1.rightTimeTV.setText(message.getTime());

                                                                    if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                                        holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                        holder1.downloadRightImage.setVisibility(View.VISIBLE);


                                                                        holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                final ChatDownloadTask chatDownloadTask =
                                                                                        new ChatDownloadTask(context,
                                                                                                holder1.adCircleProgressRightIV,
                                                                                                holder1.downloadRightImage,
                                                                                                message);

                                                                                final String localDir = "/FrenzApp/Media/gifs/";
                                                                                final String localFileName = message.getBody();
                                                                                chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                                holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        chatDownloadTask.cancel(true);
                                                                                        holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                        holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                    }
                                                                                });
                                                                            }
                                                                        });


                                                                        ////Check if auto download gifs is enabled
                                                                        //// if so start downloading
                                                                        if (Settings.autoSaveGifsToGallery()) {
                                                                            final ChatDownloadTask chatDownloadTask =
                                                                                    new ChatDownloadTask(context,
                                                                                            holder1.adCircleProgressRightIV,
                                                                                            holder1.downloadRightImage,
                                                                                            message);

                                                                            final String localDir = "/FrenzApp/Media/gifs/";
                                                                            final String localFileName = message.getBody();
                                                                            chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                            holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    chatDownloadTask.cancel(true);
                                                                                    holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                }
                                                                            });
                                                                        }


                                                                        Glide.with(context)
                                                                                .asGif()
                                                                                .load(message.getSingleUrl())
                                                                                .into(holder1.rightIV);
                                                                    } else {
                                                                        if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);


                                                                            ////Check if auto download gifs is enabled
                                                                            //// if so start downloading
                                                                            if (Settings.autoSaveGifsToGallery()) {
                                                                                final ChatDownloadTask chatDownloadTask =
                                                                                        new ChatDownloadTask(context,
                                                                                                holder1.adCircleProgressRightIV,
                                                                                                holder1.downloadRightImage,
                                                                                                message);

                                                                                final String localDir = "/FrenzApp/Media/gifs/";
                                                                                final String localFileName = message.getBody();
                                                                                chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);


                                                                                holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        chatDownloadTask.cancel(true);
                                                                                        holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                        holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                    }
                                                                                });
                                                                            }


                                                                            holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    final ChatDownloadTask chatDownloadTask =
                                                                                            new ChatDownloadTask(context,
                                                                                                    holder1.adCircleProgressRightIV,
                                                                                                    holder1.downloadRightImage,
                                                                                                    message);


                                                                                    final String localDir = "/FrenzApp/Media/gifs/";
                                                                                    final String localFileName = message.getBody();
                                                                                    chatDownloadTask.execute(message.getSingleUrl(),
                                                                                            localDir, localFileName);


                                                                                    holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            chatDownloadTask.cancel(true);
                                                                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                        }
                                                                                    });

                                                                                }
                                                                            });

                                                                            Glide.with(context)
                                                                                    .asGif()
                                                                                    .load(message.getSingleUrl())
                                                                                    .into(holder1.rightIV);

                                                                        } else {
                                                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                            holder1.downloadRightImage.setVisibility(View.GONE);
                                                                            if (message.getImageLocalLocation() != null
                                                                                    && !message.getImageLocalLocation().equals("")) {
                                                                                Glide.with(context)
                                                                                        .asGif()
                                                                                        .load(message.getImageLocalLocation())
                                                                                        .into(holder1.rightIV);
                                                                            } else {
                                                                                holder1.rightIV.setImageBitmap(null);
                                                                            }

                                                                            holder1.rightIV.setTransitionName("photoTransition");
                                                                            holder1.rightIV.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    Intent intent = new Intent(context, ImageFFActivity.class);
                                                                                    intent.putExtra("photoURI", "file://" + message.getImageLocalLocation());
                                                                                    intent.putExtra("its_gif", true);
                                                                                    ActivityOptionsCompat optionsCompat =
                                                                                            ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder1.rightIV, holder1.rightIV.getTransitionName());
                                                                                    context.startActivity(intent, optionsCompat.toBundle());
                                                                                }
                                                                            });
//
                                                                        }
                                                                    }

                                                                } else {

                                                                    if (holder instanceof LeftSoundViewHolder) {
                                                                        final LeftSoundViewHolder holder1 = (LeftSoundViewHolder) holder;

                                                                        if (message.getUserIcon() != null) {
                                                                            Picasso.get().load(message.getUserIcon()).into(holder1.leftBubbleIconIV);
                                                                        }
                                                                        holder1.senderNameTV.setText(message.getUserName());
                                                                        holder1.leftTimeTV.setText(message.getTime());

                                                                        if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                                            holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    final ChatDownloadTask chatDownloadTask =
                                                                                            new ChatDownloadTask(context,
                                                                                                    holder1.adCircleProgressLeftIV,
                                                                                                    holder1.downloadLeftImage,
                                                                                                    message);

                                                                                    final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                    final String localFileName = message.getBody();
                                                                                    chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                                    holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            chatDownloadTask.cancel(true);
                                                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });


                                                                            ////Check if auto download gifs is enabled
                                                                            //// if so start downloading
                                                                            if (Settings.autoSaveGifsToGallery()) {
                                                                                final ChatDownloadTask chatDownloadTask =
                                                                                        new ChatDownloadTask(context,
                                                                                                holder1.adCircleProgressLeftIV,
                                                                                                holder1.downloadLeftImage,
                                                                                                message);

                                                                                final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                final String localFileName = message.getBody();
                                                                                chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                                holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        chatDownloadTask.cancel(true);
                                                                                        holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                        holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                    }
                                                                                });
                                                                            }


                                                                            Glide.with(context)
                                                                                    .asGif()
                                                                                    .load(message.getSingleUrl())
                                                                                    .into(holder1.leftIV);
                                                                        } else {
                                                                            if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                holder1.downloadLeftImage.setVisibility(View.VISIBLE);


                                                                                ////Check if auto download gifs is enabled
                                                                                //// if so start downloading
                                                                                if (Settings.autoSaveGifsToGallery()) {
                                                                                    final ChatDownloadTask chatDownloadTask =
                                                                                            new ChatDownloadTask(context,
                                                                                                    holder1.adCircleProgressLeftIV,
                                                                                                    holder1.downloadLeftImage,
                                                                                                    message);

                                                                                    final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                    final String localFileName = message.getBody();
                                                                                    chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);


                                                                                    holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            chatDownloadTask.cancel(true);
                                                                                            holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                            holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                        }
                                                                                    });
                                                                                }


                                                                                holder1.downloadLeftImage.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                                new ChatDownloadTask(context,
                                                                                                        holder1.adCircleProgressLeftIV,
                                                                                                        holder1.downloadLeftImage,
                                                                                                        message);


                                                                                        final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                        final String localFileName = message.getBody();
                                                                                        chatDownloadTask.execute(message.getSingleUrl(),
                                                                                                localDir, localFileName);


                                                                                        holder1.adCircleProgressLeftIV.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                chatDownloadTask.cancel(true);
                                                                                                holder1.downloadLeftImage.setVisibility(View.VISIBLE);
                                                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                            }
                                                                                        });

                                                                                    }
                                                                                });

                                                                                Glide.with(context)
                                                                                        .asGif()
                                                                                        .load(message.getSingleUrl())
                                                                                        .into(holder1.leftIV);

                                                                            } else {
                                                                                holder1.adCircleProgressLeftIV.setVisibility(View.GONE);
                                                                                holder1.downloadLeftImage.setVisibility(View.GONE);
                                                                                if (message.getImageLocalLocation() != null
                                                                                        && !message.getImageLocalLocation().equals("")) {
                                                                                    Glide.with(context)
                                                                                            .asGif()
                                                                                            .load(message.getImageLocalLocation())
                                                                                            .into(holder1.leftIV);
                                                                                } else {
                                                                                    holder1.leftIV.setImageBitmap(null);
                                                                                }

                                                                                holder1.leftIV.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View view) {
                                                                                        String soundName = message.getBody().replace(".png", ".mp3").replace(".gif", ".mp3");
                                                                                        File file = new File(Environment.getExternalStorageDirectory()
                                                                                                .getAbsolutePath() + "/FrenzApp/Media/sounds/SoundAudios/" + soundName);
                                                                                        if (!(file.exists()) || (!file.isFile())) {
                                                                                            playSoundAudio(file, true);
                                                                                        } else if (file.exists() && file.isFile()) {
                                                                                            playSoundAudio(file, false);
                                                                                        } else {
                                                                                            Toast.makeText(context, "Error....", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }

                                                                    } else {

                                                                        if (holder instanceof RightSoundViewHolder) {
                                                                            final RightSoundViewHolder holder1 = (RightSoundViewHolder) holder;

                                                                            if (message.getUserIcon() != null) {
                                                                                Picasso.get().load(message.getUserIcon()).into(holder1.rightBubbleIconIV);
                                                                            }
                                                                            holder1.senderNameTV.setText(message.getUserName());
                                                                            holder1.rightTimeTV.setText(message.getTime());

                                                                            if (TextUtils.isEmpty(message.getImageLocalLocation())) {
                                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);


                                                                                holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                                new ChatDownloadTask(context,
                                                                                                        holder1.adCircleProgressRightIV,
                                                                                                        holder1.downloadRightImage,
                                                                                                        message);

                                                                                        final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                        final String localFileName = message.getBody();
                                                                                        chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                                        holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                chatDownloadTask.cancel(true);
                                                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });


                                                                                ////Check if auto download gifs is enabled
                                                                                //// if so start downloading
                                                                                if (Settings.autoSaveGifsToGallery()) {
                                                                                    final ChatDownloadTask chatDownloadTask =
                                                                                            new ChatDownloadTask(context,
                                                                                                    holder1.adCircleProgressRightIV,
                                                                                                    holder1.downloadRightImage,
                                                                                                    message);

                                                                                    final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                    final String localFileName = message.getBody();
                                                                                    chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);

                                                                                    holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            chatDownloadTask.cancel(true);
                                                                                            holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                            holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                        }
                                                                                    });
                                                                                }


                                                                                Glide.with(context)
                                                                                        .asGif()
                                                                                        .load(message.getSingleUrl())
                                                                                        .into(holder1.rightIV);
                                                                            } else {
                                                                                if (!(new File(message.getImageLocalLocation()).exists())) {
                                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                    holder1.downloadRightImage.setVisibility(View.VISIBLE);


                                                                                    ////Check if auto download gifs is enabled
                                                                                    //// if so start downloading
                                                                                    if (Settings.autoSaveGifsToGallery()) {
                                                                                        final ChatDownloadTask chatDownloadTask =
                                                                                                new ChatDownloadTask(context,
                                                                                                        holder1.adCircleProgressRightIV,
                                                                                                        holder1.downloadRightImage,
                                                                                                        message);

                                                                                        final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                        final String localFileName = message.getBody();
                                                                                        chatDownloadTask.execute(message.getSingleUrl(), localDir, localFileName);


                                                                                        holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                chatDownloadTask.cancel(true);
                                                                                                holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                                holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                            }
                                                                                        });
                                                                                    }


                                                                                    holder1.downloadRightImage.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            final ChatDownloadTask chatDownloadTask =
                                                                                                    new ChatDownloadTask(context,
                                                                                                            holder1.adCircleProgressRightIV,
                                                                                                            holder1.downloadRightImage,
                                                                                                            message);


                                                                                            final String localDir = "/FrenzApp/Media/sounds/SoundImages/";
                                                                                            final String localFileName = message.getBody();
                                                                                            chatDownloadTask.execute(message.getSingleUrl(),
                                                                                                    localDir, localFileName);


                                                                                            holder1.adCircleProgressRightIV.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    chatDownloadTask.cancel(true);
                                                                                                    holder1.downloadRightImage.setVisibility(View.VISIBLE);
                                                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });

                                                                                    Glide.with(context)
                                                                                            .asGif()
                                                                                            .load(message.getSingleUrl())
                                                                                            .into(holder1.rightIV);

                                                                                } else {
                                                                                    holder1.adCircleProgressRightIV.setVisibility(View.GONE);
                                                                                    holder1.downloadRightImage.setVisibility(View.GONE);
                                                                                    if (message.getImageLocalLocation() != null
                                                                                            && !message.getImageLocalLocation().equals("")) {
                                                                                        Glide.with(context)
                                                                                                .asGif()
                                                                                                .load(message.getImageLocalLocation())
                                                                                                .into(holder1.rightIV);
                                                                                    } else {
                                                                                        holder1.rightIV.setImageBitmap(null);
                                                                                    }

                                                                                    holder1.rightIV.setTransitionName("photoTransition");
                                                                                    holder1.rightIV.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View view) {
                                                                                            String soundName = message.getBody().replace(".png", ".mp3").replace(".gif", ".mp3");
                                                                                            File file = new File(Environment.getExternalStorageDirectory()
                                                                                                    .getAbsolutePath() + "/FrenzApp/Media/sounds/SoundAudios/" + soundName);
                                                                                            if (!(file.exists()) || (!file.isFile())) {
                                                                                                playSoundAudio(file, true);
                                                                                            } else if (file.exists() && file.isFile()) {
                                                                                                playSoundAudio(file, false);
                                                                                            } else {
                                                                                                Toast.makeText(context, "Error....", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }

                                                                        } else {

                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d(TAG, "onViewRecycledCalled");
        if (holder instanceof LeftVideoViewHolder) {
            ((LeftVideoViewHolder) holder).videoLL.removeAllViews();
        } else {
            if (holder instanceof RightVideoViewHolder) {
                ((RightVideoViewHolder) holder).videoLL.removeAllViews();
            }
        }

    }


    @Override
    public int getItemCount() {
        return filterList.size();
    }


    @SuppressLint("SetTextI18n")
    private void playSoundAudio(final File file, boolean showDownload) {
        if (showDownload) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.item_sound_option_dialog);

            final File soundImage = new File(file
                    .getAbsolutePath().replace("SoundAudios", "SoundImages")
                    .replace(".mp3", ".gif"));

            TextView name = dialog.findViewById(R.id.name);
            ImageView image = dialog.findViewById(R.id.goProDialogImage);
            ImageView download = dialog.findViewById(R.id.downloadSound);
            LinearLayout downloadLayout = dialog.findViewById(R.id.downloadLayout);
            LinearLayout playSoundLayout = dialog.findViewById(R.id.playSoundLayout);
            LinearLayout sendLayout = dialog.findViewById(R.id.sendLayout);
            downloadLayout.setVisibility(View.VISIBLE);
            playSoundLayout.setVisibility(View.GONE);
            sendLayout.setVisibility(View.GONE);

            name.setText("                                                       " +
                    "                                                              ");

            Glide.with(context)
                    .asGif()
                    .load(soundImage)
                    .into(image);
            dialog.show();


            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadSoundAudio(context, file.getName());
                }
            });

        } else {
            mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mMediaPlayer.start();
        }
    }

    // set adapter filtered list
    public void setList(List<Message> list) {
        this.filterList = list;
    }

    public void showLeftBubbleIcon(boolean b) {
        this.showLeftBubbleIcon = b;
    }

    public void showRightBubbleIcon(boolean b) {
        this.showRightBubbleIcon = b;
    }

    public void setLeftBubbleLayoutColor(int color) {
        this.leftBubbleLayoutColor = color;
    }

    public void setRightBubbleLayoutColor(int color) {
        this.rightBubbleLayoutColor = color;
    }

    public void setLeftBubbleTextColor(int color) {
        this.leftBubbleTextColor = color;
    }

    public void setRightBubbleTextColor(int color) {
        this.rightBubbleTextColor = color;
    }

    public void setTimeTextColor(int color) {
        this.timeTextColor = color;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void showSenderName(boolean b) {
        this.showSenderName = b;
    }

    public void setSenderNameTextColor(int color) {
        this.senderNameTextColor = color;
    }

    public void setTextSize(float size) {
        this.textSize = size;
    }


}
